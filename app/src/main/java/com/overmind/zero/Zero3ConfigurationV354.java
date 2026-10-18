package com.overmind.zero;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Versioned candidate configuration. A change requires a new checkpoint schema. */
public final class Zero3ConfigurationV354 {
    public static final int SCHEMA=354,INPUTS=194,GRU=64,SECTORS=16,CHANNELS=4,PROFILES=12;
    public static final int OPTIONS=16,OPTION_CONTEXT=12,OPTION_MIN_TRAIN=4,OPTION_MIN_VALIDATION=8,GOAL_MAX_DECISIONS=4;
    public static final int CHECKPOINT_INTERVAL=32,GRAPH_MAX_EDGES=1400;
    public static final float DT=.025f,OPTION_CONTEXT_MSE=.12f;
    public static boolean heldout(long decision){return (decision/3)%5==4;}
    public static final String DESCRIPTION="354;GRU64x194;actor5;dualCriticTargetReplay128;world3x12to20x12;options16x12x5;train80;valid20blocks3;planner3;goal4;contextMSE.12;optionN8;probation16;LRvalue.005;physicsJoint12x9normalTolerance.01;DT.025";
    public static final String HASH=hash();
    private static String hash(){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(DESCRIPTION.getBytes(StandardCharsets.UTF_8));StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format(java.util.Locale.ROOT,"%02x",x&255));return s.toString();}catch(Exception bad){throw new ExceptionInInitializerError(bad);}}
    private Zero3ConfigurationV354(){}
}
