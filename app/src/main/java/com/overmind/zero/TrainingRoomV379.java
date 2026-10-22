package com.overmind.zero;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * One editable training habitat. Only the environment differs from the
 * original game: neural owner, sensory channels, body and reward physics stay
 * with GameActivity. Future rooms can each have a separate layout key.
 */
final class TrainingRoomV379 {
    static final String LAYOUT_KEY="training_room_1_layout_v379";
    static final String ACTIVE_KEY="training_room_1_active_v379";
    static final String SCENE_KEY="training_room_1_scene_v379";
    static final String FREE_SCENE_KEY="training_room_1_free_scene_v379";
    static final float HALF_W=240f, HALF_H=320f, TILE=24f;
    static final int BLUE=0, RED=1, PURPLE=2, BOX=3, WALL=4, ERASER=5;
    static final int MAX_ITEMS=120;
    static final class Item {
        final int type;final float x,y;
        Item(int type,float x,float y){this.type=type;this.x=x;this.y=y;}
    }
    private final ArrayList<Item> items=new ArrayList<>();
    ArrayList<Item> items(){return new ArrayList<>(items);}
    void clear(){items.clear();}
    int size(){return items.size();}
    boolean place(int tool,float px,float py){
        if(!Float.isFinite(px)||!Float.isFinite(py))return false;
        float step=tool==WALL?TILE:12f;
        float x=Math.round(px/step)*step,y=Math.round(py/step)*step;
        if(Math.abs(x)>HALF_W-30||Math.abs(y)>HALF_H-30)return false;
        int near=-1;
        for(int i=items.size()-1;i>=0;i--){
            Item item=items.get(i);
            if(Math.hypot(item.x-x,item.y-y)<19f){near=i;break;}
        }
        if(tool==ERASER){if(near<0)return false;items.remove(near);return true;}
        if(tool<BLUE||tool>WALL||Math.hypot(x,y)<54f)return false;
        if(near>=0){
            Item old=items.get(near);
            if(old.type==tool&&(tool==WALL||old.x==x&&old.y==y))return false;
            items.remove(near);
        }
        if(items.size()>=MAX_ITEMS)return false;
        items.add(new Item(tool,x,y));return true;
    }
    String encode(){
        JSONArray all=new JSONArray();
        try{
            for(Item item:items){
                JSONArray e=new JSONArray();e.put(item.type);e.put(item.x);e.put(item.y);all.put(e);
            }
            JSONObject o=new JSONObject();o.put("version",1);o.put("items",all);
            return o.toString();
        }catch(Exception impossible){throw new IllegalStateException(impossible);}
    }
    static TrainingRoomV379 decode(String raw){
        TrainingRoomV379 room=new TrainingRoomV379();
        if(raw==null||raw.isEmpty())return room;
        try{
            JSONObject o=new JSONObject(raw);
            if(o.getInt("version")!=1)throw new IllegalArgumentException("training schema");
            JSONArray a=o.getJSONArray("items");
            if(a.length()>MAX_ITEMS)throw new IllegalArgumentException("training capacity");
            for(int i=0;i<a.length();i++){
                JSONArray v=a.getJSONArray(i);
                int type=v.getInt(0);float x=(float)v.getDouble(1),y=(float)v.getDouble(2);
                if(type<BLUE||type>WALL||!room.place(type,x,y))
                    throw new IllegalArgumentException("training item "+i);
            }
            return room;
        }catch(Exception invalid){throw new IllegalArgumentException("Layout Training 1 danneggiato",invalid);}
    }
    /** Fixed boundary and hand-placed walls share an actual collision law. */
    ArrayList<UnknownWorld.Entity> newEntities(){
        ArrayList<UnknownWorld.Entity> all=new ArrayList<>();
        for(float x=-HALF_W;x<=HALF_W+.1f;x+=TILE){
            all.add(wall(x,-HALF_H));all.add(wall(x,HALF_H));
        }
        for(float y=-HALF_H+TILE;y<HALF_H;y+=TILE){
            all.add(wall(-HALF_W,y));all.add(wall(HALF_W,y));
        }
        for(Item item:items){
            int law=item.type==BLUE?0:item.type==RED?1:
                item.type==PURPLE?3:item.type==BOX?5:7;
            int signature=item.type==BLUE?0:item.type==RED?1:2;
            all.add(new UnknownWorld.Entity(item.x,item.y,signature,law));
        }
        return all;
    }
    static UnknownWorld.Entity wall(float x,float y){
        return new UnknownWorld.Entity(x,y,2,7);
    }
    /** Solid wall contact, with actual particle projection and tactile pressure.
     * No motor command, goal reward, velocity injection, or neural labels.
     * The outer perimeter is represented by the same opaque fixed tiles.
     */
    static void constrain(MotorBrain.Body body,Morphology.Surface skin,
                          ArrayList<UnknownWorld.Entity> world){
        for(UnknownWorld.Entity e:world){
            if(!e.isTrainingWall())continue;
            float x=e.x,y=e.y,reach=e.radius+145f;
            if(Math.abs(body.cx-x)>reach||Math.abs(body.cy-y)>reach)continue;
            for(int i=0;i<Morphology.POINTS;i++){
                float penetration=project(skin.x,skin.y,skin.vx,skin.vy,i,x,y,e.radius);
                if(penetration>0){
                    float pressure=Math.min(1f,penetration/7f);
                    skin.touchSense[i]=Math.max(skin.touchSense[i],pressure);
                    skin.touchPulse[i]=Math.max(skin.touchPulse[i],pressure);
                }
            }
            for(int i=0;i<MotorBrain.N;i++)
                project(body.x,body.y,body.vx,body.vy,i,x,y,e.radius);
            // The same stationary tile blocks actual boxes/disks; it never
            // applies grip, locomotion or reward to the neural controller.
            for(UnknownWorld.Entity item:world){
                if(item==e||item.consumed||(!item.isBox()&&!item.isMovable()))continue;
                float r=item.isMovable()?item.radius:
                    item.isConstruction()?PanelPhysics.halfLength(item):SquareGripPuzzle.HALF;
                float dx=item.x-x,dy=item.y-y;
                float overlapX=e.radius+r-Math.abs(dx),overlapY=e.radius+r-Math.abs(dy);
                if(overlapX<=0f||overlapY<=0f)continue;
                if(overlapX<overlapY){
                    float direction=dx>=0?1f:-1f;
                    item.x+=direction*overlapX;
                    if(item.vx*direction<0f)item.vx=0f;
                }else{
                    float direction=dy>=0?1f:-1f;
                    item.y+=direction*overlapY;
                    if(item.vy*direction<0f)item.vy=0f;
                }
            }
        }
    }
    private static float project(float[] xs,float[] ys,float[] vx,float[] vy,int i,
                                 float x,float y,float r){
        float dx=xs[i]-x,dy=ys[i]-y;
        if(Math.abs(dx)>=r||Math.abs(dy)>=r)return 0f;
        float px=r-Math.abs(dx),py=r-Math.abs(dy);
        if(px<py){
            float dir=dx>=0?1f:-1f;
            xs[i]=x+dir*(r+.15f);
            if(vx[i]*dir<0)vx[i]=0f;
            return px;
        }else{
            float dir=dy>=0?1f:-1f;
            ys[i]=y+dir*(r+.15f);
            if(vy[i]*dir<0)vy[i]=0f;
            return py;
        }
    }
}
