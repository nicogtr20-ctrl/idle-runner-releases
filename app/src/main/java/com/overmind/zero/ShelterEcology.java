package com.overmind.zero;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * 0122 - physical consequences, not a construction skill or goal oracle.
 * Wind arrives from one real direction and damages EXPOSED living tissue.
 * Placed boxes have only geometric wind shadows. The brain sees tissue
 * pressure, vision and ACTUAL energetic consequences, never "build here".
 *
 * Moved blank materials are archived by their original immutable world
 * position and streamed back when revisited. Only physically placed materials
 * are saved; untouched procedural clutter may still be recycled.
 */
public final class ShelterEcology {
    public static final String SNAPSHOT="shelter_ecology_v122";
    public static final float CYCLE=150f, START=60f, LENGTH=34f, WARNING=11f;
    public static final float BARRIER_HALF=SquareGripPuzzle.HALF;
    public static final float SHADOW_RANGE=165f;
    public static final int MAX_PLACED=96;
    public float integrity=100f,stormDamage=0f,shieldedSeconds=0f,
        stormSeconds=0f,stormEnergyCost=0f;
    private final LinkedHashMap<String,Stored> placed=new LinkedHashMap<>();

    private static final class Stored {
        float sx,sy,x,y,vx,vy,peak,angle,angularVelocity;
        boolean touched;
        Stored(UnknownWorld.Entity e){
            sx=e.spawnX;sy=e.spawnY;x=e.x;y=e.y;
            vx=e.vx;vy=e.vy;peak=e.peakDisplacement;
            angle=e.angle;angularVelocity=e.angularVelocity;
            touched=e.everCollided;
        }
        UnknownWorld.Entity entity(){
            UnknownWorld.Entity e=new UnknownWorld.Entity(sx,sy,2,6);
            e.x=x;e.y=y;e.vx=vx;e.vy=vy;
            e.angle=angle;e.angularVelocity=angularVelocity;
            e.peakDisplacement=peak;e.everCollided=touched;
            // A blank building material NEVER conceals a reward.
            e.countedAsMoved=true;
            return e;
        }
    }
    private static String id(float x,float y){
        return Float.floatToIntBits(x)+":"+Float.floatToIntBits(y);
    }
    public int placedCount(){return placed.size();}
    public void newLife(){integrity=100f;}
    public boolean storm(float t){
        return t>=START&&((t-START)%CYCLE)<LENGTH;
    }
    public boolean warning(float t){
        return t>=START-WARNING&&t<START||
            t>=START&&((t-START)%CYCLE)>=CYCLE-WARNING;
    }
    /** Positive means incoming from left, negative from right. */
    public int wind(float t){
        if(t<START)return 1;
        return (((int)Math.floor((t-START)/CYCLE))&1)==0?1:-1;
    }
    public float untilStorm(float t){
        if(t<START)return START-t;
        float phase=(t-START)%CYCLE;
        return phase<LENGTH?0:CYCLE-phase;
    }
    public static boolean boxBlocks(float x,float y,int wind,
                                    UnknownWorld.Entity box){
        if(box==null||box.consumed||!box.isBox()||box.concealed)return false;
        if(box.isConstruction())return PanelPhysics.shadow(x,y,wind,box,SHADOW_RANGE);
        float dx=(x-box.x)*wind;
        return dx>BARRIER_HALF+1.5f&&dx<SHADOW_RANGE&&
               Math.abs(y-box.y)<BARRIER_HALF+1f;
    }
    private static boolean wallBlocks(float x,float y,int wind,ScarcityLab room){
        if(room==null)return false;
        // A solid room wall casts the same shadow as a real opaque object.
        // Inspect continuous wall segments, not a class label or reward cue.
        float from=x-wind*SHADOW_RANGE;
        float entry=room.entranceX(),far=room.ox+ScarcityLab.END_X;
        float top=room.oy+ScarcityLab.TOP,bottom=room.oy+ScarcityLab.BOTTOM;
        float w=ScarcityLab.WALL_HALF_THICKNESS;
        // Physical rectangles from ScarcityLab.boundaries(), replicated as
        // a pure readout to avoid invoking its private collision method.
        for(int slab=0;slab<5;slab++){
            float left,right,high,low;
            switch(slab){
                case 0:left=entry-w;right=entry+w;high=top-10;low=room.oy-ScarcityLab.OPEN_HALF_HEIGHT;break;
                case 1:left=entry-w;right=entry+w;high=room.oy+ScarcityLab.OPEN_HALF_HEIGHT;low=bottom+10;break;
                case 2:left=entry-w;right=far+w;high=top-10;low=top+w;break;
                case 3:left=entry-w;right=far+w;high=bottom-w;low=bottom+10;break;
                default:left=far-w;right=far+w;high=top-10;low=bottom+10;break;
            }
            if(y>high&&y<low&&
               (wind>0?right<x-2&&right>from:left>x+2&&left<from))return true;
        }
        float l=room.gateX()-ScarcityLab.DOOR_HALF_W,
              right=room.gateX()+ScarcityLab.DOOR_HALF_W;
        return y>room.gateTop()&&y<room.gateBottom()&&
            (wind>0?right<x-2&&right>from:l>x+2&&l<from);
    }
    public static float exposed(float x,float y,int wind,
                                ArrayList<UnknownWorld.Entity> objects,
                                ScarcityLab room){
        if(room!=null&&wallBlocks(x,y,wind,room))return .04f;
        if(objects!=null)for(UnknownWorld.Entity box:objects)
            if(boxBlocks(x,y,wind,box))return .04f;
        return 1f;
    }
    public static final class Exposure {
        public final boolean active,warning;
        public final int wind;
        public final float exposedFraction,energyLoss,integrityChange;
        private Exposure(boolean a,boolean w,int wd,float x,float loss,float change){
            active=a;warning=w;wind=wd;exposedFraction=x;
            energyLoss=loss;integrityChange=change;
        }
    }
    /** Touch pulses and subtle wind impulse represent physically exposed
     * membrane patches; they do NOT manufacture object-contact touchSense.
     * Weather cannot directly select a motor action or create a reward.
     */
    public Exposure step(MotorBrain.Body b,Morphology.Surface skin,
                         ArrayList<UnknownWorld.Entity> objects,ScarcityLab room,
                         float lifeSeconds,float dt){
        boolean active=storm(lifeSeconds),early=warning(lifeSeconds);
        int wd=wind(lifeSeconds);
        if(!Float.isFinite(dt)||dt<=0||b==null||skin==null)
            return new Exposure(active,early,wd,0,0,0);
        if(!active){
            float before=integrity;
            integrity=Math.min(100f,integrity+.085f*dt);
            return new Exposure(false,early,wd,0,0,integrity-before);
        }
        float sum=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float e=exposed(skin.x[i],skin.y[i],wd,objects,room);
            sum+=e;
            // Membrane feel, not tactile contact with a fake obstacle.
            skin.touchPulse[i]=Math.max(skin.touchPulse[i],e*.24f);
            skin.vx[i]+=wd*e*4f*dt;
        }
        float exposedFraction=sum/Morphology.POINTS;
        float sheltered=1f-exposedFraction;
        // Tissue damage is caused by real unshielded area, not an action tag.
        // Repair is possible while largely shielded or between storms.
        float before=integrity;
        integrity=Math.max(0f,Math.min(100f,integrity+
            (.22f*sheltered-.48f*exposedFraction)*dt));
        // Slightly softer metabolic penalty restores time to explore
        // after 0124 clutter; the real unshielded tissue injury and
        // geometric storm-shield benefit remain untouched.
        float loss=(.18f*exposedFraction+
            .09f*(1-integrity/100f))*dt;
        stormDamage+=Math.max(0,before-integrity);
        stormSeconds+=dt;shieldedSeconds+=sheltered*dt;
        stormEnergyCost+=loss;
        return new Exposure(true,false,wd,exposedFraction,loss,integrity-before);
    }
    /**
     * Snapshot ONLY real, moved blank materials. A simple touch, simulated
     * spawn, or invisible rare energy does not become construction progress.
     */
    public void capture(ArrayList<UnknownWorld.Entity> active){
        if(active==null)return;
        for(UnknownWorld.Entity e:active){
            if(e==null||!e.isConstruction()||e.consumed||
               e.displacementFromSpawn()<12f&&Math.abs(e.angle)<.22f)continue;
            String key=id(e.spawnX,e.spawnY);
            if(!placed.containsKey(key)&&placed.size()>=MAX_PLACED){
                String oldest=placed.keySet().iterator().next();
                placed.remove(oldest);
            }
            placed.put(key,new Stored(e));
        }
    }
    /** Reinsert nearby saved physics objects, with original coordinates,
     * without cloning a still active block or colliding with a new room. */
    public int rehydrate(ArrayList<UnknownWorld.Entity> active,float x,float y,
                         ScarcityLab room){
        if(active==null)return 0;
        int added=0;
        for(Stored s:placed.values()){
            if(Math.hypot(s.x-x,s.y-y)>320)continue;
            boolean exists=false;
            for(UnknownWorld.Entity e:active){
                if(e.isConstruction()&&id(e.spawnX,e.spawnY).equals(id(s.sx,s.sy))){
                    exists=true;break;
                }
            }
            if(exists||room!=null&&room.overlapsRoom(s.x,s.y,
                PanelPhysics.halfLength(s.sx,s.sy)+8))
                continue;
            boolean overlaps=false;
            for(UnknownWorld.Entity e:active)
                if(!e.consumed&&!e.concealed&&e.isBox()&&
                   Math.hypot(e.x-s.x,e.y-s.y)<
                      (e.isConstruction()?PanelPhysics.bound(e):SquareGripPuzzle.DIAGONAL)+
                       PanelPhysics.halfLength(s.sx,s.sy)+3f){overlaps=true;break;}
            if(overlaps)continue;
            active.add(s.entity());added++;
            if(added>=8||active.size()>=30)break;
        }
        return added;
    }
    /** Real AABB solid/solid collisions. No automatic "placement" or bonus;
     * when two 40x40 solids overlap, their positions separate along the
     * minimum penetration axis and their normal velocity is damped.
     */
    public static int separateBoxes(ArrayList<UnknownWorld.Entity> active){
        if(active==null)return 0;
        int collisions=0;
        for(int i=0;i<active.size();i++){
            UnknownWorld.Entity a=active.get(i);
            if(!a.isBilateralBox()||a.consumed)continue;
            for(int j=i+1;j<active.size();j++){
                UnknownWorld.Entity b=active.get(j);
                if(!b.isBilateralBox()||b.consumed)continue;
                float dx=b.x-a.x,dy=b.y-a.y;
                float px=SquareGripPuzzle.HALF*2-Math.abs(dx);
                float py=SquareGripPuzzle.HALF*2-Math.abs(dy);
                if(px<=0||py<=0)continue;
                // Prior code moved BOTH energy covers on any overlap,
                // bypassing their bilateral-grip law and revealing rewards.
                boolean moveA=a.bilateralHeldNow,moveB=b.bilateralHeldNow;
                if(!moveA&&!moveB)continue;
                float shareA=moveA?(moveB?.5f:1f):0f;
                float shareB=moveB?(moveA?.5f:1f):0f;
                if(px<py){
                    float signed=dx>=0?1f:-1f;
                    a.x-=signed*px*shareA;b.x+=signed*px*shareB;
                    if(moveA&&moveB){
                        float avg=(a.vx+b.vx)*.5f;a.vx=avg;b.vx=avg;
                    }else if(moveA)a.vx=0f;
                    else b.vx=0f;
                }else{
                    float signed=dy>=0?1f:-1f;
                    a.y-=signed*py*shareA;b.y+=signed*py*shareB;
                    if(moveA&&moveB){
                        float avg=(a.vy+b.vy)*.5f;a.vy=avg;b.vy=avg;
                    }else if(moveA)a.vy=0f;
                    else b.vy=0f;
                }
                collisions++;
            }
        }
        return collisions+PanelPhysics.separate(active);
    }
    private static boolean valid(float n){
        return Float.isFinite(n)&&Math.abs(n)<1000000f;
    }
    public String encode(){
        StringBuilder b=new StringBuilder("se2|").append(integrity).append('|')
            .append(stormDamage).append('|').append(shieldedSeconds).append('|')
            .append(stormSeconds).append('|').append(stormEnergyCost).append('|')
            .append(placed.size());
        for(Stored s:placed.values()){
            b.append('|').append(s.sx).append(',').append(s.sy).append(',')
             .append(s.x).append(',').append(s.y).append(',')
             .append(s.vx).append(',').append(s.vy).append(',')
             .append(s.peak).append(',').append(s.touched?1:0).append(',')
             .append(s.angle).append(',').append(s.angularVelocity);
        }
        return b.toString();
    }
    public boolean restore(String serial){
        if(serial==null||serial.length()>80000||
           (!serial.startsWith("se1|")&&!serial.startsWith("se2|")))return false;
        boolean legacy=serial.startsWith("se1|");
        try{
            String[] p=serial.split("\\|",-1);
            if(p.length<7)return false;
            float health=Float.parseFloat(p[1]),dam=Float.parseFloat(p[2]),
                safe=Float.parseFloat(p[3]),storms=Float.parseFloat(p[4]),
                energy=Float.parseFloat(p[5]);
            int count=Integer.parseInt(p[6]);
            if(!valid(health)||health<0||health>100||
               !valid(dam)||dam<0||!valid(safe)||safe<0||
               !valid(storms)||storms<0||safe>storms+.01f||
               !valid(energy)||energy<0||count<0||count>MAX_PLACED||
               p.length!=7+count)return false;
            LinkedHashMap<String,Stored> temp=new LinkedHashMap<>();
            for(int i=0;i<count;i++){
                String[] a=p[7+i].split(",",-1);
                if(a.length!=(legacy?8:10))return false;
                float[] v=new float[7];
                for(int k=0;k<7;k++){
                    v[k]=Float.parseFloat(a[k]);
                    if(!valid(v[k]))return false;
                }
                if(!"0".equals(a[7])&&!"1".equals(a[7]))return false;
                float rotation=legacy?0f:Float.parseFloat(a[8]);
                float spin=legacy?0f:Float.parseFloat(a[9]);
                if(!valid(rotation)||!valid(spin)||Math.abs(spin)>20f)return false;
                if(Math.hypot(v[2]-v[0],v[3]-v[1])<11.9f&&
                   (legacy||Math.abs(rotation)<.22f)||v[6]<0)return false;
                UnknownWorld.Entity e=new UnknownWorld.Entity(v[0],v[1],2,6);
                e.x=v[2];e.y=v[3];e.vx=v[4];e.vy=v[5];
                e.peakDisplacement=v[6];e.everCollided="1".equals(a[7]);
                e.angle=rotation;e.angularVelocity=spin;
                String key=id(e.spawnX,e.spawnY);
                if(temp.containsKey(key))return false;
                temp.put(key,new Stored(e));
            }
            placed.clear();placed.putAll(temp);integrity=health;
            stormDamage=dam;shieldedSeconds=safe;
            stormSeconds=storms;stormEnergyCost=energy;
            return true;
        }catch(RuntimeException ignored){return false;}
    }
}
