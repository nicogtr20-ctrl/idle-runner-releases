package com.overmind.zero;

import java.util.List;

public final class TelemetryChunksRegression {
    private static void check(boolean value,String error){
        if(!value)throw new AssertionError(error);
    }
    public static void main(String[] args){
        StringBuilder payload=new StringBuilder();
        for(int i=0;i<300000;i++)
            payload.append(i%5==0?"🧠":i%7==0?"è":"x");
        String source=payload.toString();
        List<String> parts=TelemetryChunks.split(source,TelemetryChunks.FRAGMENT_BYTES);
        check(parts.size()>10,"Large brain payload was not partitioned");
        check(TelemetryChunks.join(parts).equals(source),"Split erased original data");
        for(String part:parts)
            check(TelemetryChunks.size(part)<=TelemetryChunks.FRAGMENT_BYTES,
                "Fragment exceeded configured UTF-8 bound");
        check(TelemetryChunks.join(TelemetryChunks.split("little",320)).equals("little"),
            "Small payload changed");
        boolean failed=false;
        try{TelemetryChunks.split("bad\ud800",320);}
        catch(IllegalArgumentException expected){failed=true;}
        check(failed,"Broken Unicode must not silently corrupt a snapshot");
        System.out.println("ZERO TELEMETRY PASS: lossless UTF-8 shard round-trip");
    }
}
