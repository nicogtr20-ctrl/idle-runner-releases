package com.overmind.zero;

/**
 * ZERO3 G3 promotion guard. Evaluates performance on independent fresh
 * REAL-physics runs. It cannot label an untested network as competent.
 * Research gate only until profile checkpoint/snapshot integration exists.
 */
public final class Zero3MotorPromotionGate {
    private Zero3MotorPromotionGate(){}
    public static final class Decision {
        public final boolean promote;
        public final int improved;
        public final double totalImprovement,relativeCost;
        public final String reason;
        Decision(boolean promote,int improved,double improvement,double relativeCost,String reason){
            this.promote=promote;this.improved=improved;
            totalImprovement=improvement;this.relativeCost=relativeCost;this.reason=reason;
        }
    }
    /**
     * Four independent preregistered directions. A deterministic acceptance
     * condition does not prove real-world generalization, but prevents obvious
     * regressions from being accepted as a new motor skill.
     */
    public static Decision evaluate(double[] virginForward,double[] learnedForward,
                                    double[] virginCost,double[] learnedCost){
        if(virginForward==null||learnedForward==null||virginCost==null||learnedCost==null||
           virginForward.length!=4||learnedForward.length!=4||
           virginCost.length!=4||learnedCost.length!=4)
            throw new IllegalArgumentException("four matched motor directions required");
        int improved=0;
        double gain=0,baseCost=0,candidateCost=0,worst=Double.POSITIVE_INFINITY;
        for(int i=0;i<4;i++){
            double v=virginForward[i],l=learnedForward[i],c0=virginCost[i],c1=learnedCost[i];
            if(!Double.isFinite(v)||!Double.isFinite(l)||!Double.isFinite(c0)||!Double.isFinite(c1)||
                c0<0||c1<0)throw new IllegalArgumentException("nonfinite/negative outcome");
            double d=l-v;
            if(d>0.25)improved++;
            gain+=d;worst=Math.min(worst,d);
            baseCost+=c0;candidateCost+=c1;
        }
        double costRatio=(candidateCost+.001)/(baseCost+.001);
        boolean approve=improved>=3&&gain>2d&&worst>=-2d&&costRatio<=1.15d;
        String reason=approve?"PRELIMINARY_PROMOTION_CANDIDATE":
            "BLOCKED: improvements="+improved+"/4 gain="+
            String.format(java.util.Locale.US,"%.3f",gain)+
            " worst="+String.format(java.util.Locale.US,"%.3f",worst)+
            " costRatio="+String.format(java.util.Locale.US,"%.3f",costRatio);
        return new Decision(approve,improved,gain,costRatio,reason);
    }
}
