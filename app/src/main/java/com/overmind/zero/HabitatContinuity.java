package com.overmind.zero;

import java.util.ArrayList;

/**
 * 0116.6 - world management only, NEVER a neural preference or reward.
 * Preserve physically contacted objects and keep a concealed +40 source
 * paired with its cover while environments stream around a living ZERO.
 */
public final class HabitatContinuity {
    private HabitatContinuity(){}
    private static final float PROTECT_NEAR=125f;
    private static final int WORLD_TARGET=24;

    private static boolean samePair(UnknownWorld.Entity a,UnknownWorld.Entity b){
        return a!=b&&a.spawnX==b.spawnX&&a.spawnY==b.spawnY &&
            ((a.isBox()&&b.physicalLaw()==4)||
             (b.isBox()&&a.physicalLaw()==4));
    }
    private static UnknownWorld.Entity mate(ArrayList<UnknownWorld.Entity> world,
                                              UnknownWorld.Entity source){
        if(world==null||source==null)return null;
        for(UnknownWorld.Entity other:world)
            if(samePair(source,other))return other;
        return null;
    }
    /** Interaction checks use actual physical material coordinates. */
    public static boolean engaged(UnknownWorld.Entity e,MotorBrain.Body body,
                                  Morphology.Surface skin){
        if(e==null||body==null||e.consumed)return false;
        if(Math.hypot(e.x-body.cx,e.y-body.cy)<e.radius+PROTECT_NEAR)
            return true;
        if(skin!=null)for(int i=0;i<Morphology.POINTS;i++){
            boolean touching=e.isBox()
                ?Math.abs(skin.x[i]-e.x)<=SquareGripPuzzle.HALF+8f &&
                 Math.abs(skin.y[i]-e.y)<=SquareGripPuzzle.HALF+8f
                :Math.hypot(skin.x[i]-e.x,skin.y[i]-e.y)<e.radius+8f;
            if(touching)return true;
        }
        return false;
    }
    private static boolean protectedByInteraction(ArrayList<UnknownWorld.Entity> world,
                          UnknownWorld.Entity e,MotorBrain.Body body,Morphology.Surface skin){
        if(engaged(e,body,skin))return true;
        // Real placed building materials remain present for manipulation near
        // ZERO; more distant ones are archived and streamed back on return.
        if(e.isConstruction()&&e.displacementFromSpawn()>=12f&&
           Math.hypot(e.x-body.cx,e.y-body.cy)<400f)return true;
        if(e.concealed ||e.isBox()){
            UnknownWorld.Entity other=mate(world,e);
            return other!=null&&engaged(other,body,skin);
        }
        return false;
    }
    private static void removePaired(ArrayList<UnknownWorld.Entity> world,
                                     UnknownWorld.Entity selected){
        // Never abandon a concealed source without its real cover; a revealed
        // resource is independent, so collecting it won't delete the block.
        UnknownWorld.Entity other=mate(world,selected);
        if(other!=null&&(selected.concealed||other.concealed))
            world.remove(other);
        world.remove(selected);
    }

    public static void pruneDistant(ArrayList<UnknownWorld.Entity> world,
                                    MotorBrain.Body body,Morphology.Surface skin,
                                    UnknownWorld.Entity roomFood,float maxDist){
        for(int i=world.size()-1;i>=0;){
            if(i>=world.size()){i=world.size()-1;continue;}
            UnknownWorld.Entity e=world.get(i);
            if(e==roomFood){i--;continue;}
            // Genuine consumed rewards no longer exist in the world.
            if(e.consumed){
                world.remove(i);
            }else{
                UnknownWorld.Entity companion=mate(world,e);
                boolean companionNear=companion!=null&&
                    Math.hypot(companion.x-body.cx,companion.y-body.cy)<=maxDist;
                if(Math.hypot(e.x-body.cx,e.y-body.cy)>maxDist&&
                   !companionNear&&!protectedByInteraction(world,e,body,skin))
                    removePaired(world,e);
            }
            i=Math.min(i-1,world.size()-1);
        }
    }

