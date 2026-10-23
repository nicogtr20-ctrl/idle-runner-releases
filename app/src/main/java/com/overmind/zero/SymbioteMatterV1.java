package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * SYMBIOTE 1.0 — 2D active cortex + materially conserved viscous interior.
 * The existing 10 MotorBrain nodes are genuine particles in the liquid,
 * NOT spokes/anchors, and 64 further mobile parcels carry volume inside.
 * All 96 Morphology.Surface points remain true collidable membrane material.
 * There is no artificial central mass, target, entity identity or food rule.
 *
 * One fixed-duration physics step regardless of UI x1/x5/x20/TEST MAX.
 * The chosen neural motor head provides a continuous sector field of
 * extension/tangential actuation/substrate friction/cortex stiffness.
 */
public final class SymbioteMatterV1 {
    public static final String KEY="symbiote_body_v1";
    public static final String ENABLED="symbiote_body_v1_enabled";
    public static final int INNER=64, SECTORS=16, OUTPUTS=4;
    private static final int SURFACE=Morphology.POINTS;
    public final float[] x=new float[INNER],y=new float[INNER],vx=new float[INNER],vy=new float[INNER];
    private final float[] lastX=new float[INNER],lastY=new float[INNER];
    private final float[] lastSkinX=new float[SURFACE],lastSkinY=new float[SURFACE];
    private final float[] lastBodyX=new float[MotorBrain.N],lastBodyY=new float[MotorBrain.N];
    private final float[] fx=new float[SURFACE],fy=new float[SURFACE];
    // ONLY true active muscle forces get an opposite LIQUID reaction.
    // Neighbor elasticity is an INTERNAL shell-to-shell pair force;
    // feeding it to the fluid double counts the reaction and adds torque.
    private final float[] muscleFx=new float[SURFACE],muscleFy=new float[SURFACE];
    private final float[] nx=new float[SURFACE],ny=new float[SURFACE];
    private final float[] impulsesX=new float[MotorBrain.N+INNER],impulsesY=new float[MotorBrain.N+INNER];
    private final float[] fluidX=new float[MotorBrain.N+INNER],fluidY=new float[MotorBrain.N+INNER];
    private final float[] fluidVx=new float[MotorBrain.N+INNER],fluidVy=new float[MotorBrain.N+INNER];
    private static final float TWO_PI=(float)(2*Math.PI), R=28f;
    public float restArea=(float)Math.PI*R*R,areaError=0f,work=0f,maxReach=0f;
    public long ticks=0,invalid=0,topologyRepairs=0,selfContactPairs=0;
    /** Physics QA only: membrane sites formerly missing opposite fluid force. */
    public long longTipReactionFallbacks=0;
    // Read-only physics forensics; zero motor input MUST produce no
    // motor-to-fluid reaction, even when the shell is elastically strained.
    public float lastMuscleReactionImpulse=0f,lastElasticForceMagnitude=0f;
    private float time=0f;
    public SymbioteMatterV1(){}
    private static float clip(float a,float lo,float hi){return Math.max(lo,Math.min(hi,a));}
    private static float len(float a,float b){return (float)Math.sqrt(a*a+b*b);}
    /** Normal from actual living polygon, NOT radius from the center. */
    static void normalAt(Morphology.Surface skin,int i,float centerX,float centerY,
                         float handedness,float[] out){
        int prev=(i+SURFACE-1)%SURFACE,next=(i+1)%SURFACE;
        float tx=skin.x[next]-skin.x[prev],ty=skin.y[next]-skin.y[prev];
        float nx=handedness*ty,ny=-handedness*tx;
        float ll=len(nx,ny);
        if(ll<.001f){
            nx=skin.x[i]-centerX;ny=skin.y[i]-centerY;
            ll=len(nx,ny);
        }
        if(ll<.001f){nx=(float)Math.cos(i*TWO_PI/SURFACE);
                       ny=(float)Math.sin(i*TWO_PI/SURFACE);ll=1f;}
        out[0]=nx/ll;out[1]=ny/ll;
    }
    /** A concave gel needs true outward normals, while ordinary convex
     * material must preserve the already validated radial locomotor
     * reaction. Smooth, local geometry-only adaptation; no heading/goal.
     * Shared by the cortex proprioception and real physical solver.
     */
    static void actuatorDirectionAt(Morphology.Surface skin,int i,
                                    float cx,float cy,float handedness,
                                    float[] out){
        normalAt(skin,i,cx,cy,handedness,out);
        float dx=skin.x[i]-cx,dy=skin.y[i]-cy;
        float radius=len(dx,dy);
        if(radius<.001f)return;
        float rx=dx/radius,ry=dy/radius;
        float alignment=out[0]*rx+out[1]*ry;
        float correction=clip((.55f-alignment)/.8f,0f,1f);
        float ux=rx*(1f-correction)+out[0]*correction;
        float uy=ry*(1f-correction)+out[1]*correction;
        float mag=len(ux,uy);
        if(mag>.001f){out[0]=ux/mag;out[1]=uy/mag;}
    }
    /** Allocate distributed matter in deterministic concentric sample rings.
     * Initial matter coordinates are not permanent: no spoke constraints. */
    public void reset(MotorBrain.Body body,Morphology.Surface skin){
        if(body==null||skin==null)throw new IllegalArgumentException("Missing actual organism");
        float ox=body.cx,oy=body.cy;
        for(int i=0;i<MotorBrain.N;i++){
            float a=i*TWO_PI/MotorBrain.N;
            body.x[i]=ox+17f*(float)Math.cos(a);
            body.y[i]=oy+17f*(float)Math.sin(a);
            body.vx[i]=body.vy[i]=0;
        }
        int[] counts={8,12,20,24};
        float[] radii={5,10,15,21};
        int k=0;
        for(int ring=0;ring<counts.length;ring++)
            for(int j=0;j<counts[ring];j++){
                float a=TWO_PI*(j+.29f*(ring&1))/counts[ring];
                x[k]=ox+radii[ring]*(float)Math.cos(a);
                y[k]=oy+radii[ring]*(float)Math.sin(a);
                vx[k]=vy[k]=0;
                k++;
            }
        for(int i=0;i<SURFACE;i++){
            float a=i*TWO_PI/SURFACE;
            skin.x[i]=ox+R*(float)Math.cos(a);
            skin.y[i]=oy+R*(float)Math.sin(a);
            skin.vx[i]=skin.vy[i]=skin.extension[i]=0;
            skin.gripCommand[i]=skin.touchSense[i]=skin.gripHold[i]=0;
        }
        restArea=absArea(skin);
        transientGroundBrakeV382=0f;lastChosenHeadingV382=Float.NaN;
        areaError=work=maxReach=time=0;
        ticks=invalid=topologyRepairs=selfContactPairs=longTipReactionFallbacks=0;
        observeBody(body,skin);
    }
    /** Mass-weighted center of all 170 material nodes, never an accelerated core. */
    public void observeBody(MotorBrain.Body body,Morphology.Surface skin){
        float mx=0f,my=0f,mvx=0f,mvy=0f;
        for(int i=0;i<SURFACE;i++){
            mx+=skin.x[i];my+=skin.y[i];mvx+=skin.vx[i];mvy+=skin.vy[i];
        }
        float shellWeight=.5f/SURFACE;
        float centerX=mx*shellWeight,centerY=my*shellWeight;
        float centerVx=mvx*shellWeight,centerVy=mvy*shellWeight;
        mx=my=mvx=mvy=0f;
        for(int i=0;i<MotorBrain.N;i++){
            mx+=body.x[i];my+=body.y[i];mvx+=body.vx[i];mvy+=body.vy[i];
        }
        for(int i=0;i<INNER;i++){
            mx+=x[i];my+=y[i];mvx+=vx[i];mvy+=vy[i];
        }
        float fluidWeight=.5f/(MotorBrain.N+INNER);
        body.cx=centerX+mx*fluidWeight;body.cy=centerY+my*fluidWeight;
        body.cvx=centerVx+mvx*fluidWeight;body.cvy=centerVy+mvy*fluidWeight;
    }
    /**
     * OTA166: real viscous cross-layer material coupling. The cortex alone
     * loses velocity to its substrate, while internal muscle reactions could
     * otherwise counter-spin with almost no membrane/interior shear exchange.
     * Each liquid parcel exchanges velocity with its CLOSEST real membrane
     * point. Equal and opposite impulses conserve weighted total momentum
     * for unheld material; a genuinely object-held point transfers the
     * reaction to the obstacle. No center anchor, direction, gait or reward.
     */
    static void exchangeCortexFluidShear(Morphology.Surface skin,
            float[] px,float[] py,float[] pvx,float[] pvy,float dt){
        if(skin==null||px==null||py==null||pvx==null||pvy==null||
            px.length!=MotorBrain.N+INNER||py.length!=px.length||
            pvx.length!=px.length||pvy.length!=px.length||
            !Float.isFinite(dt)||dt<=0||dt>.04f)
            throw new IllegalArgumentException("Invalid living fluid shear");
        final float cutoff2=20f*20f;
        float base=1f-(float)Math.exp(-6f*dt);
        final float fluidRatio=(float)(MotorBrain.N+INNER)/SURFACE;
        for(int j=0;j<px.length;j++){
            float nearest=cutoff2;
            int closest=-1;
            for(int i=0;i<SURFACE;i++){
                float dx=px[j]-skin.x[i],dy=py[j]-skin.y[i];
                float d2=dx*dx+dy*dy;
                if(d2<nearest){nearest=d2;closest=i;}
            }
            if(closest<0)continue;
            float alpha=.55f*base*(1f-nearest/cutoff2);
            float dvx=pvx[j]-skin.vx[closest],dvy=pvy[j]-skin.vy[closest];
            if(skin.gripHold[closest]>=Morphology.GRIP_HOLD){
                // An actually contacted solid is the external sink.
                pvx[j]-=alpha*dvx;
                pvy[j]-=alpha*dvy;
            }else{
                float sx=alpha*dvx,sy=alpha*dvy;
                skin.vx[closest]+=sx;skin.vy[closest]+=sy;
                skin.x[closest]+=sx*dt;skin.y[closest]+=sy*dt;
                pvx[j]-=fluidRatio*sx;pvy[j]-=fluidRatio*sy;
            }
        }
    }
    /**
     * A moderately extended patch must be able to pull material back even
     * before it becomes an extreme filament. Preserve ordinary crawl footholds,
     * which typically use milder contractions than a deliberate tip retract.
     * Preserve full tangential floor
     * anchoring and NEVER release an actual obstacle hold. This is a passive
     * physical constitutive law, not a neural gait or food-directed impulse.
     */
    static float radialRetractionRelease(float radius,float extend,boolean obstacleHeld){
        if(obstacleHeld||!Float.isFinite(radius)||!Float.isFinite(extend))return 0f;
        return clip((radius-(R+9f))/8f,0f,1f)*
            clip((-extend-.83f)/.13f,0f,1f);
    }
    // V363 matched lab A/B knob. The original physical law remains 0.12
    // in every production organism until net motor gains are independently
    // proven across learned/frozen and virgin/resumed worlds.
    private float passiveFloorBaselineV363=.12f;
    // V382 lab-only: real substrate damping on an abrupt GRU motor change,
    // never a force, velocity or heading injected to a destination.
    private float transientGroundBrakeV382=0f;
    private float experimentalTurnBrakeCeilingV382=0f;
    private float lastChosenHeadingV382=Float.NaN;
    /** 0 = exact original V381 physics, allow A/B on the same saved cortex. */
    void configurePhysicalTurnBrakeV382(float extraTraction){
        if(!Float.isFinite(extraTraction)||extraTraction<-.06f||extraTraction>.07f)
            throw new IllegalArgumentException("V382 unsafe physical brake");
        experimentalTurnBrakeCeilingV382=extraTraction;
        transientGroundBrakeV382=0f;lastChosenHeadingV382=Float.NaN;
    }
    void observedOriginalMotorChangeV382(float goalX,float goalY){
        if(!Float.isFinite(goalX)||!Float.isFinite(goalY)||
           Math.hypot(goalX,goalY)<.90)return;
        float angle=(float)Math.atan2(goalY,goalX);
        if(Float.isFinite(lastChosenHeadingV382)&&
           Math.abs(experimentalTurnBrakeCeilingV382)>.0001f){
            float change=(float)Math.atan2(Math.sin(angle-lastChosenHeadingV382),
                                           Math.cos(angle-lastChosenHeadingV382));
            float strength=clip((Math.abs(change)-.48f)/1.5f,0f,1f);
            float requested=strength*experimentalTurnBrakeCeilingV382;
            if(Math.abs(requested)>Math.abs(transientGroundBrakeV382))
                transientGroundBrakeV382=requested;
        }
        lastChosenHeadingV382=angle;
    }
    float transientBrakeV382(){return transientGroundBrakeV382;}
    float configuredTurnBrakeLimitV382(){return experimentalTurnBrakeCeilingV382;}

