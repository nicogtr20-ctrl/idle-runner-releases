package com.overmind.zero;

import java.util.Arrays;

/** OTA162: real-matter locomotion feedback, regional-only plasticity and atomic restoration. */
public final class Zero162CoordinationRegression {
    private static void ok(boolean t,String reason){
        if(!t)throw new AssertionError("ZERO162 coordination: "+reason);
    }
    private static float[] gaitTrial(int tick){
        // Laboratory electrode program from 161; ONLY in the JVM regression.
        float[] action=new float[64];
        boolean phase=(tick/20)%2==0;
        for(int sector=0;sector<16;sector++){
            boolean forward=Math.cos((sector+.5)*Math.PI/8)>0;
            action[4*sector]=forward?(phase?.78f:-.78f):(phase?-.43f:.43f);
            action[4*sector+2]=forward?(phase?-.95f:.95f):(phase?.95f:-.95f);
        }
        return action;
    }
    private static float difference(float[] a,float[] b){
        float s=0;
        for(int i=0;i<a.length;i++)s+=Math.abs(a[i]-b[i]);
        return s;
    }
    public static void main(String[] args){
        SymbioteCoordinationV162 b=new SymbioteCoordinationV162();
        ok(b.frames()==0&&b.intervals()==0&&b.trainedIntervals()==0,
            "newborn has invented locomotion evidence");
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        float[] angles=SymbioteMotorHeadV1.measuredSectorAngles(body,skin);
        b.begin(0,angles);
        float netStartX=body.cx,netStartY=body.cy;
        for(int i=0;i<640;i++){
            float beforeX=body.cx,beforeY=body.cy;
            float[] field=gaitTrial(i);
            gel.step(body,skin,field,.025f);
            b.sample(beforeX,beforeY,body.cx,body.cy,field);
        }
        ok(gel.invalid==0&&MaterialGeometry.selfCrossings(skin)==0,
            "physical lab produced broken matter");
        ok(b.frames()>500,"frame telemetry did not observe living physics");
        float net=(float)Math.hypot(body.cx-netStartX,body.cy-netStartY);
        ok(net>5f,"laboratory does not independently prove real movement");
        float[] local=new float[320];
        float[] features=new float[320];
        float[] noise=new float[64];
        float[] output=new float[64];
        for(int i=0;i<64;i++){
            features[i*5]=1f;
            noise[i]=i%2==0?.32f:-.24f;
            output[i]=.35f;
        }
        b.consequence(true,false,noise,output,features,local,4,5);
        ok(b.intervals()==1&&b.trainedIntervals()==1,
            "trained event not counted from real motion");
        ok(difference(local,new float[320])>.00001f,
            "regional inverse motor plasticity is still negligible/no-op");
        for(int sector=0;sector<16;sector++)
            for(int f=0;f<5;f++)
                ok(local[(sector*4+2)*5+f]==0f,
                    "movement credit incorrectly changed voluntary grip channel");
        String first=b.encode();
        SymbioteCoordinationV162 reloaded=new SymbioteCoordinationV162();
        ok(reloaded.restore(first)&&reloaded.encode().equals(first),
            "saved coordination cannot survive app restart");
        ok(!reloaded.restore(first.replace("co162|","co162|-3|"))&&
            reloaded.encode().equals(first),"malformed snapshot overwrote memory");
        // A holdout may observe and count REAL movement, but never update
        // either local parameters or the tested policy baselines.
        b.begin(0,angles);
        b.sample(0,0,.7f,.2f,output);
        float[] saved=local.clone();
        float baseline=b.actionBaseline(0);
        b.consequence(false,true,noise,output,features,local,4,5);
        ok(difference(local,saved)==0f&&b.actionBaseline(0)==baseline,
            "heldout silently rewrote regional synapses");
        ok(b.heldoutIntervals()==1,"heldout observation not counted");
        b.begin(8,angles);b.sample(0,0,1.1f,0,output);
        b.consequence(true,false,noise,output,features,local,4,5);
        ok(difference(local,saved)==0f,
            "grip action was incorrectly coerced to translate");
        b.begin(1,angles);b.sample(0,0,120f,120f,output);
        b.consequence(true,false,noise,output,features,local,4,5);
        ok(difference(local,saved)==0f,
            "teleport/collision correction contaminated motor credit");
        b.resetLife();
        ok(b.trainedIntervals()==1&&b.actionSamples(0)==1,
            "new life destroyed the learned inverse motor model");
        System.out.println("ZERO162 COORDINATION PASS: "+b.frames()+
            " living physical steps, net="+net+
            ", regional plasticity="+difference(local,new float[320])+
            ", cancellation="+b.cancellation()+
            ", heldout, grip, anomaly, save and reset");
    }
}
