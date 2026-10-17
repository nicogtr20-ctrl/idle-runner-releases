package com.overmind.zero;

import java.util.Locale;

/**
 * V3.40 ONLY a read-only microscope on old V3.39 BOX lives.
 * No writes to skin, box, controller, motors, learning or reward.
 * Never changes the live game or scientific conditions.
 */
final class Zero3BilateralFaceAuditV340 {
    static final int ARMS=3, LIVES=6, FACES=4;
    static final int[] BOX_ORDINALS={961,965,969,973,977,981};
    static final String[] LABELS={"OLD_GLOBAL","FULL_LOCAL_DELAY","FROZEN"};
    static final class Bucket {
        long frames,actualBilateral,anyGrip,contactedTwoOppFaces,
            commandedTwoOppFaces,heldTwoOppFaces,geometricallySeparatedOppHolds,
            commandBothButNoHold,coverBothButNoOppCommand,holdBothNotSeparated;
        final long[] geomFace=new long[4],commandFace=new long[4],holdFace=new long[4];
        final double[] muscleAbs=new double[4];
        long firstTouchPair=-1,firstCommandPair=-1,firstHeldPair=-1,
             firstActualBilateral=-1;
        void add(Bucket x){
            frames+=x.frames;actualBilateral+=x.actualBilateral;
            anyGrip+=x.anyGrip;contactedTwoOppFaces+=x.contactedTwoOppFaces;
            commandedTwoOppFaces+=x.commandedTwoOppFaces;
            heldTwoOppFaces+=x.heldTwoOppFaces;
            geometricallySeparatedOppHolds+=x.geometricallySeparatedOppHolds;
            commandBothButNoHold+=x.commandBothButNoHold;
            coverBothButNoOppCommand+=x.coverBothButNoOppCommand;
            holdBothNotSeparated+=x.holdBothNotSeparated;
            for(int f=0;f<4;f++){
                geomFace[f]+=x.geomFace[f];
                commandFace[f]+=x.commandFace[f];
                holdFace[f]+=x.holdFace[f];
                muscleAbs[f]+=x.muscleAbs[f];
            }
        }
        String summary(){
            return String.format(Locale.US,
                "physicalFrames=%d actualBilateral=%d genericGrip=%d "+
                "bothOppGeometric=%d bothOppCommanded=%d "+
                "bothOppOwned=%d bothOwnedSeparated=%d "+
                "bothCommandNoOwned=%d bothCoverButNoOppCommand=%d "+
                "ownedOppButClose=%d "+
                "contactSides=%d,%d,%d,%d "+
                "commandSides=%d,%d,%d,%d "+
                "heldSides=%d,%d,%d,%d "+
                "abs64ByChannel=%.6f,%.6f,%.6f,%.6f "+
                "firstOppGeometric=%d firstOppCommand=%d "+
                "firstOppHeld=%d firstBilateral=%d",
                frames,actualBilateral,anyGrip,contactedTwoOppFaces,
                commandedTwoOppFaces,heldTwoOppFaces,
                geometricallySeparatedOppHolds,commandBothButNoHold,
                coverBothButNoOppCommand,holdBothNotSeparated,
                geomFace[0],geomFace[1],geomFace[2],geomFace[3],
                commandFace[0],commandFace[1],commandFace[2],commandFace[3],
                holdFace[0],holdFace[1],holdFace[2],holdFace[3],
                muscleAbs[0],muscleAbs[1],muscleAbs[2],muscleAbs[3],
                firstTouchPair,firstCommandPair,firstHeldPair,firstActualBilateral);
        }
    }
    private static final Bucket[][] life=new Bucket[ARMS][LIVES];
    private Zero3BilateralFaceAuditV340(){}
    static int lifeIndex(int ordinal){
        for(int i=0;i<BOX_ORDINALS.length;i++)
            if(BOX_ORDINALS[i]==ordinal)return i;
        return -1;
    }
    private static int face(float dx,float dy){
        float[] d={Math.abs(dx+SquareGripPuzzle.HALF),
                   Math.abs(dx-SquareGripPuzzle.HALF),
                   Math.abs(dy+SquareGripPuzzle.HALF),
                   Math.abs(dy-SquareGripPuzzle.HALF)};
        int f=0;
        for(int i=1;i<4;i++)if(d[i]<d[f])f=i;
        return f;
    }
    private static boolean opposite(boolean[] s){
        return (s[0]&&s[1])||(s[2]&&s[3]);
    }
    private static boolean distinct(int a,int b){
        if(a<0||b<0)return false;
        int d=Math.abs(a-b);
        return Math.min(d,Morphology.POINTS-d)>=Morphology.POINTS/10;
    }
    static void record(int arm,int ordinal,int decision,int substep,
                       Morphology.Surface skin,UnknownWorld.Entity box,float[] executed){
        if(arm<0||arm>=ARMS||box==null||!box.isBilateralBox()||
           executed==null||executed.length!=64)return;
        int li=lifeIndex(ordinal);
        if(li<0)return;
        if(life[arm][li]==null)life[arm][li]=new Bucket();
        Bucket b=life[arm][li];
        long tick=(long)decision*12+substep;
        if(tick!=b.frames)throw new AssertionError("V340 frame ordering");
        b.frames++;
        boolean[] geom=new boolean[4],cmd=new boolean[4],owned=new boolean[4];
        int[] ownedSite={-1,-1,-1,-1};
        int anyHold=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float dx=skin.x[i]-box.x,dy=skin.y[i]-box.y;
            boolean xFace=Math.abs(Math.abs(dx)-SquareGripPuzzle.HALF)<=4f&&
                          Math.abs(dy)<=SquareGripPuzzle.HALF+4f;
            boolean yFace=Math.abs(Math.abs(dy)-SquareGripPuzzle.HALF)<=4f&&
                          Math.abs(dx)<=SquareGripPuzzle.HALF+4f;
            if(xFace||yFace){
                int f=face(dx,dy);
                geom[f]=true;
                if(skin.gripCommand[i]>Morphology.GRIP_THRESHOLD)cmd[f]=true;
                if(box.boxGripHold[i]>=Morphology.GRIP_HOLD&&
                   skin.gripCommand[i]>Morphology.GRIP_THRESHOLD){
                    owned[f]=true;ownedSite[f]=i;
                }
            }
            if(box.boxGripHold[i]>=Morphology.GRIP_HOLD)anyHold++;
        }
        for(int f=0;f<FACES;f++){
            if(geom[f])b.geomFace[f]++;
            if(cmd[f])b.commandFace[f]++;
            if(owned[f])b.holdFace[f]++;
        }
        for(int k=0;k<executed.length;k++)b.muscleAbs[k%4]+=Math.abs(executed[k]);
        boolean twoGeom=opposite(geom),twoCmd=opposite(cmd),twoOwned=opposite(owned);
        boolean separated=distinct(ownedSite[0],ownedSite[1])||
                          distinct(ownedSite[2],ownedSite[3]);
        if(twoGeom){b.contactedTwoOppFaces++;if(b.firstTouchPair<0)b.firstTouchPair=tick;}
        if(twoCmd){b.commandedTwoOppFaces++;if(b.firstCommandPair<0)b.firstCommandPair=tick;}
        if(twoOwned){b.heldTwoOppFaces++;if(b.firstHeldPair<0)b.firstHeldPair=tick;}
        if(separated)b.geometricallySeparatedOppHolds++;
        if(twoCmd&&!twoOwned)b.commandBothButNoHold++;
        if(twoGeom&&!twoCmd)b.coverBothButNoOppCommand++;
        if(twoOwned&&!separated)b.holdBothNotSeparated++;
        if(anyHold>0)b.anyGrip++;
        if(box.bilateralHeldNow){
            b.actualBilateral++;
            if(b.firstActualBilateral<0)b.firstActualBilateral=tick;
        }
    }
    static void report(){
        Bucket[] totals=new Bucket[ARMS];
        for(int arm=0;arm<ARMS;arm++){
            totals[arm]=new Bucket();
            for(int i=0;i<LIVES;i++){
                Bucket b=life[arm][i];
                if(b==null)throw new AssertionError(
                     "V340 missing closed loop case arm="+arm+
                     " ordinal="+BOX_ORDINALS[i]);
                totals[arm].add(b);
                System.out.println("V340 BOX_LIFE arm="+LABELS[arm]+
                    " ordinal="+BOX_ORDINALS[i]+" "+b.summary());
            }
            System.out.println("V340 BOX_TOTAL arm="+LABELS[arm]+
                " "+totals[arm].summary());
        }
        if(totals[0].actualBilateral!=1546||
           totals[1].actualBilateral!=0||
           totals[2].actualBilateral!=1384)
            throw new AssertionError("V340 V339 matched evidence "+
                totals[0].actualBilateral+","+
                totals[1].actualBilateral+","+
                totals[2].actualBilateral);
        if(totals[0].frames!=6L*96*12||
           totals[1].frames!=6L*96*12||
           totals[2].frames!=6L*96*12)
            throw new AssertionError("V340 missing real world frames");
        System.out.println("V340 ORIGINAL_REPLICATION 1546,0,1384 true BOX "+
          "bilateral match=true consumedScenesDiagnosticOnly=true "+
          "NO_G3_PROMOTION G5_BLOCKED NO_OTA");
    }
}
