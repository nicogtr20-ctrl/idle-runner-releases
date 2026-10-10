package com.overmind.zero;
import java.util.*;
/**
 * ZERO-002: learns the consequences of contacting initially unidentified objects.
 *
 * The brain sees only sensor signatures, positions, and its own energy.
 * Hidden physical laws are held inside the world, not exposed to decisions.
 * No object is explicitly named "food", "danger" or "movable" for ZERO.
 */
public final class UnknownWorld {
    public static final int SIGNATURES=3;
    public static final float RARE_ENERGY=80f;
    public static final class Entity {
        public float x,y,vx,vy,cooldown,contactCooldown;
        public boolean consumed=false;
        public final float spawnX,spawnY;
        public boolean everCollided=false, countedAsMoved=false;
        public float peakDisplacement=0;
        public final int signature;
        private final int law;
        public float radius;
        public Entity(float x,float y,int signature,int physicalLaw) {
            this.x=x;this.y=y;this.spawnX=x;this.spawnY=y;
            this.signature=signature;this.law=physicalLaw;
            radius=physicalLaw==2?19:15;
        }
        /** Serialization uses physical identity without exposing it to policy. */
        int physicalLaw(){return law;}
        boolean ready(){return !consumed&&cooldown<=0;}
        void tick(float dt){
            cooldown=Math.max(0,cooldown-dt);
            contactCooldown=Math.max(0,contactCooldown-dt);
            if(law==2){
                x+=vx*dt;y+=vy*dt;
                float drag=(float)Math.exp(-2.8f*dt);
                vx*=drag;vy*=drag;
                peakDisplacement=Math.max(peakDisplacement,displacementFromSpawn());
            }
        }
        public float displacementFromSpawn(){
            return (float)Math.hypot(x-spawnX,y-spawnY);
        }
        boolean isMovable(){return law==2;}
        public boolean isRare(){return law==3;}
        float touch(float directionX,float directionY) {
            if(law==3){
                if(consumed)return 0;
                consumed=true;cooldown=Float.POSITIVE_INFINITY;
                return RARE_ENERGY;
            }
            if(law==0){
                if(consumed)return 0;
                consumed=true;cooldown=Float.POSITIVE_INFINITY;
                return 22;
            }
            if(law==1){cooldown=9;return -7;}
            // NEVER set velocity on touch. The live physics collision solver is
            // the ONLY way a movable object can receive momentum.
            cooldown=1.4f;
            return 0;
        }
        public float displacementEffect(){
            return law==2?(float)Math.hypot(vx,vy):0;
        }
    }
    public static final class Mind {
        public final float[] estimated=new float[SIGNATURES];
        public final int[] contacts=new int[SIGNATURES];
        public int discoveries=0,total=0;
        public final Random r=new Random(131); // policy's exploration (not hidden labels)
        public void observe(int signature,float energyChange,float objectMotion){
            int i=Math.max(0,Math.min(SIGNATURES-1,signature));
            // Pain/energy and change in the surrounding world are intrinsic feedback.
            float outcome=energyChange+Math.min(8,objectMotion*.042f);
            float rate=contacts[i]==0?1f:.24f;
            if(contacts[i]==0)discoveries++;
            estimated[i]+=rate*(outcome-estimated[i]);
            contacts[i]++;total++;
        }
        public Entity choose(ArrayList<Entity> objects,float x,float y,float energy) {
            Entity best=null;float score=-Float.MAX_VALUE;
            for(Entity e:objects){
                if(!e.ready())continue;
                float dist=(float)Math.hypot(e.x-x,e.y-y);
                if(dist>620)continue; // limited sensory radius
                int count=contacts[e.signature];
                float novelty=15f/(float)Math.sqrt(1+count);
                float preference=estimated[e.signature];
                // With abundant energy, curiosity can compete with a
                // familiar rewarding signal. When hungry, survival gains weight.
                // Only observed value, unknown-signature counts and energy
                // are used: hidden object physics is NOT accessible to ZERO.
                float need=Math.max(0,Math.min(1,(100-energy)/75f));
                float value=preference>=0
                    ?preference*(.10f+.90f*need)
                    :preference*(.55f+.45f*need);
                float candidate=value+novelty-dist*.025f+r.nextFloat()*3.1f;
                if(candidate>score){score=candidate;best=e;}
            }
            return best;
        }
        public String serialize(){
            StringBuilder b=new StringBuilder();
            for(int i=0;i<SIGNATURES;i++){
                if(i>0)b.append(';');
                b.append(estimated[i]).append(',').append(contacts[i]);
            }
            return b.toString();
        }
        public void deserialize(String s){
            if(s==null||s.isEmpty())return;
            String[] segments=s.split(";");
            for(int i=0;i<Math.min(SIGNATURES,segments.length);i++){
                try{
                    String[] a=segments[i].split(",");
                    estimated[i]=Float.parseFloat(a[0]);
                    contacts[i]=Math.max(0,Integer.parseInt(a[1]));
                    if(!Float.isFinite(estimated[i]))estimated[i]=0;
                    if(contacts[i]>0)discoveries++;
                    total+=contacts[i];
                }catch(Exception ignored){}
            }
        }
    }
    public static ArrayList<Entity> newHabitat(int episode){
        return newHabitat(episode,0,0);
    }
    /** Spawn surroundings relative to current body; never teleport the body. */
    public static ArrayList<Entity> newHabitat(int episode,float originX,float originY){
        return newHabitat(episode,originX,originY,false);
    }
    /** During shortage, energy is rare and every collected unit disappears.
        A new resource may emerge occasionally, never on every frame. */
    public static ArrayList<Entity> newHabitat(int episode,float originX,float originY,boolean scarce){
        ArrayList<Entity> a=new ArrayList<>();
        float spin=episode*0.39f;
        float[] angles={.2f,2.5f,4.4f,-1.1f,1.7f,-2.2f};
        int[] signals={0,1,2,0,1,2};
        int[] effects={0,1,2,0,1,2};
        for(int i=0;i<angles.length;i++){
            if(scarce){
                // 1 supply every fifth expansion, not infinite energy.
                if(signals[i]==0&&(episode%5!=0||i!=0))continue;
                if(signals[i]==1&&i!=1)continue;
                if(signals[i]==2&&i!=2)continue;
            }
            float angle=angles[i]+spin;
            float d=130+(i%3)*44;
            a.add(new Entity(originX+(float)Math.cos(angle)*d,
                             originY+(float)Math.sin(angle)*d,
                             signals[i],effects[i]));
        }
        return a;
    }
    public static void main(String[] args){
        Mind m=new Mind();
        for(int i=0;i<15;i++){
            m.observe(0,22,0);
            m.observe(1,-18,0);
            m.observe(2,0,105);
        }
        Mind copy=new Mind();copy.deserialize(m.serialize());
        ArrayList<Entity> objects=new ArrayList<>();
        objects.add(new Entity(80,0,1,1));objects.add(new Entity(90,0,0,0));
        Entity chosen=copy.choose(objects,0,0,20);
        if(m.estimated[0]<15||m.estimated[1]>-14||
           copy.contacts[2]!=15||chosen==null||chosen.signature!=0)
            throw new IllegalStateException("World-model experience-learning test failed");
        ArrayList<Entity> translated=newHabitat(4,210,-85);
        ArrayList<Entity> original=newHabitat(4);
        if(translated.size()!=original.size())throw new IllegalStateException("Habitat count changed");
        for(int i=0;i<translated.size();i++){
            Entity e=translated.get(i),z=original.get(i);
            if(Math.abs(e.x-z.x-210)>1e-3||Math.abs(e.y-z.y+85)>1e-3)
                throw new IllegalStateException("Continuous-world spawn offset failed");
        }
        Entity marked=new Entity(0,0,2,2);
        marked.x=19;marked.tick(.025f);
        if(marked.peakDisplacement<18.9f||marked.spawnX!=0)
            throw new IllegalStateException("Object displacement evidence failed");
        Entity rare=new Entity(20,0,0,3);
        if(!rare.isRare()||rare.touch(0,0)!=RARE_ENERGY||rare.touch(0,0)!=0)
            throw new IllegalStateException("Rare reward must be unique, finite and stronger");
        Entity finite=new Entity(30,0,0,0);
        if(finite.touch(0,0)!=22||finite.ready()||finite.touch(0,0)!=0)
            throw new IllegalStateException("A consumed energy pulse must disappear");
        if(newHabitat(7,0,0,true).size()>2)
            throw new IllegalStateException("Shortage still spawns abundant energy");
        System.out.printf(Locale.US,
          "ZERO-002 selftest: known_types=%d contacts=%d learned_values=[%.2f, %.2f, %.2f] preferred_signature=%d%n",
          copy.discoveries,copy.total,copy.estimated[0],copy.estimated[1],copy.estimated[2],chosen.signature);
    }
}
