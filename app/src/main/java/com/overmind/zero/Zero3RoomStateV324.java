package com.overmind.zero;

import java.io.*;
import java.lang.reflect.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * Experimental V3.24 chamber-state adapter. Preserves ALL primitive,
 * physics-dependent ScarcityLab fields, including private contact hysteresis
 * and the box/gate latch. The canonical room.food must be the same object
 * as its world entry after restore. No GameActivity or 2.2 slot writes.
 */
public final class Zero3RoomStateV324 {
    private static final int MAGIC=0x5A33524D,VERSION=324,MAX=16384;
    private final byte[] data;
    private Zero3RoomStateV324(byte[] b){data=b.clone();}
    public byte[] encoded(){return data.clone();}
    private static void need(boolean value,String error)throws IOException{
        if(!value)throw new IOException("V324 room "+error);
    }
    private static Field[] fields()throws IOException{
        ArrayList<Field> result=new ArrayList<>();
        for(Field f:ScarcityLab.class.getDeclaredFields()){
            if(Modifier.isStatic(f.getModifiers())||f.isSynthetic()||
               f.getName().equals("food")||
               f.getName().equals("ox")||f.getName().equals("oy"))continue;
            int t=tag(f.getType());
            need(t>0,"unsupported future room field "+f.getName());
            f.setAccessible(true);result.add(f);
        }
        result.sort(Comparator.comparing(Field::getName));
        return result.toArray(new Field[0]);
    }
    private static int tag(Class<?> t){
        if(t==float.class)return 1;
        if(t==int.class)return 2;
        if(t==boolean.class)return 3;
        if(t==float[].class)return 4;
        if(t==boolean[].class)return 5;
        return -1;
    }
    private static void finite(float f)throws IOException{
        need(Float.isFinite(f)&&Math.abs(f)<=1000000f,"invalid material number");
    }
    private static void writeFloat(DataOutputStream d,float v)throws IOException{
        finite(v);d.writeInt(Float.floatToRawIntBits(v));
    }
    private static float readFloat(DataInputStream d)throws IOException{
        float value=Float.intBitsToFloat(d.readInt());finite(value);return value;
    }
    private static int findFood(ScarcityLab room,List<UnknownWorld.Entity> objects)
              throws IOException{
        need(room!=null&&objects!=null&&objects.size()<=128,"missing room or world");
        int index=-1;
        for(int i=0;i<objects.size();i++){
            UnknownWorld.Entity item=objects.get(i);
            if(item==room.food){
                need(index==-1,"same food entity repeated");
                index=i;
            }
        }
        need(index>=0,"room food not present in the world by IDENTITY");
        need(room.food.physicalLaw()==3&&room.food.signature==0,
             "room food law invalid");
        return index;
    }
    static Zero3RoomStateV324 capture(ScarcityLab room,
              List<UnknownWorld.Entity> world)throws IOException {
        int index=findFood(room,world);
        try{
            ByteArrayOutputStream memory=new ByteArrayOutputStream();
            DataOutputStream d=new DataOutputStream(memory);
            d.writeInt(MAGIC);d.writeInt(VERSION);
            writeFloat(d,room.ox);writeFloat(d,room.oy);
            d.writeInt(index);
            Field[] fields=fields();d.writeInt(fields.length);
            for(Field f:fields){
                int tag=tag(f.getType());d.writeUTF(f.getName());d.writeByte(tag);
                if(tag==1)writeFloat(d,f.getFloat(room));
                else if(tag==2)d.writeInt(f.getInt(room));
                else if(tag==3)d.writeBoolean(f.getBoolean(room));
                else {
                    Object a=f.get(room);int len=Array.getLength(a);
                    need(len>=0&&len<=4096,"room field length");
                    d.writeInt(len);
                    for(int i=0;i<len;i++){
                        if(tag==4)writeFloat(d,Array.getFloat(a,i));
                        else d.writeBoolean(Array.getBoolean(a,i));
                    }
                }
            }
            d.flush();need(memory.size()<=MAX,"oversized room memento");
            Zero3RoomStateV324 snapshot=new Zero3RoomStateV324(memory.toByteArray());
            // Only a valid read/restore round-trip is accepted in the archive.
            need(Arrays.equals(snapshot.data,snapshot.roundTrip(world)),
                 "room codec is not stable");
            return snapshot;
        }catch(ReflectiveOperationException error){
            throw new IOException("cannot read room fields",error);
        }
    }
    /** Restore into a FRESH complete world, never mutate live objects on fail. */
    public Restored restore(List<UnknownWorld.Entity> physicalWorld)throws IOException {
        need(physicalWorld!=null&&physicalWorld.size()<=128,
             "missing scene for room");
        try{
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(data));
            need(in.readInt()==MAGIC&&in.readInt()==VERSION,
                 "room version mismatch");
            float x=readFloat(in),y=readFloat(in);
            int foodIndex=in.readInt();
            need(foodIndex>=0&&foodIndex<physicalWorld.size(),
                 "missing room food identity");
            ScarcityLab room=new ScarcityLab(x,y);
            Field[] fields=fields();
            need(in.readInt()==fields.length,"unsupported room schema");
            for(Field f:fields){
                int tag=tag(f.getType());
                need(f.getName().equals(in.readUTF())&&tag==in.readUnsignedByte(),
                     "room schema drift "+f.getName());
                if(tag==1)f.setFloat(room,readFloat(in));
                else if(tag==2){
                    int n=in.readInt();need(n>=0,"negative physics counter");
                    f.setInt(room,n);
                }else if(tag==3)f.setBoolean(room,in.readBoolean());
                else {
                    Object values=f.get(room);int n=in.readInt();
                    need(n==Array.getLength(values),"wrong physical hysteresis length");
                    for(int i=0;i<n;i++){
                        if(tag==4)Array.setFloat(values,i,readFloat(in));
                        else Array.setBoolean(values,i,in.readBoolean());
                    }
                }
            }
            need(in.available()==0,"trailing room bytes");
            UnknownWorld.Entity original=physicalWorld.get(foodIndex);
            need(original!=null&&original.physicalLaw()==3&&
                original.signature==0&&
                Float.floatToRawIntBits(original.spawnX)==
                    Float.floatToRawIntBits(room.food.spawnX)&&
                Float.floatToRawIntBits(original.spawnY)==
                    Float.floatToRawIntBits(room.food.spawnY),
                "room food identity/position mismatch");
            // Transfer the *actual* consumed/cooldown and physical status from
            // the verified world entity into the final room.food instance.
            ByteArrayOutputStream b=new ByteArrayOutputStream();
            DataOutputStream d=new DataOutputStream(b);
            Zero3OrganismStoreV322.FieldCodec.write(d,original);d.flush();
            Zero3OrganismStoreV322.FieldCodec.read(
                new DataInputStream(new ByteArrayInputStream(b.toByteArray())),
                room.food);
            ArrayList<UnknownWorld.Entity> connected=
                new ArrayList<>(physicalWorld);
            connected.set(foodIndex,room.food);
            need(connected.get(foodIndex)==room.food,"food alias broken");
            return new Restored(room,connected,foodIndex);
        }catch(ReflectiveOperationException|IllegalArgumentException error){
            throw new IOException("room restore rejected",error);
        }
    }
    /** Validate content and reserialize without editing caller's world. */
    private byte[] roundTrip(List<UnknownWorld.Entity> world)throws IOException {
        ArrayList<UnknownWorld.Entity> tmp=new ArrayList<>(world);
        Restored recovered=restore(tmp);
        return encodeFields(recovered.room,recovered.foodIndex);
    }
    private static byte[] encodeFields(ScarcityLab room,int index)throws IOException {
        try{
            ByteArrayOutputStream b=new ByteArrayOutputStream();
            DataOutputStream d=new DataOutputStream(b);
            d.writeInt(MAGIC);d.writeInt(VERSION);
            writeFloat(d,room.ox);writeFloat(d,room.oy);
            d.writeInt(index);
            Field[] fields=fields();d.writeInt(fields.length);
            for(Field f:fields){
                int tag=tag(f.getType());d.writeUTF(f.getName());d.writeByte(tag);
                if(tag==1)writeFloat(d,f.getFloat(room));
                else if(tag==2)d.writeInt(f.getInt(room));
                else if(tag==3)d.writeBoolean(f.getBoolean(room));
                else{
                    Object a=f.get(room);int n=Array.getLength(a);
                    need(n<=4096,"oversized array");
                    d.writeInt(n);
                    for(int i=0;i<n;i++){
                        if(tag==4)writeFloat(d,Array.getFloat(a,i));
                        else d.writeBoolean(Array.getBoolean(a,i));
                    }
                }
            }
            d.flush();return b.toByteArray();
        }catch(ReflectiveOperationException e){throw new IOException(e);}
    }
    public static Zero3RoomStateV324 decode(byte[] raw)throws IOException{
        need(raw!=null&&raw.length>=40&&raw.length<=MAX,"room length");
        return new Zero3RoomStateV324(raw);
    }
    public String fingerprint(){
        try{
            byte[] sha=MessageDigest.getInstance("SHA-256").digest(data);
            StringBuilder s=new StringBuilder();
            for(byte v:sha)s.append(String.format(Locale.ROOT,"%02x",v&255));
            return s.toString();
        }catch(Exception e){throw new IllegalStateException(e);}
    }
    public static final class Restored {
        public final ScarcityLab room;
        public final ArrayList<UnknownWorld.Entity> objects;
        public final int foodIndex;
        Restored(ScarcityLab r,ArrayList<UnknownWorld.Entity> w,int id){
            room=r;objects=w;foodIndex=id;
        }
    }
}
