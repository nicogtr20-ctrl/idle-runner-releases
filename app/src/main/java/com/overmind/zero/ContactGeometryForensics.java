package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Passive forensic evidence for an apparently immobile body whose centroid
 * trail seems to collect resources. This class is NEVER read by a policy,
 * muscle, collision or energy calculation. In particular, trail dots have
 * no contact geometry or ability to collect.
 *
 * Stored with the working profile and voluntarily uploaded via the already
 * existing explicit-share pipeline. No background network or time-series
 * scans. Both buffers and text size are strictly bounded.
 */
public final class ContactGeometryForensics {
    public static final String KEY="contact_geometry_forensics_v127";
    private static final int MAX_PICKUPS=18, MAX_TRAPS=9;
    private static final int MAX_BYTES=75000;
    private final ArrayDeque<String> pickups=new ArrayDeque<>();
    private final ArrayDeque<String> traps=new ArrayDeque<>();
    private float nextTrapAt=-1f;
    public long pickupEvents=0,trapEvents=0;

    private static int q(float f){
        if(!Float.isFinite(f))return 0;
        return (int)Math.max(-100000000,Math.min(100000000,Math.round(f*10f)));
    }
    private static void bounded(ArrayDeque<String> dest,String record,int capacity){
        dest.addLast(record);
        while(dest.size()>capacity)dest.removeFirst();
    }
    private void enforceMaxBytes(){
        while(encode().length()>MAX_BYTES){
            if(!traps.isEmpty()&&(pickups.isEmpty()||traps.size()>2))
                traps.removeFirst();
            else if(!pickups.isEmpty())pickups.removeFirst();
            else if(!traps.isEmpty())traps.removeFirst();
            else break;
        }
    }
    private static float nearestMass(MotorBrain.Body body,float x,float y){
        float best=Float.POSITIVE_INFINITY;
        for(int i=0;i<MotorBrain.N;i++)
            best=Math.min(best,(float)Math.hypot(body.x[i]-x,body.y[i]-y));
        return best;
    }
    private static float nearestPatch(Morphology.Surface skin,float x,float y){
        float best=Float.POSITIVE_INFINITY;
        for(int i=0;i<Morphology.POINTS;i++)
            best=Math.min(best,(float)Math.hypot(skin.x[i]-x,skin.y[i]-y));
        return best;
    }
    private static String evidence(MotorBrain.Body body,Morphology.Surface skin,
                                   float x,float y,float radius){
        float r=radius+2f;
        boolean interior=BodyIntegrity.encloses(skin.x,skin.y,x,y);
        float edge=(float)Math.sqrt(MaterialGeometry.surfaceDistanceSquared(skin,x,y));
        float mass=nearestMass(body,x,y);
        float patch=nearestPatch(skin,x,y);
        float nearest=Math.min(mass,patch);
        boolean actual=MaterialGeometry.touches(body,skin,x,y,r);
        boolean fallback=nearest<radius+18f;
        // Test the exact existing order, not a substitute guess. Record
        // simultaneous predicates so a phantom 18-unit pickup is detectable.
        String mode=interior?"interior":edge<r?"membraneEdge":
                    mass<r?"internalMass":fallback?"extra18":"NONE";
        return "source="+mode+",material="+(actual?1:0)+
            ",extra="+(fallback?1:0)+",edge="+q(edge)+",mass="+q(mass)+
            ",patch="+q(patch)+",near="+q(nearest);
    }
    /** Complete physical coordinates relative to source/centroid at pickup.
     * Patches and masses are NOT interchangeable with the decorative trail.
     * The real camera anchor and trail coordinates are also recorded. */
    private static void geometry(StringBuilder b,MotorBrain.Body body,
                                 Morphology.Surface skin,float originX,
                                 float originY,float[] trailX,float[] trailY,
                                 int trailCount){
        b.append(";centroid=").append(q(body.cx)).append(',').append(q(body.cy));
        b.append(";mass=");
        for(int i=0;i<MotorBrain.N;i++){
            if(i>0)b.append('/');
            b.append(q(body.x[i]-originX)).append(':').append(q(body.y[i]-originY));
        }
        b.append(";membrane=");
        for(int i=0;i<Morphology.POINTS;i++){
            if(i>0)b.append('/');
            b.append(q(skin.x[i]-originX)).append(':').append(q(skin.y[i]-originY));
        }
        b.append(";trail=");
        int n=trailX==null||trailY==null?0:
            Math.min(Math.min(trailCount,trailX.length),trailY.length);
        for(int i=0;i<n;i++){
            if(i>0)b.append('/');
            b.append(q(trailX[i]-originX)).append(':').append(q(trailY[i]-originY));
        }
        int held=0,red=0;
        for(int i=0;i<Morphology.POINTS;i++){
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD)held++;
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD&&skin.touchSense[i]>.5f)red++;
        }
        b.append(";held=").append(held).append(";redSites=").append(red);
        b.append(";crossings=").append(MaterialGeometry.selfCrossings(skin));
    }
    /** Caller invokes ONLY after a truly positive real pickup, never on
     * visual proximity or a theoretical target. No resources are created,
     * removed or moved by this method. */
    public void pickup(int life,float time,int chosen,MotorBrain.Body body,
                       Morphology.Surface skin,UnknownWorld.Entity source,
                       float gain,float[] trailX,float[] trailY,int trailCount,
                       String category){
        if(body==null||skin==null||source==null||gain<=0)return;
        StringBuilder b=new StringBuilder(2900);
        b.append("P,").append(life).append(',').append(q(time)).append(',')
         .append(chosen).append(',').append(category).append(',')
         .append(q(gain)).append(",law=").append(source.physicalLaw())
         .append(",gem=").append(q(source.x)).append(',').append(q(source.y))
         .append(",radius=").append(q(source.radius)).append(',')
         .append(evidence(body,skin,source.x,source.y,source.radius));
        geometry(b,body,skin,source.x,source.y,trailX,trailY,trailCount);
        bounded(pickups,b.toString(),MAX_PICKUPS);
        pickupEvents++;
        enforceMaxBytes();
    }
    /** Samples at most once every 8 simulated seconds; never each frame.
     * Only two distinct physically held square covers qualify. Captures
     * overlap of per-box patch ownership, not a generic red pixel count. */
    public boolean trap(int life,float time,int action,MotorBrain.Body body,
                        Morphology.Surface skin,ArrayList<UnknownWorld.Entity> world,
                        float[] trailX,float[] trailY,int trailCount){
        if(body==null||skin==null||world==null||time<0)return false;
        if(nextTrapAt>=0&&time<nextTrapAt)return false;
        nextTrapAt=time+8f;
        UnknownWorld.Entity first=null,second=null;
        int firstN=0,secondN=0;
        for(UnknownWorld.Entity box:world){
            if(!box.isBilateralBox()||box.consumed)continue;
            int n=0;
            for(int i=0;i<Morphology.POINTS;i++)
                if(box.boxGripHold[i]>=Morphology.GRIP_HOLD)n++;
            if(n>=4&&n>firstN){
                second=first;secondN=firstN;first=box;firstN=n;
            }else if(n>=4&&n>secondN){
                second=box;secondN=n;
            }
        }
        if(second==null)return false;
        int simultaneous=0;
        for(int i=0;i<Morphology.POINTS;i++)
            if(first.boxGripHold[i]>=Morphology.GRIP_HOLD&&
               second.boxGripHold[i]>=Morphology.GRIP_HOLD)simultaneous++;
        StringBuilder b=new StringBuilder(2900);
        b.append("T,").append(life).append(',').append(q(time)).append(',')
         .append(action).append(",boxA=").append(q(first.x)).append(':')
         .append(q(first.y)).append(':').append(firstN)
         .append(",boxB=").append(q(second.x)).append(':')
         .append(q(second.y)).append(':').append(secondN)
         .append(",shared=").append(simultaneous);
        geometry(b,body,skin,body.cx,body.cy,trailX,trailY,trailCount);
        bounded(traps,b.toString(),MAX_TRAPS);
        trapEvents++;
        enforceMaxBytes();
        return true;
    }
    public void newLife(){nextTrapAt=-1f;}
    public String encode(){
        StringBuilder b=new StringBuilder("cg1|")
            .append(pickupEvents).append('|').append(trapEvents).append('\n');
        for(String e:pickups)b.append(e).append('\n');
        for(String e:traps)b.append(e).append('\n');
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.isEmpty())return true;
        if(raw.length()>MAX_BYTES+2000||!raw.startsWith("cg1|"))return false;
        try{
            String[] lines=raw.split("\n");
            String[] head=lines[0].split("\\|");
            if(head.length!=3)return false;
            long p=Long.parseLong(head[1]),t=Long.parseLong(head[2]);
            if(p<0||t<0||p>1000000000L||t>1000000000L)return false;
            ArrayDeque<String> ps=new ArrayDeque<>(),ts=new ArrayDeque<>();
            for(int i=1;i<lines.length;i++){
                String line=lines[i];
                if(line.length()>7000)return false;
                if(line.startsWith("P,"))bounded(ps,line,MAX_PICKUPS);
                else if(line.startsWith("T,"))bounded(ts,line,MAX_TRAPS);
                else return false;
            }
            pickups.clear();pickups.addAll(ps);
            traps.clear();traps.addAll(ts);
            pickupEvents=p;trapEvents=t;nextTrapAt=-1f;
            return true;
        }catch(RuntimeException ex){return false;}
    }
    private static void yes(boolean x,String message){
        if(!x)throw new AssertionError(message);
    }
    public static void main(String[] args){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        ContactGeometryForensics recorder=new ContactGeometryForensics();
        UnknownWorld.Entity energy=new UnknownWorld.Entity(b.x[0],b.y[0],0,0);
        float origX=b.x[0],origY=b.y[0],origMass=b.x[1],origSkin=s.x[1];
        float[] tx={-10f,5f},ty={-10f,20f};
        recorder.pickup(2,9.25f,11,b,s,energy,22,tx,ty,2,"habitat");
        String log=recorder.encode();
        yes(log.contains("P,2,93,11,habitat,220"),"Pickup missing");
        yes(log.contains("source=")&&log.contains(";mass=")&&
            log.contains(";membrane=")&&log.contains(";trail="),
            "Cannot audit true physical contact vs trail");
        yes(b.x[0]==origX&&b.y[0]==origY&&b.x[1]==origMass&&s.x[1]==origSkin,
            "Passive observer changed real material");
        UnknownWorld.Entity aa=new UnknownWorld.Entity(10,10,2,5);
        UnknownWorld.Entity bb=new UnknownWorld.Entity(45,10,2,5);
        for(int i=0;i<96;i++){
            aa.boxGripHold[i]=Morphology.GRIP_HOLD+.1f;
            bb.boxGripHold[i]=Morphology.GRIP_HOLD+.1f;
        }
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        world.add(aa);world.add(bb);
        yes(recorder.trap(2,10,11,b,s,world,tx,ty,2),
            "Simultaneous 96-patch dual-box bind not reported");
        yes(!recorder.trap(2,12,11,b,s,world,tx,ty,2),
            "Recorder polling too frequently");
        yes(recorder.encode().contains("shared=96"),
            "Double ownership of the same tissue was hidden");
        ContactGeometryForensics restored=new ContactGeometryForensics();
        yes(restored.restore(recorder.encode())&&
            restored.encode().equals(recorder.encode()),
            "Geom forensic save/reload lost contact or trap");
        yes(!restored.restore("cg1|corrupt|0\n")&&
            restored.encode().equals(recorder.encode()),
            "Bad diagnostic data damaged good history");
        for(int i=0;i<100;i++)
            recorder.pickup(2,i,11,b,s,energy,22,tx,ty,2,"habitat");
        yes(recorder.encode().length()<=MAX_BYTES&&
            recorder.encode().split("\n").length<=MAX_PICKUPS+MAX_TRAPS+1,
            "Forensic snapshots grew without bound");
        System.out.println("ContactGeometryForensics PASS: reason, 10 real masses, 96 patches, centroid trail, 2-box entrapment, bounded persistence, no body changes");
    }
}
