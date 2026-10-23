package com.overmind.zero;
import java.util.ArrayList;

/**
 * V364 RESEARCH CANDIDATE — a single ZERO3 GRU CHOOSES the intention.
 * A previously tested sensorimotor peripheral from V2.2 can transduce that
 * chosen heading to real 96-site membrane muscles. No V2.2 brain, critic,
 * navigation arbiter, food vector or forced animal behavior is run.
 *
 * The original V319 brain owns object adhesion (CH2) unconditionally.
 * The V3 cortex retains 25% actual authority on CH0, CH1, CH3, remains
 * trainable from physical outcomes, and still produces proprioceptive
 * final-command efference. This peripheral is deliberately frozen while
 * blended, because its untouched 2.2 learning attribution assumes sole
 * actuator ownership. Avoid attributing outcomes to unexecuted commands.
 *
 * The .75 proportion is an OPEN physical A/B candidate, not yet evidence of
 * actual red/blue survival. Never enable an OTA on this result alone.
 */
final class Zero3MotorPeripheralBridgeV364 {
    static final float PROVEN_CANDIDATE_SHARE=.75f;
    private static final int S=SymbioteMatterV1.SECTORS,
                             CHANNELS=SymbioteMatterV1.OUTPUTS;
    private static final float[] QUIET_RECURRENCE=new float[RecurrentCoreV22.Z];
    private Zero3MotorPeripheralBridgeV364(){}
    static int actionBankFromV3(float x,float y){
        if(!Float.isFinite(x)||!Float.isFinite(y)||
           Math.hypot(x,y)<.5)
            throw new IllegalArgumentException("V364 invalid neural choice direction");
        return Math.floorMod(Math.round((float)Math.atan2(y,x)/(float)(Math.PI/4)),8);
    }
    static void begin(SymbioteMotorHeadV1 peripheral,
                      MotorBrain.Body body,Morphology.Surface skin,
                      ScarcityLab chamber,ArrayList<UnknownWorld.Entity> objects,
                      float chosenX,float chosenY){
        if(peripheral==null||body==null||skin==null||objects==null)
            throw new IllegalArgumentException("Missing original motor peripheral");
        int bank=actionBankFromV3(chosenX,chosenY);
        // The old 2.2 motor is ONLY a physical readout. NEVER let it select
        // a heading, train on credited commands it didn't alone execute,
        // or recover another agent's stored actions.
        peripheral.setAdaptationEnabled(false);
        peripheral.chosenOriginalV381(bank,chosenX,chosenY,
            DistributedBodySense.sense(body,skin,chamber,objects),
            QUIET_RECURRENCE,
            SymbioteMotorHeadV1.measuredSectorAngles(body,skin),body,skin);
        // Previously adaptationEnabled(false) prevented weight updates
        // but left fastProbe true every third decision. That jitter was
        // part of the old 2.2 training motor, NOT a valid V3 command.
        peripheral.silenceFrozenV3MotorProbeV372();
        // The GRU3 intent is continuous: never round it to a 45-degree
        // motor direction in the 75% frozen fast circuit (V380 loss).
        peripheral.fastOriginalHeadingV381(chosenX,chosenY);
    }
    static void fastPhysicalRefine(float[] executing,SymbioteMotorHeadV1 peripheral,
                                   MotorBrain.Body body,Morphology.Surface skin,
                                   float dt){
        if(executing==null||executing.length!=S*CHANNELS||
           peripheral==null||body==null||skin==null||
           !Float.isFinite(dt)||dt<=0f||dt>.04f)
            throw new IllegalArgumentException("Invalid V364 physical motor timestep");
        peripheral.fastTickOriginalV370(body,skin,dt);
        float[] old=peripheral.commands();
        for(int sector=0;sector<S;sector++)for(int channel=0;channel<CHANNELS;channel++){
            int k=sector*CHANNELS+channel;
            // CH2 is ALWAYS the actual V319 voluntary contact-grip command:
            // no covert replacement by old action banks or auto-hand logic.
            if(channel==2)continue;
            float value=(1f-PROVEN_CANDIDATE_SHARE)*executing[k]+
                        PROVEN_CANDIDATE_SHARE*old[k];
            executing[k]=Math.max(-1f,Math.min(1f,value));
        }
    }
}
