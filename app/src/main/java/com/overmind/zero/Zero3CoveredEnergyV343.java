package com.overmind.zero;

import java.util.ArrayList;
import java.util.IdentityHashMap;

/**
 * V343: LAB-ONLY environment consequence, never an actor suggestion.
 *
 * A concealed finite source becomes collectable only after its true paired
 * square was genuinely held bilaterally AND moved far enough to expose it.
 * No world identity or intrinsic object-reward is injected into V315/V317.
 * This exposes physical food, not an instrumental goal or a learned value.
 */
final class Zero3CoveredEnergyV343 {
    private static final class CoverRecord {
        final UnknownWorld.Entity cover;
        boolean priorGenuineBilateral,exposed;
        CoverRecord(UnknownWorld.Entity cover){this.cover=cover;}
    }
    private final IdentityHashMap<UnknownWorld.Entity,CoverRecord> records=
        new IdentityHashMap<>();
    private int exposures,heldEvents;
    private Zero3CoveredEnergyV343(){}
    static Zero3CoveredEnergyV343 laboratory(ArrayList<UnknownWorld.Entity> world){
        if(world==null)throw new IllegalArgumentException("no actual world");
        Zero3CoveredEnergyV343 observer=new Zero3CoveredEnergyV343();
        for(UnknownWorld.Entity item:world){
            if(!item.concealed||item.physicalLaw()!=4||item.consumed)continue;
            UnknownWorld.Entity cover=null;
            for(UnknownWorld.Entity other:world)
                if(other!=item&&other.isBilateralBox()&&
                   other.spawnX==item.spawnX&&other.spawnY==item.spawnY&&
                   !other.consumed){
                    if(cover!=null)throw new IllegalArgumentException(
                        "ambiguous concealed energy cover");
                    cover=other;
                }
            if(cover==null)throw new IllegalArgumentException(
                "concealed energy must pair real BOX by spawn");
            observer.records.put(item,new CoverRecord(cover));
        }
        return observer;
    }
    /** Must be called AFTER all physical box constraints and tick, never BEFORE. */
    void afterWorld(ArrayList<UnknownWorld.Entity> world){
        if(world==null)throw new IllegalArgumentException("missing world");
        for(UnknownWorld.Entity source:records.keySet()){
            CoverRecord state=records.get(source);
            if(state.exposed||source.consumed)continue;
            if(!world.contains(source)||!world.contains(state.cover)||
               state.cover.consumed)throw new IllegalStateException(
                   "paired energy/cover disappeared from world");
            if(!source.concealed)throw new IllegalStateException(
                  "concealed energy exposed outside real BOX proof");
            if(state.cover.bilateralHeldNow){
                state.priorGenuineBilateral=true;
                heldEvents++;
            }
            if(state.priorGenuineBilateral&&
               state.cover.displacementFromSpawn()>=
                   SquareGripPuzzle.UNCOVER_DISTANCE){
                // Exactly one transition. Does not change any learned intent,
                // physical velocity, owner, motor effort, fuel or food value.
                source.concealed=false;
                state.exposed=true;
                exposures++;
            }
        }
    }
    int exposedSources(){return exposures;}
    int validatedHeldFrames(){return heldEvents;}
    int coveredSources(){
        int n=0;
        for(CoverRecord v:records.values())if(!v.exposed)n++;
        return n;
    }
    boolean hasTracked(UnknownWorld.Entity source){
        return records.containsKey(source);
    }
}
