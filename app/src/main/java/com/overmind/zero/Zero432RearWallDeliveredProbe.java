package com.overmind.zero;

/** V432: signed rear-wall local learning requires an actually delivered
 * muscle perturbation, NOT positive acceleration. A negative measured
 * response is legitimate evidence for the existing signed V417 gradient.
 * Does not choose directions, create rewards, or change body physics.
 */
final class Zero432RearWallDeliveredProbe {
    private Zero432RearWallDeliveredProbe(){}
    static boolean eligible(boolean realWall,int rearFrames,int frontFrames,
            float signedDisplacement,
            Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe){
        if(!Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
                realWall,rearFrames,frontFrames,signedDisplacement))
            return false;
        if(probe==null||probe.deliveredNoise==null||
           probe.deliveredNoise.length!=64)return false;
        int channel=probe.selectedChannel;
        if(channel<0||channel>=4||channel==2)return false;
        boolean delivered=false;
        for(int sector=0;sector<16;sector++){
            float actual=probe.deliveredNoise[sector*4+channel];
            if(!Float.isFinite(actual))
                throw new IllegalArgumentException("V432 nonfinite delivered motor");
            if(actual!=0f)delivered=true;
        }
        return delivered;
    }
}
