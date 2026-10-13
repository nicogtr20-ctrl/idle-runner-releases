package com.overmind.zero;

import java.io.ByteArrayOutputStream;

/** java.util.Base64 is API26; this portable codec preserves Android API24. */
final class Base64CodecV22 {
    private static final String C="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    static String encode(byte[] bytes){
        StringBuilder s=new StringBuilder((bytes.length+2)/3*4);
        for(int i=0;i<bytes.length;i+=3){
            int n=(bytes[i]&255)<<16;
            if(i+1<bytes.length)n|=(bytes[i+1]&255)<<8;
            if(i+2<bytes.length)n|=bytes[i+2]&255;
            s.append(C.charAt(n>>>18)).append(C.charAt((n>>>12)&63))
             .append(i+1<bytes.length?C.charAt((n>>>6)&63):'=')
             .append(i+2<bytes.length?C.charAt(n&63):'=');
        }
        return s.toString();
    }
    static byte[] decode(String text){
        if(text.length()%4!=0)throw new IllegalArgumentException("base64 length");
        ByteArrayOutputStream out=new ByteArrayOutputStream(text.length()/4*3);
        for(int i=0;i<text.length();i+=4){
            int a=C.indexOf(text.charAt(i)),b=C.indexOf(text.charAt(i+1));
            char cc=text.charAt(i+2),dd=text.charAt(i+3);
            int c=cc=='='?0:C.indexOf(cc),d=dd=='='?0:C.indexOf(dd);
            if(a<0||b<0||c<0||d<0||cc=='='&&dd!='='||
                (cc=='='||dd=='=')&&i+4!=text.length())throw new IllegalArgumentException("base64 character");
            int n=a<<18|b<<12|c<<6|d;out.write(n>>>16);
            if(cc!='=')out.write((n>>>8)&255);if(dd!='=')out.write(n&255);
        }
        byte[] bytes=out.toByteArray();
        if(!encode(bytes).equals(text))throw new IllegalArgumentException("noncanonical base64");
        return bytes;
    }
}
