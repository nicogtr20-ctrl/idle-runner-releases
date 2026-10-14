package com.overmind.zero;

import java.util.ArrayList;
import java.util.List;

/** Geometry-only spawn policy. Never delivers targets, costs or object
 * identities to ZERO's brain. A concealed source belongs to its cover and
 * is not an independently spawned visible object. */
public final class WorldPlacement {
    private static final float GAP=9f;
    private WorldPlacement(){}

    static boolean free(UnknownWorld.Entity item,float originX,float originY,
                        ScarcityLab room,List<UnknownWorld.Entity> active,
                        List<UnknownWorld.Entity> proposed){
        if(item==null||item.concealed||item.consumed)return false;
        if(Math.hypot(item.x-originX,item.y-originY)<item.radius+68f)
            return false;
        if(room!=null&&room.overlapsRoom(item.x,item.y,item.radius+GAP))
            return false;
        for(UnknownWorld.Entity e:active)
            if(!e.concealed&&!e.consumed&&e!=roomFood(room)&&
               Math.hypot(item.x-e.x,item.y-e.y)<
               item.radius+e.radius+GAP)return false;
        for(UnknownWorld.Entity e:proposed)
            if(!e.concealed&&!e.consumed&&
               Math.hypot(item.x-e.x,item.y-e.y)<
               item.radius+e.radius+GAP)return false;
        return true;
    }
    private static UnknownWorld.Entity roomFood(ScarcityLab room){
        return room==null?null:room.food;
    }
    public static ArrayList<UnknownWorld.Entity> spawn(
        int episode,float originX,float originY,boolean scarce,
        ScarcityLab room,List<UnknownWorld.Entity> existing){
        ArrayList<UnknownWorld.Entity> proposed=UnknownWorld.newHabitat(
            episode,originX,originY,scarce);
        ArrayList<UnknownWorld.Entity> safe=new ArrayList<>();
        for(int i=0;i<proposed.size();i++){
            UnknownWorld.Entity e=proposed.get(i);
            if(e.concealed)continue;
            if(e.isConstruction()){
                // A new panel must not crowd the life-saving old bilateral
                // energy boxes. Keep existing PLACED panels intact; throttle
                // only brand-new procedural sheet spawns.
                int nearby=0;
                for(UnknownWorld.Entity q:existing)
                    if(q.isConstruction()&&!q.consumed)nearby++;
                for(UnknownWorld.Entity q:safe)
                    if(q.isConstruction()&&!q.consumed)nearby++;
                if(episode%3!=0||nearby>=3)continue;
                boolean nearEnergyBox=false;
                for(UnknownWorld.Entity q:existing)
                    if(q.isBilateralBox()&&!q.consumed&&
                       Math.hypot(e.x-q.x,e.y-q.y)<e.radius+q.radius+70f)
                        nearEnergyBox=true;
                for(UnknownWorld.Entity q:safe)
                    if(q.isBilateralBox()&&!q.consumed&&
                       Math.hypot(e.x-q.x,e.y-q.y)<e.radius+q.radius+70f)
                        nearEnergyBox=true;
                if(nearEnergyBox)continue;
                // Scarce free food must have a navigable contact area.
                // This is world generation only; ZERO sees no food label.
                boolean nearFreeEnergy=false;
                for(UnknownWorld.Entity q:existing)
                    if(!q.consumed&&!q.concealed&&q.physicalLaw()==0&&
                       Math.hypot(e.x-q.x,e.y-q.y)<e.radius+q.radius+65f)
                        nearFreeEnergy=true;
                for(UnknownWorld.Entity q:safe)
                    if(!q.consumed&&!q.concealed&&q.physicalLaw()==0&&
                       Math.hypot(e.x-q.x,e.y-q.y)<e.radius+q.radius+65f)
                        nearFreeEnergy=true;
                if(nearFreeEnergy)continue;
            }
            boolean valid=free(e,originX,originY,room,existing,safe);
            if(!valid)continue;
            safe.add(e);
            // A hidden source is an inseparable spawn pair with its cover.
            if(e.isBox()&&i+1<proposed.size()){
                UnknownWorld.Entity underneath=proposed.get(i+1);
                if(underneath.concealed&&underneath.physicalLaw()==4&&
                   underneath.spawnX==e.spawnX&&underneath.spawnY==e.spawnY)
                    safe.add(underneath);
            }
        }
        return safe;
    }
    private static void require(boolean condition,String error){
        if(!condition)throw new AssertionError(error);
    }
    public static void main(String[] args){
        ArrayList<UnknownWorld.Entity> existing=new ArrayList<>();
        ScarcityLab room=new ScarcityLab(0,0);
        // The gate extends LEFT of the doorway and must be included in
        // the spawn exclusion geometry even when no visual wall is there.
        require(room.overlapsRoom(room.gateX(),0,16f),
            "Room exclusion forgets physical gate");
        ArrayList<UnknownWorld.Entity> first=spawn(1,0,0,false,null,existing);
        for(int i=0;i<first.size();i++){
            UnknownWorld.Entity e=first.get(i);
            if(e.concealed)continue;
            for(int j=0;j<i;j++){
                UnknownWorld.Entity z=first.get(j);
                if(z.concealed)continue;
                require(Math.hypot(e.x-z.x,e.y-z.y)>=
                    e.radius+z.radius+GAP,"Visible objects spawned overlapped");
            }
        }
        ArrayList<UnknownWorld.Entity> next=spawn(7,room.gateX(),0,true,room,first);
        for(UnknownWorld.Entity e:next){
            if(e.concealed)continue;
            require(!room.overlapsRoom(e.x,e.y,e.radius+GAP),
                "Item spawned inside gate or chamber");
            for(UnknownWorld.Entity z:first){
                if(z.concealed||z.consumed)continue;
                require(Math.hypot(e.x-z.x,e.y-z.y)>=e.radius+z.radius+GAP,
                    "Expansion spawned over a live older object");
            }
        }
        // Every actual energy cover comes with exactly one existing finite
        // +40 rare source, both in raw generation and after legal placement.
        // Blank metal panels are intentionally NOT energy covers.
        for(int episode=1;episode<=48;episode++){
            ArrayList<UnknownWorld.Entity> raw=UnknownWorld.newHabitat(
                episode,episode*800f,0,false);
            ArrayList<UnknownWorld.Entity> placed=spawn(
                episode,episode*800f,0,false,null,
                new ArrayList<UnknownWorld.Entity>());
            for(ArrayList<UnknownWorld.Entity> sample:
                java.util.Arrays.asList(raw,placed)){
                int covers=0,hidden=0;
                for(UnknownWorld.Entity box:sample){
                    if(!box.isBilateralBox())continue;
                    covers++;
                    int matching=0;
                    for(UnknownWorld.Entity energy:sample)
                        if(energy.physicalLaw()==4&&energy.concealed&&
                           !energy.consumed&&
                           energy.spawnX==box.spawnX&&
                           energy.spawnY==box.spawnY)matching++;
                    require(matching==1,
                        "Energy box missing unique purple gem at "+episode);
                }
                for(UnknownWorld.Entity energy:sample){
                    if(energy.physicalLaw()!=4)continue;
                    hidden++;
                    int matching=0;
                    for(UnknownWorld.Entity box:sample)
                        if(box.isBilateralBox()&&
                           box.spawnX==energy.spawnX&&
                           box.spawnY==energy.spawnY)matching++;
                    require(matching==1,
                        "Orphan purple gem at episode "+episode);
                }
                require(covers==hidden,"Mismatch between covers and purple gems");
            }
        }
        System.out.println("WorldPlacement PASS: every energy box has exactly one purple gem; no overlapping resources");
    }
}