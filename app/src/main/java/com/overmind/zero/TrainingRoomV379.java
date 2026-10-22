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
    /**
     * V380: every Training tile shares the existing physical AABB CCD, not
     * a different weak overlap-only rule. Wall contact is an observable
     * material constraint, never an issued heading, neural reward or impulse.
     */
    static void constrain(MotorBrain.Body body,Morphology.Surface skin,
                          ArrayList<UnknownWorld.Entity> world){
        constrain(body,skin,world,null,null,null,null);
    }
    static void constrain(MotorBrain.Body body,Morphology.Surface skin,
                          ArrayList<UnknownWorld.Entity> world,
                          float[] oldSkinX,float[] oldSkinY,
                          float[] oldBodyX,float[] oldBodyY){
        for(UnknownWorld.Entity e:world){
            if(!e.isTrainingWall()||e.consumed)continue;
            float x=e.x,y=e.y,r=e.radius;
            // Body centroid bounds are only an acceleration; nothing is
            // projected from the centroid or pushed toward room center.
            if(Math.abs(body.cx-x)>r+170f||Math.abs(body.cy-y)>r+170f)continue;
            float l=x-r,t=y-r,right=x+r,bottom=y+r;
            for(int i=0;i<Morphology.POINTS;i++){
                float px=skin.x[i],py=skin.y[i];
                boolean hit=SquareCollisionSolver.project(
                    skin.x,skin.y,skin.vx,skin.vy,i,
                    oldSkinX==null?px:oldSkinX[i],
                    oldSkinY==null?py:oldSkinY[i],
                    l,t,right,bottom,0f);
                if(hit){
                    skin.touchSense[i]=Math.max(skin.touchSense[i],1f);
                    skin.touchPulse[i]=Math.max(skin.touchPulse[i],.9f);
                    skin.gripHold[i]=0f;
                }
            }
            for(int i=0;i<MotorBrain.N;i++){
                float px=body.x[i],py=body.y[i];
                SquareCollisionSolver.project(body.x,body.y,body.vx,body.vy,i,
                    oldBodyX==null?px:oldBodyX[i],
                    oldBodyY==null?py:oldBodyY[i],
                    l,t,right,bottom,1.5f);
            }
            // Prevent a membrane/body connection from passing through a tile
            // even when both real endpoints lie beyond its opposite faces.
            repairLinks(skin.x,skin.y,skin.vx,skin.vy,
                        Morphology.POINTS,e,skin);
            repairLinks(body.x,body.y,body.vx,body.vy,MotorBrain.N,e,null);
            // Existing material disks and covers cannot tunnel through walls.
            for(UnknownWorld.Entity item:world){
                if(item==e||item.consumed||
                   (!item.isBox()&&!item.isMovable()))continue;
                float size=item.isMovable()?item.radius:
                    item.isConstruction()?PanelPhysics.halfLength(item):
                    SquareGripPuzzle.HALF;
                float dx=item.x-x,dy=item.y-y;
                float overlapX=r+size-Math.abs(dx),overlapY=r+size-Math.abs(dy);
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
    static void constrainInner(SymbioteMatterV1 gel,
                               UnknownWorld.Entity tile,float dt){
        if(gel==null||tile==null||!tile.isTrainingWall()||dt<=0)return;
        float r=tile.radius;
        for(int i=0;i<SymbioteMatterV1.INNER;i++){
            float x=gel.x[i],y=gel.y[i],oldX=x-gel.vx[i]*dt,oldY=y-gel.vy[i]*dt;
            // Swept bounding segment test. Absolute distances at BOTH ends
            // cannot exclude a crossing (one mass may tunnel wall-to-wall).
            if(Math.max(x,oldX)<tile.x-r-4f||
               Math.min(x,oldX)>tile.x+r+4f||
               Math.max(y,oldY)<tile.y-r-4f||
               Math.min(y,oldY)>tile.y+r+4f)continue;
            SquareCollisionSolver.project(gel.x,gel.y,gel.vx,gel.vy,i,
                oldX,oldY,tile.x-r,tile.y-r,tile.x+r,tile.y+r,1.5f);
        }
    }
    private static void repairLinks(float[] xs,float[] ys,float[] vx,float[] vy,
                                    int length,UnknownWorld.Entity tile,
                                    Morphology.Surface skin){
        float r=tile.radius;
        float l=tile.x-r,t=tile.y-r,right=tile.x+r,bottom=tile.y+r;
        for(int i=0;i<length;i++){
            int j=(i+1)%length;
            if(!ScarcityLab.crossesInterior(xs[i],ys[i],xs[j],ys[j],
                                            l,t,right,bottom))continue;
            // Shared supporting face with least actual displacement.
            float leftCost=Math.max(0,xs[i]-l)+Math.max(0,xs[j]-l);
            float rightCost=Math.max(0,right-xs[i])+Math.max(0,right-xs[j]);
            float topCost=Math.max(0,ys[i]-t)+Math.max(0,ys[j]-t);
            float bottomCost=Math.max(0,bottom-ys[i])+Math.max(0,bottom-ys[j]);
            int side=0;float shortest=leftCost;
            if(rightCost<shortest){side=1;shortest=rightCost;}
            if(topCost<shortest){side=2;shortest=topCost;}
            if(bottomCost<shortest)side=3;
            for(int k=0;k<2;k++){
                int n=k==0?i:j;
                if(side==0&&xs[n]>l){xs[n]=l;vx[n]=Math.min(vx[n],0);}
                else if(side==1&&xs[n]<right){xs[n]=right;vx[n]=Math.max(vx[n],0);}
                else if(side==2&&ys[n]>t){ys[n]=t;vy[n]=Math.min(vy[n],0);}
                else if(side==3&&ys[n]<bottom){ys[n]=bottom;vy[n]=Math.max(vy[n],0);}
                if(skin!=null){
                    skin.gripHold[n]=0f;skin.touchSense[n]=1f;
                    skin.touchPulse[n]=Math.max(skin.touchPulse[n],.9f);
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
