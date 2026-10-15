package com.overmind.zero;

import java.lang.reflect.Field;
import java.util.Arrays;

/** Real 2.2 cortex actuator, no fake reward or prescribed gait.
 * Independent laboratory activation is test-only and never in gameplay.
 */
public final class Zero161CortexRegression {
    private static void ok(boolean v,String msg){
        if(!v)throw new AssertionError("ZERO161 regional cortex: "+msg);
    }
    private static float[] local(SymbioteMotorHeadV1 motor)throws Exception {
        Field f=SymbioteMotorHeadV1.class.getDeclaredField("localWeights");
        f.setAccessible(true);
        return (float[])f.get(motor);
    }
    private static float[] features(){return new float[DistributedBodySense.FEATURES];}
    private static float[] recurrent(){
        float[] r=new float[RecurrentCoreV22.Z];r[0]=.7f;return r;
    }
    private static float[] sectorAngles(MotorBrain.Body b,Morphology.Surface s){
        return SymbioteMotorHeadV1.measuredSectorAngles(b,s);
    }
    private static float maxDiff(float[] a,float[] b){
        float d=0;
        for(int i=0;i<a.length;i++)d=Math.max(d,Math.abs(a[i]-b[i]));
        return d;
    }
    public static void main(String[] args)throws Exception {
        SymbioteMotorHeadV1 old=new SymbioteMotorHeadV1();
        String oldState=old.encode();
        ok(old.parameterCount()==84 && old.localParameterCount()==320,
            "shared brain's old weight format changed");
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 matter=new SymbioteMatterV1();
        matter.reset(body,skin);
        float[] a=sectorAngles(body,skin);
        float[] d=features(),z=recurrent();
        old.chosen(0,d,z,0f,null,a);
        float[] original=old.commands().clone();

        SymbioteMotorHeadV1 changed=new SymbioteMotorHeadV1();
        ok(changed.restore(oldState),"old smh1 profile cannot restore");
        float[] neurons=local(changed);
        for(int sector:new int[]{0,5,10})
            neurons[(sector*4)*5]=.50f; // Test-only electrode stimulation.
        changed.chosen(0,d,z,0f,null,a);
        float[] muscle=changed.commands().clone();
        for(int sector:new int[]{0,5,10})
            ok(muscle[sector*4]>original[sector*4]+.35f,
                "cannot independently drive sector "+sector);
        for(int sector=0;sector<16;sector++)if(sector!=0&&sector!=5&&sector!=10)
            ok(Math.abs(muscle[sector*4]-original[sector*4])<.00001f,
                "regional actuation incorrectly bleeds into other sectors");
        ok(changed.encode().equals(oldState),
            "independent regional learning overwrote the old 84 weights");
        String remembered=changed.encodeLocal();
        SymbioteMotorHeadV1 restored=new SymbioteMotorHeadV1();
        ok(restored.restore(oldState)&&restored.restoreLocal(remembered),
            "legacy + regional migration failed");
        ok(restored.encodeLocal().equals(remembered),"local weights lost at reload");
        String[] malformed=remembered.split("\\|",-1);
        malformed[4]="NaN";
        String corrupted=String.join("|",malformed);
        ok(!restored.restoreLocal(corrupted) &&
            restored.encodeLocal().equals(remembered),
            "malformed local snapshot overwrote the working brain");
        changed.resetLife();
        changed.chosen(0,d,z,0f,null,a);
        float[] previous=changed.commands().clone();
        float[] newReadings=d.clone();
        newReadings[DistributedBodySense.SHAPE]=.9f;
        SymbioteMotorHeadV1 fresh=new SymbioteMotorHeadV1();
        ok(fresh.restore(oldState)&&fresh.restoreLocal(changed.encodeLocal()),
            "fresh comparison cortex cannot load");
        fresh.chosen(0,newReadings,z,0f,null,a);
        changed.chosen(0,newReadings,z,0f,null,a);
        for(int i=0;i<64;i++){
            float actual=changed.commands()[i];
            float expected=.55f*previous[i]+.45f*fresh.commands()[i];
            ok(Math.abs(actual-expected)<.0001f,
                "same action does not preserve smooth physical contraction");
        }
        String before=changed.encodeLocal();
        changed.chosen(7,d,z,1f,null,a);
        changed.consequence(22f,0f,0f,0f,false,true);
        ok(changed.encodeLocal().equals(before),
            "heldout accidentally trained regional weights");
        changed.chosen(7,d,z,1f,null,a);
        changed.consequence(22f,0f,0f,0f,true,false);
        ok(!changed.encodeLocal().equals(before),
            "real positive consequence did not train local actuator");
        ok(changed.localTrainedOutcomes()==1,"regional training count incorrect");
        changed.resetLife();
        ok(changed.localTrainedOutcomes()==1,
            "death erased motor learning");
        System.out.println("ZERO161 CORTEX PASS: 320 regional neurons, three independent sector drives, smooth motor action, legacy weights, heldout and persistent plasticity");
    }
}
