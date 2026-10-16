package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * OTA170 acceptance. No Android controller is scripted. Real 96+74 gel
 * laboratory impulses are the independently identified external benchmark.
 * Adversarial tests reject false memory from passive/coasting/external drift,
 * incomparable body posture, spaced failures, incompatible save migration,
 * and require real A/B improvements before retaining recovered motor genes.
 */
public final class Zero170MotorReliabilityRegression {
    private static void check(boolean pass,String why){
        if(!pass)throw new AssertionError("ZERO170 AUDIT: "+why);
    }
    private static final class Fixture{
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface skin=new Morphology.Surface(body);
        final SymbioteMatterV1 matter=new SymbioteMatterV1();
        Fixture(){matter.reset(body,skin);}
    }
    private static float[] cortex(){
        float[] x=new float[SymbioteMotorSkillMemoryV169.WIDTH];
        for(int i=0;i<x.length;i++)x[i]=(float)Math.sin(i*.073f)*.05f;
        return x;
    }
    private static float[] drive(){
        float[] c=new float[64];
        for(int i=0;i<16;i++){
            boolean first=Math.cos((i+.5f)*Math.PI/8)>0;
            c[4*i]=first?.78f:-.43f;
            c[4*i+2]=first?-.95f:.95f;
        }
        return c;
    }
    private static final class Motion{
        float dx,dy,gross;
        int frames;
    }
    /** A single real trial of the unchanged symbiote material; no teleport. */
    private static Motion lived(SymbioteMotorSkillMemoryV169 bank,int action,
                                float[] snapshot,boolean train,boolean heldout,
                                boolean external,boolean motors,Fixture fixture){
        Fixture f=fixture==null?new Fixture():fixture;
        Motion out=new Motion();
        float[] commands=motors?drive():new float[64];
        float x=f.body.cx,y=f.body.cy;
        bank.begin(action,snapshot,f.body,f.skin);
        for(int j=0;j<320;j++){
            float bx=f.body.cx,by=f.body.cy;
            f.matter.step(f.body,f.skin,commands,.025f);
            if(external){
                // Exogenous world push with NO physiological motor force.
                // This is an adversarial verification source, not gameplay.
                f.body.cx+=.08f;
                for(int k=0;k<f.body.x.length;k++)f.body.x[k]+=.08f;
                for(int k=0;k<f.skin.x.length;k++)f.skin.x[k]+=.08f;
            }
            bank.physical(bx,by,f.body,f.skin,
                motors?f.matter.lastMuscleReactionImpulse:0f,external);
            out.gross+=(float)Math.hypot(f.body.cx-bx,f.body.cy-by);
            check(f.matter.invalid==0&&MaterialGeometry.selfCrossings(f.skin)==0,
                "material integrity lost under candidate scoring");
            out.frames++;
        }
        out.dx=f.body.cx-x;out.dy=f.body.cy-y;
        bank.finish(train,heldout,0f);
        return out;
    }
    private static int heading(Motion m){
        float t=(float)Math.atan2(m.dy,m.dx);
        if(t<0)t+=(float)(Math.PI*2);
        return Math.round(t*4f/(float)Math.PI)%8;
    }
    private static void failures(SymbioteMotorSkillMemoryV169 b,int action,
                                 int times,float[] config){
        for(int i=0;i<times;i++){
            Fixture f=new Fixture();
            b.begin(action,config,f.body,f.skin);
            for(int k=0;k<8;k++)
                b.physical(f.body.cx,f.body.cy,f.body,f.skin,0f,false);
            b.finish(false,false,0f);
        }
    }
    private static SymbioteMotorSkillMemoryV169 verified(int action,float[] config){
        SymbioteMotorSkillMemoryV169 bank=new SymbioteMotorSkillMemoryV169();
        lived(bank,action,config,true,false,false,true,null);
        check(bank.candidatesCount()==1&&bank.verifiedCount()==0,
            "single train trial inappropriately validated a skill");
        bank.newLife();
        lived(bank,action,config,false,true,false,true,null);
        check(bank.verifiedCount()==0,"first heldout promoted a skill");
        bank.newLife();
        lived(bank,action,config,false,true,false,true,null);
        check(bank.verifiedCount()==1,"distinct-life motor memory not learned");
        return bank;
    }
    public static void main(String[] args){
        float[] known=cortex(),other=known.clone();
        other[0]+=.50f;other[84]+=.35f;other[404]+=.43f;
        SymbioteMotorSkillMemoryV169 blind=new SymbioteMotorSkillMemoryV169();
        // Muscles were silent, the world shifted ZERO: no real motor learning.
        lived(blind,0,known,true,false,true,false,null);
        check(blind.candidatesCount()==0,"environment push became a learnt gait");
        // Even if real motor tension is present, external corrections veto.
        Motion actual=lived(blind,0,known,true,false,true,true,null);
        check(blind.candidatesCount()==0,"outside force contaminated motor skill");
        Motion source=lived(new SymbioteMotorSkillMemoryV169(),-1,known,
            false,false,false,true,null);
        int action=heading(source);
        float angle=action*(float)Math.PI/4f;
        float forward=source.dx*(float)Math.cos(angle)+
            source.dy*(float)Math.sin(angle);
        check(forward>=4f&&forward/source.gross>.38f,
            "lab physical actuator cannot make a measurable forward skill");
        SymbioteMotorSkillMemoryV169 good=verified(action,known);

        // Legacy MS169 had NO body context and NO motor impulse evidence.
        // Old verified strings must survive as untrusted CANDIDATES ONLY.
        String ms170=good.encode();
        String[] newer=ms170.split("\\|",-1);
        String oldEntry=newer[12].substring(0,newer[12].lastIndexOf(','));
        // Explicitly drop the 11 new context columns plus contextKnown bit.
        String[] fields=newer[12].split(",");
        StringBuilder legacyEntry=new StringBuilder();
        for(int i=0;i<6+SymbioteMotorSkillMemoryV169.WIDTH;i++){
            if(i>0)legacyEntry.append(',');
            legacyEntry.append(fields[i]);
        }
        String legacy="ms169|"+newer[1]+"|"+newer[2]+"|"+newer[3]+
            "|"+newer[4]+"|"+newer[5]+"|"+newer[6]+"|1|0|"+legacyEntry;
        SymbioteMotorSkillMemoryV169 migrated=new SymbioteMotorSkillMemoryV169();
        check(migrated.restore(legacy)&&migrated.verifiedCount()==0&&
              migrated.candidatesCount()==1,
            "uncontextualized OTA169 skill remained automatically executable");
        failures(migrated,action,4,other);
        Fixture fi=new Fixture();
        check(migrated.recoverIfStalled(action,other,fi.body,fi.skin)==null,
            "unsafe legacy skill automatically recalled without fresh verification");

        // Four failures of the same intent, with an INTERRUPTION, are NOT
        // a consecutive sequence. A contextually unrelated body is vetoed.
        failures(good,action,3,other);
        failures(good,(action+1)%8,1,other);
        failures(good,action,1,other);
        check(good.consecutiveFailedCount()==1,
            "failed attempts leaked across unrelated brain intentions");
        check(good.recoverIfStalled(action,other,fi.body,fi.skin)==null,
            "nonconsecutive failures caused forced motor rollback");
        failures(good,action,3,other);
        Fixture twisted=new Fixture();
        // Deliberately strong different living posture: same weights cannot
        // be assumed suitable without a comparable predecision body.
        for(int j=0;j<Morphology.POINTS;j++)
            twisted.skin.x[j]=twisted.body.cx+
                (twisted.skin.x[j]-twisted.body.cx)*1.85f;
        check(good.recoverIfStalled(action,other,twisted.body,twisted.skin)==null,
            "verified technique recalled on incompatible body posture");
        Fixture compatible=new Fixture();
        float[] recovered=good.recoverIfStalled(action,other,
            compatible.body,compatible.skin);
        check(Arrays.equals(known,recovered)&&good.recoveryUnderReview(),
            "verified postured technique could not be recalled after 4 failures");
        check(good.recoverIfStalled(action,other,compatible.body,
            compatible.skin)==null,"recovery spam defeated cooldown");

        // Failed post-recovery behavior: genuinely measure three failed
        // same-intent real decisions, then force reversion to prior synapses.
        float[] rollback=null;
        for(int j=0;j<3;j++){
            good.begin(action,recovered,compatible.body,compatible.skin);
            for(int k=0;k<8;k++)
                good.physical(compatible.body.cx,compatible.body.cy,
                    compatible.body,compatible.skin,0f,false);
            rollback=good.finish(false,false,0f);
        }
        check(Arrays.equals(other,rollback)&&!good.recoveryUnderReview()&&
            good.recoveryRollbackCount()==1,
            "failed A/B trial did not return previous 724 neuron weights");

        // Proved, genuine positive post-recovery movement should RETAIN the
        // configuration after >=2 of 3 observed same action trials.
        SymbioteMotorSkillMemoryV169 success=verified(action,known);
        failures(success,action,4,other);
        recovered=success.recoverIfStalled(action,other,compatible.body,
            compatible.skin);
        check(recovered!=null,"positive A/B controller could not be invoked");
        for(int i=0;i<3;i++){
            Fixture f=new Fixture();
            success.begin(action,recovered,f.body,f.skin);
            float[] sig=drive();
            for(int k=0;k<320;k++){
                float px=f.body.cx,py=f.body.cy;
                f.matter.step(f.body,f.skin,sig,.025f);
                success.physical(px,py,f.body,f.skin,
                    f.matter.lastMuscleReactionImpulse,false);
            }
            check(success.finish(false,false,0f)==null,
                "good recovery was mistakenly reverted");
        }
        check(success.recoveryProvenCount()==1&&
            success.recoveryRollbackCount()==0&&!success.recoveryUnderReview(),
            "successful verified A/B intervention was never retained");

        // Exact independent save including context and pending rollback.
        String saved=success.encode();
        SymbioteMotorSkillMemoryV169 copy=new SymbioteMotorSkillMemoryV169();
        check(copy.restore(saved)&&saved.equals(copy.encode()),
            "ms170 contextual checkpoint failed byte-perfect restoration");
        check(!copy.restore(saved+"TRUNC")&&saved.equals(copy.encode()),
            "corrupted save replaced good physically validated memory");

        // Full live linkage: a 2.2-chosen action can install ONLY the proven
        // motor cortex, not silently choose or alter an action.
        SymbioteMotorHeadV1 cortical=new SymbioteMotorHeadV1();
        check(cortical.restoreMemory(success.encode()),
            "skill archive disconnected from living symbiote controller");
        float[] incumbent=cortical.verifiedMotorSnapshot();
        Fixture neuralBody=new Fixture();
        failures(cortical.skillMemory(),action,4,incumbent);
        float[] sensors=new float[DistributedBodySense.FEATURES];
        float[] brainState=new float[RecurrentCoreV22.Z];
        cortical.chosen(action,sensors,brainState,.25f,null,
            SymbioteMotorHeadV1.measuredSectorAngles(
                neuralBody.body,neuralBody.skin),
            neuralBody.body,neuralBody.skin);
        check(cortical.skillMemory().recoveryUnderReview()&&
            Arrays.equals(cortical.verifiedMotorSnapshot(),known)&&
            cortical.decisionCount()==1,
            "verified motor recovery not actually applied by living 2.2 action");

        // Mid-recovery crash/restart: preserve untrusted A/B state in full
        // profile, then fail closed and restore previous motor synapses.
        SymbioteMotorSkillMemoryV169 pending=verified(action,known);
        failures(pending,action,4,other);
        Fixture same=new Fixture();
        check(pending.recoverIfStalled(action,other,
            same.body,same.skin)!=null,"A/B checkpoint not initiated");
        String checkpoint=pending.encode();
        SymbioteMotorSkillMemoryV169 resumed=new SymbioteMotorSkillMemoryV169();
        check(resumed.restore(checkpoint)&&
            resumed.recoveryUnderReview()&&
            checkpoint.equals(resumed.encode()),
            "pending A/B motor experiment was not serialized safely");
        check(Arrays.equals(resumed.abortIncompleteRecovery(),other)&&
            resumed.recoveryRollbackCount()==1&&
            !resumed.recoveryUnderReview(),
            "restarting application accepted an unvalidated motor recovery");

        // Never publish or set application updater version from this test.
        System.out.printf(Locale.US,
            "ZERO170 PASS: 96+74 actuator actualNet=%.3f gross=%.3f; "+
            "false passive/external blocked; legacy 169 quarantined; "+
            "context-aware 4 true consecutive action failures; "+
            "bad recovery reverted, good recovery retained, "+
            "new checkpoint exact and crash-safe. NO OTA PUBLISH.%n",
            forward,source.gross);
    }
}
