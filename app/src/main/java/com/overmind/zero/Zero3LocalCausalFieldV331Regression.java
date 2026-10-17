package com.overmind.zero;

import java.util.Locale;

/** V331 preregistered 576 real three-world paired causal probes, not gait. */
public final class Zero3LocalCausalFieldV331Regression {
    static final String[] NAMES={"FREE","BOX_NEAR","DISK_NEAR","BOX_FAR"};
    static final int[] CHANNELS={0,1,3};
    static void check(boolean v,String reason){
        if(!v)throw new AssertionError("ZERO3 V331 "+reason);
    }
    static final class Stats{
        int trials,nonzero,counterproductive,physicalContact,physicalGrip,
            delayedLarger,nonLinear,movementIntent;
        double earlyAbs,finalAbs,lateAbs,along,work,nonlinearity,
               lowChannelDifference,maxDifference;
        void record(Zero3LocalCausalFieldV331.Delta d){
            trials++;
            double delta=d.netDifference();
            if(delta>1e-5)nonzero++;
            if(d.finalAlong<0)counterproductive++;
            if(d.contactFrames>0)physicalContact++;
            if(d.gripFrames>0)physicalGrip++;
            if(Math.abs(d.lateAlong)>Math.abs(d.earlyAlong))delayedLarger++;
            if(d.curvature>1e-5)nonLinear++;
            if(d.movementIntent)movementIntent++;
            earlyAbs+=Math.hypot(d.earlyX,d.earlyY);
            finalAbs+=delta;
            lateAbs+=Math.hypot(d.finalX-d.earlyX,d.finalY-d.earlyY);
            along+=d.finalAlong;work+=d.extraWork;
            nonlinearity+=d.curvature;
            if(delta>maxDifference)maxDifference=delta;
        }
        void merge(Stats o){
            trials+=o.trials;nonzero+=o.nonzero;
            counterproductive+=o.counterproductive;
            physicalContact+=o.physicalContact;physicalGrip+=o.physicalGrip;
            delayedLarger+=o.delayedLarger;nonLinear+=o.nonLinear;
            movementIntent+=o.movementIntent;
            earlyAbs+=o.earlyAbs;finalAbs+=o.finalAbs;lateAbs+=o.lateAbs;
            along+=o.along;work+=o.work;nonlinearity+=o.nonlinearity;
            maxDifference=Math.max(maxDifference,o.maxDifference);
        }
        String format(){
            return String.format(Locale.US,
                "n=%d effectAbove1e-5=%d wrongAlongActor=%d "+
                "delayedLarger=%d nonlinear=%d movements=%d "+
                "realContactTriplets=%d realHoldTriplets=%d "+
                "earlyMean=%.9f finalMean=%.9f lateMean=%.9f "+
                "signedAlongTotal=%.9f workPlusMinusTotal=%.9f "+
                "curvatureMean=%.9f maxPhysicalDifference=%.9f",
                trials,nonzero,counterproductive,delayedLarger,nonLinear,
                movementIntent,physicalContact,physicalGrip,
                earlyAbs/Math.max(1,trials),
                finalAbs/Math.max(1,trials),
                lateAbs/Math.max(1,trials),along,work,
                nonlinearity/Math.max(1,trials),maxDifference);
        }
    }
    public static void main(String[] args){
        Stats total=new Stats();
        int familyNonzero=0;
        for(int family=0;family<4;family++){
            Zero3LocalCausalFieldV331.pairedControlDeterminism(family,0);
            Stats fs=new Stats();
            for(int pose=0;pose<6;pose++){
                Stats ps=new Stats();
                for(int sector=0;sector<16;sector+=2){
                    for(int channel:CHANNELS){
                        Zero3LocalCausalFieldV331.Delta d=
                            Zero3LocalCausalFieldV331.compare(
                                family,pose,sector,channel);
                        check(d.family==family&&d.pose==pose&&
                              d.sector==sector&&d.channel==channel,
                              "wrong causal sector/channel ownership");
                        ps.record(d);fs.record(d);
                    }
                }
                System.out.println("ZERO3 V331 POSE family="+NAMES[family]+
                    " ordinal="+pose+" "+ps.format());
            }
            check(fs.trials==6*8*3,"missing physical interventions");
            if(fs.nonzero>0)familyNonzero++;
            total.merge(fs);
            System.out.println("ZERO3 V331 FAMILY="+NAMES[family]+
                " "+fs.format());
        }
        check(total.trials==576&&total.movementIntent>0,
              "benchmark did not finish actor-directed three-arm interventions");
        boolean detectable=total.nonzero>=288;
        System.out.println("ZERO3 V331 SUMMARY matchedTriplets="+total.trials+
            " trueGelSolverSubsteps="+(total.trials*3*36+4*2*36)+
            " physicalResponseFamilies="+familyNonzero+
            " scientificPhysicalSensitivity="+(detectable?"PASS":"FAIL")+
            " technicalCounterfactualIntegrity=PASS"+
            " G3_MOTOR_SKILL_BLOCKED G5_BLOCKED NO_OTA");
        System.out.println("ZERO3 V331 OVERALL "+total.format());
    }
}
