package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.Locale;

/**
 * Passive last-256 V3 decisions, no destination, analytics SDK, or second
 * motor controller. Stored with the same named profile; uploaded ONLY via
 * the already user-initiated "Condividi dati con GPT" mechanism.
 */
final class Zero381MotorFlightRecorder {
    static final String KEY="zero3_motor_flight_recorder_v381";
    private static final int MAX=256;
    private final ArrayDeque<String> entries=new ArrayDeque<>(MAX+1);
    private long decisions;

    void completed(long decision,float intentX,float intentY,
                   float appliedMotorX,float appliedMotorY,
                   float preVx,float preVy,float postVx,float postVy,
                   float dx,float dy,float dt,float realImpulse,
                   float positiveEnergy,float physicalDamage,
                   float[] photons194,float[] commands64,
                   boolean physical,boolean corrected,boolean solid){
        if(photons194==null||photons194.length<44||
           commands64==null||commands64.length!=64)return;
        float maxBlue=-1,maxRed=-1;
        int blueAt=-1,redAt=-1;
        for(int j=0;j<8;j++){
            float blue=photons194[20+j],red=photons194[28+j];
            if(blue>maxBlue){maxBlue=blue;blueAt=j;}
            if(red>maxRed){maxRed=red;redAt=j;}
        }
        float ax=0,lat=0,stiff=0;
        for(int s=0;s<16;s++){
            ax+=commands64[s*4];
            lat+=commands64[s*4+1];
            stiff+=commands64[s*4+3];
        }
        String row=String.format(Locale.US,
            "%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.4f,%.4f,%.4f,%.4f,%.4f,%d,%.3f,%d,%.3f,%.4f,%.4f,%.4f,%d,%d,%d",
            decision,intentX,intentY,appliedMotorX,appliedMotorY,
            preVx,preVy,postVx,postVy,dx,dy,dt,realImpulse,
            positiveEnergy,physicalDamage,
            ax/16,lat/16,stiff/16,blueAt,maxBlue,redAt,maxRed,
            dx*intentX+dy*intentY,
            (dx-preVx*dt)*intentX+(dy-preVy*dt)*intentY,
            (float)Math.hypot(dx,dy),
            physical?1:0,corrected?1:0,solid?1:0);
        if(entries.size()>=MAX)entries.removeFirst();
        entries.addLast(row);decisions++;
    }
    long counted(){return decisions;}
    String encode(){
        StringBuilder b=new StringBuilder(256*176);
        b.append("zmf381|1|").append(decisions).append('\n');
        for(String line:entries)b.append(line).append('\n');
        return b.toString();
    }
    boolean restore(String raw){
        if(raw==null||raw.isEmpty())return true;
        try{
            String[] lines=raw.split("\n");
            if(lines.length<1||lines.length>MAX+1||!lines[0].startsWith("zmf381|1|"))return false;
            long n=Long.parseLong(lines[0].substring("zmf381|1|".length()));
            if(n<0||n>100000000)return false;
            ArrayDeque<String> restored=new ArrayDeque<>();
            for(int i=1;i<lines.length;i++){
                if(lines[i].isEmpty())continue;
                if(lines[i].length()>350||lines[i].split(",",-1).length!=28)return false;
                restored.addLast(lines[i]);
            }
            entries.clear();entries.addAll(restored);decisions=n;return true;
        }catch(Exception ignored){return false;}
    }
}
