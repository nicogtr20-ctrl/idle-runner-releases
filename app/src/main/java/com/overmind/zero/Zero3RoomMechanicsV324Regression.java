package com.overmind.zero;

import java.io.*;
import java.util.*;

/** Isolated real ScarcityLab gate mechanics checkpoint contract test.
 * The two controlled patch commands are a BENCH FIXTURE, NOT ZERO's learned
 * behavior; use this only to prove gate/food restoration, never G3 skill.
 */
public final class Zero3RoomMechanicsV324Regression {
    private static void check(boolean b,String m){if(!b)throw new AssertionError("V324 gate "+m);}
    private static Morphology.Surface copy(Morphology.Surface from){
        Morphology.Surface s=new Morphology.Surface(new MotorBrain.Body());
        System.arraycopy(from.x,0,s.x,0,96);
        System.arraycopy(from.y,0,s.y,0,96);
        System.arraycopy(from.vx,0,s.vx,0,96);
        System.arraycopy(from.vy,0,s.vy,0,96);
        System.arraycopy(from.gripCommand,0,s.gripCommand,0,96);
        System.arraycopy(from.gripHold,0,s.gripHold,0,96);
        System.arraycopy(from.touchSense,0,s.touchSense,0,96);
        return s;
    }
    public static void main(String[] args)throws Exception{
        ScarcityLab room=new ScarcityLab(-81f,0f);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        world.add(room.food);
        Morphology.Surface skin=new Morphology.Surface(new MotorBrain.Body());
        int left=12,right=60;
        skin.x[left]=room.gateX()-ScarcityLab.DOOR_HALF_W-1f;
        skin.x[right]=room.gateX()+ScarcityLab.DOOR_HALF_W+1f;
        skin.y[left]=skin.y[right]=room.gateCenterY();
        skin.vy[left]=skin.vy[right]=20f;
        skin.gripCommand[left]=skin.gripCommand[right]=.95f;
        for(int k=0;k<10;k++)room.solveGate(skin,.025f);
        check(room.grasps>0&&room.gateContactFrames>0&&room.gateHeldFrames>0,
              "real gate fixture did not exercise bilateral contact memory");
        Zero3RoomStateV324 saved=Zero3RoomStateV324.capture(room,world);
        Zero3RoomStateV324.Restored loaded=saved.restore(world);
        check(loaded.objects.get(loaded.foodIndex)==loaded.room.food,
              "room food alias was duplicated by restore");
        check(room.grasps==loaded.room.grasps&&
              Float.floatToRawIntBits(room.doorY)==
                  Float.floatToRawIntBits(loaded.room.doorY),
              "counter or moving gate lost");
        Morphology.Surface restoredSkin=copy(skin);
        for(int k=0;k<8;k++){
            room.solveGate(skin,.025f);
            loaded.room.solveGate(restoredSkin,.025f);
        }
        Zero3RoomStateV324 a=Zero3RoomStateV324.capture(room,world);
        Zero3RoomStateV324 b=Zero3RoomStateV324.capture(
            loaded.room,loaded.objects);
        check(Arrays.equals(a.encoded(),b.encoded()),
              "real gate resumed behavior diverged from uninterrupted");
        check(!saved.fingerprint().isEmpty(),"missing state fingerprint");
        boolean rejected=false;
        try{
            Zero3RoomStateV324.capture(room,
                new ArrayList<UnknownWorld.Entity>());
        }catch(IOException correct){rejected=true;}
        check(rejected,"orphan room food accepted as an independent entity");
        byte[] damage=saved.encoded();damage[0]=(byte)0xAB;
        rejected=false;
        try{Zero3RoomStateV324.decode(damage).restore(world);}
        catch(IOException correct){rejected=true;}
        check(rejected,"bad version accepted");
        System.out.println("ZERO3 V324 REAL_GATE_PHYSICS_BILATERAL_MEMORY=PASS "+
              "graspEvents="+room.grasps+" gateDrive="+room.gateDriveFrames+
              " resumedRoomHash="+b.fingerprint()+" FOOD_IDENTITY=PASS");
    }
}
