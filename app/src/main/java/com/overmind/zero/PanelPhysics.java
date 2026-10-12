package com.overmind.zero;

import java.util.ArrayList;

/**
 * ZERO 0124: genuinely thin rigid construction panels. The legacy 40x40
 * energy-cover square retains its strict bilateral adhesive requirement.
 * A panel moves with ordinary unilateral material impulse and rotates if
 * pressed away from its center. No motor command or construction bonus.
 */
public final class PanelPhysics {
    private PanelPhysics(){}
    public static final float HALF_THICKNESS=7f;
    private static final float MAX_SPEED=105f,MAX_SPIN=2.5f;
    private static float clip(float v,float low,float high){
        return Math.min(high,Math.max(low,v));
    }
    /** Three stable lengths derived from the physical birth anchor. */
    public static float halfLength(float x,float y){
        int n=Float.floatToIntBits(x)*31+Float.floatToIntBits(y);
        return 37f+9f*Math.floorMod(n,3); // half-length 37,46,55 units
    }
    public static float halfLength(UnknownWorld.Entity p){
        return halfLength(p.spawnX,p.spawnY);
    }
    public static float bound(UnknownWorld.Entity p){
        return (float)Math.hypot(halfLength(p),HALF_THICKNESS);
    }
    public static final class Contact {
        public int points=0,solidNodes=0;
        public float pressureImpulse=0,torqueImpulse=0;
    }
    private static float norm(float x,float y){
        return (float)Math.hypot(x,y);
    }
    /** Signed OBB local coordinates of any world-space point. */
    static float[] local(UnknownWorld.Entity p,float x,float y){
        float c=(float)Math.cos(p.angle),s=(float)Math.sin(p.angle);
        float dx=x-p.x,dy=y-p.y;
        return new float[]{dx*c+dy*s,-dx*s+dy*c};
    }
    static float[] worldVector(UnknownWorld.Entity p,float u,float v){
        float c=(float)Math.cos(p.angle),s=(float)Math.sin(p.angle);
        return new float[]{u*c-v*s,u*s+v*c};
    }
    private static boolean within(float u,float v,float a,float b,float clearance){
        return Math.abs(u)<=a+clearance&&Math.abs(v)<=b+clearance;
    }
    /** True geometric wind shade projected from rotated thin material. */
    public static boolean shadow(float x,float y,int direction,UnknownWorld.Entity p,
                                 float range){
        if(p==null||!p.isConstruction()||p.consumed||p.concealed)return false;
        float a=halfLength(p),b=HALF_THICKNESS;
        // A horizontal incident ray intersects the oriented panel OBB.
        // Slab interval in local coordinates with finite upstream reach.
        float[] uv=local(p,x,y);
        float c=(float)Math.cos(p.angle),s=(float)Math.sin(p.angle);
        float du=-direction*c,dv=direction*s;
        float enter=0,exit=range;
        if(Math.abs(du)<1e-5f){
            if(Math.abs(uv[0])>a)return false;
        }else{
            float t0=(-a-uv[0])/du,t1=(a-uv[0])/du;
            enter=Math.max(enter,Math.min(t0,t1));
            exit=Math.min(exit,Math.max(t0,t1));
        }
        if(Math.abs(dv)<1e-5f){
            if(Math.abs(uv[1])>b)return false;
        }else{
            float t0=(-b-uv[1])/dv,t1=(b-uv[1])/dv;
            enter=Math.max(enter,Math.min(t0,t1));
            exit=Math.min(exit,Math.max(t0,t1));
        }
        return enter<exit&&exit>1.5f&&enter<range&&
            (x-p.x)*direction> -a-4f;
    }
    /** CCD: trace a real particle from previous to current material position
     * against a local OBB slab. A thin plate can never be jumped just because
     * the endpoints are on opposite sides in the same physics tick.
     */
    private static boolean hit(UnknownWorld.Entity p,float priorX,float priorY,
                               float nowX,float nowY,float radius,
                               float[] result){
        float[] old=local(p,priorX,priorY),cur=local(p,nowX,nowY);
        float a=halfLength(p)+radius,b=HALF_THICKNESS+radius;
        float ox=old[0],oy=old[1],dx=cur[0]-ox,dy=cur[1]-oy;
        if(within(cur[0],cur[1],a,b,0)){
            // If already embedded, push to the shallowest local edge.
            float left=a+cur[0],right=a-cur[0],top=b+cur[1],bottom=b-cur[1];
            float best=left;int face=0;
            if(right<best){best=right;face=1;}
            if(top<best){best=top;face=2;}
            if(bottom<best){face=3;}
            result[0]=face==0?-a:face==1?a:cur[0];
            result[1]=face==2?-b:face==3?b:cur[1];
            result[2]=face==0?-1:face==1?1:0;
            result[3]=face==2?-1:face==3?1:0;
            return true;
        }
        // Trace only from a position genuinely outside; a zero-length sample
        // on an outer boundary cannot invent an energetic impulse.
        float lo=0,hi=1,nu=0,nv=0;
        if(Math.abs(dx)<1e-7f){if(Math.abs(ox)>a)return false;}
        else{
            float first=(-a-ox)/dx,second=(a-ox)/dx;
            float near=Math.min(first,second),far=Math.max(first,second);
            if(near>lo){lo=near;nu=dx>0?-1:1;nv=0;}
            hi=Math.min(hi,far);
        }
        if(Math.abs(dy)<1e-7f){if(Math.abs(oy)>b)return false;}
        else{
            float first=(-b-oy)/dy,second=(b-oy)/dy;
            float near=Math.min(first,second),far=Math.max(first,second);
            if(near>lo){lo=near;nu=0;nv=dy>0?-1:1;}
            hi=Math.min(hi,far);
        }
        if(lo>hi||lo<0||lo>1||hi<0||nu==0&&nv==0)return false;
        result[0]=clip(ox+dx*Math.max(0,lo-.001f),-a,a);
        result[1]=clip(oy+dy*Math.max(0,lo-.001f),-b,b);
        result[2]=nu;result[3]=nv;
        return true;
    }
    private static void project(float[] xs,float[] ys,float[] vxs,float[] vys,
                                int i,float[] collision,UnknownWorld.Entity p){
        float[] v=worldVector(p,collision[0],collision[1]);
        xs[i]=p.x+v[0];ys[i]=p.y+v[1];
        float[] n=worldVector(p,collision[2],collision[3]);
        float inward=vxs[i]*n[0]+vys[i]*n[1];
        if(inward<0){vxs[i]-=inward*n[0];vys[i]-=inward*n[1];}
    }
    /** Run after existing square physics. Panel is NOT a bilateral box. */
    public static Contact constrain(MotorBrain.Body body,Morphology.Surface s,
                                    UnknownWorld.Entity panel,float dt,
                                    float[] priorSkinX,float[] priorSkinY,
                                    float[] priorBodyX,float[] priorBodyY){
        Contact out=new Contact();
        if(body==null||s==null||panel==null||!panel.isConstruction()||
           !Float.isFinite(dt)||dt<=0)return out;
        float[] sample=new float[4];
        float spin=0,forceX=0,forceY=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float x=s.x[i],y=s.y[i],vx=s.vx[i],vy=s.vy[i];
            float priorX=priorSkinX==null?x-vx*dt:priorSkinX[i];
            float priorY=priorSkinY==null?y-vy*dt:priorSkinY[i];
            if(!hit(panel,priorX,priorY,x,y,0.45f,sample))continue;
            float[] n=worldVector(panel,sample[2],sample[3]);
            float cx=panel.x+worldVector(panel,sample[0],sample[1])[0];
            float cy=panel.y+worldVector(panel,sample[0],sample[1])[1];
            float armX=cx-panel.x,armY=cy-panel.y;
            float panelVx=panel.vx-panel.angularVelocity*armY;
            float panelVy=panel.vy+panel.angularVelocity*armX;
            // Relative inward tissue velocity or genuine actuator target
            // pushing through the plate is necessary for propulsion.
            float inward=Math.max(0,-((vx-panelVx)*n[0]+(vy-panelVy)*n[1]));
            float motorPush=Math.max(0,-((s.targetX(i)-x)*n[0]+
                                        (s.targetY(i)-y)*n[1]));
            float impulse=clip(inward*.08f+motorPush*.045f,0,1.6f);
            // A nearly stationary touch with no motor drive never moves panel.
            if(s.hasValidMotorTargets()&&impulse>.055f){
                forceX-=n[0]*impulse;forceY-=n[1]*impulse;
                spin+=(armX*(-n[1])-armY*(-n[0]))*impulse;
                out.pressureImpulse+=impulse;
                out.torqueImpulse+=Math.abs((armX*(-n[1])-armY*(-n[0]))*impulse);
                panel.everCollided=true;
            }
            s.touchSense[i]=1;
            s.touchPulse[i]=Math.max(s.touchPulse[i],Math.min(1f,.2f+impulse*.3f));
            project(s.x,s.y,s.vx,s.vy,i,sample,panel);
            out.points++;
        }
        // A thin plate also blocks real membrane SEGMENTS whose two end
        // particles happen to lie on opposing faces. Project both endpoints
        // onto the nearest shared supporting face, just as old square/room
        // membrane anti-penetration already does. This is geometry only:
        // no automatic sheet motion, no reward and no new grip command.
        float length=halfLength(panel),thick=HALF_THICKNESS;
        for(int pass=0;pass<2;pass++){
            boolean changed=false;
            for(int i=0;i<Morphology.POINTS;i++){
                int j=(i+1)%Morphology.POINTS;
                float[] a=local(panel,s.x[i],s.y[i]);
                float[] b=local(panel,s.x[j],s.y[j]);
                if(!ScarcityLab.crossesInterior(a[0],a[1],b[0],b[1],
                    -length,-thick,length,thick))continue;
                float[] scores={
                    Math.max(0,a[0]+length)+Math.max(0,b[0]+length),
                    Math.max(0,length-a[0])+Math.max(0,length-b[0]),
                    Math.max(0,a[1]+thick)+Math.max(0,b[1]+thick),
                    Math.max(0,thick-a[1])+Math.max(0,thick-b[1])
                };
                int face=0;
                for(int k=1;k<4;k++)if(scores[k]<scores[face])face=k;
                for(int p=0;p<2;p++){
                    int at=p==0?i:j;
                    float[] point=p==0?a:b;
                    float xx=face==0?-length-.8f:face==1?length+.8f:point[0];
                    float yy=face==2?-thick-.8f:face==3?thick+.8f:point[1];
                    float[] pos=worldVector(panel,xx,yy);
                    s.x[at]=panel.x+pos[0];s.y[at]=panel.y+pos[1];
                    float[] n=worldVector(panel,
                        face==0?-1f:face==1?1f:0,
                        face==2?-1f:face==3?1f:0);
                    float inward=s.vx[at]*n[0]+s.vy[at]*n[1];
                    if(inward<0){
                        s.vx[at]-=inward*n[0];s.vy[at]-=inward*n[1];
                    }
                }
                changed=true;
            }
            if(!changed)break;
        }
        // Preserve legitimate internal mass collisions with thin panels too.
        for(int i=0;i<MotorBrain.N;i++){
            float x=body.x[i],y=body.y[i],vx=body.vx[i],vy=body.vy[i];
            float priorX=priorBodyX==null?x-vx*dt:priorBodyX[i];
            float priorY=priorBodyY==null?y-vy*dt:priorBodyY[i];
            if(!hit(panel,priorX,priorY,x,y,3.5f,sample))continue;
            float[] n=worldVector(panel,sample[2],sample[3]);
            float inward=Math.max(0,-((vx-panel.vx)*n[0]+(vy-panel.vy)*n[1]));
            float impulse=clip(inward*.07f,0,1.2f);
            if(impulse>.08f){
                forceX-=n[0]*impulse;forceY-=n[1]*impulse;
                float[] at=worldVector(panel,sample[0],sample[1]);
                spin+=(at[0]*(-n[1])-at[1]*(-n[0]))*impulse;
                panel.everCollided=true;
            }
            project(body.x,body.y,body.vx,body.vy,i,sample,panel);
            out.solidNodes++;
        }
        // A physically displaced plate retains momentum with friction;
        // off-center impulses generate signed angular motion.
        panel.vx=clip(panel.vx+forceX*.64f,-MAX_SPEED,MAX_SPEED);
        panel.vy=clip(panel.vy+forceY*.64f,-MAX_SPEED,MAX_SPEED);
        float inertia=halfLength(panel)*halfLength(panel)*.35f+
                      HALF_THICKNESS*HALF_THICKNESS;
        panel.angularVelocity=clip(panel.angularVelocity+
            spin*14f/Math.max(80f,inertia),-MAX_SPIN,MAX_SPIN);
        return out;
    }
    /**
     * Iterative SAT: rotated panel/old square are true solid convex objects.
     * Applies no positive physics impulses or arbitrary "build" reward.
     */
    public static int separate(ArrayList<UnknownWorld.Entity> all){
        if(all==null)return 0;
        int hits=0;
        for(int i=0;i<all.size();i++){
            UnknownWorld.Entity a=all.get(i);
            if(a.consumed||!a.isBox())continue;
            for(int j=i+1;j<all.size();j++){
                UnknownWorld.Entity b=all.get(j);
                if(b.consumed||!b.isBox()||(!a.isConstruction()&&!b.isConstruction()))
                    continue;
                float[] axisX=new float[4],axisY=new float[4];
                int n=0;
                n=axes(a,axisX,axisY,n);n=axes(b,axisX,axisY,n);
                float pen=Float.POSITIVE_INFINITY,ex=0,ey=0;
                float dx=b.x-a.x,dy=b.y-a.y;boolean overlap=true;
                for(int axis=0;axis<n;axis++){
                    float u=axisX[axis],v=axisY[axis];
                    float overlapDepth=projection(a,u,v)+projection(b,u,v)-
                        Math.abs(dx*u+dy*v);
                    if(overlapDepth<=.001f){overlap=false;break;}
                    if(overlapDepth<pen){
                        pen=overlapDepth;
                        float sign=(dx*u+dy*v)>=0?1:-1;
                        ex=u*sign;ey=v*sign;
                    }
                }
                if(!overlap||!Float.isFinite(pen))continue;
                a.x-=ex*pen*.5f;a.y-=ey*pen*.5f;
                b.x+=ex*pen*.5f;b.y+=ey*pen*.5f;
                float normalAvg=(a.vx*ex+a.vy*ey+b.vx*ex+b.vy*ey)*.5f;
                float an=a.vx*ex+a.vy*ey,bn=b.vx*ex+b.vy*ey;
                a.vx+=(normalAvg-an)*ex;a.vy+=(normalAvg-an)*ey;
                b.vx+=(normalAvg-bn)*ex;b.vy+=(normalAvg-bn)*ey;
                hits++;
            }
        }
        return hits;
    }
    private static int axes(UnknownWorld.Entity p,float[] x,float[] y,int i){
        float angle=p.isConstruction()?p.angle:0f;
        float c=(float)Math.cos(angle),s=(float)Math.sin(angle);
        x[i]=c;y[i]=s;i++;
        x[i]=-s;y[i]=c;i++;
        return i;
    }
    private static float projection(UnknownWorld.Entity p,float nx,float ny){
        float a=p.isConstruction()?halfLength(p):SquareGripPuzzle.HALF;
        float b=p.isConstruction()?HALF_THICKNESS:SquareGripPuzzle.HALF;
        float angle=p.isConstruction()?p.angle:0f;
        float c=(float)Math.cos(angle),s=(float)Math.sin(angle);
        return a*Math.abs(nx*c+ny*s)+b*Math.abs(-nx*s+ny*c);
    }
}
