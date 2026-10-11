package com.overmind.zero;

import java.util.Random;

/**
 * Passive cohesive material mechanics, NOT a neuron, action, reward or
 * scripted locomotion. The nucleus is a mass attached to the ten body nodes;
 * each of the 96 membrane nodes is elastically attached to the SAME actual
 * interpolated body ring. Old separated bodies are repaired gradually.
 *
 * Every displacement is limited per physics substep. Collision solvers still
 * own the final no-penetration decisions. No new contact or grip is created.
 */
public final class BodyIntegrity {
    private BodyIntegrity(){}
    public static final float MIN_CORE_SPOKE=12f;
    public static final float MAX_SHELL_ANCHOR_STRAIN=16f;
    private static final float MAX_CORE_CORRECTION=1.8f;
    private static final float MAX_SHELL_CORRECTION=1.9f;
    private static final float TWO_PI=(float)(Math.PI*2);
    private static float limited(float value,float maximum){
        return Math.max(-maximum,Math.min(maximum,value));
    }
    private static boolean finite(float a,float b){
        return Float.isFinite(a)&&Float.isFinite(b);
    }

    /** Soft nucleus-to-muscle connective tissue, integrated with the
     * already existing ten actual ring masses. Intact muscle identity and
     * free contraction/extension remain unchanged.
     */
    public static void core(MotorBrain.Body body,float dt){
        if(body==null||!Float.isFinite(dt)||dt<=0)return;
        float sx=0,sy=0;
        for(int i=0;i<MotorBrain.N;i++){sx+=body.x[i];sy+=body.y[i];}
        float mx=sx/MotorBrain.N,my=sy/MotorBrain.N;
        if(!finite(mx,my)||!finite(body.cx,body.cy))return;
        float dx=mx-body.cx,dy=my-body.cy;
        float distance=(float)Math.hypot(dx,dy);
        // Material response damps core drift. It does not seek any world
        // position or destination and does not modify the neural commands.
        if(distance>.3f){
            float step=Math.min(MAX_CORE_CORRECTION,distance*.09f);
            float cx=dx/distance*step,cy=dy/distance*step;
            body.cx+=cx;body.cy+=cy;
            body.cvx=limited(body.cvx+cx*dt*55f,280);
            body.cvy=limited(body.cvy+cy*dt*55f,280);
        }
        // Body node angular ordering is an actual material property of the
        // closed ten-node ring. Prevent all ten nodes from migrating to one
        // side of the nucleus; this is symmetric over the whole body and
        // does not create preferred "arms" or a scripted walking direction.
        for(int i=0;i<MotorBrain.N;i++){
            float angle=TWO_PI*i/MotorBrain.N;
            float ux=(float)Math.cos(angle),uy=(float)Math.sin(angle);
            float rx=body.x[i]-body.cx,ry=body.y[i]-body.cy;
            float outward=rx*ux+ry*uy;
            if(outward>=MIN_CORE_SPOKE)continue;
            float correction=Math.min(1.8f,(MIN_CORE_SPOKE-outward)*.24f);
            body.x[i]+=ux*correction;body.y[i]+=uy*correction;
            // The existing solid collision solvers have the final word.
            float vn=body.vx[i]*ux+body.vy[i]*uy;
            if(vn<0){body.vx[i]-=vn*ux*.6f;body.vy[i]-=vn*uy*.6f;}
        }
    }

