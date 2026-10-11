package com.overmind.zero;
import java.util.Random;
import java.util.Locale;

/** Read-only copies of ZERO's actual OTA84 controller coefficients.
 * Evaluate the physics transition; never overwrite a saved brain.
 */
public final class MaterialMotionRegression {
    private static float[] parse(String s){
        String[] p=s.split(",");
        float[] v=new float[p.length];
        for(int i=0;i<v.length;i++)v[i]=Float.parseFloat(p[i]);
        return v;
    }
    private static void trial(String label,float[] motor,float[] hand,float[] morph){
        for(int scenario=0;scenario<3;scenario++){
            MotorBrain.Body b=new MotorBrain.Body();
            Morphology.Surface skin=new Morphology.Surface(b);
            double totalPath=0,active=0;
            float x=b.cx,y=b.cy;
            for(int i=0;i<1900;i++){
                float angle=scenario==0?0f:scenario==1?
                   (float)(Math.PI*.5):(float)Math.sin(i*.009);
                float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
                float cx=b.cx,cy=b.cy;
                b.step(motor,dx,dy,.025f,hand,b.cx+dx*145,b.cy+dy*145);
                skin.step(b,morph,b.cx+dx*145,b.cy+dy*145,.025f,0);
                totalPath+=Math.hypot(b.cx-cx,b.cy-cy);
                if(i>150&&Math.hypot(b.cvx,b.cvy)>.3)active++;
                if(!Float.isFinite(b.cx)||!Float.isFinite(b.cy))
                    throw new AssertionError("Non-finite autonomous physical trajectory");
            }
            float net=(float)Math.hypot(b.cx-x,b.cy-y);
            System.out.printf(Locale.US,"%s scenario %d: path %.2f, net %.2f, active %.1f%%%n",
                label,scenario,totalPath,net,100*active/1750);
            if(totalPath<.5f)
                throw new AssertionError("Controller completely immobilized by distributed physics");
        }
    }
    public static void main(String[] args){
        float[] learnedMotor=parse("-0.10764648,-0.17886794,-0.10514997,0.8709914,-0.12996352,-0.950242,0.22964153,-0.03983822,0.67886114,0.7646977,0.80351794,0.9978763,-0.3125933,0.5400684,0.46343082,0.5030327,0.22636542,-0.8576357,0.24999315,0.14499995,0.4112421,-0.4181873,0.24549147,0.23584814,-0.93510056,0.63699883,0.11688511,-0.34686202,-1.0179267,-0.0491547,0.052653104,0.19940107,0.4933352,-0.17411172,0.2363286,-0.29730627,-0.22119816,-1.8998015,0.42139366,-0.08913972,0.2398163,-0.29699224,0.49671245,-2.4399943,-0.22738677,-0.28140688,-0.15442742,0.08070225,0.20242843,2.3240147,-0.734937,-1.4899213,-0.71991616,-1.7648661,1.8563432,1.8831877,0.38019,-2.2956617,1.0619158,0.0066760033,-0.23905474,-1.2301929");
        float[] learnedHand=parse("-2.630652,-2.4320817,3.5,3.5,0.8651402,-0.2641462,2.993827,0.31696934,0.8372037,-1.9306333,2.1502037,0.92261803,-0.8065683,-3.5,-2.79922,2.833837,-1.4001899,-0.6887417");
        float[] learnedSkin=parse("0.0034255981,-0.94412136,-0.93375885,0.949373,1.3112624,0.20651123,0.15595633,-0.33408526,-0.81143296,0.5478303,0.8186019,-0.23414959,0.5382502,-1.4754575,0.119846836,0.28850812,-1.1025921,1.0649645,0.06630117,0.5976136,-0.53535056,-0.7219256,-0.60918665,2.4090176,0.04687786,0.45851925,-0.9280622,0.7079255,0.805678,-0.93476474,2.3171527,1.4259176,1.4220808,-1.1470349,0.56329155,-1.3286569,1.0064737,0.46597865,0.7573415,1.450494,-1.0640587,0.6119536,-1.3754406,0.16321892,-0.6792501,-0.33751297,-0.368995,-2.0744977,1.3841438,0.41551852,-0.23116027,0.37029564,-0.64905745,0.78168064,-0.86116576,-0.9886429,0.8664515,-1.0552738,-0.7085615,1.1792703,-0.111787006,0.10136029,0.61580575,-1.2140476,0.27168465,0.58915776,1.2914447,0.5688347,-0.44920668,0.51999736,-0.17443712,-0.08974424,-1.7065213,-0.89020705,0.068994224,-3.1511316,3.416465,-0.54066193,2.1101716,0.80496854,-2.2950497,0.80398273");
        if(learnedMotor.length!=MotorBrain.PARAMS||
           learnedHand.length!=ContactLearning.PARAMS||
           learnedSkin.length!=Morphology.PARAMS)
            throw new AssertionError("Original saved coefficient dimensions changed");
        trial("EXISTING learned ZERO (copied)",learnedMotor,learnedHand,learnedSkin);
        trial("VIRGIN neural controllers",MotorBrain.virgin(new Random(23)),
            ContactLearning.virgin(),Morphology.virgin(new Random(25)));
    }
}