    void setPassiveFloorBaselineForLabV363(float value){
        if(!Float.isFinite(value)||value<.02f||value>.12f)
            throw new IllegalArgumentException("outside passive floor calibration");
        passiveFloorBaselineV363=value;
    }
    /** V363 verified targeted calibration for the ORIGINAL game's sole V3
     * neural owner. Restore/new-life only. Legacy V2.2 and bench baseline
     * remain at 0.12, so their physical behavior is not silently changed.
     *
     * Actual material, viscosity and local compression-dependent footholds
     * still govern every step. Not a velocity boost, energy or gait script.
     */
    void enableOriginalZero3TractionV363(){
        passiveFloorBaselineV363=.08f;
    }
    float passiveFloorBaselineV363(){return passiveFloorBaselineV363;}
    static float substrateTraction(float extend,float tension,float meanTension){
        return substrateTractionWithBaselineV363(extend,tension,meanTension,.12f);
    }
    static float substrateTractionWithBaselineV363(float extend,float tension,
                                                  float meanTension,float passiveBase){
        float compression=Math.max(0f,-extend);
        // Material cortex tension, NOT the separate object-grip command,
        // changes the local ground's normal loading. Uniform extra tension
        // cannot secretly pin the entire symbiote to the substrate.
        float delta=tension-meanTension;
        if(Math.abs(delta)<.0001f)delta=0f;
        return clip(passiveBase+1.45f*compression+
            .72f*clip(delta,-1f,1f),0f,.95f);
    }
    static float radialRetractionDrag(float traction,float release,float dt){
        return (float)Math.exp(-(1.8f+
            66f*clip(traction,0f,.95f)*(1f-.70f*clip(release,0f,1f)))*dt);
    }
    public static float signedArea(Morphology.Surface skin){
        float a=0;
        for(int i=0;i<SURFACE;i++){
            int j=(i+1)%SURFACE;
            a+=skin.x[i]*skin.y[j]-skin.x[j]*skin.y[i];
        }
        return .5f*a;
    }
    public static float absArea(Morphology.Surface skin){return Math.abs(signedArea(skin));}
    private static float cross(float ax,float ay,float bx,float by,float cx,float cy){
        return (bx-ax)*(cy-ay)-(by-ay)*(cx-ax);
    }
    private static boolean intersects(float ax,float ay,float bx,float by,
                                      float cx,float cy,float dx,float dy){
        float a=cross(ax,ay,bx,by,cx,cy),b=cross(ax,ay,bx,by,dx,dy);
        float c=cross(cx,cy,dx,dy,ax,ay),d=cross(cx,cy,dx,dy,bx,by);
        return ((a>1e-4f&&b<-1e-4f)||(a< -1e-4f&&b>1e-4f))&&
               ((c>1e-4f&&d<-1e-4f)||(c< -1e-4f&&d>1e-4f));
    }
    /**
     * A saved scene may already contain an edge crossing. OTA143-147 only
     * rolled a crossed step back to the immediately previous contour; when
     * THAT contour was crossed as well it zeroed the velocity of every free
     * membrane node forever. Recover pre-existing crossings locally using
     * short real-neighbour surface-tension projections, never an impulse to
     * the centroid, resource, or a target. Fully free sites only; held sites
     * remain owned by their real contacted obstacle.
     */
    private void recoverPersistedCrossings(Morphology.Surface skin){
        if(MaterialGeometry.selfCrossings(skin)==0)return;
        for(int pass=0;pass<24;pass++){
            boolean touched=false;
            for(int i=0;i<SURFACE&&!touched;i++){
                int ii=(i+1)%SURFACE;
                for(int j=i+2;j<SURFACE;j++){
                    int jj=(j+1)%SURFACE;
                    if(jj==i)continue;
                    if(!intersects(skin.x[i],skin.y[i],skin.x[ii],skin.y[ii],
                                   skin.x[j],skin.y[j],skin.x[jj],skin.y[jj]))continue;
                    // At most four adjoining cortex particles per repair.
                    // Midpoint attraction is the existing local membrane
                    // tension, applied with a per-pass displacement limit.
                    for(int k:new int[]{i,ii,j,jj}){
                        if(skin.gripHold[k]>=Morphology.GRIP_HOLD)continue;
                        int prev=(k+SURFACE-1)%SURFACE,next=(k+1)%SURFACE;
                        float tx=(skin.x[prev]+skin.x[next])*.5f;
                        float ty=(skin.y[prev]+skin.y[next])*.5f;
                        skin.x[k]+=clip((tx-skin.x[k])*.58f,-.65f,.65f);
                        skin.y[k]+=clip((ty-skin.y[k])*.58f,-.65f,.65f);
                    }
                    topologyRepairs++;touched=true;break;
                }
            }
            if(!touched||MaterialGeometry.selfCrossings(skin)==0)return;
        }
    }
    /** Continuous material self-collision guard. The previous contour is a
     * real known non-intersecting configuration. A proposed pair of crossed
     * segments yields, with the more displaced material patch returning to
     * last physical position; no snap to centroid or to a resource. */
    private void repairTopology(Morphology.Surface skin){
        for(int pass=0;pass<7;pass++){
            boolean changed=false;
            for(int i=0;i<SURFACE;i++){
                int ii=(i+1)%SURFACE;
                for(int j=i+2;j<SURFACE;j++){
                    int jj=(j+1)%SURFACE;
                    if(jj==i)continue;
                    if(!intersects(skin.x[i],skin.y[i],skin.x[ii],skin.y[ii],
                        skin.x[j],skin.y[j],skin.x[jj],skin.y[jj]))continue;
                    float first=len(skin.x[i]-lastSkinX[i],skin.y[i]-lastSkinY[i])+
                        len(skin.x[ii]-lastSkinX[ii],skin.y[ii]-lastSkinY[ii]);
                    float second=len(skin.x[j]-lastSkinX[j],skin.y[j]-lastSkinY[j])+
                        len(skin.x[jj]-lastSkinX[jj],skin.y[jj]-lastSkinY[jj]);
                    int start=first>=second?i:j,other=(start+1)%SURFACE;
                    // An actually attached point cannot be unhooked by a
                    // numerical self-contact repair. Favor the free edge.
                    if(skin.gripHold[start]>=Morphology.GRIP_HOLD||
                       skin.gripHold[other]>=Morphology.GRIP_HOLD){
                        start=first>=second?j:i;other=(start+1)%SURFACE;
                    }
                    for(int k:new int[]{start,other}){
                        if(skin.gripHold[k]>=Morphology.GRIP_HOLD)continue;
                        skin.x[k]=lastSkinX[k];skin.y[k]=lastSkinY[k];
                        skin.vx[k]=skin.vy[k]=0;
                    }
                    topologyRepairs++;changed=true;
                    break;
                }
                if(changed)break;
            }
            if(!changed)return;
        }
        // Last resort only if both edges repeatedly conflict: rollback
        // this rejected physical step, NOT the body or the learned brain.
        if(MaterialGeometry.selfCrossings(skin)>0){
            // A rollback is valid ONLY if the previous contour was simple.
            // Rolling back to a previously self-crossed contour zeroes all
            // velocities permanently despite ongoing neural muscle commands.
            // In that case relax the obstructing real membrane pieces rather
            // than reverting every living patch to the same broken geometry.
            if(previousContourCrossed()){
                recoverPersistedCrossings(skin);
            }else{
                for(int i=0;i<SURFACE;i++){
                    if(skin.gripHold[i]>=Morphology.GRIP_HOLD)continue;
                    skin.x[i]=lastSkinX[i];skin.y[i]=lastSkinY[i];
                    skin.vx[i]=skin.vy[i]=0;
                }
                topologyRepairs++;
            }
        }
    }
    /** True if the solver's actual pre-integration perimeter was invalid. */
    private boolean previousContourCrossed(){
        for(int i=0;i<SURFACE;i++){
            int ii=(i+1)%SURFACE;
            for(int j=i+2;j<SURFACE;j++){
                int jj=(j+1)%SURFACE;
                if(jj==i)continue;
                if(intersects(lastSkinX[i],lastSkinY[i],
                              lastSkinX[ii],lastSkinY[ii],
                              lastSkinX[j],lastSkinY[j],
                              lastSkinX[jj],lastSkinY[jj]))return true;
            }
        }
        return false;
    }
    /** A material parcel is never created, destroyed or snapped to a goal. */
    public float areaRelativeError(Morphology.Surface skin){
        return Math.abs(absArea(skin)-restArea)/Math.max(1f,restArea);
    }
    private static boolean inside(Morphology.Surface skin,float px,float py){
        boolean state=false;
        for(int i=0,j=SURFACE-1;i<SURFACE;j=i++){
            float ax=skin.x[i],ay=skin.y[i],bx=skin.x[j],by=skin.y[j];
            if((ay>py)!=(by>py)&&px<(bx-ax)*(py-ay)/(by-ay)+ax)state=!state;
        }
        return state;
    }
    /** XPBD membrane area constraint; 2D mass conservation without a
     * centroid force or radial length target. */
    private void projectArea(Morphology.Surface skin,float dt){
        float signed=signedArea(skin);
        float diff=signed-restArea;
        float sum=0;
        for(int i=0;i<SURFACE;i++){
            int a=(i+SURFACE-1)%SURFACE,b=(i+1)%SURFACE;
            nx[i]=.5f*(skin.y[b]-skin.y[a]);
            ny[i]=.5f*(skin.x[a]-skin.x[b]);
            // Real adhesive contact is not teleported to fix shape.
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD)continue;
            sum+=nx[i]*nx[i]+ny[i]*ny[i];
        }
        if(sum<.0001f)return;
        float alpha=.001f/(dt*dt);
        float change=-diff/(sum+alpha);
        for(int i=0;i<SURFACE;i++){
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD)continue;
            float dx=clip(change*nx[i],-1.8f,1.8f);
            float dy=clip(change*ny[i],-1.8f,1.8f);
            skin.x[i]+=dx;skin.y[i]+=dy;
        }
    }
    /** Conservative interior density/viscosity: all core and liquid parcels
     * are exchangeable material; no central anchor is privileged. */
    private void projectInterior(MotorBrain.Body body,Morphology.Surface skin){
        final int n=MotorBrain.N+INNER;
        for(int k=0;k<MotorBrain.N;k++){fluidX[k]=body.x[k];fluidY[k]=body.y[k];}
        for(int k=0;k<INNER;k++){fluidX[k+MotorBrain.N]=x[k];fluidY[k+MotorBrain.N]=y[k];}
        Arrays.fill(impulsesX,0f);Arrays.fill(impulsesY,0f);
        final float min=4.15f;
        for(int i=0;i<n;i++)for(int j=i+1;j<n;j++){
            float dx=fluidX[j]-fluidX[i],dy=fluidY[j]-fluidY[i];
            float d2=dx*dx+dy*dy;
            if(d2>=min*min)continue;
            if(d2<.00001f){ // deterministic alternate directions, NOT a goal
                float a=(i*0.311f+j*.79f);
                dx=(float)Math.cos(a)*.004f;dy=(float)Math.sin(a)*.004f;
                d2=dx*dx+dy*dy;
            }
            float d=(float)Math.sqrt(d2), force=.35f*(min-d)/d;
            impulsesX[i]-=dx*force;impulsesY[i]-=dy*force;
            impulsesX[j]+=dx*force;impulsesY[j]+=dy*force;
        }
        // Unenclosed fluid cannot escape from the actual membrane: correct
        // to nearest physical segment inward, never to an imaginary nucleus.
        for(int i=0;i<n;i++){
            float px=fluidX[i]+clip(impulsesX[i],-1.8f,1.8f);
            float py=fluidY[i]+clip(impulsesY[i],-1.8f,1.8f);
            if(!inside(skin,px,py)){
                float best=Float.MAX_VALUE,cx=px,cy=py;int closestSegment=-1;
                for(int j=0;j<SURFACE;j++){
                    int next=(j+1)%SURFACE;
                    float ax=skin.x[j],ay=skin.y[j],bx=skin.x[next],by=skin.y[next];
                    float dx=bx-ax,dy=by-ay,div=dx*dx+dy*dy;
                    float t=div<.00001f?0:clip(((px-ax)*dx+(py-ay)*dy)/div,0,1);
                    float nearX=ax+t*dx,nearY=ay+t*dy;
                    float dist=(px-nearX)*(px-nearX)+(py-nearY)*(py-nearY);
                    if(dist<best){best=dist;cx=nearX;cy=nearY;closestSegment=j;}
                }
                float orientation=signedArea(skin)>=0f?1f:-1f;
                float bestInside=Float.POSITIVE_INFINITY;
                boolean found=false;
                // Centroids need not be inside concave living tissue. Search
                // the actual oriented edge, then verify with the polygon test.
                for(int order=0;order<=SURFACE;order++){
                    // The nearest face normally suffices. Exhaustive fallback
                    // is only for a degenerate vertex, never the ordinary tick.
                    int j=order==0?closestSegment:order-1;
                    if(j<0||(order>0&&j==closestSegment))continue;
                    if(order>0&&found)break;
                    int next=(j+1)%SURFACE;
                    float ax=skin.x[j],ay=skin.y[j],ex=skin.x[next]-ax,ey=skin.y[next]-ay;
                    float length2=ex*ex+ey*ey;
                    if(length2<1e-8f)continue;
                    float along=clip(((px-ax)*ex+(py-ay)*ey)/length2,0f,1f);
                    float qx=ax+along*ex,qy=ay+along*ey;
                    float length=(float)Math.sqrt(length2);
                    for(int attempt=0;attempt<3;attempt++){
                        float epsilon=attempt==0?.05f:attempt==1?.2f:.8f;
                        float ix=qx-orientation*ey/length*epsilon;
                        float iy=qy+orientation*ex/length*epsilon;
                        float distance=(ix-px)*(ix-px)+(iy-py)*(iy-py);
                        if(distance<bestInside&&inside(skin,ix,iy)){
                            bestInside=distance;cx=ix;cy=iy;found=true;
                        }
                    }
                }
                if(found){px=cx;py=cy;}

            }
            fluidX[i]=px;fluidY[i]=py;
        }
        for(int i=0;i<MotorBrain.N;i++){body.x[i]=fluidX[i];body.y[i]=fluidY[i];}
        for(int i=0;i<INNER;i++){x[i]=fluidX[i+MotorBrain.N];y[i]=fluidY[i+MotorBrain.N];}
    }
    /**
     * Excluded-volume self-contact between unrelated membrane material
     * points. Adjacent/next-nearest points already have elastic bonds.
     * Conservative symmetric projections preserve momentum locally and
     * never target a centroid, resource, world direction, or gait.
     */
    private void projectSelfContact(Morphology.Surface skin){
        final float min=3.0f, minSq=min*min;
        for(int i=0;i<SURFACE;i++)for(int j=i+4;j<SURFACE;j++){
            if(j-i>=SURFACE-3)continue;
            float dx=skin.x[j]-skin.x[i],dy=skin.y[j]-skin.y[i];
            float d2=dx*dx+dy*dy;
            if(!Float.isFinite(d2)||d2>=minSq)continue;
            float d=(float)Math.sqrt(Math.max(d2,.000001f));
            if(d2<.000001f){
                int next=(i+1)%SURFACE,prev=(i+SURFACE-1)%SURFACE;
                dx=skin.y[next]-skin.y[prev];
                dy=skin.x[prev]-skin.x[next];
                float scale=len(dx,dy);
                if(scale<.0001f){dx=1f;dy=0f;}
                else{dx/=scale;dy/=scale;}
                d=1f;
            }
            float strength=clip((min-d)*.38f,0f,.65f)/d;
            float px=dx*strength,py=dy*strength;
            boolean freeI=skin.gripHold[i]<Morphology.GRIP_HOLD;
            boolean freeJ=skin.gripHold[j]<Morphology.GRIP_HOLD;
            if(freeI&&freeJ){
                skin.x[i]-=px*.5f;skin.y[i]-=py*.5f;
                skin.x[j]+=px*.5f;skin.y[j]+=py*.5f;
            }else if(freeI){
                skin.x[i]-=px;skin.y[i]-=py;
            }else if(freeJ){
                skin.x[j]+=px;skin.y[j]+=py;
            }else continue;
            selfContactPairs++;
        }
    }
    public void step(MotorBrain.Body body,Morphology.Surface skin,float[] cortex,
                     float dt){
        if(body==null||skin==null||cortex==null||
           cortex.length!=SECTORS*OUTPUTS||!Float.isFinite(dt)||dt<=0||dt>.04f)
            throw new IllegalArgumentException("Invalid symbiote physical input");
        for(float v:cortex)if(!Float.isFinite(v)||v<-1.01f||v>1.01f)
            throw new IllegalArgumentException("Non-finite muscle command");
        float previousCumulativeEffort=work;
        lastMuscleReactionImpulse=lastElasticForceMagnitude=0f;
        time+=dt;body.time+=dt;ticks++;
        if(Math.abs(transientGroundBrakeV382)>.000001f)
            transientGroundBrakeV382*=(float)Math.exp(-1.8f*dt);
        // This living tissue, not the inactive legacy membrane motor,
        // owns simulated-time pulse decay in Symbiote mode. New real
        // contacts and storm impulses are added later in this frame.
        skin.t+=dt;
        float pulseDecay=(float)Math.exp(-2.5f*dt);
        for(int i=0;i<SURFACE;i++){
            skin.touchPulse[i]*=pulseDecay;
            skin.weatherPulseV370[i]*=pulseDecay;
        }
        // A proprioceptive sensor must describe THIS frame, never a stale
        // maximum recorded across previous extensions in the same life.
        skin.maxExtension=0f;skin.peakExtension=0f;
        skin.stretchedPoints=0;skin.intentionalPoints=0;
        // Valid snapshots keep their exact material positions. Only a
        // physically crossed saved contour needs localized rehabilitation.
        recoverPersistedCrossings(skin);
        observeBody(body,skin);
        float cx=body.cx,cy=body.cy;
        float contourSign=signedArea(skin)>=0f?1f:-1f;
        float[] physicalNormal=new float[2];
        // Local cortical TENSION may vary floor-normal loading without
        // adding a fifth motor channel or hijacking the object-grip command.
        // Object grip MUST stay strictly disconnected from uncontacted floor.
        float meanTension=0f;
        for(int sector=0;sector<SECTORS;sector++)
            meanTension+=(clip(cortex[sector*OUTPUTS+3],-1f,1f)*.5f+.5f)/SECTORS;
        for(int i=0;i<SURFACE;i++){
            lastSkinX[i]=skin.x[i];lastSkinY[i]=skin.y[i];
            float dx=skin.x[i]-cx,dy=skin.y[i]-cy;
            float rr=len(dx,dy);
            if(rr<.00001f){dx=(float)Math.cos(i*TWO_PI/SURFACE);dy=(float)Math.sin(i*TWO_PI/SURFACE);rr=1;}
            // A centroid ray can point INWARD on the concave bays of a
            // live gel. Apply forces in the actual polygon's outward normal.
            actuatorDirectionAt(skin,i,cx,cy,contourSign,physicalNormal);
            float ux=physicalNormal[0],uy=physicalNormal[1];
            int sector=i*SECTORS/SURFACE;
            float extend=clip(cortex[sector*OUTPUTS],-1,1);
            float lateral=clip(cortex[sector*OUTPUTS+1],-1,1);
            float adhesive=Math.max(0,clip(cortex[sector*OUTPUTS+2],-1,1));
            float stiffness=clip(cortex[sector*OUTPUTS+3],-1,1)*.5f+.5f;
            // Local active cortico-muscular force. Equal reaction is applied
            // to real liquid particles below, not to the centroid.
            // Finite local strain response: a pressurized membrane can extend
            // a new pseudopod, but an ALREADY stretched tip cannot keep
            // accelerating as if its material had infinite spare area.
            // This modifies no neural command, center target or world goal.
            float strain=Math.max(0f,rr/R-1f);
            float extensionCompliance=extend>0f?1f/(1f+4f*strain):1f;
            muscleFx[i]=ux*extend*680f*extensionCompliance-uy*lateral*210f;
            muscleFy[i]=uy*extend*680f*extensionCompliance+ux*lateral*210f;
            fx[i]=muscleFx[i];fy[i]=muscleFy[i];
            // Neighbour smoothing = surface tension; stiffness modulates
            // corticoelastic relaxation without a fixed radial spring.
            int p=(i+SURFACE-1)%SURFACE,n=(i+1)%SURFACE;
            fx[i]+=(skin.x[p]+skin.x[n]-2*skin.x[i])*(85f+stiffness*190f);
            fy[i]+=(skin.y[p]+skin.y[n]-2*skin.y[i])*(85f+stiffness*190f);
            lastElasticForceMagnitude+=len(fx[i]-muscleFx[i],fy[i]-muscleFy[i]);
            lastMuscleReactionImpulse+=len(muscleFx[i],muscleFy[i])*dt;
            // Grip command is an INTENTION. Real box/door contact solver owns
            // the hold timer. No compulsory automatic adhesion.
            skin.gripCommand[i]=adhesive;
            skin.extension[i]=Math.max(0,extend)*Morphology.MAX_REACH;
            skin.maxExtension=Math.max(skin.maxExtension,skin.extension[i]);
            if(adhesive>Morphology.GRIP_THRESHOLD)skin.intentionalPoints++;
            skin.totalEffort+=(Math.abs(extend)+.25f*Math.abs(lateral))*.0002f*dt;
            // Independent contact with the floor: body-side normal loading
            // comes from the cortex's voluntary outward pressure, not from
            // object-grip intention. OTA146 incorrectly assigned EVERY patch
            // 50% floor anchoring even at rest and also mapped the global
            // high-bank object grip onto ground friction. This could lock all
            // 96 sites while the newborn was trying to deform its body.
            // Viscous drag remains nonzero, but the substrate can support
            // reaction only where living material physically presses into it.
            // No food vector, desired travel, oscillator or reward is added.
            // Passive material still touches the substrate and needs a
            // modest frictional reference for independent pseudopods.
            // Local compression increases anchoring; extending cortex can
            // slide past it. Object-grip intention NEVER anchors open floor.
            float compression=Math.max(0f,-extend);
            // Original OTA160 floor traction is unchanged. The physics
            // fix is excluded-volume self-contact, not weaker footholds.
            // A stiffer-than-average cortex patch bears more substrate
            // loading; a softer patch can slip. This control is independent
            // of the radial contraction and never uses object-grip intent.
            float traction=substrateTractionWithBaselineV363(extend,stiffness,meanTension,
                passiveFloorBaselineV363+transientGroundBrakeV382);
            float drag=(float)Math.exp(-(1.8f+66f*traction)*dt);
            float preX=skin.x[i],preY=skin.y[i];
            float appliedVx=skin.vx[i]+fx[i]*dt;
            float appliedVy=skin.vy[i]+fy[i]*dt;
            // OTA164: isolated strong pseudopod retraction needs radial
            // mobility. The original >R+32 gate never engaged on real ~43U
            // extents. Release radial drag progressively from R+9 to R+17
            // ONLY under strong (> .83) contraction of unheld material. Existing
            // tangential traction is untouched; solid obstacle holds stay fixed.
            float release=radialRetractionRelease(rr,extend,
                skin.gripHold[i]>=Morphology.GRIP_HOLD);
            if(release>0f){
                float radial=appliedVx*ux+appliedVy*uy;
                float tangent=-appliedVx*uy+appliedVy*ux;
                float radialDrag=radialRetractionDrag(traction,release,dt);
                skin.vx[i]=clip(radial*radialDrag*ux-tangent*drag*uy,-140f,140f);
                skin.vy[i]=clip(radial*radialDrag*uy+tangent*drag*ux,-140f,140f);
            }else{
                skin.vx[i]=clip(appliedVx*drag,-140f,140f);
                skin.vy[i]=clip(appliedVy*drag,-140f,140f);
            }
            float freeX=preX+skin.vx[i]*dt,freeY=preY+skin.vy[i]*dt;
            // Real pre-constraint cortical target, later compared with
            // achieved geometry AFTER density, shape and world collisions.
            skin.symbioteMotorTarget(i,preX,preY,ux,uy,freeX,freeY);
            skin.x[i]=freeX;skin.y[i]=freeY;
            work+=(Math.abs(extend)+.25f*Math.abs(lateral)+adhesive*.1f)*dt*.0002f;
        }
        // Real opposite impulse distributed by proximity of membrane patches
        // to actual fluid material, respecting momentum (before substrate
        // damping), rather than pulling against an infinite-mass ghost core.
        final int n=INNER+MotorBrain.N;
        for(int i=0;i<MotorBrain.N;i++){
            lastBodyX[i]=body.x[i];lastBodyY[i]=body.y[i];
            fluidX[i]=body.x[i];fluidY[i]=body.y[i];
            fluidVx[i]=body.vx[i];fluidVy[i]=body.vy[i];
        }
        for(int i=0;i<INNER;i++){
            lastX[i]=x[i];lastY[i]=y[i];
            fluidX[i+MotorBrain.N]=x[i];fluidY[i+MotorBrain.N]=y[i];
            fluidVx[i+MotorBrain.N]=vx[i];fluidVy[i+MotorBrain.N]=vy[i];
        }
        Arrays.fill(impulsesX,0f);Arrays.fill(impulsesY,0f);
        for(int i=0;i<SURFACE;i++){
            float normalizer=0;
            for(int j=0;j<n;j++){
                float d=len(fluidX[j]-lastSkinX[i],fluidY[j]-lastSkinY[i]);
                if(d<24f)normalizer+=1f/(3f+d);
            }
            if(normalizer<.0001f){
                longTipReactionFallbacks++;
                // A stretched tip may be >24 U from EVERY interior parcel.
                // Skipping reaction makes a ghost muscle. Use the 3 closest
                // REAL particles for the equal/opposite impulse instead.
                int a=-1,b=-1,c=-1;
                float da=Float.MAX_VALUE,db=Float.MAX_VALUE,dc=Float.MAX_VALUE;
                for(int j=0;j<n;j++){
                    float d=len(fluidX[j]-lastSkinX[i],fluidY[j]-lastSkinY[i]);
                    if(d<da){c=b;dc=db;b=a;db=da;a=j;da=d;}
                    else if(d<db){c=b;dc=db;b=j;db=d;}
                    else if(d<dc){c=j;dc=d;}
                }
                float wa=1f/(3f+da),wb=1f/(3f+db),wc=1f/(3f+dc);
                float norm=wa+wb+wc;
                if(a>=0){impulsesX[a]-=muscleFx[i]*dt*(wa/norm)*n/SURFACE;
                         impulsesY[a]-=muscleFy[i]*dt*(wa/norm)*n/SURFACE;}
                if(b>=0){impulsesX[b]-=muscleFx[i]*dt*(wb/norm)*n/SURFACE;
                         impulsesY[b]-=muscleFy[i]*dt*(wb/norm)*n/SURFACE;}
                if(c>=0){impulsesX[c]-=muscleFx[i]*dt*(wc/norm)*n/SURFACE;
                         impulsesY[c]-=muscleFy[i]*dt*(wc/norm)*n/SURFACE;}
                continue;
            }
            for(int j=0;j<n;j++){
                float d=len(fluidX[j]-lastSkinX[i],fluidY[j]-lastSkinY[i]);
                if(d>=24f)continue;
                float share=(1f/(3f+d))/normalizer;
                // Weighted parcel masses: the thin cortex has total mass 1,
                // and the interior parcels also sum to mass 1. Convert a
                // patch's force into equal/opposite parcel acceleration.
                // This is not an impulse absorbed by a fictional centroid.
                impulsesX[j]-=muscleFx[i]*dt*share*n/SURFACE;
                impulsesY[j]-=muscleFy[i]*dt*share*n/SURFACE;
            }
        }
        for(int j=0;j<n;j++){
            // Damping through real material rather than position teleport.
            float drag=(float)Math.exp(-2.3f*dt);
            fluidVx[j]=clip((fluidVx[j]+impulsesX[j])*drag,-140f,140f);
            fluidVy[j]=clip((fluidVy[j]+impulsesY[j])*drag,-140f,140f);
        }
        // The liquid must also exchange shear with its real CORTEX, not
        // only with itself. Without this, strong opposite spin can build
        // up in the mobile inner core while the substrate slows the shell.
        exchangeCortexFluidShear(skin,fluidX,fluidY,fluidVx,fluidVy,dt);
        // XSPH viscosity exchanges velocity between neighbouring *real*
        // parcels. Pairwise impulses sum to zero: fluid shear diffuses,
        // rather than each particle behaving like an independent rubber ball.
        Arrays.fill(impulsesX,0);Arrays.fill(impulsesY,0);
        for(int i=0;i<n;i++)for(int j=i+1;j<n;j++){
            float dx=fluidX[j]-fluidX[i],dy=fluidY[j]-fluidY[i];
            float dd=dx*dx+dy*dy;
            if(dd>=196f)continue; // 14-unit material neighborhood
            float viscosity=.22f*dt*(1f-dd/196f);
            float ux=(fluidVx[j]-fluidVx[i])*viscosity;
            float uy=(fluidVy[j]-fluidVy[i])*viscosity;
            impulsesX[i]+=ux;impulsesY[i]+=uy;
            impulsesX[j]-=ux;impulsesY[j]-=uy;
        }
        for(int j=0;j<n;j++){
            fluidVx[j]=clip(fluidVx[j]+impulsesX[j],-140f,140f);
            fluidVy[j]=clip(fluidVy[j]+impulsesY[j],-140f,140f);
            fluidX[j]+=fluidVx[j]*dt;fluidY[j]+=fluidVy[j]*dt;
        }
        for(int i=0;i<MotorBrain.N;i++){
            body.x[i]=fluidX[i];body.y[i]=fluidY[i];
        }
        for(int i=0;i<INNER;i++){x[i]=fluidX[i+MotorBrain.N];y[i]=fluidY[i+MotorBrain.N];}
        // Bounded XPBD-like projections: cortex arc-length/tension,
        // quasi-incompressible area, and local density in the same iteration.
        for(int iter=0;iter<3;iter++){
            // A soft cortex can stretch locally; it cannot tear unboundedly.
            for(int i=0;i<SURFACE;i++){
                int j=(i+1)%SURFACE;
                float dx=skin.x[j]-skin.x[i],dy=skin.y[j]-skin.y[i];
                float d=len(dx,dy);
                if(d<.00001f)continue;
                float span=2f*(float)Math.PI*R/SURFACE;
                float limit=span*2.8f;
                float over=d-limit;
                if(over>0){
                    float correction=clip(over*.24f,0,1.2f)/d;
                    if(skin.gripHold[i]<Morphology.GRIP_HOLD){
                        skin.x[i]+=dx*correction;skin.y[i]+=dy*correction;
                    }
                    if(skin.gripHold[j]<Morphology.GRIP_HOLD){
                        skin.x[j]-=dx*correction;skin.y[j]-=dy*correction;
                    }
                }
            }
            projectArea(skin,dt);
            projectInterior(body,skin);
            // Prevent non-neighbor living cortex sites from interpenetrating
            // BEFORE expensive emergency edge rollback can lock the tissue.
            projectSelfContact(skin);
        }
        repairTopology(skin);
        // Finite-difference post-constraint velocities include real reaction.
        for(int i=0;i<SURFACE;i++){
            skin.vx[i]=clip((skin.x[i]-lastSkinX[i])/dt,-180f,180f);
            skin.vy[i]=clip((skin.y[i]-lastSkinY[i])/dt,-180f,180f);
        }
        for(int i=0;i<MotorBrain.N;i++){
            body.vx[i]=clip((body.x[i]-lastBodyX[i])/dt,-180f,180f);
            body.vy[i]=clip((body.y[i]-lastBodyY[i])/dt,-180f,180f);
        }
        for(int i=0;i<INNER;i++){
            vx[i]=clip((x[i]-lastX[i])/dt,-180f,180f);
            vy[i]=clip((y[i]-lastY[i])/dt,-180f,180f);
        }
        observeBody(body,skin);
        // Achieved shape measurement distinct from requested extension.
        // The old Surface.step no longer runs in this physics mode; the
        // primary 26D sense channel 3 and EST HUD need real material data.
        for(int i=0;i<SURFACE;i++){
            float achieved=Math.max(0f,len(skin.x[i]-body.cx,
                skin.y[i]-body.cy)-R);
            skin.peakExtension=Math.max(skin.peakExtension,achieved);
            if(achieved>22f)skin.stretchedPoints++;
        }
        areaError=areaRelativeError(skin);
        float reach=0;
        for(int i=0;i<SURFACE;i++){
            float d=len(skin.x[i]-body.cx,skin.y[i]-body.cy);
            reach=Math.max(reach,d);
            if(!Float.isFinite(d)||d>400f){invalid++;break;}
        }
        for(int i=0;i<INNER;i++)
            if(!Float.isFinite(x[i])||!Float.isFinite(y[i])){invalid++;break;}
        maxReach=Math.max(maxReach,reach);
        // 'work' is already a lifetime integral of commands * dt. Never
        // integrate that cumulative value a second time into body effort.
        // GameActivity's metabolic fuel debit remains based on the same
        // per-step delta and is not altered by this correction.
        body.totalWork+=work-previousCumulativeEffort;
    }
    public String summary(){
        return String.format(Locale.US,"SYMBIOTE V1 matter64 cortex96 areaError=%.3f maxReach=%.1f ticks=%d invalid=%d",areaError,maxReach,ticks,invalid);
    }
}
