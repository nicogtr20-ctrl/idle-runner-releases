package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * Object-conditioned learning from the organism's OWN sixteen spatial rays,
 * twelve visible wavelength sectors, performed actions and lived outcomes.
 * A category is a fallible sensory hypothesis, NEVER an Entity ID or law.
 *
 * Mechanical success, energy contact and a possible causal link are separate
 * facts. A temporal coincidence cannot train the object-specific reward.
 */
public final class ObjectExperienceV20 {
    public static final String KEY="object_experience_v20";
    private static final int N=48,F=13,A=SensorimotorMind.ACTIONS;
    private static final float MATCH=.115f;
    private final float[][] prototype=new float[N][F];
    private final int[] encounters=new int[N],moves=new int[N],successes=new int[N];
    private final int[] positive=new int[N],negative=new int[N],uncertain=new int[N];
    private final float[] benefit=new float[N],harm=new float[N],cost=new float[N];
    private final int[][] tried=new int[N][A],worked=new int[N][A];
    private final int[] genericTried=new int[A],genericWorked=new int[A];
    private int size=0,current=-1,bearing=-1,pending=-1,pendingBearing=-1,pendingAction=-1;
    private float[] pendingSpectra=new float[3];
    private float pendingReserve=0,contactDelta=0;
    private int touchedSignature=-1,energyContacts=0;
    private long views=0,unknown=0,ambiguities=0,physics=0,possibleLinks=0,heldout=0;
    private double error=0,baseline=0;
    private boolean dirty=false;
    public int categories(){return size;}
    public long observations(){return views;}
    public long physicalSuccesses(){return physics;}
    public long possibleCausalLinks(){return possibleLinks;}
    public long ambiguousOutcomes(){return ambiguities;}
    public int matchedCategory(){return current;}
    public int tried(int c){return c<0||c>=size?0:encounters[c];}
    public int moved(int c){return c<0||c>=size?0:moves[c];}
    public int positive(int c){return c<0||c>=size?0:positive[c];}
    public int negative(int c){return c<0||c>=size?0:negative[c];}
    public boolean takeDirty(){boolean b=dirty;dirty=false;return b;}
    private static float clamp(float a,float lo,float hi){return Math.max(lo,Math.min(hi,a));}
    private static boolean valid(float[] a,int n){
        if(a==null||a.length!=n)return false;
        for(float v:a)if(!Float.isFinite(v))return false;
        return true;
    }
    /** Four quadrant frequency sectors are measured, not object labels. */
    private static int quadrant(int ray){
        float angle=(float)(ray*Math.PI/8);
        if(angle>=Math.PI)angle-=(float)(2*Math.PI);
        return Math.min(3,Math.max(0,(int)Math.floor((angle+Math.PI)/(Math.PI/2))));
    }
    private static float[] describe(float[] rays,float[] senses){
        if(!valid(rays,16)||!valid(senses,SensorimotorMind.S))return null;
        int closest=-1,hits=0;float depth=1.001f;
        for(int i=0;i<16;i++){
            if(rays[i]<0||rays[i]>1.001f)return null;
            if(rays[i]<.995f){hits++;if(rays[i]<depth){depth=rays[i];closest=i;}}
        }
        if(closest<0)return null;
        // Opposing nearby surfaces are not one invented object.
        for(int i=0;i<16;i++){
            int gap=Math.abs(i-closest);gap=Math.min(gap,16-gap);
            if(gap>4&&rays[i]<.995f&&rays[i]<depth+.075f)return null;
        }
        float[] d=new float[F];
        for(int i=0;i<8;i++){
            float v=rays[(closest+i+13)%16];
            d[i]=v>=.995f?1f:clamp(.4f+v-depth,0f,1f);
        }
        d[8]=depth;
        d[9]=Math.min(1f,hits/8f);
        int sector=quadrant(closest);
        for(int i=0;i<3;i++)d[10+i]=clamp(senses[14+i*4+sector],0,1);
        return d;
    }
    private static int nearestRay(float[] rays){
        int index=-1;float best=.995f;
        for(int i=0;i<16;i++)if(rays[i]<best){index=i;best=rays[i];}
        return index;
    }
    private static float distance(float[] x,float[] y){
        float spatial=0,spectral=0;
        for(int i=0;i<10;i++)spatial+=Math.abs(x[i]-y[i]);
        for(int i=10;i<13;i++)spectral+=Math.abs(x[i]-y[i]);
        return spatial/10f*.65f+spectral/3f*.35f;
    }
    /** May abstain rather than make up an identity on occlusion or ambiguity. */
    public void view(float[] rays,float[] senses,boolean train){
        views++;current=-1;bearing=-1;
        float[] d=describe(rays,senses);
        if(d==null){unknown++;return;}
        bearing=nearestRay(rays);
        float best=Float.POSITIVE_INFINITY,second=Float.POSITIVE_INFINITY;
        int idx=-1;
        for(int i=0;i<size;i++){
            float dist=distance(d,prototype[i]);
            if(dist<best){second=best;best=dist;idx=i;}
            else if(dist<second)second=dist;
        }
        if(idx>=0&&best<MATCH&&second-best<.009f){ambiguities++;return;}
        if(idx>=0&&best<MATCH){
            current=idx;
            if(train){
                int n=Math.min(2000,++encounters[idx]);
                float rate=1f/Math.min(48,n);
                for(int j=0;j<F;j++)prototype[idx][j]+=rate*(d[j]-prototype[idx][j]);
                dirty=true;
            }
        }else if(train&&size<N){
            current=size++;
            encounters[current]=1;
            System.arraycopy(d,0,prototype[current],0,F);
            dirty=true;
        }else unknown++;
    }
    public void chosen(int action,float[] senses){
        pending=current;pendingBearing=bearing;pendingAction=action;
        pendingReserve=valid(senses,SensorimotorMind.S)?senses[0]*100f:0;
        for(int i=0;i<3;i++)pendingSpectra[i]=pending>=0?prototype[pending][10+i]:0;
        contactDelta=0;touchedSignature=-1;energyContacts=0;
    }
    /** An energy event is real, but its cause is not inferred from timing. */
    public void energyContact(float delta,int signature){
        if(!Float.isFinite(delta)||Math.abs(delta)<.01f)return;
        if(pendingAction<0)return;
        energyContacts++;
        contactDelta+=delta;
        if(energyContacts==1&&signature>=0&&signature<3)touchedSignature=signature;
        else touchedSignature=-1; // more than one source = confounded
    }
    /** Measured external motion is independent of energetic valence. */
    public void consequence(float displacement,float[] after,float[] afterRays,
                            boolean training,boolean validation){
        int c=pending,a=pendingAction,oldBearing=pendingBearing;
        pending=-1;pendingAction=-1;
        if(c<0||c>=size||a<0||a>=A||!valid(after,SensorimotorMind.S)||
           !Float.isFinite(displacement)||displacement<0)return;
        boolean moved=displacement>.015f;
        if(moved)physics++;
        if(validation&&!training){
            double p=(worked[c][a]+1d)/(tried[c][a]+4d);
            double b=(genericWorked[a]+1d)/(genericTried[a]+4d);
            double actual=moved?1:0;
            error+=(p-actual)*(p-actual);
            baseline+=(b-actual)*(b-actual);
            heldout++;
            return;
        }
        if(!training)return;
        tried[c][a]++;genericTried[a]++;
        if(moved){worked[c][a]++;genericWorked[a]++;moves[c]++;}
        // Cost of the attempt excluding actual, independently registered
        // resource contacts; cap anomalous hunger/physics and use a slow mean.
        float expense=clamp(pendingReserve-after[0]*100f+contactDelta,0,25);
        cost[c]+=(expense-cost[c])/(Math.min(90,tried[c][a])+1f);
        // Cross-event object-source attribution is *uncertain* by default.
        // A plausible reveal requires motion, a single source, a localized
        // spectral transition near the same previously perceived bearing.
        boolean plausible=false;
        if(moved&&energyContacts==1&&touchedSignature>=0&&
           valid(afterRays,16)&&oldBearing>=0){
            int currentRay=nearestRay(afterRays);
            int gap=Math.abs(currentRay-oldBearing);gap=Math.min(gap,16-gap);
            int q=quadrant(oldBearing),s=touchedSignature;
            float later=clamp(after[14+s*4+q],0,1);
            // Physical source may have been consumed before 'after' was read;
            // low/vanished signal is NOT evidence of the source's location.
            plausible=gap<=2 && later>pendingSpectra[s]+.20f;
        }
        if(plausible){
            possibleLinks++;
            if(contactDelta>0){positive[c]++;benefit[c]+=(contactDelta-benefit[c])/
                         Math.max(1,positive[c]);}
            else{negative[c]++;harm[c]+=(-contactDelta-harm[c])/
                         Math.max(1,negative[c]);}
        }else if(energyContacts>0)uncertain[c]++;
        dirty=true;
    }
    public float confidence(){
        if(heldout<80||physics<12||baseline<1e-8)return 0;
        double e=error/heldout,b=baseline/heldout;
        if(b<.015||e>=.90*b)return 0;
        return clamp((float)((b-e)/b),0,.35f);
    }
    /** No scripted heading or grasp; a tiny learned suggestion only after
     * independent validation and repeated comparable physical successes. */
    public float choiceBias(int action,float reserveFraction){
        if(current<0||action<0||action>=A||confidence()<=0||
           tried[current][action]<6||moves[current]<6||pendingAction>=0)return 0;
        double own=(worked[current][action]+1d)/(tried[current][action]+4d);
        double generic=(genericWorked[action]+1d)/(genericTried[action]+4d);
        float mechanical=clamp((float)(own-generic),-.8f,.8f);
        // Energetic credits are only tentative hypotheses. Require many
        // repeated plausible links before permitting a very small influence.
        float energetic=0;
        int associated=positive[current]+negative[current];
        if(associated>=20&&encounters[current]>=80){
            float pp=(positive[current]+.5f)/(encounters[current]+2f);
            float pn=(negative[current]+.5f)/(encounters[current]+2f);
            float risk=reserveFraction<.25f?1.8f:1f;
            energetic=clamp((pp*benefit[current]-pn*harm[current]*risk-
                              cost[current])/40f,-.4f,.4f);
        }
        return clamp((mechanical*.16f+energetic*.08f)*
                     Math.min(1,confidence()/.18f),-.18f,.18f);
    }
    public void resetLife(){
        pending=-1;pendingAction=-1;current=-1;bearing=-1;
        contactDelta=0;energyContacts=0;touchedSignature=-1;
    }
    public String encode(){
        StringBuilder b=new StringBuilder("oe2|").append(size).append('|').append(views)
            .append('|').append(unknown).append('|').append(ambiguities)
            .append('|').append(physics).append('|').append(possibleLinks)
            .append('|').append(heldout).append('|').append(error)
            .append('|').append(baseline);
        for(int a=0;a<A;a++)b.append('|').append(genericTried[a])
                                  .append('|').append(genericWorked[a]);
        for(int i=0;i<size;i++){
            b.append('|').append(encounters[i]).append(',').append(moves[i])
             .append(',').append(positive[i]).append(',').append(negative[i])
             .append(',').append(uncertain[i]).append(',').append(benefit[i])
             .append(',').append(harm[i]).append(',').append(cost[i]);
            for(float f:prototype[i])b.append(',').append(f);
            for(int a=0;a<A;a++)b.append(',').append(tried[i][a])
                                        .append(',').append(worked[i][a]);
        }
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("oe2|")||raw.length()>180000)return false;
        try{
            String[] p=raw.split("\\|",-1);
            int n=Integer.parseInt(p[1]);
            if(n<0||n>N||p.length!=9+2*A+n)return false;
            long v=Long.parseLong(p[2]),u=Long.parseLong(p[3]),amb=Long.parseLong(p[4]),
                 phys=Long.parseLong(p[5]),links=Long.parseLong(p[6]),ho=Long.parseLong(p[7]);
            double err=Double.parseDouble(p[8]),base=Double.parseDouble(p[9]);
            if(v<0||u<0||amb<0||phys<0||links<0||ho<0||
               !Double.isFinite(err)||!Double.isFinite(base)||err<0||base<0)return false;
            // Parse into a temporary instance to avoid partial corruption.
            ObjectExperienceV20 t=new ObjectExperienceV20();
            t.size=n;t.views=v;t.unknown=u;t.ambiguities=amb;
            t.physics=phys;t.possibleLinks=links;t.heldout=ho;t.error=err;t.baseline=base;
            int k=10;
            for(int a=0;a<A;a++){
                t.genericTried[a]=Integer.parseInt(p[k++]);
                t.genericWorked[a]=Integer.parseInt(p[k++]);
                if(t.genericTried[a]<0||t.genericWorked[a]<0||t.genericWorked[a]>t.genericTried[a])return false;
            }
            for(int i=0;i<n;i++){
                String[] x=p[k++].split(",",-1);
                if(x.length!=8+F+2*A)return false;
                int z=0;
                t.encounters[i]=Integer.parseInt(x[z++]);t.moves[i]=Integer.parseInt(x[z++]);
                t.positive[i]=Integer.parseInt(x[z++]);t.negative[i]=Integer.parseInt(x[z++]);
                t.uncertain[i]=Integer.parseInt(x[z++]);
                t.benefit[i]=Float.parseFloat(x[z++]);t.harm[i]=Float.parseFloat(x[z++]);
                t.cost[i]=Float.parseFloat(x[z++]);
                if(t.encounters[i]<1||t.moves[i]<0||t.positive[i]<0||t.negative[i]<0||
                   t.uncertain[i]<0||!Float.isFinite(t.benefit[i])||
                   !Float.isFinite(t.harm[i])||!Float.isFinite(t.cost[i]))return false;
                for(int j=0;j<F;j++){
                    t.prototype[i][j]=Float.parseFloat(x[z++]);
                    if(!Float.isFinite(t.prototype[i][j])||t.prototype[i][j]<0||
                       t.prototype[i][j]>1.01f)return false;
                }
                for(int a=0;a<A;a++){
                    t.tried[i][a]=Integer.parseInt(x[z++]);
                    t.worked[i][a]=Integer.parseInt(x[z++]);
                    if(t.tried[i][a]<0||t.worked[i][a]<0||
                       t.worked[i][a]>t.tried[i][a])return false;
                }
            }
            size=t.size;views=t.views;unknown=t.unknown;ambiguities=t.ambiguities;
            physics=t.physics;possibleLinks=t.possibleLinks;heldout=t.heldout;
            error=t.error;baseline=t.baseline;
            for(int a=0;a<A;a++){genericTried[a]=t.genericTried[a];genericWorked[a]=t.genericWorked[a];}
            for(int i=0;i<n;i++){
                System.arraycopy(t.prototype[i],0,prototype[i],0,F);
                encounters[i]=t.encounters[i];moves[i]=t.moves[i];positive[i]=t.positive[i];
                negative[i]=t.negative[i];uncertain[i]=t.uncertain[i];
                benefit[i]=t.benefit[i];harm[i]=t.harm[i];cost[i]=t.cost[i];
                System.arraycopy(t.tried[i],0,tried[i],0,A);
                System.arraycopy(t.worked[i],0,worked[i],0,A);
            }
            resetLife();dirty=false;return true;
        }catch(RuntimeException ex){return false;}
    }
}
