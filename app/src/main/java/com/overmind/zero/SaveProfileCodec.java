package com.overmind.zero;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/** Independently testable, versioned and checksummed representation of ONE
 * complete ZERO working profile: neural weights, learned episodes, statistics
 * and skill banks. A named slot never changes during automatic play.
 */
public final class SaveProfileCodec {
    static final int MAGIC=0x5A45524F, VERSION=1;
    static final int MAX_BYTES=8*1024*1024, MAX_ENTRY=4*1024*1024, MAX_KEYS=256;
    private SaveProfileCodec(){}
    private static void putText(DataOutputStream out,String s)throws IOException{
        byte[] bytes=s.getBytes(StandardCharsets.UTF_8);
        if(bytes.length>MAX_ENTRY)throw new IOException("Profile field too large");
        out.writeInt(bytes.length);out.write(bytes);
    }
    private static String getText(DataInputStream in)throws IOException{
        int n=in.readInt();
        if(n<0||n>MAX_ENTRY||n>in.available())throw new IOException("Invalid profile field length");
        byte[] b=new byte[n];in.readFully(b);
        return new String(b,StandardCharsets.UTF_8);
    }
    private static byte[] checksum(byte[] data)throws Exception{
        return MessageDigest.getInstance("SHA-256").digest(data);
    }
    public static byte[] encode(Map<String,?> source){
        try{
            if(source==null||source.size()>MAX_KEYS)throw new IOException("Invalid key count");
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            DataOutputStream out=new DataOutputStream(bos);
            out.writeInt(MAGIC);out.writeInt(VERSION);out.writeInt(source.size());
            TreeMap<String,?> sorted=new TreeMap<>(source);
            for(Map.Entry<String,?> entry:sorted.entrySet()){
                String key=entry.getKey();
                if(key==null||key.isEmpty()||key.length()>150)
                    throw new IOException("Invalid key");
                putText(out,key);
                Object value=entry.getValue();
                if(value instanceof String){out.writeByte(1);putText(out,(String)value);}
                else if(value instanceof Integer){out.writeByte(2);out.writeInt((Integer)value);}
                else if(value instanceof Long){out.writeByte(3);out.writeLong((Long)value);}
                else if(value instanceof Float){
                    float v=(Float)value;if(!Float.isFinite(v))throw new IOException("Invalid float");
                    out.writeByte(4);out.writeFloat(v);
                }else if(value instanceof Boolean){out.writeByte(5);out.writeBoolean((Boolean)value);}
                else if(value instanceof Set){
                    out.writeByte(6);
                    TreeSet<String> values=new TreeSet<>();
                    for(Object o:(Set<?>)value){
                        if(!(o instanceof String))throw new IOException("Invalid set");
                        values.add((String)o);
                    }
                    if(values.size()>256)throw new IOException("Set too large");
                    out.writeInt(values.size());
                    for(String v:values)putText(out,v);
                }else throw new IOException("Unsupported preference type");
            }
            out.flush();
            byte[] body=bos.toByteArray();
            if(body.length>MAX_BYTES-32)throw new IOException("Profile too large");
            bos.write(checksum(body));
            return bos.toByteArray();
        }catch(Exception e){throw new IllegalArgumentException("Cannot encode ZERO profile",e);}
    }
    public static Map<String,Object> decode(byte[] serialized){
        try{
            if(serialized==null||serialized.length<44||serialized.length>MAX_BYTES)
                throw new IOException("Invalid profile size");
            int dataLength=serialized.length-32;
            byte[] data=Arrays.copyOf(serialized,dataLength);
            byte[] expected=checksum(data);
            for(int i=0;i<32;i++)
                if(expected[i]!=serialized[dataLength+i])throw new IOException("Integrity checksum mismatch");
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(data));
            if(in.readInt()!=MAGIC||in.readInt()!=VERSION)throw new IOException("Wrong schema");
            int count=in.readInt();
            if(count<0||count>MAX_KEYS)throw new IOException("Invalid key count");
            Map<String,Object> result=new LinkedHashMap<>();
            for(int i=0;i<count;i++){
                String key=getText(in);
                if(key.isEmpty()||key.length()>150||result.containsKey(key))
                    throw new IOException("Duplicate/invalid key");
                int type=in.readUnsignedByte();Object value;
                switch(type){
                    case 1:value=getText(in);break;
                    case 2:value=in.readInt();break;
                    case 3:value=in.readLong();break;
                    case 4:
                        float f=in.readFloat();
                        if(!Float.isFinite(f))throw new IOException("Nonfinite float");
                        value=f;break;
                    case 5:value=in.readBoolean();break;
                    case 6:
                        int n=in.readInt();
                        if(n<0||n>256)throw new IOException("Invalid set");
                        HashSet<String> values=new HashSet<>();
                        for(int j=0;j<n;j++)values.add(getText(in));
                        value=values;break;
                    default:throw new IOException("Unsupported type");
                }
                result.put(key,value);
            }
            if(in.available()!=0)throw new IOException("Trailing data");
            return result;
        }catch(Exception e){throw new IllegalArgumentException("Corrupted ZERO profile",e);}
    }
    public static void main(String[] args){
        Map<String,Object> initial=new HashMap<>();
        initial.put("best","0.1,0.2,0.3");
        char[] large=new char[160000];Arrays.fill(large,'x');
        initial.put("sensorimotor_model_v09",new String(large));
        initial.put("attempts",4321);
        initial.put("scarcity_active_v08",true);
        initial.put("record_net_v094",42.75f);
        initial.put("clock",11L);
        initial.put("demo_set",new HashSet<>(Arrays.asList("a","b")));
        byte[] saved=encode(initial);
        Map<String,Object> restored=decode(saved);
        if(!restored.equals(initial))throw new AssertionError("Full typed profile mismatch");
        saved[25]^=1;
        try{decode(saved);throw new AssertionError("Corrupted snapshot accepted");}
        catch(IllegalArgumentException expected){}
        if(!Arrays.equals(encode(initial),encode(initial)))
            throw new AssertionError("Unstable serialization");
        System.out.println("ZERO-0100: typed full brain profile, long model, checksum, corrupt-snapshot rollback pass");
    }
}
