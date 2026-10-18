package com.overmind.zero;

import java.util.*;

/** Experimental simultaneous topology/solid constraint, no neural commands or object forces.
 * Only commits feasible bounded membrane corrections; otherwise changes nothing.
 */
final class Zero3JointConstraintV354 {
    private static final int N=96;
    static final class Result {
        final int initial,remaining,moved;final boolean attempted,repaired;
        Result(int a,int b,int m){initial=a;remaining=b;moved=m;attempted=a>0;repaired=a>0&&b==0;}
    }
    static int defects(Morphology.Surface s,List<UnknownWorld.Entity> objects,ScarcityLab room,boolean[] bad){
        int score=MaterialGeometry.selfCrossings(s);
        if(score>0&&bad!=null)Arrays.fill(bad,true);
        for(UnknownWorld.Entity box:objects)if(box.isBilateralBox()){
            Zero3JointBoxConstraintV338.Defect d=Zero3JointBoxConstraintV338.measure(s,box);
            score+=d.wall+d.embedded;
            if(bad!=null)for(int i:d.indices)bad[i]=true;
        }
        for(UnknownWorld.Entity panel:objects)if(panel.isConstruction()){
            float h=PanelPhysics.halfLength(panel),t=PanelPhysics.HALF_THICKNESS;
            for(int i=0;i<N;i++){
                int j=(i+1)%N;float[] a=PanelPhysics.local(panel,s.x[i],s.y[i]),b=PanelPhysics.local(panel,s.x[j],s.y[j]);
                boolean embedded=Math.abs(a[0])<h-1e-4&&Math.abs(a[1])<t-1e-4;
                if(embedded||ScarcityLab.crossesInterior(a[0],a[1],b[0],b[1],-h,-t,h,t)){score++;if(bad!=null){bad[i]=true;bad[j]=true;}}
            }
        }
        if(room!=null)for(float[] r:room.collisionRectanglesV354())for(int i=0;i<N;i++){
            int j=(i+1)%N;
            boolean point=s.x[i]>r[0]+1e-4&&s.x[i]<r[2]-1e-4&&s.y[i]>r[1]+1e-4&&s.y[i]<r[3]-1e-4;
            boolean link=ScarcityLab.crossesInterior(s.x[i],s.y[i],s.x[j],s.y[j],r[0],r[1],r[2],r[3]);
            if(point||link){score++;if(bad!=null){bad[i]=true;bad[j]=true;}}
        }
        return score;
    }
    private static void nearest(float[] r,float x,float y,float[] out){
        float[] distances={Math.abs(x-r[0]),Math.abs(x-r[2]),Math.abs(y-r[1]),Math.abs(y-r[3])};
        int side=0;for(int k=1;k<4;k++)if(distances[k]<distances[side])side=k;
        out[0]=x;out[1]=y;
        if(side==0)out[0]=r[0]-.02f;else if(side==1)out[0]=r[2]+.02f;
        else if(side==2)out[1]=r[1]-.02f;else out[1]=r[3]+.02f;
    }
    private static boolean legalHeld(Morphology.Surface s,List<UnknownWorld.Entity> objects,ScarcityLab room,int i,
                                     float oldX,float oldY,float x,float y){
        if(room!=null&&room.gateGripHold[i]>=Morphology.GRIP_HOLD&&GripSurfaceComposition.gateFace(room,oldX,oldY))
            if(!GripSurfaceComposition.gateFace(room,x,y)||Math.abs(x-oldX)>.01f)return false;
        for(UnknownWorld.Entity e:objects){
            if(e.boxGripHold[i]<Morphology.GRIP_HOLD)continue;
            if(e.isBilateralBox()&&GripSurfaceComposition.boxFace(e,oldX,oldY)){
                if(!GripSurfaceComposition.boxFace(e,x,y))return false;
                float h=SquareGripPuzzle.HALF;
                float a=Math.min(Math.abs(Math.abs(oldX-e.x)-h),Math.abs(Math.abs(oldY-e.y)-h));
                float b=Math.min(Math.abs(Math.abs(x-e.x)-h),Math.abs(Math.abs(y-e.y)-h));
                if(Math.abs(a-b)>.01f)return false;
            }else if(e.isConstruction()&&PanelPhysics.touching(e,oldX,oldY)){
                if(!PanelPhysics.touching(e,x,y))return false;
                float[] a=PanelPhysics.local(e,oldX,oldY),b=PanelPhysics.local(e,x,y);float h=PanelPhysics.halfLength(e),t=PanelPhysics.HALF_THICKNESS;
                if(Math.abs(Math.min(Math.abs(Math.abs(a[0])-h),Math.abs(Math.abs(a[1])-t))-
                    Math.min(Math.abs(Math.abs(b[0])-h),Math.abs(Math.abs(b[1])-t)))>.01f)return false;
            }
        }
        return true;
    }
    static Result solve(Morphology.Surface s,List<UnknownWorld.Entity> objects,ScarcityLab room,
                        float[] gelX,float[] gelY,float[] preX,float[] preY){
        boolean[] bad=new boolean[N],frozen=new boolean[N];
        int initial=defects(s,objects,room,bad);if(initial==0)return new Result(0,0,0);
        float[] x0=s.x.clone(),y0=s.y.clone(),vx0=s.vx.clone(),vy0=s.vy.clone(),target=new float[2];
        for(int i=0;i<N;i++)if(s.gripCommand[i]>Morphology.GRIP_THRESHOLD){
            if(room!=null&&room.gateGripHold[i]>=Morphology.GRIP_HOLD&&GripSurfaceComposition.gateFace(room,s.x[i],s.y[i]))frozen[i]=true;
            for(UnknownWorld.Entity panel:objects)if(panel.isConstruction()&&PanelPhysics.held(panel,i,s))frozen[i]=true;
            for(UnknownWorld.Entity box:objects)if(box.isBilateralBox()&&box.boxGripHold[i]>=Morphology.GRIP_HOLD&&
                GripSurfaceComposition.boxFace(box,s.x[i],s.y[i]))frozen[i]=true;
        }
        int current=initial;
        for(int iteration=0;iteration<12&&current>0;iteration++){
            float[] bestX=null,bestY=null;int best=current;
            float[] startX=s.x.clone(),startY=s.y.clone();
            search:
            for(int mode=0;mode<3;mode++)for(int radius:new int[]{96,0,1,3})for(int center=0;center<(radius==96?1:N);center++){
                if(radius!=96&&!bad[center])continue;
                for(float fraction:new float[]{1f,.5f,.25f}){
                    for(int i=0;i<N;i++){
                        s.x[i]=startX[i];s.y[i]=startY[i];
                        int distance=Math.min((i-center+N)%N,(center-i+N)%N);
                        if(!(radius==96?bad[i]:distance<=radius))continue;
                        float tx=mode==0?gelX[i]:preX[i],ty=mode==0?gelY[i]:preY[i];
                        if(mode==2){
                            tx=startX[i];ty=startY[i];
                            for(UnknownWorld.Entity box:objects)if(box.isBilateralBox()){
                                float h=SquareGripPuzzle.HALF;
                                if(tx>box.x-h-1&&tx<box.x+h+1&&ty>box.y-h-1&&ty<box.y+h+1){
                                    nearest(new float[]{box.x-h,box.y-h,box.x+h,box.y+h},tx,ty,target);tx=target[0];ty=target[1];
                                }
                            }
                        }
                        tx=startX[i]+fraction*(tx-startX[i]);ty=startY[i]+fraction*(ty-startY[i]);
                        if(Float.isFinite(tx)&&Float.isFinite(ty)&&Math.hypot(tx-x0[i],ty-y0[i])<=9&&(!frozen[i]||legalHeld(s,objects,room,i,x0[i],y0[i],tx,ty))){s.x[i]=tx;s.y[i]=ty;}
                    }
                    int candidate=defects(s,objects,room,null);
                    if(candidate<best){best=candidate;bestX=s.x.clone();bestY=s.y.clone();if(best==0)break search;}
                }
            }
            System.arraycopy(startX,0,s.x,0,N);System.arraycopy(startY,0,s.y,0,N);
            if(bestX==null)break;
            System.arraycopy(bestX,0,s.x,0,N);System.arraycopy(bestY,0,s.y,0,N);
            current=best;Arrays.fill(bad,false);defects(s,objects,room,bad);
        }
        if(current!=0&&Boolean.getBoolean("v354.dumpFailure")){
            StringBuilder dump=new StringBuilder();
            dump.append("ROOM ").append(room.ox).append(' ').append(room.oy).append(' ').append(room.doorY).append('\n');
            for(int i=0;i<N;i++)dump.append("S ").append(i).append(' ').append(x0[i]).append(' ').append(y0[i]).append(' ').append(vx0[i]).append(' ').append(vy0[i]).append(' ').append(s.gripCommand[i]).append(' ').append(frozen[i]?1:0).append(' ').append(gelX[i]).append(' ').append(gelY[i]).append(' ').append(preX[i]).append(' ').append(preY[i]).append('\n');
            for(UnknownWorld.Entity e:objects)if(e.isBox()){dump.append("BOX ").append(e.physicalLaw()).append(' ').append(e.x).append(' ').append(e.y).append(' ').append(e.spawnX).append(' ').append(e.spawnY).append(' ').append(e.angle);for(float hold:e.boxGripHold)dump.append(' ').append(hold);dump.append('\n');}
            try{java.nio.file.Files.write(java.nio.file.Paths.get("/tmp/zero354-failure.txt"),dump.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));}catch(Exception ignore){}
            System.err.println("V354 unresolved initial="+initial+" remaining="+current+" crossed="+MaterialGeometry.selfCrossings(s));
        }
        int moved=0;
        if(current!=0){System.arraycopy(x0,0,s.x,0,N);System.arraycopy(y0,0,s.y,0,N);}
        else for(int i=0;i<N;i++)if(s.x[i]!=x0[i]||s.y[i]!=y0[i]){
            if(frozen[i]&&!legalHeld(s,objects,room,i,x0[i],y0[i],s.x[i],s.y[i]))throw new IllegalStateException("V354 changed physical adhesive constraint");
            s.vx[i]=s.vy[i]=0;moved++;
        }
        if(current!=0){System.arraycopy(vx0,0,s.vx,0,N);System.arraycopy(vy0,0,s.vy,0,N);}
        return new Result(initial,current,moved);
    }
}