    /** Soft cap: do NOT delete a real object being manipulated by ZERO.
     * Unprotected oldest objects are recycled first, as before. */
    public static void enforceLimit(ArrayList<UnknownWorld.Entity> world,
                                    MotorBrain.Body body,Morphology.Surface skin,
                                    UnknownWorld.Entity roomFood){
        while(world.size()>WORLD_TARGET){
            UnknownWorld.Entity eligible=null;
            for(UnknownWorld.Entity e:world){
                if(e==roomFood)continue;
                if(!protectedByInteraction(world,e,body,skin)){
                    eligible=e;break;
                }
            }
            if(eligible==null)break;
            removePaired(world,eligible);
        }
    }

    /** Prevent a newly activated room or routine map cleanup from deleting
     * an object while the real membrane is still touching it. */
    public static void excludeRoomOverlap(ArrayList<UnknownWorld.Entity> world,
                            ScarcityLab room,MotorBrain.Body body,
                            Morphology.Surface skin){
        if(room==null)return;
        for(int i=world.size()-1;i>=0;){
            if(i>=world.size()){i=world.size()-1;continue;}
            UnknownWorld.Entity e=world.get(i);
            if(e==room.food){i--;continue;}
            if(room.overlapsRoom(e.x,e.y,e.radius)&&
               !protectedByInteraction(world,e,body,skin))
                removePaired(world,e);
            i=Math.min(i-1,world.size()-1);
        }
    }

    /** Try nearby physically vacant locations instead of manifesting a room
     * OVER an existing solid that ZERO is currently learning to handle. */
    public static ScarcityLab clearChamber(float x,float y,
                                            ArrayList<UnknownWorld.Entity> world){
        final float[] offsets={0,245,-245,490,-490,735,-735};
        for(float shift:offsets){
            ScarcityLab candidate=new ScarcityLab(x,y+shift);
            boolean clear=true;
            for(UnknownWorld.Entity e:world){
                if(e.consumed||e.concealed)continue;
                if(candidate.overlapsRoom(e.x,e.y,e.radius+9f)){
                    clear=false;break;
                }
            }
            if(clear)return candidate;
        }
        return null;
    }
    private static void yes(boolean ok,String message){
        if(!ok)throw new AssertionError(message);
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        UnknownWorld.Entity cover=new UnknownWorld.Entity(70,0,2,5);
        UnknownWorld.Entity hidden=new UnknownWorld.Entity(70,0,0,4);
        hidden.concealed=true;
        world.add(cover);world.add(hidden);
        // The existing room would overlap the solid cover and delete it.
        ScarcityLab newRoom=clearChamber(0,0,world);
        yes(newRoom!=null&&!newRoom.overlapsRoom(cover.x,cover.y,cover.radius),
            "A new room still materializes over ZERO's touched square");
        for(int i=0;i<42;i++)
            world.add(new UnknownWorld.Entity(600+i*35f,200,2,2));
        enforceLimit(world,body,skin,null);
        yes(world.contains(cover)&&world.contains(hidden),
            "Oldest actively held square or its reward was culled");
        yes(world.size()<=24,"World cap not enforced while distant objects exist");
        // Not all positions are protected: once ZERO moves away, garbage
        // collection is allowed, but must remove a still-concealed source
        // together with its cover, never leave orphan hidden energy.
        body.cx=3000;body.cy=3000;
        enforceLimit(world,body,skin,null);
        yes(world.contains(cover)==world.contains(hidden),
            "Covered source and cover were orphaned by recycling");
        // Long limbs still protect the physical contact when the core is far.
        body.cx=-300;body.cy=0;
        skin.x[3]=cover.x-SquareGripPuzzle.HALF;
        skin.y[3]=cover.y;
        yes(engaged(cover,body,skin),
            "A real physically held box was mistaken for abandoned");
        System.out.println("HabitatContinuity PASS: active boxes persist, "+
            "covered rewards remain paired, chambers spawn clear, remote recycle");
    }
}