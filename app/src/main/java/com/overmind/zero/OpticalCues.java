package com.overmind.zero;

import java.util.ArrayList;

/**
 * 0116.5: Passive optical transport, not an organism skill or reward.
 *
 * Glass is transparent for photons but remains an ordinary physical wall for
 * collision and obstacle rays. A covered +40 energy source leaks a *weak*
 * spectral signal at the NEAR visible rim of its existing physical square.
 * No new sensors, class identifiers, targets or privileged guidance.
 */
public final class OpticalCues {
    private OpticalCues(){}
    public static final float GLASS_TOP=-85f,GLASS_BOTTOM=-52f;
    public static final float BOX_RIM=SquareGripPuzzle.HALF+5f;
    public static final float LEAK_POWER=.20f;

    public static boolean frontWindow(ScarcityLab room,float x,float y){
        if(room==null)return false;
        return x>=room.entranceX()-ScarcityLab.WALL_HALF_THICKNESS &&
               x<=room.entranceX()+ScarcityLab.WALL_HALF_THICKNESS &&
               y>=room.oy+GLASS_TOP&&y<=room.oy+GLASS_BOTTOM;
    }

    /** Physical source is unchanged. Each emitted ray originates just outside
     * the NEAR square face; distant faces cannot shine through the opaque box.
     * Caller must still check ray occlusion by OTHER solids. */
    public static float rimX(float observerX,float observerY,
                             UnknownWorld.Entity square){
        float dx=observerX-square.x,dy=observerY-square.y;
        if(Math.abs(dx)>=Math.abs(dy))
            return square.x+(dx>=0?BOX_RIM:-BOX_RIM);
        return square.x;
    }
    public static float rimY(float observerX,float observerY,
                             UnknownWorld.Entity square){
        float dx=observerX-square.x,dy=observerY-square.y;
        if(Math.abs(dy)>Math.abs(dx))
            return square.y+(dy>=0?BOX_RIM:-BOX_RIM);
        return square.y;
    }
    /** Only a physically present, not-yet-uncovered +40 source emits. */
    public static UnknownWorld.Entity coverOf(UnknownWorld.Entity hidden,
                                              ArrayList<UnknownWorld.Entity> world){
        if(hidden==null||!hidden.concealed||hidden.consumed||
           hidden.physicalLaw()!=4||world==null)return null;
        for(UnknownWorld.Entity other:world){
            if(other.isBox()&&!other.consumed &&
               other.spawnX==hidden.spawnX&&other.spawnY==hidden.spawnY &&
               other.displacementFromSpawn()<SquareGripPuzzle.UNCOVER_DISTANCE)
                return other;
        }
        return null;
    }
    public static float leakBrightness(float distance){
        if(!Float.isFinite(distance)||distance>210f||distance<0)return 0;
        return LEAK_POWER*Math.max(0f,1f-distance/230f);
    }
    private static void yes(boolean condition,String message){
        if(!condition)throw new AssertionError(message);
    }
    public static void main(String[] args){
        ScarcityLab r=new ScarcityLab(0,0);
        yes(frontWindow(r,r.entranceX(),r.oy-64f),"Glass missing");
        yes(!frontWindow(r,r.entranceX(),0),"Open door is not glass");
        yes(!frontWindow(r,r.entranceX(),r.oy-105f),"Roof is not glass");
        UnknownWorld.Entity box=new UnknownWorld.Entity(100,0,2,5);
        UnknownWorld.Entity hidden=new UnknownWorld.Entity(100,0,0,4);
        hidden.concealed=true;
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        objects.add(box);objects.add(hidden);
        yes(coverOf(hidden,objects)==box,"Concealed source lost its cover");
        yes(rimX(0,0,box)<box.x-SquareGripPuzzle.HALF,
            "Light unexpectedly travels through box");
        yes(leakBrightness(80)>0&&leakBrightness(80)<.20f,
            "Hidden source should leak weak light, not full visible energy");
        hidden.concealed=false;
        yes(coverOf(hidden,objects)==null,"Exposed source should not leak twice");
        hidden.concealed=true;
        box.x+=SquareGripPuzzle.UNCOVER_DISTANCE;
        yes(coverOf(hidden,objects)==null,"Moved-away box still acts as cover");
        yes(hidden.touch(0,0)==40f&&hidden.touch(0,0)==0f,
            "Optical changes modified actual finite contact reward");
        System.out.println("OpticalCues PASS: real occluded box rim, inert glass, weak spectral photons, unchanged 40 energy");
    }
}
