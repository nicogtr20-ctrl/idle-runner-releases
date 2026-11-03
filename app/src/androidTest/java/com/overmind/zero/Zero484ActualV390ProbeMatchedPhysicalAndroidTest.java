package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO484 read-only matched original-S21 physical causal audit.
 * The ON arm uses the actual V390 random single-muscle neural probe
 * (raw amplitude <= .14) through the real tanh/effort/clipped CH0/1/3
 * output. OFF is an independent original-cortex/170-material clone
 * starting from the exact same saved state, posture and PRE velocity.
 * No live policy, weights, GRU, rewards, directions or saved slots change.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero484ActualV390ProbeMatchedPhysicalAndroidTest {
    private static final float DT=1f/60f;
    private static final int FRAMES=24;
    private static final float[] PRE_WEST={.35f,1f,2f};

    static final class Result {
        final float firstX,firstY,lastX,lastY,delivered,raw,work;
        final int site,channel,contacts;
        Result(float fx,float fy,float lx,float ly,float d,float r,float w,
                int site,int ch,int contacts){
            firstX=fx;firstY=fy;lastX=lx;lastY=ly;
            delivered=d;raw=r;work=w;this.site=site;channel=ch;
            this.contacts=contacts;
        }
    }

    private static Zero3LearningFoundation.AdaptiveCortex cloneCortex(
            Zero3LearningFoundation.AdaptiveCortex.State state){
        Zero3LearningFoundation.AdaptiveCortex c=
            new Zero3LearningFoundation.AdaptiveCortex(state.learningRate());
        c.restore(state);
        c.enablePhysicalContextV412();
        c.enableDeliveredLocalCreditLabV421();
        return c;
    }

    private static Result run(JSONObject free,
            Zero3LearningFoundation.AdaptiveCortex.State state,
            float speed,int heading,boolean probe)throws Exception {
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            free.getString("training_room_1_scene_v379"));
        assertNotNull(scene);
        assertNotNull(scene.matter);
        Zero413MatchedPhysicalMotorCausalityAndroidTest.shiftWholeBody(
            scene,-speed,0f);
        scene.matter.enableOriginalZero3TractionV363();
        scene.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=cloneCortex(state);
        Zero3LearningFoundation.Coordinator motor=
            new Zero3LearningFoundation.Coordinator(cortex);
        motor.enableCausalMotorCreditV390();
        float ux=heading==0?1f:heading==2?-1f:0f;
        float uy=heading==1?1f:heading==3?-1f:0f;
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(
                484L,0L,cortex.revision(),ux,uy,.5f,.5f,.4f);
        motor.begin(intent,scene.body,false,true,probe,true,true);
        int site=probe?cortex.isolatedSectorV390():-1;
        int ch=probe?cortex.lastActiveProbeChannel():-1;
        if(probe){
            assertEquals(1,cortex.activeProbeCountV390());
            assertTrue(site>=0&&site<16);
            assertTrue(ch==0||ch==1||ch==3);
        }
        float startX=scene.body.cvx,startY=scene.body.cvy;
        float firstX=0f,firstY=0f;
        int contacts=0;
        float workAccum=0f;
        ArrayList<UnknownWorld.Entity> emptyWorld=new ArrayList<>();
        for(int frame=0;frame<FRAMES;frame++){
            float[] cmd=motor.actuate(scene.body,scene.skin,DT);
            float previousWork=scene.matter.work;
            float[] previousSkinX=scene.skin.x.clone();
            float[] previousSkinY=scene.skin.y.clone();
            scene.matter.step(scene.body,scene.skin,cmd,DT);
            float[] gelX=scene.skin.x.clone(),gelY=scene.skin.y.clone();
            Zero387TactileFrame.begin(scene.skin);
            for(float touch:scene.skin.touchSense)
                if(touch>.23f){contacts++;break;}
            SymbioteWorldStepV171.interior(scene.matter,scene.body,
                scene.skin,null,emptyWorld,DT,gelX,gelY);
            if(frame==0){
                firstX=scene.body.cvx-startX;
                firstY=scene.body.cvy-startY;
            }
            float actualStepWork=Math.max(0f,scene.matter.work-previousWork);
            workAccum+=actualStepWork;
            motor.observeContactV390(scene.body,scene.skin,DT,
                actualStepWork,
                scene.matter.lastMuscleReactionImpulse,false,false);
            assertEquals("original physical solver valid",0L,scene.matter.invalid);
        }
        float delivered=0f,raw=0f;
        if(probe){
            Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 p=
                cortex.captureProbeV332();
            int idx=site*4+ch;
            assertNotNull(p.deliveredNoise);
            delivered=p.deliveredNoise[idx];
            raw=p.noise[idx];
            assertTrue("original V390 raw perturbation is bounded",
                Math.abs(raw)>=.0209f&&Math.abs(raw)<=.14001f);
            assertEquals("only one original cortex site sampled",
                ch,p.selectedChannel);
        }
        motor.finish();
        assertEquals("no original user cortex synapses updated",
            state.updates,cortex.updates());
        return new Result(firstX,firstY,
            scene.body.cvx-startX,scene.body.cvy-startY,
            delivered,raw,workAccum,site,ch,contacts);
    }

    @Test(timeout=1500000)
    public void actualNeuralV390ProbeHasMatchedPhysicalNoProbeCounterfactual()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,saved.decisions);
        assertEquals(19452L,saved.motorUpdates);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        Zero3LearningFoundation.AdaptiveCortex.State original=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user).checkpoint();
        int total=0,measured=0,physical=0,discordant=0,firstPhysical=0;
        for(float speed:PRE_WEST){
            Zero3LearningFoundation.AdaptiveCortex sequence=cloneCortex(original);
            sequence.enableIsolatedCreditV390();
            HashSet<Integer> visited=new HashSet<>();
            int localMeasured=0,localPhysical=0,localDiscordant=0;
            for(int sample=0;sample<48;sample++){
                int heading=sample%4; // balanced lab reference, NEVER live steering
                Zero3LearningFoundation.AdaptiveCortex.State state=
                    sequence.checkpoint();
                Result on=run(free,state,speed,heading,true);
                Result off=run(free,state,speed,heading,false);
                assertTrue("unique actual CH0/1/3 probe",visited.add(
                    on.site*4+on.channel));
                float ux=heading==0?1f:heading==2?-1f:0f;
                float uy=heading==1?1f:heading==3?-1f:0f;
                float difference=(on.lastX-off.lastX)*ux+
                    (on.lastY-off.lastY)*uy;
                float first=(on.firstX-off.firstX)*ux+
                    (on.firstY-off.firstY)*uy;
                float unpaired=on.lastX*ux+on.lastY*uy;
                assertTrue(Float.isFinite(difference));
                assertTrue(Float.isFinite(on.delivered));
                if(Math.abs(on.delivered)>1e-8f){
                    measured++;localMeasured++;
                    assertEquals(Math.signum(on.raw),
                        Math.signum(on.delivered),0f);
                }
                if(Math.abs(difference)>1e-6f){
                    physical++;localPhysical++;
                    if(Math.abs(on.delivered)>1e-8f&&
                       unpaired*on.delivered*difference<0f){
                        discordant++;localDiscordant++;
                    }
                }
                if(Math.abs(first)>1e-7f)firstPhysical++;
                total++;
                // Advance the REAL original V390 RNG/probe sequence without
                // executing or learning from a fabricated extra decision.
                Zero3LearningFoundation.MotorIntent dummy=
                    new Zero3LearningFoundation.MotorIntent(
                        484L,0L,sequence.revision(),ux,uy,.5f,.5f,.4f);
                sequence.begin(true,dummy,true);
                sequence.discard();
            }
            assertEquals("all original 16 x CH0/1/3 sampled",48,visited.size());
            Log.i("ZERO484_CAUSAL",String.format(Locale.US,
                "ACTUAL_V390_S21 speedWest=%.3f sampled=48"+
                " deliveredNonzero=%d physicalNonzero=%d"+
                " V417UnpairedSignDiscordance=%d",
                speed,localMeasured,localPhysical,localDiscordant));
        }
        Log.i("ZERO484_CAUSAL",
            "ORIGINAL_S21_ACTUAL_V390_MATCHED_CAUSAL"+
            " samples="+total+" deliveredNonzero="+measured+
            " physicalNonzero="+physical+" firstFramePhysical="+firstPhysical+
            " unpairedSignDiscordance="+discordant+
            " originalGRUWeightsUntouched=true liveBehaviorChanged=false");
        assertEquals(144,total);
        assertTrue("actual small neural probes reach original material",measured>0);
        assertTrue("matched physical contrast must be measurable",physical>0);
    }

    // ZERO485 diagnostic: individual sign discordance is not proof of
    // a biased randomized policy-gradient estimator. Compare repeated
    // actual V390 score-times-delivered with same-state ON-minus-OFF.
    private static double v417Score(Result r,float ux,float uy){
        return 3.0*(r.lastX*ux+r.lastY*uy)/
            (4.0+Math.hypot(r.lastX,r.lastY))-.01*r.work;
    }

    @Test(timeout=1500000)
    public void repeatedOriginalV390SameStateGradientEstimatorAudit()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,saved.decisions);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        Zero3LearningFoundation.AdaptiveCortex.State original=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user).checkpoint();
        int total=0;
        for(float speed:PRE_WEST){
            Zero3LearningFoundation.AdaptiveCortex sequence=cloneCortex(original);
            sequence.enableIsolatedCreditV390();
            double[] estimated=new double[48],counterfactual=new double[48];
            int[] positive=new int[48],negative=new int[48];
            int discordantSingle=0,nonzero=0;
            for(int repeat=0;repeat<8;repeat++){
                HashSet<Integer> visited=new HashSet<>();
                for(int sample=0;sample<48;sample++){
                    int heading=sample%4;
                    Zero3LearningFoundation.AdaptiveCortex.State state=
                        sequence.checkpoint();
                    Result on=run(free,state,speed,heading,true);
                    Result off=run(free,state,speed,heading,false);
                    int channelIndex=on.channel==3?2:on.channel;
                    assertTrue(on.channel==0||on.channel==1||on.channel==3);
                    int site=on.site*3+channelIndex;
                    assertTrue(site>=0&&site<48);
                    assertTrue("one probe per site in each original V390 cycle",
                        visited.add(site));
                    float ux=heading==0?1f:heading==2?-1f:0f;
                    float uy=heading==1?1f:heading==3?-1f:0f;
                    double observed=v417Score(on,ux,uy);
                    double control=v417Score(off,ux,uy);
                    double estimate=observed*on.delivered;
                    double paired=(observed-control)*on.delivered;
                    assertTrue(Double.isFinite(estimate)&&Double.isFinite(paired));
                    assertTrue("physical delivered perturbation",
                        Math.abs(on.delivered)>1e-8f);
                    estimated[site]+=estimate;
                    counterfactual[site]+=paired;
                    if(on.raw>0f)positive[site]++;else negative[site]++;
                    if(estimate*paired<0)discordantSingle++;
                    if(Math.abs(paired)>1e-9)nonzero++;
                    total++;
                    Zero3LearningFoundation.MotorIntent dummy=
                        new Zero3LearningFoundation.MotorIntent(
                            485L,0L,sequence.revision(),ux,uy,.5f,.5f,.4f);
                    sequence.begin(true,dummy,true);
                    sequence.discard();
                }
                assertEquals("all original 16 x CH0/1/3 sites",48,visited.size());
            }
            double dot=0d,estSq=0d,causalSq=0d,estAbs=0d,causalAbs=0d;
            int meanSignDisagree=0,bothSigns=0;
            for(int site=0;site<48;site++){
                double e=estimated[site]/8d,c=counterfactual[site]/8d;
                dot+=e*c;estSq+=e*e;causalSq+=c*c;
                estAbs+=Math.abs(e);causalAbs+=Math.abs(c);
                if(e*c<0)meanSignDisagree++;
                if(positive[site]>0&&negative[site]>0)bothSigns++;
            }
            double cosine=dot/Math.sqrt(estSq*causalSq);
            assertTrue("finite empirical estimator alignment",Double.isFinite(cosine));
            Log.i("ZERO485_CAUSAL",String.format(Locale.US,
                "ORIGINAL_S21_V390_REPEATED_ESTIMATOR speedWest=%.3f"+
                " repeats=8 sites=48 total=384 physicalNonzero=%d"+
                " singleSignDiscordance=%d meanSiteSignDiscordance=%d"+
                " bothRawSignsSites=%d estimatorCausalCosine=%.6f"+
                " estimatorMeanAbs=%.9f causalMeanAbs=%.9f"+
                " liveMotorChanged=false",
                speed,nonzero,discordantSingle,meanSignDisagree,
                bothSigns,cosine,estAbs/48d,causalAbs/48d));
            assertTrue("bidirectional real neural probes",bothSigns>0);
        }
        assertEquals(1152,total);
        Log.i("ZERO485_CAUSAL",
            "ORIGINAL_S21_V390_REPEATED_ESTIMATOR_COMPLETE samples="+total+
            " sameStateONOFF=true originalSavedWeightsUntouched=true"+
            " noLiveCreditReplacement=true");
    }

    /**
     * ZERO486 read-only, prospective control-variate test. The candidate
     * baseline is learned ONLY from PREVIOUS genuine ON-arm physical scores
     * with the same issued heading; the paired OFF-arm is NEVER consulted
     * to construct it. OFF is used solely as heldout causal ground truth.
     * Each original V390 site is evaluated at all four lab headings.
     * No online brain, saved weight, reward, solver or motor is changed.
     */
    @Test(timeout=1500000)
    public void independentObservedBaselineAgainstMatchedCausalV486()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,saved.decisions);
        assertEquals(19452L,saved.motorUpdates);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        Zero3LearningFoundation.AdaptiveCortex.State original=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user).checkpoint();
        int grandTotal=0,grandEvaluated=0;
        for(float speed:PRE_WEST){
            Zero3LearningFoundation.AdaptiveCortex sequence=cloneCortex(original);
            sequence.enableIsolatedCreditV390();
            double[] naive=new double[48],centered=new double[48],causal=new double[48];
            int[] counts=new int[48];
            double[] priorObservedMean=new double[4];
            int[] priorCount=new int[4];
            int evaluated=0;
            for(int repeat=0;repeat<16;repeat++){
                HashSet<Integer> visited=new HashSet<>();
                for(int sample=0;sample<48;sample++){
                    int heading=(sample+repeat)%4;
                    Zero3LearningFoundation.AdaptiveCortex.State state=
                        sequence.checkpoint();
                    Result on=run(free,state,speed,heading,true);
                    Result off=run(free,state,speed,heading,false);
                    int site=on.site*3+(on.channel==3?2:on.channel);
                    assertTrue(site>=0&&site<48);
                    assertTrue("all original sites once per cycle",visited.add(site));
                    assertTrue("delivered genuine V390 motor intervention",
                        Math.abs(on.delivered)>1e-8f);
                    float ux=heading==0?1f:heading==2?-1f:0f;
                    float uy=heading==1?1f:heading==3?-1f:0f;
                    double observed=v417Score(on,ux,uy);
                    double offScore=v417Score(off,ux,uy);
                    assertTrue(Double.isFinite(observed)&&Double.isFinite(offScore));
                    // First complete cycle warms up each independent heading.
                    // Future estimates use only PREVIOUS ON observations.
                    if(priorCount[heading]>=12){
                        double p=on.delivered;
                        naive[site]+=observed*p;
                        centered[site]+=(observed-priorObservedMean[heading])*p;
                        causal[site]+=(observed-offScore)*p;
                        counts[site]++;
                        evaluated++;
                    }
                    // Incremental observed-only mean, never matched OFF.
                    priorCount[heading]++;
                    priorObservedMean[heading]+=
                        (observed-priorObservedMean[heading])/priorCount[heading];
                    grandTotal++;
                    Zero3LearningFoundation.MotorIntent dummy=
                        new Zero3LearningFoundation.MotorIntent(
                            486L,0L,sequence.revision(),ux,uy,.5f,.5f,.4f);
                    sequence.begin(true,dummy,true);
                    sequence.discard();
                }
                assertEquals(48,visited.size());
            }
            double dotN=0,dotB=0,sqN=0,sqB=0,sqC=0;
            double absN=0,absB=0,absC=0;
            int wrongN=0,wrongB=0;
            for(int site=0;site<48;site++){
                assertEquals("15 heldout samples per original site",15,counts[site]);
                double n=naive[site]/counts[site];
                double b=centered[site]/counts[site];
                double t=causal[site]/counts[site];
                dotN+=n*t;dotB+=b*t;
                sqN+=n*n;sqB+=b*b;sqC+=t*t;
                absN+=Math.abs(n);absB+=Math.abs(b);absC+=Math.abs(t);
                if(n*t<0)wrongN++;
                if(b*t<0)wrongB++;
            }
            double cosineN=dotN/Math.sqrt(sqN*sqC);
            double cosineB=dotB/Math.sqrt(sqB*sqC);
            assertTrue("finite unbiased-comparison diagnostic",
                Double.isFinite(cosineN)&&Double.isFinite(cosineB));
            assertEquals(720,evaluated);
            grandEvaluated+=evaluated;
            Log.i("ZERO486_CAUSAL",String.format(Locale.US,
                "ORIGINAL_S21_OBSERVED_BASELINE_HELDOUT speedWest=%.3f"+
                " repeats=16 total=768 evaluated=720 sites=48"+
                " naiveCosine=%.6f observedBaselineCosine=%.6f"+
                " naiveWrongSites=%d observedBaselineWrongSites=%d"+
                " naiveMeanAbs=%.9f observedBaselineMeanAbs=%.9f"+
                " matchedCausalMeanAbs=%.9f"+
                " baselineUsesOnlyPriorPhysicalON=true liveMotorChanged=false",
                speed,cosineN,cosineB,wrongN,wrongB,
                absN/48d,absB/48d,absC/48d));
        }
        assertEquals(2304,grandTotal);
        assertEquals(2160,grandEvaluated);
        Log.i("ZERO486_CAUSAL",
            "ORIGINAL_S21_OBSERVED_BASELINE_HELDOUT_COMPLETE"+
            " originalV390Probes=2304 evaluated=2160"+
            " noPairedCounterfactualUsedForBaseline=true"+
            " originalSavedWeightsUntouched=true noLiveCreditReplacement=true");
    }

}
