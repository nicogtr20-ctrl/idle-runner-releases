package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO 3.38, experimental LAB ONLY.
 *
 * A transactional, deterministic joint inequality-constrained contact
 * projection. It considers the full simple polygon, every physical chord
 * and every shell particle against the SAME true BOX geometry, while
 * explicitly freezing genuinely held adhesive patches. It never sets
 * commands, timers, owners, energies, box forces or learned targets.
 *
 * This is deliberately a bounded feasibility solver, not a proof that a
 * feasible solution always exists. A failed search changes NOTHING and
 * reports UNRESOLVED; this includes invalid pre-world contours.
 *
 * Call only from an explicitly selected test mode, NEVER game/OTA dispatch.
 */
final class Zero3JointBoxConstraintV338 {
    static final int N=Morphology.POINTS;
    static final float MAX_MOVEMENT=9f;
    static final int MAX_ITER=18;
    static final float[] FRACTIONS={.25f,.5f,.75f,1f};

    static final class Result {
        final boolean attempted,repaired,unresolved;
        final int initialCross,initialWalls,initialEmbedded;
        final int finalCross,finalWalls,finalEmbedded;
        final int movedPoints;
        Result(boolean a,boolean ok,int c,int w,int e,
               int fc,int fw,int fe,int m){
            attempted=a;repaired=a&&ok;unresolved=a&&!ok;
            initialCross=c;initialWalls=w;initialEmbedded=e;
            finalCross=fc;finalWalls=fw;finalEmbedded=fe;
            movedPoints=m;
        }
        String line(){
            return "attempted="+attempted+" repaired="+repaired+
                " unresolved="+unresolved+" first="+
                initialCross+","+initialWalls+","+initialEmbedded+
                " final="+finalCross+","+finalWalls+","+finalEmbedded+
                " moved="+movedPoints;
        }
    }
    private Zero3JointBoxConstraintV338(){}
    static final class Defect {
        final int cross,wall,embedded;
        final int[] indices;
        Defect(int c,int w,int e,int[] ids){
            cross=c;wall=w;embedded=e;indices=ids;
        }
        int total(){return cross+wall+embedded;}
    }
    private static boolean crossed(float ax,float ay,float bx,float by,
                                   float cx,float cy,float dx,float dy){
        float a=(bx-ax)*(cy-ay)-(by-ay)*(cx-ax);
        float b=(bx-ax)*(dy-ay)-(by-ay)*(dx-ax);
        float c=(dx-cx)*(ay-cy)-(dy-cy)*(ax-cx);
        float d=(dx-cx)*(by-cy)-(dy-cy)*(bx-cx);
        return ((a>1e-4f&&b< -1e-4f)||(a< -1e-4f&&b>1e-4f))&&
               ((c>1e-4f&&d< -1e-4f)||(c< -1e-4f&&d>1e-4f));
    }
    static Defect measure(Morphology.Surface skin,UnknownWorld.Entity box){
        int[] problem=new int[N];
        int crossing=0,embedded=0;
        float l=box.x-SquareGripPuzzle.HALF,r=box.x+SquareGripPuzzle.HALF;
        float t=box.y-SquareGripPuzzle.HALF,bb=box.y+SquareGripPuzzle.HALF;
        for(int i=0;i<N;i++){
            if(skin.x[i]>l+1e-4f&&skin.x[i]<r-1e-4f&&
               skin.y[i]>t+1e-4f&&skin.y[i]<bb-1e-4f){
                embedded++;problem[i]=1;
            }
            int ni=(i+1)%N;
            if(ScarcityLab.crossesInterior(skin.x[i],skin.y[i],
                        skin.x[ni],skin.y[ni],l,t,r,bb)){
                problem[i]=problem[ni]=1;
            }
            for(int j=i+2;j<N;j++){
                if((j+1)%N==i)continue;
                int nj=(j+1)%N;
                if(crossed(skin.x[i],skin.y[i],skin.x[ni],skin.y[ni],
                           skin.x[j],skin.y[j],skin.x[nj],skin.y[nj])){
                    crossing++;
                    problem[i]=problem[ni]=problem[j]=problem[nj]=1;
                }
            }
        }
        int count=0;
        for(int i:problem)count+=i;
        int[] ids=new int[count];
        int p=0;
        for(int i=0;i<N;i++)if(problem[i]!=0)ids[p++]=i;
        return new Defect(crossing,
            SquareCollisionSolver.intersections(skin,box),embedded,ids);
    }
    private static boolean anchored(Morphology.Surface skin,
                                     UnknownWorld.Entity box,int i){
        return box.boxGripHold[i]>=Morphology.GRIP_HOLD&&
               skin.gripCommand[i]>Morphology.GRIP_THRESHOLD;
    }
    /** At most 9 physical units from the original BOX-corrected proposal. */
    private static boolean bounded(float x,float y,float x0,float y0){
        return Float.isFinite(x)&&Float.isFinite(y)&&
               Math.hypot(x-x0,y-y0)<=MAX_MOVEMENT;
    }
    static Result solve(Morphology.Surface skin,
              UnknownWorld.Entity box,float[] gelSkinX,float[] gelSkinY){
        if(skin==null||box==null||!box.isBilateralBox()||
           gelSkinX==null||gelSkinY==null||
           gelSkinX.length!=N||gelSkinY.length!=N)
            throw new IllegalArgumentException("V338 physics input");
        Defect begin=measure(skin,box);
        if(begin.total()==0)
            return new Result(false,true,0,0,0,0,0,0,0);
        float[] x0=skin.x.clone(),y0=skin.y.clone(),
                vx0=skin.vx.clone(),vy0=skin.vy.clone();
        // Timer, owner, command and actual BOX position/velocity are never
        // written by this solver, even during speculative trials.
        Defect at=begin;
        int changed=0;
        for(int iteration=0;iteration<MAX_ITER&&at.total()>0;iteration++){
            int winner=-1;
            float nx=0,ny=0;
            int best=at.total();
            double bestTravel=Double.POSITIVE_INFINITY;
            for(int i:at.indices){
                if(anchored(skin,box,i))continue;
                float oldX=skin.x[i],oldY=skin.y[i];
                if(!Float.isFinite(gelSkinX[i])||
                   !Float.isFinite(gelSkinY[i]))continue;
                for(float f:FRACTIONS){
                    float tx=oldX+(gelSkinX[i]-oldX)*f;
                    float ty=oldY+(gelSkinY[i]-oldY)*f;
                    if(!bounded(tx,ty,x0[i],y0[i]))continue;
                    skin.x[i]=tx;skin.y[i]=ty;
                    Defect d=measure(skin,box);
                    // Minimize all 3 defects together; a change that trades
                    // one crossing for one embedded patch is NEVER accepted.
                    // Avoid tearing a truly held patch and do not move BOX.
                    if(d.total()<best){
                        best=d.total();winner=i;nx=tx;ny=ty;
                        bestTravel=(tx-x0[i])*(tx-x0[i])+
                                   (ty-y0[i])*(ty-y0[i]);
                    }else if(d.total()==best&&d.total()<at.total()){
                        double travel=(tx-x0[i])*(tx-x0[i])+
                                      (ty-y0[i])*(ty-y0[i]);
                        if(travel<bestTravel){
                            winner=i;nx=tx;ny=ty;bestTravel=travel;
                        }
                    }
                    skin.x[i]=oldX;skin.y[i]=oldY;
                }
            }
            if(winner<0)break;
            skin.x[winner]=nx;skin.y[winner]=ny;
            // Collisions should never add active power. Damping speculative
            // corrective velocity is permitted; no active gait impulse.
            skin.vx[winner]=0f;skin.vy[winner]=0f;
            changed++;
            at=measure(skin,box);
        }
        boolean resolved=at.total()==0;
        if(resolved){
            // Independent full-ring consistency; all actual owner/timer
            // snapshots remain untouched because those arrays are never
            // mutated above. No relocation beyond a local patch trust range.
            for(int i=0;i<N;i++)
                if(anchored(skin,box,i)&&
                   (Float.floatToIntBits(x0[i])!=
                    Float.floatToIntBits(skin.x[i])||
                    Float.floatToIntBits(y0[i])!=
                    Float.floatToIntBits(skin.y[i])))
                    resolved=false;
            if(measure(skin,box).total()!=0)resolved=false;
        }
        if(!resolved){
            System.arraycopy(x0,0,skin.x,0,N);
            System.arraycopy(y0,0,skin.y,0,N);
            System.arraycopy(vx0,0,skin.vx,0,N);
            System.arraycopy(vy0,0,skin.vy,0,N);
            at=begin;
            changed=0;
        }
        return new Result(true,resolved,begin.cross,begin.wall,
                          begin.embedded,at.cross,at.wall,at.embedded,changed);
    }
}
