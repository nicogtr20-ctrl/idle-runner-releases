package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO169: durable evidence-gated motor skill MEMORY for the living symbiote.
 * Does NOT choose a world action, supply a gait, add thrust, or train neurons.
 *
 * A skill is a snapshot of a motor configuration that actually generated
 * net movement ALONG the brain's own chosen heading. A TRAIN sample only
 * nominates a candidate. TWO separate HELDOUT real-physics decisions from
 * TWO distinct lives are needed before it can ever be recovered.
 *
 * A prolonged inability to realize the SAME brain-chosen action may restore
 * a previously verified motor configuration. The 2.2 brain, its 40 neurons,
 * memories, reward, headings and proposed actions are NEVER changed.
 */
public final class SymbioteMotorSkillMemoryV169 {
    public static final String KEY="symbiote_motor_skills_v169";
    static final int SHARED=84,REGIONAL=320,FAST=FastSomaticCircuitV167.PARAMETERS;
    public static final int WIDTH=SHARED+REGIONAL+FAST,MAX_CHAMPIONS=4,MAX_CANDIDATES=6;
    private static final int A=RecurrentCoreV22.A;
    private static final float MIN_FORWARD=1.8f,MIN_RATIO=.38f;
    private static final float MAX_SNAPSHOT_RMS=.036f;
    private final ArrayList<Entry> candidates=new ArrayList<>(),champions=new ArrayList<>();
    private final int[] badStreak=new int[A];
    private final float[] trial=new float[WIDTH];
    private int action=-1,life=1,validFrames=0,cooldown=0;
    private float netX,netY,gross,angularMotion;
    private long samples=0,heldoutSamples=0,confirmed=0,recoveries=0,discarded=0;
    private static final class Entry{
        int action,firstLife,lastLife,verifications,failed;
        float quality;
        final float[] config=new float[WIDTH];
        Entry(int a,int life,float score,float[] w){
            action=a;firstLife=lastLife=life;quality=score;
            System.arraycopy(w,0,config,0,WIDTH);
        }
        Entry copy(){
            Entry e=new Entry(action,firstLife,quality,config);
            e.lastLife=lastLife;e.verifications=verifications;e.failed=failed;
            return e;
        }
    }
    private static float clip(float v,float a,float b){return Math.max(a,Math.min(b,v));}
    private static boolean valid(float[] w){
        if(w==null||w.length!=WIDTH)return false;
        for(int i=0;i<WIDTH;i++){
            float v=w[i],limit=i<SHARED?2.501f:
                i<SHARED+REGIONAL?1.501f:1.101f;
            if(!Float.isFinite(v)||Math.abs(v)>limit)return false;
        }
        return true;
    }
    static float distance(float[] a,float[] b){
        if(!valid(a)||!valid(b))return Float.POSITIVE_INFINITY;
        double sum=0;
        for(int i=0;i<WIDTH;i++){
            double x=a[i]-b[i];sum+=x*x;
        }
        return (float)Math.sqrt(sum/WIDTH);
    }
    public int verifiedCount(){return champions.size();}
    public int candidatesCount(){return candidates.size();}
    public long confirmedCount(){return confirmed;}
    public long recoveryCount(){return recoveries;}
    public long rejectedCount(){return discarded;}
    public int currentLife(){return life;}
    public long measuredDecisions(){return samples;}
    public long measuredHeldouts(){return heldoutSamples;}
    public boolean isProvenFor(int action){
        for(Entry e:champions)if(e.action==action)return true;
        return false;
    }
    public void newLife(){
        if(life<1000000000)life++;
        cancelTrial();
        Arrays.fill(badStreak,0);
    }
    public void resumeLife(){cancelTrial();}
    public void cancelTrial(){action=-1;netX=netY=gross=angularMotion=0f;validFrames=0;}
    public void begin(int chosen,float[] shared,float[] local,float[] fast){
        cancelTrial();
        if(chosen<0||chosen>=A||shared==null||shared.length!=SHARED||
           local==null||local.length!=REGIONAL||fast==null||fast.length!=FAST)
            return;
        System.arraycopy(shared,0,trial,0,SHARED);
        System.arraycopy(local,0,trial,SHARED,REGIONAL);
        System.arraycopy(fast,0,trial,SHARED+REGIONAL,FAST);
        if(valid(trial))action=chosen;
    }
    /** Every REAL post-collision physics frame. Relative membrane rotation
     * is only a regularizer; never counts as net movement or earned energy. */
    public void physical(float oldX,float oldY,MotorBrain.Body body,
                         Morphology.Surface skin){
        if(action<0||body==null||skin==null)return;
        float dx=body.cx-oldX,dy=body.cy-oldY;
        if(!Float.isFinite(dx)||!Float.isFinite(dy))return;
        float m=(float)Math.hypot(dx,dy);
        if(m>3f)return; // rejected teleport/reset/collision spike
        netX+=dx;netY+=dy;gross+=m;validFrames++;
        double torque=0,inertia=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float x=skin.x[i]-body.cx,y=skin.y[i]-body.cy;
            float vx=skin.vx[i]-body.cvx,vy=skin.vy[i]-body.cvy;
            torque+=x*vy-y*vx;inertia+=x*x+y*y;
        }
        if(inertia>1e-3){
            float omega=(float)(torque/inertia);
            if(Float.isFinite(omega))angularMotion+=clip(Math.abs(omega)*.025f,0f,.25f);
        }
    }
    private static float score(float forward,float gross,float spin,float cost){
        if(!Float.isFinite(forward)||!Float.isFinite(gross)||
           !Float.isFinite(spin)||!Float.isFinite(cost)||gross<1f)return 0f;
        float ratio=clip(forward/Math.max(2f,gross),-1f,1f);
        float purposeful=clip(forward/5f,0f,1f);
        return clip(.7f*ratio+.3f*purposeful-
            .10f*clip(spin,0f,3f)-
            .06f*clip(cost/10f,0f,1f),-1f,1f);
    }
    private Entry match(ArrayList<Entry> list,int chosen,float[] snap){
        Entry nearest=null;float distance=MAX_SNAPSHOT_RMS;
        for(Entry entry:list){
            if(entry.action!=chosen)continue;
            float d=distance(entry.config,snap);
            if(d<distance){nearest=entry;distance=d;}
        }
        return nearest;
    }
    /**
     * End of a REAL 2.2 decision. Every potential good movement is scored,
     * but ONLY ordinary TRAIN outcomes nominate skills; only independent
     * HELDOUT outcomes can confirm them. Never train on heldout labels.
     */
    public void finish(boolean train,boolean heldout,float realMotorEnergy){
        if(action<0)return;
        int a=action;
        float direction=a%8*(float)Math.PI/4f;
        float forward=netX*(float)Math.cos(direction)+
                      netY*(float)Math.sin(direction);
        float quality=score(forward,gross,angularMotion,Math.max(0f,realMotorEnergy));
        boolean actualProgress=validFrames>=4&&gross>=2.8f&&
            forward>=MIN_FORWARD&&forward/gross>=MIN_RATIO&&
            quality>=.39f;
        samples++;
        if(heldout)heldoutSamples++;
        if(cooldown>0)cooldown--;
        if(actualProgress)badStreak[a]=0;
        else if(badStreak[a]<100)badStreak[a]++;
        if(train&&!heldout&&actualProgress){
            Entry similar=match(candidates,a,trial);
            if(similar==null){
                if(candidates.size()>=MAX_CANDIDATES){
                    int worst=0;
                    for(int j=1;j<candidates.size();j++)
                        if(candidates.get(j).quality<candidates.get(worst).quality)
                            worst=j;
                    if(quality<=candidates.get(worst).quality){
                        discarded++;cancelTrial();return;
                    }
                    candidates.remove(worst);
                }
                candidates.add(new Entry(a,life,quality,trial));
            }else{
                // A candidate's actual trained configuration is immutable.
                // Only the quality estimate improves; otherwise the heldout
                // experiment would validate a moving target.
                similar.quality=Math.max(similar.quality,quality);
            }
        }else if(heldout){
            Entry e=match(candidates,a,trial);
            if(e!=null){
                if(actualProgress&&quality>=.39f){
                    // At most ONE corroboration per life; heldout counts
                    // are not independent simply because 100 frames elapsed.
                    if(e.lastLife!=life){
                        e.verifications++;
                        e.lastLife=life;
                        e.quality=.8f*e.quality+.2f*quality;
                    }
                    if(e.verifications>=2&&life>e.firstLife+1){
                        promote(e);
                        candidates.remove(e);
                    }
                }else if(++e.failed>=3){
                    candidates.remove(e);discarded++;
                }
            }
        }
        cancelTrial();
    }
    private void promote(Entry e){
        Entry same=match(champions,e.action,e.config);
        if(same!=null){
            if(e.quality>same.quality){
                System.arraycopy(e.config,0,same.config,0,WIDTH);
                same.quality=e.quality;
                same.verifications=e.verifications;
                same.lastLife=e.lastLife;
            }
            confirmed++;return;
        }
        if(champions.size()>=MAX_CHAMPIONS){
            int worst=0;
            for(int i=1;i<champions.size();i++)
                if(champions.get(i).quality<champions.get(worst).quality)
                    worst=i;
            if(e.quality<=champions.get(worst).quality){
                discarded++;return;
            }
            champions.remove(worst);
        }
        champions.add(e.copy());confirmed++;
    }
    /**
     * Motor-memory recovery is offered ONLY when the brain has ALREADY
     * selected this action and at least four consecutive real attempts to
     * realize that action have failed. No covert choice change, no gait.
     * Returns null rather than installing an unverified or identical cortex.
     */
    public float[] recoverIfStalled(int chosen,float[] current){
        if(chosen<0||chosen>=A||cooldown>0||badStreak[chosen]<4||
           !valid(current))return null;
        Entry best=null;
        for(Entry e:champions)
            if(e.action==chosen&&(best==null||e.quality>best.quality))
                best=e;
        if(best==null||distance(best.config,current)<.005f)return null;
        cooldown=32;
        badStreak[chosen]=0;
        recoveries++;
        return best.config.clone();
    }
    public String encode(){
        StringBuilder s=new StringBuilder("ms169|").append(life).append('|')
            .append(samples).append('|').append(heldoutSamples).append('|')
            .append(confirmed).append('|').append(recoveries).append('|')
            .append(discarded).append('|').append(champions.size()).append('|')
            .append(candidates.size());
        for(Entry e:champions)appendEntry(s,e);
        for(Entry e:candidates)appendEntry(s,e);
        return s.toString();
    }
    private static void appendEntry(StringBuilder b,Entry e){
        b.append('|').append(e.action).append(',').append(e.firstLife)
            .append(',').append(e.lastLife).append(',').append(e.verifications)
            .append(',').append(e.failed).append(',').append(e.quality);
        for(float w:e.config)b.append(',').append(w);
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("ms169|")||raw.length()>155000)return false;
        try{
            String[] parts=raw.split("\\|",-1);
            if(parts.length<9)return false;
            int l=Integer.parseInt(parts[1]);
            long samplesRead=Long.parseLong(parts[2]),holdRead=Long.parseLong(parts[3]),
                confirmedRead=Long.parseLong(parts[4]),recovRead=Long.parseLong(parts[5]),
                rejectedRead=Long.parseLong(parts[6]);
            int nc=Integer.parseInt(parts[7]),np=Integer.parseInt(parts[8]);
            if(l<1||l>1000000000||nc<0||nc>MAX_CHAMPIONS||
               np<0||np>MAX_CANDIDATES||parts.length!=9+nc+np||
               samplesRead<0||holdRead<0||holdRead>samplesRead||
               confirmedRead<0||recovRead<0||rejectedRead<0||
               confirmedRead>samplesRead||recovRead>samplesRead||
               samplesRead>1000000000L)return false;
            ArrayList<Entry> good=new ArrayList<>(),tentative=new ArrayList<>();
            for(int n=0;n<nc+np;n++){
                String[] q=parts[9+n].split(",",-1);
                if(q.length!=6+WIDTH)return false;
                int a=Integer.parseInt(q[0]),first=Integer.parseInt(q[1]),
                    last=Integer.parseInt(q[2]),verified=Integer.parseInt(q[3]),
                    failed=Integer.parseInt(q[4]);
                float quality=Float.parseFloat(q[5]);
                if(a<0||a>=A||first<1||first>l||last<first||last>l||
                   verified<0||verified>l||failed<0||failed>3||
                   (n<nc&&verified<2)||!Float.isFinite(quality)||
                   quality<.39f||quality>1.001f)return false;
                float[] w=new float[WIDTH];
                for(int i=0;i<WIDTH;i++)w[i]=Float.parseFloat(q[6+i]);
                if(!valid(w))return false;
                Entry e=new Entry(a,first,quality,w);
                e.lastLife=last;e.verifications=verified;e.failed=failed;
                (n<nc?good:tentative).add(e);
            }
            champions.clear();champions.addAll(good);
            candidates.clear();candidates.addAll(tentative);
            life=l;samples=samplesRead;heldoutSamples=holdRead;
            confirmed=confirmedRead;recoveries=recovRead;discarded=rejectedRead;
            cooldown=0;Arrays.fill(badStreak,0);cancelTrial();
            return true;
        }catch(RuntimeException bad){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "SYMBIOTE verified motor memory: skills=%d/%d candidates=%d heldout=%d recalls=%d",
            champions.size(),MAX_CHAMPIONS,candidates.size(),heldoutSamples,recoveries);
    }
}
