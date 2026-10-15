package com.overmind.zero;

import java.lang.reflect.Field;

/**
 * Regression on the actual motor head: tissue rotation/deformation must
 * change the world-oriented meaning of a chosen action, without rotating
 * the world target, changing old weights, or adding a gait/goal/reward.
 */
public final class SymbioteHeadingGeometryRegression {
    private static void need(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    private static float wrap(float a){
        return (float)Math.atan2(Math.sin(a),Math.cos(a));
    }
    private static float[] privateFeature(SymbioteMotorHeadV1 head)throws Exception{
        Field field=SymbioteMotorHeadV1.class.getDeclaredField("feature");
        field.setAccessible(true);
        return ((float[])field.get(head)).clone();
    }
    private static float difference(float[] a,float[] b){
        float sum=0;for(int i=0;i<a.length;i++)sum+=Math.abs(a[i]-b[i]);return sum;
    }
    public static void main(String[] args)throws Exception{
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        final float[] input=new float[DistributedBodySense.FEATURES],
            recurrent=new float[RecurrentCoreV22.Z];
        SymbioteMotorHeadV1 motor=new SymbioteMotorHeadV1();
        final float[] initial=SymbioteMotorHeadV1.measuredSectorAngles(body,skin);
        need(initial.length==16,"missing 16 separate physical cortex sectors");
        need(Math.abs(wrap(initial[0]-(float)Math.PI/16f))<.05f,
            "newborn material azimuth should match its true initial ring");
        // Chosen action=0 conventionally references world +X. No learned
        // steering/destination/energy object is passed to the motor.
        motor.chosen(0,input,recurrent,0f,null,initial);
        float[] features0=privateFeature(motor),oldMove=motor.commands().clone();
        String saveBefore=motor.encode();
        // Rotate the ENTIRE REAL membrane around the SAME mass centre 90°.
        for(int i=0;i<Morphology.POINTS;i++){
            float x=skin.x[i]-body.cx,y=skin.y[i]-body.cy;
            skin.x[i]=body.cx-y;skin.y[i]=body.cy+x;
        }
        float[] rotated=SymbioteMotorHeadV1.measuredSectorAngles(body,skin);
        for(int i=0;i<16;i++)
            need(Math.abs(wrap(rotated[i]-initial[i]-(float)Math.PI/2))<.05f,
                "true world-body orientation does not follow rotating fluid");
        motor.chosen(0,input,recurrent,0f,null,rotated);
        float[] features90=privateFeature(motor),newMove=motor.commands().clone();
        for(int sector=0;sector<16;sector++){
            int k=sector*21;
            need(Math.abs(features90[k+9]-(float)Math.cos(rotated[sector]))<.0001f,
                "cortex action heading still uses frozen birth-angle index");
            need(Math.abs(features90[k+10]-(float)Math.sin(rotated[sector]))<.0001f,
                "cortex action angular phase disconnected from live tissue");
        }
        need(difference(features0,features90)>2f,
            "physical rotation never reached actual chosen muscle inputs");
        need(difference(oldMove,newMove)>.00001f,
            "same world heading incorrectly yields identical motor outputs after physical rotation");
        need(motor.encode().split("\\|").length==90,
            "existing motor weight save format changed");
        // The 84 trained parameters and baseline are IDENTICAL; only the
        // temporary sensor inputs change, like any true proprioceptive signal.
        String[] sa=saveBefore.split("\\|"),sb=motor.encode().split("\\|");
        need(sa.length==sb.length,"motor save width changed unexpectedly");
        for(int i=5;i<sa.length;i++)
            need(sa[i].equals(sb[i]),"cortex learned weights changed during sensing");

        // A stretched body no longer has sector angles implied by index.
        for(int i=0;i<6;i++){skin.x[i]+=22f;skin.y[i]+=8f;}
        float[] deformed=SymbioteMotorHeadV1.measuredSectorAngles(body,skin);
        need(Math.abs(wrap(deformed[0]-rotated[0]))>.04f,
            "local pseudopod extension ignored by sector orientation");
        motor.chosen(6,input,recurrent,0f,null,deformed);
        float[] again=privateFeature(motor);
        need(Math.abs(again[9]-(float)Math.cos(deformed[0]-6*Math.PI/4))<.0001f,
            "chosen world action 6 still not registered to real sector 0");

        // Diagnostic-only comparison: ORIGINAL uncorrected actuator readout
        // receives null angles, stays fixed even though body rotated.
        motor.chosen(0,input,recurrent,0f,null,null);
        float[] oldNominal=privateFeature(motor);
        need(Math.abs(oldNominal[9]-features0[9])<.05f,
            "legacy angular reference test lost expected null fallback");
        System.out.println("PASS OTA153: current 16-sector world azimuth, rigid rotation, live deformation, world action coupling and all 84 saved weights unchanged");
    }
}
