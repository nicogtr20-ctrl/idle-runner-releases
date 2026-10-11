package com.overmind.zero;

import java.util.Random;

/**
 * 0120: local cohesion of 96 independent physical perimeter patches.
 * NO physical nucleus exists. The saved body centroid is a read-only
 * measurement of ten real tissue masses, and NEVER receives an impulse.
 * Neural membrane extension and real solid contacts retain priority.
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
    /** Compatibility-only logical coordinate synchronization. This cannot
     * pull an invisible nucleus, position tissue, or apply physical forces.
     */
    @Deprecated public static void core(MotorBrain.Body body,float dt){
        MaterialGeometry.observe(body);
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
        MotorBrain.Body body=new MotorBrain.Body();
        float[] positions=body.x.clone();
        body.cx+=987f;body.cy-=754f;
        core(body,.025f);
        for(int i=0;i<MotorBrain.N;i++)
            expect(body.x[i]==positions[i],
                "Compatibility call moved real material to a ghost center");
        float x=0;for(float v:positions)x+=v/MotorBrain.N;
        expect(Math.abs(body.cx-x)<.001f,
            "Logical center did not derive from physical material");
        System.out.println("Fluid cohesion: no physical nucleus or rigid centering");
    }
}
