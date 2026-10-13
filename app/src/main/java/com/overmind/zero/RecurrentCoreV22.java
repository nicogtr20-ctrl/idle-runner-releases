package com.overmind.zero;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/** Active sensory recurrent controller. No simulator object, target or recipe.
 * Fixed recurrent reservoir + learned action-conditioned readouts (NOT Dreamer).
 * Real energy, terminal and body outcomes train the controller; imagination
 * never produces training samples. Held-out transitions never enter replay.
 */
public final class RecurrentCoreV22 {
    public static final String KEY="recurrent_core_v22";
    public static final int A=16,F=164,H=24,Z=F+H,HEAD=12;
    private static final int WIDTH=Z+1,RECENT=72,RARE=24;
    private final float[] dynamics=new float[A*F*HEAD],q=new float[A*WIDTH],
        reward=new float[A*WIDTH],risk=new float[A*WIDTH];
    private final int[] samples=new int[A],validation=new int[A];
    private final float[] modelError=new float[A],baselineError=new float[A],
        energyError=new float[A],energyBase=new float[A];
    private final float[] hidden=new float[H],current=new float[F];
    private float[] pending;
    private int pendingAction=-1,recentCursor,rareCursor;
    private final ArrayList<Experience> recent=new ArrayList<>(),rare=new ArrayList<>();
    private final Random replayRandom=new Random(22013);
    private long transitions,trainSteps,replays,choices,modelChoices,invalid,terminals;
    private int lastAction=-1;
    private float lastMargin,lastConfidence;
    private static final class Experience {
        float[] before,after;int action;float earned,harm;boolean terminal;
        Experience(float[] b,float[] n,int a,float r,float d,boolean t){
            before=b.clone();after=n.clone();action=a;earned=r;harm=d;terminal=t;
        }
    }
    private static float clip(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private static boolean valid(float[] x,int n){
        if(x==null||x.length!=n)return false;
        for(float v:x)if(!Float.isFinite(v)||Math.abs(v)>1.601f)return false;
        return true;
    }
    /** 26 senses + 16x5 measured membrane channels + 16x2 physical eye
     * offsets + 16 observed depth rays + four anonymous focus features + six projections of ACTUALLY APPLIED control coefficients.
     * No entity identity or energy colour semantics are added.
     */
    public void sense(float[] s,float[] local,float[] origins,float[] controls){
        sense(s,local,origins,controls,null,null);
    }
    public void sense(float[] s,float[] local,float[] origins,float[] controls,float[] rays,float[] belief){
        if(!valid(s,26)){invalid++;return;}
        Arrays.fill(current,0);System.arraycopy(s,0,current,0,26);
        int[] channels={DistributedBodySense.TOUCH,DistributedBodySense.GRIP_HELD,
            DistributedBodySense.SHAPE,DistributedBodySense.STRAIN,DistributedBodySense.OPTIC};
        if(local!=null&&local.length==DistributedBodySense.FEATURES){
            for(int i=0;i<Morphology.POINTS;i++)for(int c=0;c<5;c++){
                float v=local[i*DistributedBodySense.CHANNELS+channels[c]];
                if(Float.isFinite(v))current[26+(i*16/Morphology.POINTS)*5+c]+=clip(v,-1,1)/6f;
            }
        }
        if(origins!=null&&origins.length==32)for(int i=0;i<32;i++)
            current[106+i]=Float.isFinite(origins[i])?clip(origins[i]/160f,-1,1):0;
        if(controls!=null&&controls.length==6)for(int i=0;i<6;i++)
            current[138+i]=Float.isFinite(controls[i])?clip(controls[i],-1,1):0;
        if(rays!=null&&rays.length==16)for(int i=0;i<16;i++)
            current[144+i]=Float.isFinite(rays[i])?clip(rays[i],0,1):1;
        if(belief!=null&&belief.length==4)for(int i=0;i<4;i++)
            current[160+i]=Float.isFinite(belief[i])?clip(belief[i],-1,1):0;
    }
    /** Called by Android with real weights, no mutation or execution here. */
    public static float[] controlState(float[] motor,float[] hand,float[] membrane){
        float[] result=new float[6];float[][] groups={motor,hand,membrane};
        for(int g=0;g<3;g++)if(groups[g]!=null&&groups[g].length>0){
            for(int i=0;i<groups[g].length;i++){
                float v=groups[g][i];if(!Float.isFinite(v))continue;
                result[2*g]+=v/groups[g].length/5f;
                result[2*g+1]+=v*((i&1)==0?1:-1)/groups[g].length/5f;
            }
        }
        return result;
    }
    // Same recurrence for lived and imagined states. Pure, deterministic;
    // rolling out never changes hidden, counters, replay, weights or RNG.
    private float[] advance(float[] x,float[] memory,int action){
        float[] next=new float[H];
        for(int h=0;h<H;h++){
            float sum=.45f*memory[h]+.15f*memory[(h+H-1)%H];
            for(int k=0;k<12;k++){
                int f=(h*19+k*11)%F;
                sum+=x[f]*(((h+k)&1)==0?.10f:-.10f);
            }
            if(action>=0)sum+=((action+3*h)%7-3)*.035f;
            next[h]=(float)Math.tanh(sum);
        }
        return next;
    }
    private static float[] join(float[] x,float[] h){
        float[] z=new float[Z];System.arraycopy(x,0,z,0,F);System.arraycopy(h,0,z,F,H);return z;
    }
    public float[] state(){return join(current,hidden);}
    private float[] head(float[] z,int out){
        int h=out%H;
        return new float[]{1,z[out],z[(out+F-1)%F],z[(out+1)%F],
            z[0],z[1],z[2],z[4],z[F+h],z[F+(h+7)%H],z[F+(h+13)%H],z[138+(out%6)]};
    }
    private float forecastDelta(float[] z,int action,int out){
        int h=out%H,off=(action*F+out)*HEAD;
        float sum=dynamics[off]+dynamics[off+1]*z[out]+dynamics[off+2]*z[(out+F-1)%F]+
            dynamics[off+3]*z[(out+1)%F]+dynamics[off+4]*z[0]+dynamics[off+5]*z[1]+
            dynamics[off+6]*z[2]+dynamics[off+7]*z[4]+dynamics[off+8]*z[F+h]+
            dynamics[off+9]*z[F+(h+7)%H]+dynamics[off+10]*z[F+(h+13)%H]+dynamics[off+11]*z[138+(out%6)];
        return clip(sum,-.75f,.75f);
    }
    public float[] imagine(float[] z,int action){
        if(!valid(z,Z)||action<0||action>=A)return null;
        float[] x=new float[F];
        for(int j=0;j<F;j++)x[j]=clip(z[j]+forecastDelta(z,action,j),j==1||j==2||(j>=106&&j<144)||j==160||j==161?-1f:0f,1f);
        // The control coefficients do not spontaneously mutate in imagination.
        System.arraycopy(z,138,x,138,6);
        return join(x,advance(x,Arrays.copyOfRange(z,F,Z),action));
    }
    private float read(float[] weights,float[] z,int action){
        int off=action*WIDTH;float sum=weights[off+Z];
        for(int k=0;k<Z;k++)sum+=weights[off+k]*z[k];
        return clip(sum,-2,2);
    }
    private void fit(float[] weights,float[] z,int action,float target,float lr){
        int off=action*WIDTH;float norm=1;
        for(float v:z)norm+=v*v;
        float grad=lr*clip(target-read(weights,z,action),-2,2)/norm;
        for(int k=0;k<Z;k++)weights[off+k]=clip(weights[off+k]+grad*z[k],-3,3);
        weights[off+Z]=clip(weights[off+Z]+grad,-3,3);
    }
    private float value(float[] z,int a){return read(q,z,a)-.30f*Math.max(0,read(risk,z,a));}
    private int greedy(float[] z){int win=0;float best=-Float.MAX_VALUE;
        for(int a=0;a<A;a++){float v=value(z,a);if(v>best){win=a;best=v;}}return win;}
    public float confidence(int a){
        if(a<0||a>=A||samples[a]<24||validation[a]<8||baselineError[a]<.00001f||
            modelError[a]>=baselineError[a]*.96f||energyError[a]>energyBase[a]+.00002f)return 0;
        return clip(1-modelError[a]/baselineError[a],0,.8f);
    }
    private float rollout(float[] start,int first){
        float[] z=start;float total=0,discount=1;int a=first;
        for(int depth=0;depth<3;depth++){
            float trust=confidence(a);
            if(trust<=0)break;
            total+=discount*(read(reward,z,a)-.30f*Math.max(0,read(risk,z,a)));
            z=imagine(z,a);discount*=.90f;a=greedy(z);
        }
        return total+discount*value(z,greedy(z));
    }
    /** Returns a score for EVERY action BEFORE the real choice. Old scores
     * are a bounded migration prior which decays with actual new evidence.
     * Information seeking is affordable and bounded, never a colour recipe.
     */
    public float[] evaluate(float[] oldScores){
        float[] z=state(),scores=new float[A];
        float oldTrust=Math.max(.12f,1f/(1+transitions/240f));
        lastConfidence=0;
        for(int a=0;a<A;a++){
            float trust=confidence(a);lastConfidence=Math.max(lastConfidence,trust);
            float prior=oldScores==null?0:clip(oldScores[a],-2,2)*.15f*oldTrust;
            float information=.009f*clip(current[0]/.4f,0,1)/(float)Math.sqrt(1+samples[a]);
            scores[a]=value(z,a)+(trust>0?.35f*trust*(rollout(z,a)-value(z,a)):0)+prior+information;
        }
        int best=0;float runner=-Float.MAX_VALUE;
        for(int a=1;a<A;a++)if(scores[a]>scores[best])best=a;
        for(int a=0;a<A;a++)if(a!=best)runner=Math.max(runner,scores[a]);
        lastMargin=scores[best]-runner;choices++;
        if(confidence(best)>0)modelChoices++;
        return scores;
    }
    public float exploration(){return Math.max(.04f,.28f/(1+transitions/300f));}
    public void chosen(int a){
        if(a<0||a>=A){invalid++;pending=null;pendingAction=-1;return;}
        pending=state();pendingAction=a;lastAction=a;
    }
    private void train(Experience e,boolean model){
        int a=e.action;
        if(model)for(int j=0;j<F;j++){
            if(j>=138&&j<144)continue;
            float[] x=head(e.before,j);float error=e.after[j]-e.before[j]-forecastDelta(e.before,a,j);
            float norm=1;for(float v:x)norm+=v*v;
            float grad=.10f*clip(error,-1,1)/norm;int off=(a*F+j)*HEAD;
            for(int k=0;k<HEAD;k++)dynamics[off+k]=clip(dynamics[off+k]*.99999f+grad*x[k],-2,2);
        }
        fit(reward,e.before,a,e.earned,.18f);
        fit(risk,e.before,a,e.harm,.12f);
        float future=e.terminal?0:value(e.after,greedy(e.after));
        fit(q,e.before,a,clip(e.earned+.90f*future,-2,2),.18f);
        trainSteps++;
    }
    /** One actual executed decision interval, including terminal intervals.
     * net reserve change includes hunger and weather; contact harm is measured
     * separately. Never train on imagined reward or a held-out experience.
     */
    public void consequence(float actualContactEnergy,float actualDamage,boolean train,boolean holdout,boolean terminal){
        if(pending==null||pendingAction<0)return;
        int a=pendingAction;
        float[] nextHidden=advance(current,hidden,a),after=join(current,nextHidden);
        float earned=clip(current[0]-pending[0]-(terminal?.20f:0),-1,1);
        float harm=clip(actualDamage/100f,0,1);
        Experience e=new Experience(pending,after,a,earned,harm,terminal);
        if(holdout){
            float error=0,base=0;
            for(int j=0;j<F;j++){
                if(j>=138&&j<144)continue;
                float delta=after[j]-pending[j],res=delta-forecastDelta(pending,a,j);
                error+=res*res;base+=delta*delta;
            }
            validation[a]++;
            float alpha=validation[a]==1?1:.08f;
            modelError[a]+=(error/(F-6)-modelError[a])*alpha;
            baselineError[a]+=(base/(F-6)-baselineError[a])*alpha;
            float deltaEnergy=after[0]-pending[0];
            float ee=deltaEnergy-forecastDelta(pending,a,0);
            energyError[a]+=(ee*ee-energyError[a])*alpha;
            energyBase[a]+=(deltaEnergy*deltaEnergy-energyBase[a])*alpha;
        }else if(train){
            train(e,true);if(samples[a]<Integer.MAX_VALUE)samples[a]++;
            if(recent.size()<RECENT)recent.add(e);else recent.set(recentCursor++%RECENT,e);
            if(Math.abs(actualContactEnergy)>.5f||terminal){
                if(rare.size()<RARE)rare.add(e);else rare.set(rareCursor++%RARE,e);
            }
            for(int k=0;k<2;k++){
                ArrayList<Experience> pool=k==1&&!rare.isEmpty()?rare:recent;
                if(!pool.isEmpty()){train(pool.get(replayRandom.nextInt(pool.size())),true);replays++;}
            }
        }
        System.arraycopy(nextHidden,0,hidden,0,H);
        transitions++;if(terminal)terminals++;
        pending=null;pendingAction=-1;
    }
    public void resetLife(){Arrays.fill(hidden,0);Arrays.fill(current,0);pending=null;pendingAction=-1;lastAction=-1;}
    public void resumeLife(){pending=null;pendingAction=-1;}
    public long transitions(){return transitions;}
    public long replayUpdates(){return replays;}
    public int replaySize(){return recent.size()+rare.size();}
    public long plannedChoices(){return modelChoices;}
    public String summary(){return String.format(Locale.US,
        "ZERO 2.2 ACTIVE: lived=%d replay=%d/%d planned=%d confidence=%.3f margin=%.4f terminal=%d invalid=%d",
        transitions,replaySize(),replays,modelChoices,lastConfidence,lastMargin,terminals,invalid);}
    private static void array(DataOutputStream out,float[] a)throws IOException{for(float v:a)out.writeFloat(v);}
    private static void readArray(DataInputStream in,float[] a,float bound)throws IOException{
        for(int i=0;i<a.length;i++){float v=in.readFloat();if(!Float.isFinite(v)||Math.abs(v)>bound)throw new IOException("weight");a[i]=v;}
    }
    private static void bank(DataOutputStream out,ArrayList<Experience> list)throws IOException{
        out.writeInt(list.size());for(Experience e:list){out.writeInt(e.action);out.writeFloat(e.earned);out.writeFloat(e.harm);out.writeBoolean(e.terminal);array(out,e.before);array(out,e.after);}
    }
    private static void bank(DataInputStream in,ArrayList<Experience> list,int max)throws IOException{
        int n=in.readInt();if(n<0||n>max)throw new IOException("replay size");
        for(int i=0;i<n;i++){
            int a=in.readInt();float r=in.readFloat(),d=in.readFloat();boolean t=in.readBoolean();
            if(a<0||a>=A||!Float.isFinite(r)||Math.abs(r)>1||!Float.isFinite(d)||d<0||d>1)throw new IOException("sample");
            float[] b=new float[Z],e=new float[Z];readArray(in,b,1.601f);readArray(in,e,1.601f);list.add(new Experience(b,e,a,r,d,t));
        }
    }
    /** Compact compressed, checksummed payload, comfortably under slot limit.
     * Decode validates into a temporary object before touching live weights.
     */
    public String encode(){try{
        ByteArrayOutputStream raw=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(raw);
        out.writeInt(0x5a433232);for(long v:new long[]{transitions,trainSteps,replays,choices,modelChoices,invalid,terminals})out.writeLong(v);
        for(int[] list:new int[][]{samples,validation})for(int n:list)out.writeInt(n);
        for(float[] list:new float[][]{dynamics,q,reward,risk,modelError,baselineError,energyError,energyBase,hidden,current})array(out,list);
        bank(out,recent);bank(out,rare);out.flush();byte[] body=raw.toByteArray();CRC32 crc=new CRC32();crc.update(body);
        ByteArrayOutputStream compressed=new ByteArrayOutputStream();DeflaterOutputStream zip=new DeflaterOutputStream(compressed);zip.write(body);zip.close();
        return "rc22|"+crc.getValue()+"|"+Base64CodecV22.encode(compressed.toByteArray());
    }catch(IOException ex){throw new IllegalStateException(ex);}}
    public boolean restore(String text){
        if(text==null||text.length()>2000000||!text.startsWith("rc22|"))return false;
        try{
            String[] p=text.split("\\|",-1);if(p.length!=3)return false;
            InflaterInputStream zip=new InflaterInputStream(new ByteArrayInputStream(Base64CodecV22.decode(p[2])));
            ByteArrayOutputStream raw=new ByteArrayOutputStream();byte[] buffer=new byte[4096];int n;
            while((n=zip.read(buffer))!=-1){if(raw.size()+n>600000)return false;raw.write(buffer,0,n);}zip.close();
            byte[] bytes=raw.toByteArray();CRC32 crc=new CRC32();crc.update(bytes);if(crc.getValue()!=Long.parseLong(p[1]))return false;
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));if(in.readInt()!=0x5a433232)return false;
            RecurrentCoreV22 c=new RecurrentCoreV22();long[] counters=new long[7];for(int i=0;i<7;i++){counters[i]=in.readLong();if(counters[i]<0||counters[i]>1000000000000L)return false;}
            if(counters[4]>counters[3]||counters[6]>counters[0])return false;
            for(int[] list:new int[][]{c.samples,c.validation})for(int i=0;i<A;i++){list[i]=in.readInt();if(list[i]<0||list[i]>counters[0])return false;}
            for(float[] list:new float[][]{c.dynamics,c.q,c.reward,c.risk})readArray(in,list,3.001f);
            for(float[] list:new float[][]{c.modelError,c.baselineError,c.energyError,c.energyBase}){readArray(in,list,10);for(float v:list)if(v<0)return false;}
            readArray(in,c.hidden,1.001f);readArray(in,c.current,1.601f);bank(in,c.recent,RECENT);bank(in,c.rare,RARE);if(in.available()!=0)return false;
            for(float[][] pair:new float[][][]{ {c.dynamics,dynamics},{c.q,q},{c.reward,reward},{c.risk,risk}, {c.modelError,modelError},{c.baselineError,baselineError},{c.energyError,energyError},{c.energyBase,energyBase},{c.hidden,hidden},{c.current,current}})System.arraycopy(pair[0],0,pair[1],0,pair[0].length);
            System.arraycopy(c.samples,0,samples,0,A);System.arraycopy(c.validation,0,validation,0,A);
            transitions=counters[0];trainSteps=counters[1];replays=counters[2];choices=counters[3];modelChoices=counters[4];invalid=counters[5];terminals=counters[6];
            recent.clear();recent.addAll(c.recent);rare.clear();rare.addAll(c.rare);recentCursor=rareCursor=0;resumeLife();return true;
        }catch(IOException|RuntimeException ex){return false;}
    }
}
