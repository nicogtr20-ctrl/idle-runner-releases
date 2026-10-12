package com.overmind.zero;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/** Save the ACTUAL current physical scene, not just the learned profile.
 * Loads use all-or-nothing validation: a malformed scene is rejected before
 * live GameActivity objects are replaced. Object/membrane physics are not
 * reset, directed or influenced by this storage format.
 */
public final class GameSceneSnapshot {
    private static final int VERSION=1;
    private GameSceneSnapshot(){}

    public static final class Scene {
        public MotorBrain.Body body;
        public Morphology.Surface skin;
        public ScarcityLab room;
        public ArrayList<UnknownWorld.Entity> objects;
    }
    private static JSONArray array(float[] values)throws Exception{
        JSONArray a=new JSONArray();
        for(float f:values){
            if(!Float.isFinite(f))throw new IllegalArgumentException("Nonfinite physical mass");
            a.put((double)f);
        }
        return a;
    }
    private static void putArrays(JSONObject root,String[] names,float[][] arrays)throws Exception{
        for(int i=0;i<names.length;i++)root.put(names[i],array(arrays[i]));
    }
    private static void readArrays(JSONObject root,String[] names,float[][] arrays)throws Exception{
        for(int n=0;n<names.length;n++){
            JSONArray a=root.getJSONArray(names[n]);
            if(a.length()!=arrays[n].length)throw new IllegalArgumentException("Invalid particle count");
            for(int i=0;i<a.length();i++){
                double d=a.getDouble(i);
                if(!Double.isFinite(d)||Math.abs(d)>1000000)
                    throw new IllegalArgumentException("Invalid physical coordinate");
                arrays[n][i]=(float)d;
            }
        }
    }
    private static JSONObject entity(UnknownWorld.Entity e,boolean chamberFood)throws Exception{
        JSONObject x=new JSONObject();
        x.put("food",chamberFood);
        x.put("sx",e.spawnX);x.put("sy",e.spawnY);
        x.put("x",e.x);x.put("y",e.y);x.put("vx",e.vx);x.put("vy",e.vy);
        x.put("signature",e.signature);x.put("law",e.physicalLaw());
        x.put("consumed",e.consumed);x.put("concealed",e.concealed);
        x.put("cooldown",Float.isFinite(e.cooldown)?e.cooldown:1000000000f);
        x.put("contactCooldown",e.contactCooldown);
        x.put("everCollided",e.everCollided);x.put("countedAsMoved",e.countedAsMoved);
        x.put("peakDisplacement",e.peakDisplacement);x.put("radius",e.radius);
        if(e.isBox())x.put("boxGripHold",array(e.boxGripHold));
        if(e.isConstruction()){
            x.put("panelAngle",e.angle);
            x.put("panelSpin",e.angularVelocity);
        }
        return x;
    }
    private static UnknownWorld.Entity loadEntity(JSONObject x,ScarcityLab room)throws Exception{
        boolean isFood=x.getBoolean("food");
        int law=x.getInt("law"),signature=x.getInt("signature");
        if(law<0||law>6||signature<0||signature>=UnknownWorld.SIGNATURES)
            throw new IllegalArgumentException("Invalid physical signature");
        float sx=(float)x.getDouble("sx"),sy=(float)x.getDouble("sy");
        UnknownWorld.Entity e;
        if(isFood){
            if(room==null||law!=3)throw new IllegalArgumentException("Missing room food");
            e=room.food;
        }else e=new UnknownWorld.Entity(sx,sy,signature,law);
        e.x=(float)x.getDouble("x");e.y=(float)x.getDouble("y");
        e.vx=(float)x.getDouble("vx");e.vy=(float)x.getDouble("vy");
        e.cooldown=(float)x.getDouble("cooldown");
        e.contactCooldown=(float)x.getDouble("contactCooldown");
        e.consumed=x.getBoolean("consumed");
        // Optional: v1 saves from before the square puzzle remain valid.
        e.concealed=x.optBoolean("concealed",false);
        e.everCollided=x.getBoolean("everCollided");
        e.countedAsMoved=x.getBoolean("countedAsMoved");
        e.peakDisplacement=(float)x.getDouble("peakDisplacement");
        e.radius=(float)x.getDouble("radius");
        if(e.isBox()&&x.has("boxGripHold"))
            readArrays(x,new String[]{"boxGripHold"},new float[][]{e.boxGripHold});
        if(e.isConstruction()){
            // Historic OTA 93 construction squares migrate to thin plates in
            // their ACTUAL saved position; no new objects are fabricated.
            e.angle=(float)x.optDouble("panelAngle",0);
            e.angularVelocity=(float)x.optDouble("panelSpin",0);
            e.radius=PanelPhysics.bound(e);
        }
        if(!Float.isFinite(e.angle)||!Float.isFinite(e.angularVelocity)||
           Math.abs(e.angularVelocity)>20f||
           !Float.isFinite(e.x)||!Float.isFinite(e.y)||
           !Float.isFinite(e.vx)||!Float.isFinite(e.vy)||e.radius<0||
           !Float.isFinite(e.radius)||!Float.isFinite(e.cooldown)||
           !Float.isFinite(e.contactCooldown)||
           (e.concealed&&law!=4))
            throw new IllegalArgumentException("Invalid world object");
        return e;
    }
    public static String capture(MotorBrain.Body body,Morphology.Surface skin,
                                 List<UnknownWorld.Entity> environment,
                                 ScarcityLab room){
        try{
            JSONObject root=new JSONObject();root.put("version",VERSION);
            JSONObject b=new JSONObject();
            b.put("cx",body.cx);b.put("cy",body.cy);
            b.put("cvx",body.cvx);b.put("cvy",body.cvy);
            b.put("time",body.time);b.put("work",body.totalWork);
            putArrays(b,new String[]{"x","y","vx","vy","act","grip"},
                new float[][]{body.x,body.y,body.vx,body.vy,body.act,body.grip});
            root.put("body",b);
            JSONObject s=new JSONObject();
            s.put("t",skin.t);s.put("effort",skin.totalEffort);
            s.put("peak",skin.peakExtension);s.put("max",skin.maxExtension);
            putArrays(s,new String[]{"x","y","vx","vy","extension","command","sense","hold"},
                new float[][]{skin.x,skin.y,skin.vx,skin.vy,skin.extension,
                    skin.gripCommand,skin.touchSense,skin.gripHold});
            root.put("surface",s);
            if(room!=null){
                JSONObject r=new JSONObject();r.put("ox",room.ox);r.put("oy",room.oy);
                r.put("doorY",room.doorY);r.put("doorVelocity",room.doorVelocity);
                r.put("dualContactTime",room.dualContactTime);
                r.put("grasps",room.grasps);
                r.put("oneSidedContacts",room.oneSidedContacts);
                r.put("opposedContactFrames",room.opposedContactFrames);
                r.put("tactileFrames",room.tactileFrames);
                r.put("adhesiveContactFrames",room.adhesiveContactFrames);
                r.put("dualTouchNoGrip",room.dualTouchNoGrip);
                r.put("gateContactFrames",room.gateContactFrames);
                r.put("gateDriveFrames",room.gateDriveFrames);
                r.put("gateNoDriveFrames",room.gateNoDriveFrames);
                r.put("gateCommandFrames",room.gateCommandFrames);
                r.put("gateHeldFrames",room.gateHeldFrames);
                r.put("peakTouchGripCommand",room.peakTouchGripCommand);
                r.put("adhesiveEffort",room.adhesiveEffort);
                r.put("reachedEnergy",room.reachedEnergy);
                r.put("everOpened",room.everOpened);
                r.put("maxDoorTravel",room.maxDoorTravel);
                r.put("solidLinkCorrections",room.solidLinkCorrections);
                r.put("graspingLatch",room.graspingLatch());
                r.put("gateGripHold",array(room.gateGripHold));
                root.put("room",r);
            }
            JSONArray a=new JSONArray();
            for(UnknownWorld.Entity e:environment)
                a.put(entity(e,room!=null&&e==room.food));
            root.put("objects",a);
            return root.toString();
        }catch(Exception error){throw new IllegalStateException("Cannot snapshot physical scene",error);}
    }
    public static Scene restore(String raw){
        try{
            if(raw==null||raw.isEmpty()||raw.length()>2000000)return null;
            JSONObject root=new JSONObject(raw);
            if(root.getInt("version")!=VERSION)return null;
            Scene out=new Scene();out.body=new MotorBrain.Body();
            JSONObject b=root.getJSONObject("body");
            out.body.cx=(float)b.getDouble("cx");out.body.cy=(float)b.getDouble("cy");
            out.body.cvx=(float)b.getDouble("cvx");out.body.cvy=(float)b.getDouble("cvy");
            out.body.time=(float)b.getDouble("time");
            out.body.totalWork=(float)b.getDouble("work");
            readArrays(b,new String[]{"x","y","vx","vy","act","grip"},
                new float[][]{out.body.x,out.body.y,out.body.vx,out.body.vy,out.body.act,out.body.grip});
            out.skin=new Morphology.Surface(out.body);
            JSONObject s=root.getJSONObject("surface");
            out.skin.t=(float)s.getDouble("t");
            out.skin.totalEffort=(float)s.getDouble("effort");
            out.skin.peakExtension=(float)s.getDouble("peak");
            out.skin.maxExtension=(float)s.getDouble("max");
            readArrays(s,new String[]{"x","y","vx","vy","extension","command","sense","hold"},
                new float[][]{out.skin.x,out.skin.y,out.skin.vx,out.skin.vy,
                    out.skin.extension,out.skin.gripCommand,out.skin.touchSense,out.skin.gripHold});
            if(root.has("room")){
                JSONObject r=root.getJSONObject("room");
                out.room=new ScarcityLab((float)r.getDouble("ox"),(float)r.getDouble("oy"));
                out.room.doorY=(float)r.getDouble("doorY");
                out.room.doorVelocity=(float)r.getDouble("doorVelocity");
                out.room.dualContactTime=(float)r.getDouble("dualContactTime");
                out.room.grasps=r.getInt("grasps");
                out.room.oneSidedContacts=r.getInt("oneSidedContacts");
                out.room.opposedContactFrames=r.getInt("opposedContactFrames");
                out.room.tactileFrames=r.getInt("tactileFrames");
                out.room.adhesiveContactFrames=r.getInt("adhesiveContactFrames");
                out.room.dualTouchNoGrip=r.getInt("dualTouchNoGrip");
                out.room.gateContactFrames=r.getInt("gateContactFrames");
                out.room.gateDriveFrames=r.getInt("gateDriveFrames");
                out.room.gateNoDriveFrames=r.getInt("gateNoDriveFrames");
                out.room.gateCommandFrames=r.getInt("gateCommandFrames");
                out.room.gateHeldFrames=r.getInt("gateHeldFrames");
                out.room.peakTouchGripCommand=(float)r.getDouble("peakTouchGripCommand");
                out.room.adhesiveEffort=(float)r.getDouble("adhesiveEffort");
                out.room.reachedEnergy=r.getBoolean("reachedEnergy");
                out.room.everOpened=r.getBoolean("everOpened");
                out.room.maxDoorTravel=(float)r.getDouble("maxDoorTravel");
                out.room.solidLinkCorrections=r.getInt("solidLinkCorrections");
                out.room.restoreGraspingLatch(r.getBoolean("graspingLatch"));
                if(r.has("gateGripHold")){
                    float[] hold=new float[Morphology.POINTS];
                    readArrays(r,new String[]{"gateGripHold"},new float[][]{hold});
                    out.room.restoreGateGrip(hold);
                }
                if(!Float.isFinite(out.room.doorY)||out.room.doorY<0||
                   out.room.doorY>ScarcityLab.DOOR_MAX_TRAVEL)return null;
            }
            out.objects=new ArrayList<>();
            JSONArray a=root.getJSONArray("objects");
            if(a.length()>64)return null;
            int foodCount=0;
            for(int i=0;i<a.length();i++){
                JSONObject obj=a.getJSONObject(i);
                if(obj.getBoolean("food"))foodCount++;
                out.objects.add(loadEntity(obj,out.room));
            }
            if(foodCount>1)return null;
            if(!Float.isFinite(out.body.cx)||!Float.isFinite(out.body.cy)||
               !Float.isFinite(out.body.cvx)||!Float.isFinite(out.body.cvy)||
               !Float.isFinite(out.body.time)||!Float.isFinite(out.skin.t))
                return null;
            return out;
        }catch(Exception error){return null;}
    }
}
