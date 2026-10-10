package com.overmind.zero;

import java.util.HashSet;

/**
 * Observes the actual simulated nucleus, never chooses a destination.
 * Explored cells are an unsigned history of places touched by the body.
 * Re-visiting a cell yields no additional exploration credit within a life.
 * Long loops can remain useful if they produce contact or energy gain.
 */
public final class MotionExploration {
    public static final float CELL=75f;
    private static final int MAX_CELLS_PER_LIFE=4096;
    private final HashSet<Long> cells=new HashSet<>();
    private float originX,originY;
    public int lifeCells=0,totalCells=0;
    public float netDistance=0,maxNetLife=0,recordNet=0;
    public float lifeEnergySpent=0,totalEnergySpent=0;
    public float lifeEnergyGained=0;
    private int newSinceAction=0;

    public void restore(int storedCells,float storedRecord,float storedEnergy){
        totalCells=Math.max(0,storedCells);
        recordNet=Float.isFinite(storedRecord)?Math.max(0,storedRecord):0;
        totalEnergySpent=Float.isFinite(storedEnergy)?Math.max(0,storedEnergy):0;
    }
    public void newLife(float x,float y){
        cells.clear();
        originX=x;originY=y;
        lifeCells=0;netDistance=0;maxNetLife=0;
        lifeEnergySpent=0;lifeEnergyGained=0;newSinceAction=0;
        addCell(x,y);
    }
    private void addCell(float x,float y){
        if(cells.size()>=MAX_CELLS_PER_LIFE)return;
        long gridX=(long)Math.floor(x/CELL);
        long gridY=(long)Math.floor(y/CELL);
        long key=(gridX<<32)^(gridY&0xffffffffL);
        if(cells.add(key)){lifeCells++;totalCells++;newSinceAction++;}
    }
    /** Save exactly the current life's visited cells, so loading a saved
     * physical position never invents new exploration credit or net travel.
     * Global totals are already persisted in the working brain profile. */
    public String saveLife(){
        StringBuilder b=new StringBuilder("life1|")
            .append(originX).append(',').append(originY).append(',')
            .append(lifeCells).append(',').append(netDistance).append(',')
            .append(maxNetLife).append(',').append(lifeEnergySpent).append(',')
            .append(lifeEnergyGained).append(',').append(newSinceAction).append('|');
        boolean first=true;
        for(Long key:cells){
            if(!first)b.append(',');first=false;
            b.append(Long.toString(key,36));
        }
        return b.toString();
    }
    public boolean loadLife(String raw){
        if(raw==null||raw.length()>150000||!raw.startsWith("life1|"))return false;
        try{
            String[] parts=raw.split("\\|",-1);
            if(parts.length!=3)return false;
            String[] v=parts[1].split(",",-1);
            if(v.length!=8)return false;
            float ox=Float.parseFloat(v[0]),oy=Float.parseFloat(v[1]);
            int amount=Integer.parseInt(v[2]),newCount=Integer.parseInt(v[7]);
            float net=Float.parseFloat(v[3]),max=Float.parseFloat(v[4]),
                  spent=Float.parseFloat(v[5]),gained=Float.parseFloat(v[6]);
            if(!Float.isFinite(ox)||!Float.isFinite(oy)||
               !Float.isFinite(net)||!Float.isFinite(max)||
               !Float.isFinite(spent)||!Float.isFinite(gained)||
               amount<0||amount>MAX_CELLS_PER_LIFE||newCount<0||
               newCount>MAX_CELLS_PER_LIFE)return false;
            HashSet<Long> parsed=new HashSet<>();
            if(!parts[2].isEmpty())
                for(String item:parts[2].split(","))parsed.add(Long.parseLong(item,36));
            if(parsed.size()!=amount)return false;
            originX=ox;originY=oy;
            cells.clear();cells.addAll(parsed);
            lifeCells=amount;netDistance=net;maxNetLife=max;
            lifeEnergySpent=spent;lifeEnergyGained=gained;newSinceAction=newCount;
            return true;
        }catch(RuntimeException ignored){return false;}
    }
    /** Read only after the world's collision/energy computation. */
    public void observe(float x,float y,float energyBefore,float energyAfter){
        if(!Float.isFinite(x)||!Float.isFinite(y)||
           !Float.isFinite(energyBefore)||!Float.isFinite(energyAfter))return;
        addCell(x,y);
        netDistance=(float)Math.hypot(x-originX,y-originY);
        maxNetLife=Math.max(maxNetLife,netDistance);
        recordNet=Math.max(recordNet,maxNetLife);
        float energyDelta=energyAfter-energyBefore;
        if(energyDelta<0){
            lifeEnergySpent+=-energyDelta;
            totalEnergySpent+=-energyDelta;
        }else lifeEnergyGained+=energyDelta;
    }
    public int takeNewCells(){
        int n=newSinceAction;
        newSinceAction=0;
        return n;
    }
    /** Maximum net reach per unit actually lost in this life; 0 means stuck. */
    public float efficiency(){return maxNetLife/Math.max(1,lifeEnergySpent);}
    public static void main(String[] args){
        MotionExploration m=new MotionExploration();
        m.newLife(0,0);
        if(m.lifeCells!=1||m.takeNewCells()!=1)throw new AssertionError("seed");
        float[][] loop={{55,0},{90,0},{90,85},{0,85},{0,0}};
        for(float[] p:loop)m.observe(p[0],p[1],80,79);
        if(m.netDistance>0.01||m.maxNetLife<120||m.lifeCells<3)
            throw new AssertionError("Loop path was confused with net");
        int visited=m.lifeCells;
        for(float[] p:loop)m.observe(p[0],p[1],70,70);
        if(m.lifeCells!=visited)throw new AssertionError("Circular repeat got novelty");
        int before=m.totalCells;
        float record=m.recordNet;
        m.newLife(0,0);
        if(m.lifeCells!=1||m.totalCells!=before+1||
           m.recordNet!=record||m.lifeEnergySpent!=0)
            throw new AssertionError("Lived world reset lost historical record");
        m.observe(150,0,80,77);
        if(m.netDistance!=150||m.recordNet<150||m.efficiency()<=10)
            throw new AssertionError("Real movement/energy metrics failed");
        System.out.println("ZERO-009.4 telemetry: loop suppression, net travel, finite energy, history preserved");
    }
}
