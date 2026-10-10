package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Observer-only statistics for lives completed in the REAL simulation.
 * Never feeds a value or reward to any decision-making controller.
 * One simulated second is the sum of the physics slices, NOT wall-clock time.
 */
public final class LifeStatistics {
    public static final int HISTORY=50;
    public static final String KEY="life_statistics_v104";
    public static final int DURATION=0,GOOD=1,HARM=2,GAIN=3,DAMAGE=4,
                            DISKS=5,CELLS=6,NET=7,GATE=8,ROOMS=9,RECALL=10;
    public static final String[] LABELS={
        "Sopravvivenza","Risorse positive raccolte","Contatti dannosi",
        "Energia raccolta","Energia persa per contatto","Dischi spostati",
        "Zone nuove","Massima distanza netta","Apertura massima porta",
        "Stanze risolte","Ricordi con nuovo effetto positivo"
    };
    public static final class Life {
        public double seconds=0,gain=0,damage=0,maxNet=0,maxGate=0;
        public int good=0,harm=0,disks=0,cells=0,rooms=0,recall=0;
        public boolean partial=false;
        public Life copy(){
            Life r=new Life();
            r.seconds=seconds;r.gain=gain;r.damage=damage;
            r.maxNet=maxNet;r.maxGate=maxGate;
            r.good=good;r.harm=harm;r.disks=disks;r.cells=cells;
            r.rooms=rooms;r.recall=recall;r.partial=partial;return r;
        }
        public double value(int metric){
            switch(metric){
                case DURATION:return seconds;
                case GOOD:return good;
                case HARM:return harm;
                case GAIN:return gain;
                case DAMAGE:return damage;
                case DISKS:return disks;
                case CELLS:return cells;
                case NET:return maxNet;
                case GATE:return maxGate;
                case ROOMS:return rooms;
                case RECALL:return recall;
                default:throw new IllegalArgumentException("metric");
            }
        }
        private String encode(){
            return seconds+","+good+","+harm+","+gain+","+damage+","+disks+","+
                   cells+","+maxNet+","+maxGate+","+rooms+","+recall+","+(partial?1:0);
        }
        private static Life decode(String s){
            String[] a=s.split(",",-1);
            if(a.length!=12)throw new IllegalArgumentException("life fields");
            Life v=new Life();
            v.seconds=Double.parseDouble(a[0]);v.good=Integer.parseInt(a[1]);
            v.harm=Integer.parseInt(a[2]);v.gain=Double.parseDouble(a[3]);
            v.damage=Double.parseDouble(a[4]);v.disks=Integer.parseInt(a[5]);
            v.cells=Integer.parseInt(a[6]);v.maxNet=Double.parseDouble(a[7]);
            v.maxGate=Double.parseDouble(a[8]);v.rooms=Integer.parseInt(a[9]);
            v.recall=Integer.parseInt(a[10]);v.partial="1".equals(a[11]);
            if((!"1".equals(a[11])&&!"0".equals(a[11]))||
                !finite(v.seconds)||!finite(v.gain)||!finite(v.damage)||
                !finite(v.maxNet)||!finite(v.maxGate)||v.seconds<0||
                v.seconds>8640000||v.gain<0||v.damage<0||v.maxNet<0||v.maxGate<0||
                v.good<0||v.harm<0||v.disks<0||v.cells<0||v.rooms<0||v.recall<0)
                    throw new IllegalArgumentException("invalid life");
            return v;
        }
    }
    private Life current=new Life();
    private final ArrayList<Life> history=new ArrayList<>();
    private int completed=0;
    private static boolean finite(double x){return !Double.isNaN(x)&&!Double.isInfinite(x);}
    public Life current(){return current.copy();}
    public int completed(){return completed;}
    public int historyCount(){return history.size();}
    public Life recent(int backwards){
        if(backwards<0||backwards>=history.size())throw new IllegalArgumentException("index");
        return history.get(history.size()-1-backwards).copy();
    }
    public void markPartial(){current.partial=true;}
    public boolean partial(){return current.partial;}
    public void tick(float simulatedSeconds){
        if(Float.isFinite(simulatedSeconds)&&simulatedSeconds>0)
            current.seconds=Math.min(8640000,current.seconds+simulatedSeconds);
    }
    /** The clamp must happen in GameActivity first. Physiological hunger
     * is deliberately excluded from contact damage. */
    public void contact(double actualEnergyDelta){
        if(!finite(actualEnergyDelta))return;
        if(actualEnergyDelta>0){
            current.good++;current.gain+=actualEnergyDelta;
        }else if(actualEnergyDelta<0){
            current.harm++;current.damage+=-actualEnergyDelta;
        }
    }
    public void diskMoved(){current.disks++;}
    public void roomSolved(){current.rooms++;}
    public void confirmedRecall(){current.recall++;}
    public void physical(double maxNet,int newCells,double maxGate){
        if(finite(maxNet)&&maxNet>=0)current.maxNet=Math.max(current.maxNet,maxNet);
        if(newCells>=0)current.cells=Math.max(current.cells,newCells);
        if(finite(maxGate)&&maxGate>=0)current.maxGate=Math.max(current.maxGate,maxGate);
    }
    /** Only a genuine death completes a life. Loading/switching activities
     * never creates a false death. A life already in progress before migration
     * is deliberately excluded from averages because its events are partial. */
    public void death(){
        if(!current.partial&&current.seconds>0){
            history.add(current.copy());
            if(history.size()>HISTORY)history.remove(0);
            if(completed<Integer.MAX_VALUE)completed++;
        }
        current=new Life();
    }
    public int sampleCount(int last){return Math.min(Math.max(0,last),history.size());}
    public double average(int metric,int last){
        int n=sampleCount(last);
        if(n==0)return Double.NaN;
        double sum=0;
        for(int i=0;i<n;i++)sum+=history.get(history.size()-1-i).value(metric);
        return sum/n;
    }
    public String encode(){
        StringBuilder b=new StringBuilder("ls1|").append(completed).append('|')
            .append(current.encode()).append('|').append(history.size());
        for(Life v:history)b.append('|').append(v.encode());
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>20000||!raw.startsWith("ls1|"))return false;
        try{
            String[] parts=raw.split("\\|",-1);
            if(parts.length<4)return false;
            int count=Integer.parseInt(parts[1]),n=Integer.parseInt(parts[3]);
            if(n<0||n>HISTORY||count<n||parts.length!=4+n)return false;
            Life working=Life.decode(parts[2]);
            ArrayList<Life> valid=new ArrayList<>();
            for(int i=0;i<n;i++){
                Life item=Life.decode(parts[i+4]);
                if(item.partial) return false;
                valid.add(item);
            }
            current=working;history.clear();history.addAll(valid);completed=count;
            return true;
        }catch(RuntimeException ignored){return false;}
    }
    public static String duration(double seconds){
        int total=(int)Math.max(0,Math.floor(seconds));
        return String.format(Locale.ITALY,"%d:%02d",total/60,total%60);
    }
    public static void main(String[] args){
        LifeStatistics stats=new LifeStatistics();
        for(int i=0;i<100;i++)stats.tick(.035f);
        if(Math.abs(stats.current.seconds-3.5)>.002)
            throw new AssertionError("Simulation time incorrect");
        // Same rendered frame at 1x/5x/20x has 1/5/20 physics slices.
        LifeStatistics x1=new LifeStatistics(),x5=new LifeStatistics(),x20=new LifeStatistics();
        for(int k=0;k<100;k++){x1.tick(.02f);
            for(int j=0;j<5;j++)x5.tick(.02f);
            for(int j=0;j<20;j++)x20.tick(.02f);}
        if(Math.abs(x5.current.seconds/x1.current.seconds-5)>.0001||
           Math.abs(x20.current.seconds/x1.current.seconds-20)>.0001)
            throw new AssertionError("x1/x5/x20 must scale simulated time");
        stats.contact(30);stats.contact(-7);stats.contact(0);
        stats.diskMoved();stats.physical(241,13,90);stats.roomSolved();stats.confirmedRecall();
        stats.death();
        if(stats.completed()!=1||stats.historyCount()!=1||
           stats.recent(0).good!=1||stats.recent(0).harm!=1||
           stats.recent(0).damage!=7||stats.recent(0).disks!=1||
           stats.recent(0).maxNet!=241||stats.recent(0).maxGate!=90)
            throw new AssertionError("Life metrics not recorded correctly");
        for(int i=0;i<58;i++){stats.tick(10+i);stats.death();}
        if(stats.historyCount()!=50||stats.completed()!=59||stats.recent(0).seconds<60)
            throw new AssertionError("Rolling 50-life history broken");
        if(Math.abs(stats.average(DURATION,10)-62.5)>.001)
            throw new AssertionError("Previous-ten averages wrong");
        LifeStatistics copy=new LifeStatistics();
        if(!copy.restore(stats.encode())||copy.completed()!=59||
           copy.historyCount()!=50||copy.recent(0).seconds!=stats.recent(0).seconds)
            throw new AssertionError("Stored life history failed to restore");
        copy.tick(22);copy.markPartial();copy.death();
        if(copy.completed()!=59||copy.historyCount()!=50)
            throw new AssertionError("Incomplete migrated life polluted averages");
        LifeStatistics fresh=new LifeStatistics();
        if(fresh.restore("ls1|100|no data|3")||fresh.completed()!=0)
            throw new AssertionError("Corrupt stats accepted");
        System.out.println("ZERO life statistics: simulated clock x1/x5/x20, real outcomes, history 50, rolling averages, migration and save passed");
    }
}
