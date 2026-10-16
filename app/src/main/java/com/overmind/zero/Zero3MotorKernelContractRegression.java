package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO3: the extracted motor response kernel must be identical to the
 * physically validated laboratory math, read-only and untrusted before
 * independent calibration. Intent only originates in the lab here.
 */
public final class Zero3MotorKernelContractRegression {
    static ArrayList<Zero3MotorResponseKernel.PhysicalSample> convert(
        ArrayList<Zero3MotorImpulseResponseRegression.Trial> trials,long episode){
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> out=new ArrayList<>();
        for(int i=0;i<trials.size();i++){
            Zero3MotorImpulseResponseRegression.Trial t=trials.get(i);
            out.add(new Zero3MotorResponseKernel.PhysicalSample(
                t.features,t.early,t.late,t.extraWork,episode,i,72,false));
        }
        return out;
    }
    static void check(boolean a,String reason){
        if(!a)throw new AssertionError("ZERO3 RESPONSE KERNEL "+reason);
    }
    public static void main(String[] args){
        float[] trained={0,(float)(Math.PI/2)};
        float[] calibration={(float)(Math.PI/8),(float)(-Math.PI/8)};
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> train=convert(
            Zero3MotorDynamicResponseRegression.sample(trained,new int[]{0}),10);
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> calib=convert(
            Zero3MotorDynamicResponseRegression.sample(calibration,new int[]{0}),11);
        Zero3MotorResponseKernel.Predictor model=
            new Zero3MotorResponseKernel.Predictor(train,.5);
        check(!model.isLabCalibrated(),"pretrained without independent validation");
        boolean rejectedLeak=false;
        try{model.calibrate(train);}catch(IllegalArgumentException expected){
            rejectedLeak=true;
        }
        check(rejectedLeak,"accepted calibration on the very same training transitions");
        int[] allowed={0,4,8,12};
        check(model.suggest(1,0,.2f,allowed,.005)==null,
            "uncalibrated model proposed muscle command");
        double[] before=model.parameterSnapshot();
        check(model.calibrate(calib),"model failed known independent calibration");
        boolean rejectedRetune=false;
        try{model.calibrate(calib);}catch(IllegalStateException expected){
            rejectedRetune=true;
        }
        check(rejectedRetune,"allowed repeated selection on calibration outcomes");
        check(Arrays.equals(before,model.parameterSnapshot()),
            "calibration illegally fitted synapses");
        Zero3MotorImpulseResponseRegression.Scenario virgin=
            new Zero3MotorImpulseResponseRegression.Scenario(0,0);
        float anatomy=Zero3MotorResponseKernel.membraneAxis(virgin.body,virgin.skin);
        int improved=0,costBounded=0;
        double signed=0,incrementalCost=0;
        for(int dir=0;dir<4;dir++)for(float rotation:new float[]{
                (float)(Math.PI/4),(float)(-Math.PI/4)})
            for(int precondition:new int[]{1,2,3}){
                Zero3MotorImpulseResponseRegression.Scenario starting=
                    new Zero3MotorImpulseResponseRegression.Scenario(
                        dir,rotation,precondition);
                float pose=Zero3MotorResponseKernel.relativePose(
                    Zero3MotorResponseKernel.membraneAxis(starting.body,starting.skin),
                    anatomy);
                check(Math.abs(pose-starting.observedPose)<.001,
                    "pose not measured from actual morphology");
                float dx=dir==0?1:dir==2?-1:0;
                float dy=dir==1?1:dir==3?-1:0;
                Zero3MotorResponseKernel.Suggestion proposed=
                    model.suggest(dx,dy,pose,allowed,.005);
                if(proposed==null){
                    System.out.printf(Locale.US,
                        "ZERO3 MOTOR KERNEL caseDir=%d pose=%.4f precondition=%d NO_PULSE%n",
                        dir,pose,precondition);
                    continue;
                }
                Zero3MotorImpulseResponseRegression.Trial measured=
                    Zero3MotorImpulseResponseRegression.physical(
                        dir,rotation,proposed.sector,proposed.channel,
                        proposed.sign,precondition);
                if(measured.signedResponse()>0)improved++;
                if(measured.extraWork<.005)costBounded++;
                signed+=measured.signedResponse();
                incrementalCost+=measured.extraWork;
                float[] efference=new float[64];
                Zero3MotorResponseKernel.applyLocalPulse(efference,proposed,.2f);
                int nonzero=0;
                for(float v:efference){
                    check(Float.isFinite(v)&&Math.abs(v)<=1,
                        "pulse exceeds actuator physical bounds");
                    if(v!=0)nonzero++;
                }
                check(nonzero==5,"pulse touched other than five neighboring sectors");
            }
        check(Arrays.equals(before,model.parameterSnapshot()),
            "blind read-only evaluation mutated prediction weights");
        check(model.isLabCalibrated(),"lost independent readiness gate");
        boolean refused=false;
        try{
            new Zero3MotorResponseKernel.PhysicalSample(
                new double[42],0,0,0,0,0,0,false);
        }catch(IllegalArgumentException expected){refused=true;}
        check(refused,"accepted fake zero-frame transition");
        System.out.printf(Locale.US,
            "ZERO3 EXTRACTED KERNEL SUMMARY train=%d calib=%d"+
            " blindCases=24 positive=%d/24 withinCost=%d/24"+
            " totalSigned=%.5f extraWork=%.7f immutableWeights=true"+
            " noPrematureExecution=true (LAB ONLY, no on-device cortex)%n",
            train.size(),calib.size(),improved,costBounded,signed,incrementalCost);
    }
}
