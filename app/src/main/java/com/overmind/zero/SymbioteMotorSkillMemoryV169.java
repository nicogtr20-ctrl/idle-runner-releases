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
    public static final int WIDTH=SHARED+REGIONAL+FAST,MAX_CHAMPIONS=4,MAX_CANDIDATES=10;
    private static final int A=RecurrentCoreV22.A;
    private static final float MIN_FORWARD=1.8f,MIN_RATIO=.38f;
    private static final float MAX_SNAPSHOT_RMS=.036f;
    private static final int CONTEXT=10;
    private static final float MAX_CONTEXT_RMS=.22f;
    private static final float MIN_ACTIVE_MUSCLE_IMPULSE=.5f;
    private static final float MAX_INITIAL_CENTER_SPEED=5.0f;
    private final ArrayList<Entry> candidates=new ArrayList<>(),champions=new ArrayList<>();
    private int failedAction=-1,badStreak=0,failureScoreCount=0;
    private float recentFailureScore=0f;
    private final float[] trial=new float[WIDTH],trialContext=new float[CONTEXT];
    private int action=-1,life=1,validFrames=0,cooldown=0,externalFrames=0;
    private float netX,netY,gross,angularMotion,activeImpulse,initialSpeed;
    // A recalled configuration is temporarily ON PROBATION. It must beat its
    // own observed failure baseline or the previous 724 weights are returned.
    private float[] recoveryOriginal=null;
    private int recoveryAction=-1,recoveryTrials=0,recoveryPasses=0,recoveryDeadline=0;
    private float recoveryBaseline=0f,recoverySum=0f;
    private long recoveryConfirmed=0,recoveryReverted=0;
    private long samples=0,heldoutSamples=0,confirmed=0,recoveries=0,discarded=0;
    private static final class Entry{
        int action,firstLife,lastLife,verifications,failed;
        float quality;
        boolean contextKnown;
        final float[] config=new float[WIDTH],context=new float[CONTEXT];
        Entry(int a,int life,float score,float[] w,float[] context){
            action=a;firstLife=lastLife=life;quality=score;
            System.arraycopy(w,0,config,0,WIDTH);
            if(context!=null&&context.length==CONTEXT){
                System.arraycopy(context,0,this.context,0,CONTEXT);
                contextKnown=true;
            }
        }
        Entry copy(){
            Entry e=new Entry(action,firstLife,quality,config,
                contextKnown?context:null);
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
    public long recoveryProvenCount(){return recoveryConfirmed;}
    public long recoveryRollbackCount(){return recoveryReverted;}
    public int lastConsecutiveFailedAction(){return failedAction;}
    public int consecutiveFailedCount(){return badStreak;}
    public boolean recoveryUnderReview(){return recoveryOriginal!=null;}
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
        badStreak=0;failedAction=-1;
        recentFailureScore=0f;failureScoreCount=0;
        // An unfinished A/B recovery must never be silently accepted when
        // crossing a death boundary. The caller will undo it at the boundary.
        recoveryDeadline=0;
    }
    public void resumeLife(){
        cancelTrial();badStreak=0;failedAction=-1;
        recentFailureScore=0f;failureScoreCount=0;
    }
    public void cancelTrial(){
        action=-1;netX=netY=gross=angularMotion=activeImpulse=initialSpeed=0f;
        validFrames=externalFrames=0;
    }
    /** All context values are measured from the real 96-site living cortex.
     * No target coordinate, resource category, reward or prescribed posture. */
    public static float[] bodyContext(MotorBrain.Body body,Morphology.Surface skin){
        if(body==null||skin==null)return null;
        float[] c=new float[CONTEXT];
        double xx=0,yy=0,xy=0,rv=0,tv=0,rs=0;
        float maxR=0f;
        for(int i=0;i<Morphology.POINTS;i++){
            float x=skin.x[i]-body.cx,y=skin.y[i]-body.cy;
            float r=(float)Math.hypot(x,y);
            if(!Float.isFinite(r)||!Float.isFinite(skin.vx[i])||
                !Float.isFinite(skin.vy[i]))return null;
            maxR=Math.max(maxR,r);rs+=r;
            xx+=x*x;yy+=y*y;xy+=x*y;
            if(r>.001f){
                float vx=skin.vx[i]-body.cvx,vy=skin.vy[i]-body.cvy;
                rv+=(x*vx+y*vy)/r;tv+=(x*vy-y*vx)/r;
            }
            if(skin.touchSense[i]>.20f)c[7]+=1f;
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD)c[8]+=1f;
        }
        double anis=Math.sqrt((xx-yy)*(xx-yy)+4*xy*xy);
        c[0]=clip((float)(rs/Morphology.POINTS)/42f,0f,2f);
        c[1]=clip(maxR/80f,0f,2f);
        c[2]=clip((float)(anis/Math.max(1,xx+yy)),0f,1f);
        // For nearly circular bodies, a principal-axis angle is undefined.
        // Multiply orientation by anisotropy so tiny numeric asymmetry cannot
        // invent a completely different motor-skill body context.
        c[3]=clip((float)((xx-yy)/Math.max(1,xx+yy)),-1f,1f);
        c[4]=clip((float)((2*xy)/Math.max(1,xx+yy)),-1f,1f);
        c[5]=clip((float)(rv/Morphology.POINTS)/50f,-1f,1f);
        c[6]=clip((float)(tv/Morphology.POINTS)/50f,-1f,1f);
        c[7]/=Morphology.POINTS;c[8]/=Morphology.POINTS;
        c[9]=clip((float)Math.hypot(body.cvx,body.cvy)/60f,0f,2f);
        for(float v:c)if(!Float.isFinite(v))return null;
        return c;
    }
    private static float contextRms(float[] a,float[] b){
        if(a==null||b==null||a.length!=CONTEXT||b.length!=CONTEXT)
            return Float.POSITIVE_INFINITY;
        float sum=0;
        for(int i=0;i<CONTEXT;i++){float d=a[i]-b[i];sum+=d*d;}
        return (float)Math.sqrt(sum/CONTEXT);
    }
    public void begin(int chosen,float[] combinedSnapshot,
                      MotorBrain.Body body,Morphology.Surface skin){
        cancelTrial();
        float[] ctx=bodyContext(body,skin);
        if(chosen<0||chosen>=A||!valid(combinedSnapshot)||ctx==null)return;
        System.arraycopy(combinedSnapshot,0,trial,0,WIDTH);
        System.arraycopy(ctx,0,trialContext,0,CONTEXT);
        initialSpeed=(float)Math.hypot(body.cvx,body.cvy);
        action=chosen;
    }
    /** Every REAL post-collision physics frame. Relative membrane rotation
     * is only a regularizer; never counts as net movement or earned energy. */
    public void physical(float oldX,float oldY,MotorBrain.Body body,
                         Morphology.Surface skin,float realMuscleImpulse,
                         boolean externalDisturbance){
        if(action<0||body==null||skin==null)return;
        float dx=body.cx-oldX,dy=body.cy-oldY;
        if(!Float.isFinite(dx)||!Float.isFinite(dy))return;
        float m=(float)Math.hypot(dx,dy);
        if(m>3f)return; // rejected teleport/reset/collision spike
        netX+=dx;netY+=dy;gross+=m;validFrames++;
        if(Float.isFinite(realMuscleImpulse)&&realMuscleImpulse>0f)
            activeImpulse+=Math.min(500f,realMuscleImpulse);
        if(externalDisturbance)externalFrames++;
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
    private Entry match(ArrayList<Entry> list,int chosen,float[] snap,
                        float[] context,boolean contextRequired){
        Entry nearest=null;float distance=MAX_SNAPSHOT_RMS;
        for(Entry entry:list){
            if(entry.action!=chosen)continue;
            if(contextRequired&&(!entry.contextKnown||
                contextRms(entry.context,context)>MAX_CONTEXT_RMS))continue;
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
    /** Returns the previous controller only if the real recovery A/B
     * failed. Caller must install BEFORE any additional training writes. */
    public float[] finish(boolean train,boolean heldout,float realMotorEnergy){
        if(action<0)return null;
        int a=action;
        float theta=(a%8)*(float)Math.PI/4f;
        float forward=netX*(float)Math.cos(theta)+
                      netY*(float)Math.sin(theta);
        float quality=score(forward,gross,angularMotion,Math.max(0f,realMotorEnergy));
        // A moving body is NOT proof of a learned skill. No passive/coasting
        // momentum, no box/door correction, storm, wind or teleport may label
        // an active actuator as successful.
        boolean credible=validFrames>=4&&externalFrames==0&&
            initialSpeed<=MAX_INITIAL_CENTER_SPEED&&
            activeImpulse>=MIN_ACTIVE_MUSCLE_IMPULSE;
        boolean actualProgress=credible&&gross>=2.8f&&
            forward>=MIN_FORWARD&&forward/gross>=MIN_RATIO&&
            quality>=.39f;
        samples++;
        if(heldout)heldoutSamples++;
        if(cooldown>0)cooldown--;
        if(failedAction!=a){
            failedAction=a;badStreak=0;
            recentFailureScore=0f;failureScoreCount=0;
        }
        if(actualProgress){
            badStreak=0;recentFailureScore=0f;failureScoreCount=0;
        }else if(validFrames>=4&&externalFrames==0&&
                 initialSpeed<=MAX_INITIAL_CENTER_SPEED){
            if(badStreak<100)badStreak++;
            recentFailureScore+=clip(quality,-1f,1f);
            failureScoreCount++;
            if(failureScoreCount>4){
                // Keep a bounded moving estimate of last failed trials.
                recentFailureScore*=.8f;failureScoreCount=4;
            }
        }else{
            // External motion breaks comparability and consecutiveness.
            badStreak=0;recentFailureScore=0f;failureScoreCount=0;
        }

        float[] rollback=null;
        // Quantified post-recovery behavioral A/B with the SAME brain intent.
        // A brief lucky move is not enough to keep overwritten synapses.
        if(recoveryOriginal!=null){
            recoveryDeadline--;
            if(a==recoveryAction&&validFrames>=4){
                recoveryTrials++;
                recoverySum+=quality;
                if(actualProgress&&quality>=recoveryBaseline+.14f)
                    recoveryPasses++;
            }
            if(recoveryTrials>=3||recoveryDeadline<=0){
                if(recoveryTrials>=3&&recoveryPasses>=2&&
                    recoverySum/recoveryTrials>=recoveryBaseline+.14f){
                    recoveryConfirmed++;
                }else{
                    rollback=recoveryOriginal.clone();
                    recoveryReverted++;
                }
                recoveryOriginal=null;recoveryAction=-1;recoveryTrials=0;
                recoveryPasses=0;recoveryDeadline=0;recoverySum=0f;
            }
        }

        if(train&&!heldout&&actualProgress&&rollback==null){
            Entry similar=match(candidates,a,trial,trialContext,true);
            if(similar==null){
                // Keep legacy 169 records as untrusted evidence, but never
                // auto-recover them until remeasured with new posture + motor
                // causality and TWO fresh independent heldout life samples.
                Entry orphan=match(candidates,a,trial,trialContext,false);
                if(orphan!=null&&!orphan.contextKnown){
                    System.arraycopy(trialContext,0,orphan.context,0,CONTEXT);
                    orphan.contextKnown=true;
                    orphan.firstLife=orphan.lastLife=life;
                    orphan.verifications=orphan.failed=0;
                    orphan.quality=quality;
                    similar=orphan;
                }
            }
            if(similar==null){
                if(candidates.size()>=MAX_CANDIDATES){
                    int worst=0;
                    for(int j=1;j<candidates.size();j++)
                        if(candidates.get(j).quality<candidates.get(worst).quality)
                            worst=j;
                    if(quality<=candidates.get(worst).quality){
                        discarded++;
                    }else{
                        candidates.remove(worst);
                        candidates.add(new Entry(a,life,quality,trial,trialContext));
                    }
                }else candidates.add(new Entry(a,life,quality,trial,trialContext));
            }else{
                similar.quality=Math.max(similar.quality,quality);
            }
        }else if(heldout&&rollback==null){
            Entry e=match(candidates,a,trial,trialContext,true);
            if(e!=null){
                if(actualProgress){
                    if(e.lastLife!=life){
                        e.verifications++;
                        e.lastLife=life;
                        e.quality=.8f*e.quality+.2f*quality;
                    }
                    if(e.verifications>=2&&life>e.firstLife+1){
                        promote(e);candidates.remove(e);
                    }
                }else if(++e.failed>=3){
                    candidates.remove(e);discarded++;
                }
            }
        }
        cancelTrial();
        return rollback;
    }
    private void promote(Entry e){
        Entry same=match(champions,e.action,e.config,e.context,true);
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
    public float[] recoverIfStalled(int chosen,float[] current,
                                   MotorBrain.Body body,Morphology.Surface skin){
        float[] context=bodyContext(body,skin);
        // Count must correspond to four ACTUALLY CONSECUTIVE outcomes on the
        // same action in this same life; gaps or other intents break the run.
        if(chosen!=failedAction){
            badStreak=0;failedAction=-1;
            recentFailureScore=0f;failureScoreCount=0;
        }
        if(chosen<0||chosen>=A||failedAction!=chosen||badStreak<4||
           cooldown>0||recoveryOriginal!=null||context==null||
           !valid(current))return null;
        Entry best=null;
        for(Entry e:champions){
            if(e.action!=chosen||!e.contextKnown||
               contextRms(e.context,context)>MAX_CONTEXT_RMS)continue;
            if(best==null||e.quality>best.quality)best=e;
        }
        if(best==null||distance(best.config,current)<.005f)return null;
        recoveryOriginal=current.clone();
        recoveryAction=chosen;
        recoveryBaseline=failureScoreCount>0?
            recentFailureScore/failureScoreCount:0f;
        // Compare against ZERO's actual last failed trials, never a
        // fabricated zero, or a different brain intent.
        recoveryDeadline=18;recoveryTrials=recoveryPasses=0;recoverySum=0f;
        cooldown=32;badStreak=0;
        recentFailureScore=0f;failureScoreCount=0;
        recoveries++;
        return best.config.clone();
    }
    /** Death/restart must NEVER treat an unfinished test as validated. */
    public float[] abortIncompleteRecovery(){
        if(recoveryOriginal==null)return null;
        float[] rollback=recoveryOriginal.clone();
        recoveryOriginal=null;recoveryAction=-1;recoveryDeadline=0;
        recoveryTrials=recoveryPasses=0;recoverySum=0f;
        recoveryReverted++;
        return rollback;
    }
    public String encode(){
        StringBuilder s=new StringBuilder("ms170|").append(life).append('|')
            .append(samples).append('|').append(heldoutSamples).append('|')
            .append(confirmed).append('|').append(recoveries).append('|')
            .append(discarded).append('|').append(champions.size()).append('|')
            .append(candidates.size()).append('|')
            .append(recoveryConfirmed).append('|').append(recoveryReverted);
        if(recoveryOriginal==null)s.append("|-");
        else{
            s.append('|').append(recoveryAction).append(',')
                .append(recoveryTrials).append(',').append(recoveryPasses)
                .append(',').append(recoveryDeadline).append(',')
                .append(recoveryBaseline).append(',').append(recoverySum);
            for(float f:recoveryOriginal)s.append(',').append(f);
        }
        for(Entry e:champions)appendEntry(s,e);
        for(Entry e:candidates)appendEntry(s,e);
        return s.toString();
    }
    private static void appendEntry(StringBuilder b,Entry e){
        b.append('|').append(e.action).append(',').append(e.firstLife)
            .append(',').append(e.lastLife).append(',').append(e.verifications)
            .append(',').append(e.failed).append(',').append(e.quality);
        for(float w:e.config)b.append(',').append(w);
        b.append(',').append(e.contextKnown?1:0);
        for(float x:e.context)b.append(',').append(x);
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>180000||
            !(raw.startsWith("ms170|")||raw.startsWith("ms169|")))return false;
        try{
            String[] parts=raw.split("\\|",-1);
            boolean old=parts[0].equals("ms169");
            int start=old?9:12;
            if(parts.length<start)return false;
            int l=Integer.parseInt(parts[1]);
            long sr=Long.parseLong(parts[2]),hr=Long.parseLong(parts[3]),
                cr=Long.parseLong(parts[4]),rr=Long.parseLong(parts[5]),
                dr=Long.parseLong(parts[6]);
            int nc=Integer.parseInt(parts[7]),np=Integer.parseInt(parts[8]);
            long kept=old?0:Long.parseLong(parts[9]),
                reverted=old?0:Long.parseLong(parts[10]);
            if(l<1||l>1000000000||nc<0||nc>MAX_CHAMPIONS||
               np<0||np>MAX_CANDIDATES||parts.length!=start+nc+np||
               sr<0||hr<0||hr>sr||cr<0||rr<0||dr<0||
               cr>sr||rr>sr||sr>1000000000L||
               kept<0||reverted<0||kept+reverted>rr)return false;
            float[] oldMotor=null;
            int ra=-1,rt=0,rp=0,rd=0;float rb=0f,rs=0f;
            if(!old&&!parts[11].equals("-")){
                String[] q=parts[11].split(",",-1);
                if(q.length!=6+WIDTH)return false;
                ra=Integer.parseInt(q[0]);rt=Integer.parseInt(q[1]);
                rp=Integer.parseInt(q[2]);rd=Integer.parseInt(q[3]);
                rb=Float.parseFloat(q[4]);rs=Float.parseFloat(q[5]);
                if(ra<0||ra>=A||rt<0||rt>3||rp<0||rp>rt||
                   rd<1||rd>18||!Float.isFinite(rb)||
                   !Float.isFinite(rs)||Math.abs(rs)>3f)return false;
                oldMotor=new float[WIDTH];
                for(int i=0;i<WIDTH;i++)oldMotor[i]=Float.parseFloat(q[6+i]);
                if(!valid(oldMotor))return false;
            }
            ArrayList<Entry> checked=new ArrayList<>(),pending=new ArrayList<>();
            for(int n=0;n<nc+np;n++){
                String[] q=parts[start+n].split(",",-1);
                if(q.length!=(old?6+WIDTH:7+WIDTH+CONTEXT))return false;
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
                float[] ctx=null;
                if(!old){
                    int known=Integer.parseInt(q[6+WIDTH]);
                    if(known!=0&&known!=1)return false;
                    float[] context=new float[CONTEXT];
                    for(int k=0;k<CONTEXT;k++){
                        context[k]=Float.parseFloat(q[7+WIDTH+k]);
                        if(!Float.isFinite(context[k])||
                           Math.abs(context[k])>2.01f)return false;
                    }
                    if(known==1)ctx=context;
                    if(n<nc&&ctx==null)return false;
                }
                Entry e=new Entry(a,first,quality,w,ctx);
                e.lastLife=last;e.verifications=verified;e.failed=failed;
                if(old){
                    // No historically stored posture or active-force evidence.
                    // Keep legacy memories only as candidates, NEVER champions.
                    e.verifications=0;e.failed=0;
                    if(pending.size()<MAX_CANDIDATES)pending.add(e);
                }else (n<nc?checked:pending).add(e);
            }
            champions.clear();champions.addAll(checked);
            candidates.clear();candidates.addAll(pending);
            life=l;samples=sr;heldoutSamples=hr;
            confirmed=old?0:cr;recoveries=rr;discarded=dr;
            recoveryConfirmed=kept;recoveryReverted=reverted;
            recoveryOriginal=oldMotor;recoveryAction=ra;
            recoveryTrials=rt;recoveryPasses=rp;recoveryDeadline=rd;
            recoveryBaseline=rb;recoverySum=rs;
            cooldown=0;badStreak=0;failedAction=-1;cancelTrial();
            return true;
        }catch(RuntimeException bad){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "SYMBIOTE verified motor memory: skills=%d/%d candidates=%d heldout=%d recalls=%d",
            champions.size(),MAX_CHAMPIONS,candidates.size(),heldoutSamples,recoveries);
    }
}
