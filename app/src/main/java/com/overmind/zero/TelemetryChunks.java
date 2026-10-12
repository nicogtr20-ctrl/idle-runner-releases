package com.overmind.zero;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Bounded, lossless UTF-8 splitting. No network or game-state access. */
public final class TelemetryChunks {
    public static final int PART_BYTES=256*1024,FRAGMENT_BYTES=32*1024,MAX_PARTS=128;
    private TelemetryChunks(){}
    public static int size(String value){
        return value.getBytes(StandardCharsets.UTF_8).length;
    }
    public static List<String> split(String input,int maxBytes){
        if(input==null||maxBytes<4)throw new IllegalArgumentException("Invalid input");
        ArrayList<String> pieces=new ArrayList<>();
        int start=0,used=0;
        for(int i=0;i<input.length();){
            int cp=input.codePointAt(i);
            if(cp>=0xd800&&cp<=0xdfff)
                throw new IllegalArgumentException("Invalid Unicode");
            int n=cp<128?1:cp<2048?2:cp<65536?3:4;
            if(used+n>maxBytes){
                pieces.add(input.substring(start,i));start=i;used=0;
            }
            used+=n;i+=Character.charCount(cp);
        }
        if(start<input.length()||pieces.isEmpty())
            pieces.add(input.substring(start));
        return pieces;
    }
    public static String join(List<String> pieces){
        StringBuilder builder=new StringBuilder();
        for(String part:pieces)builder.append(part);
        return builder.toString();
    }
}
