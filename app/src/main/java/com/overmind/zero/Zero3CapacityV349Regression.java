package com.overmind.zero;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * V349 read-only archive schema boundary test. The 10k observations here are
 * synthetic codec fixtures and MUST NOT count as genuine physical decisions.
 */
public final class Zero3CapacityV349Regression {
    private Zero3CapacityV349Regression(){}
    private static void check(boolean ok,String reason){
        if(!ok)throw new AssertionError("V349 capacity "+reason);
    }
    public static void main(String[] unused)throws Exception {
        check(Zero3SegmentedArchiveV328.maxFor(false)==3500,
              "legacy V317 archival ceiling altered");
        check(Zero3SegmentedArchiveV328.maxFor(true)==10000,
              "GRU348 archival ceiling differs from 10k");

        ArrayList<Zero3SurvivalLedgerV323.Trace> history=new ArrayList<>();
        for(int i=0;i<10000;i++){
            history.add(new Zero3SurvivalLedgerV323.Trace(i,i*12L,(i+1)*12L,
                100f,100f,0f,0f,0f,0f,0,0,0));
        }
        Zero3SurvivalLedgerV323.State before=new Zero3SurvivalLedgerV323.State(
            100f,3000f,3000f,true,10000,0,0,0,0f,0f,0f,0f,history);
        byte[] encoded=before.encoded();
        check(encoded.length==560053,
            "real fixed binary record schema not exactly 53+56*10000");
        Zero3SurvivalLedgerV323.State decoded=
            Zero3SurvivalLedgerV323.State.decoded(encoded);
        check(decoded.next==10000&&decoded.history.size()==10000&&
              Arrays.equals(encoded,decoded.encoded()),
              "10k metabolic metadata not canonical through cold load");
        byte[] truncated=Arrays.copyOf(encoded,encoded.length-1);
        boolean rejected=false;
        try{Zero3SurvivalLedgerV323.State.decoded(truncated);}
        catch(IOException expected){rejected=true;}
        check(rejected,"truncated 10k metabolic state accepted");
        byte[] extra=Arrays.copyOf(encoded,encoded.length+1);
        rejected=false;
        try{Zero3SurvivalLedgerV323.State.decoded(extra);}
        catch(IOException expected){rejected=true;}
        check(rejected,"extra trailing data accepted");
        System.out.println("V349 SCHEMA_10000_CAPACITY=PASS "+
            "legacy3500_gru10000=true metabolic10000_bytes560053=true "+
            "canonicalColdDecode=true truncatedAndExtraReject=true "+
            "CODEC_ONLY_NOT_10000_PHYSICAL_DECISIONS NO_OTA NO_MERGE");
    }
}
