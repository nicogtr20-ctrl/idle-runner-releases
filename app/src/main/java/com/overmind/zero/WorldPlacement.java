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
        System.out.println("WorldPlacement PASS: no overlapping resources, boxes, gate or hidden-source orphan");
    }
}