    /** The same actual interpolation anchors used by Morphology.Surface.
     * Bonds only relax excess strain where there is NO real physical hold
     * or solid contact. This is NOT a visual reposition or fake traction.
     */
    public static void membrane(MotorBrain.Body body,Morphology.Surface skin,float dt){
        if(body==null||skin==null||!Float.isFinite(dt)||dt<=0)return;
        for(int i=0;i<Morphology.POINTS;i++){
            // Do not pull an adhesive contact through a wall or box.
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD||
               skin.touchSense[i]>.5f||skin.touchPulse[i]>.35f)continue;
            float u=i*(float)MotorBrain.N/Morphology.POINTS;
            int j=(int)u;
            float f=u-j;
            int next=(j+1)%MotorBrain.N;
            float ax=body.x[j]*(1-f)+body.x[next]*f;
            float ay=body.y[j]*(1-f)+body.y[next]*f;
            float dx=ax-skin.x[i],dy=ay-skin.y[i];
            float distance=(float)Math.hypot(dx,dy);
            // Intentional neural extensions and lateral reach remain free.
            float allowed=Math.max(MAX_SHELL_ANCHOR_STRAIN,skin.extension[i]+32f);
            if(!Float.isFinite(distance)||distance<=allowed)continue;
            float shift=Math.min(MAX_SHELL_CORRECTION,
                (distance-allowed)*.12f);
            float sx=dx/distance*shift,sy=dy/distance*shift;
            skin.x[i]+=sx;skin.y[i]+=sy;
            skin.vx[i]=limited(skin.vx[i]+sx*dt*35f,230f);
            skin.vy[i]=limited(skin.vy[i]+sy*dt*35f,230f);
        }
    }

    /** Odd/even crossings against the ACTUAL polygon vertices. This is
     * diagnostics only: it never returns an artificial success/reward. */
    public static boolean encloses(float[] xx,float[] yy,float cx,float cy){
        if(xx==null||yy==null||xx.length!=yy.length||xx.length<3||
           !finite(cx,cy))return false;
        boolean inside=false;
        for(int i=0,j=xx.length-1;i<xx.length;j=i++){
            float xi=xx[i],yi=yy[i],xj=xx[j],yj=yy[j];
            if(!finite(xi,yi)||!finite(xj,yj))return false;
            if((yi>cy)!=(yj>cy) &&
               cx<(xj-xi)*(cy-yi)/(yj-yi)+xi)inside=!inside;
        }
        return inside;
    }
    public static boolean innerEncloses(MotorBrain.Body b){
        return encloses(b.x,b.y,b.cx,b.cy);
    }
    public static boolean outerEncloses(MotorBrain.Body b,Morphology.Surface s){
        return encloses(s.x,s.y,b.cx,b.cy);
    }
    private static void expect(boolean value,String message){
        if(!value)throw new AssertionError(message);
    }
    public static void main(String[] args){
        Random random=new Random(119090);
        int outsideInner=0,outsideOuter=0,frames=0;
        for(int run=0;run<7;run++){
            MotorBrain.Body body=new MotorBrain.Body();
            Morphology.Surface skin=new Morphology.Surface(body);
            float[] motor=MotorBrain.virgin(new Random(810+run));
            float[] membrane=Morphology.virgin(new Random(610+run));
            for(int i=0;i<850;i++){
                float a=(float)(Math.sin((i+run*83)*.015)*Math.PI);
                float ux=(float)Math.cos(a),uy=(float)Math.sin(a);
                float tx=body.cx+145*ux,ty=body.cy+145*uy;
                body.step(motor,ux,uy,.025f);
                skin.step(body,membrane,tx,ty,.025f);
                if(i>150){
                    if(!innerEncloses(body))outsideInner++;
                    if(!outerEncloses(body,skin))outsideOuter++;
                    frames++;
                }
            }
        }
        System.out.println("Body integrity long trials: frames="+frames+
            " innerOutside="+outsideInner+" outerOutside="+outsideOuter);
        expect(outsideInner<frames/20,"Core too frequently escapes muscle ring");
        expect(outsideOuter<frames/20,"Core too frequently escapes physical membrane");
        // A previously separated life must progressively recover without
        // rewriting ANY saved brain, surface command or adhesive force.
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        for(int i=0;i<MotorBrain.N;i++)body.x[i]+=35;
        for(int i=0;i<Morphology.POINTS;i++)skin.x[i]+=57;
        expect(!innerEncloses(body)&&!outerEncloses(body,skin),
            "Fixture should begin with detached core");
        float[] motor=MotorBrain.virgin(new Random(7));
        float[] morph=Morphology.virgin(new Random(9));
        int first=-1;
        for(int i=0;i<450;i++){
            body.step(motor,0,0,.025f);
            skin.step(body,morph,body.cx,body.cy,.025f);
            if(innerEncloses(body)&&outerEncloses(body,skin)&&first<0)first=i;
        }
        expect(first>=0,"Detached saved body never regained containment");
        System.out.println("Legacy detached body self-repaired by step "+first);
    }
}
