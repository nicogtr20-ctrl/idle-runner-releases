package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * Real 96-site/74-parcel motor-skill retention test. ONLY the JVM laboratory
 * supplies a fixed electrode; the Android neural policy has no such gait.
 * Tests learned snapshots, independent heldout lives, guarded same-intent
 * stall recovery, unchanged 2.2 brain, and corrupt-checkpoint fail-closed.
 */
public final class Zero169MotorMemoryRegression {
    private static void ok(boolean v,String why){
        if(!v)throw new AssertionError("ZERO169 MOTOR MEMORY: "+why);
    }
    private static float[] lab(){
        float[] c=new float[64];
        for(int s=0;s<16;s++){
            float a=(float)((s+.5)*Math.PI/8f);
            boolean forward=Math.cos(a)>0;
            c[s*4]=forward?.78f:-.43f;
            c[s*4+2]=forward?-.95f:.95f;
        }
        return c;
    }
    private static final class Record {
        float dx,dy,gross;
        int crossed;
    }
    private static Record actualBody(int action,float[] config,
                                     SymbioteMotorSkillMemoryV169 memory,
                                     boolean train,boolean heldout){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 matter=new SymbioteMatterV1();matter.reset(b,skin);
        float[] signal=lab();
        Record result=new Record();
        float sx=b.cx,sy=b.cy,prevX=b.cx,prevY=b.cy;
        if(memory!=null)memory.begin(action,config,b,skin);
        for(int t=0;t<320;t++){
            float x=b.cx,y=b.cy;
            matter.step(b,skin,signal,.025f);
            result.gross+=(float)Math.hypot(b.cx-x,b.cy-y);
            if(memory!=null)
                memory.physical(x,y,b,skin,matter.lastMuscleReactionImpulse,false);
            ok(matter.invalid==0&&MaterialGeometry.selfCrossings(skin)==0,
                "moving material lost physical topology");
            prevX=b.cx;prevY=b.cy;
        }
        result.dx=b.cx-sx;result.dy=b.cy-sy;
        result.crossed=MaterialGeometry.selfCrossings(skin);
        if(memory!=null)memory.finish(train,heldout,0f);
        return result;
    }
    public static void main(String[] args){
        float[] proven=new float[SymbioteMotorSkillMemoryV169.WIDTH];
        for(int i=0;i<proven.length;i++)
            proven[i]=(float)Math.sin(i*.077f)*.06f;
        Record physical=actualBody(-1,proven,null,false,false);
        float direction=(float)Math.atan2(physical.dy,physical.dx);
        if(direction<0)direction+=(float)(Math.PI*2);
        int action=(Math.round(direction*4f/(float)Math.PI))%8;
        float theta=action*(float)Math.PI/4f;
        float forward=physical.dx*(float)Math.cos(theta)+
                      physical.dy*(float)Math.sin(theta);
        ok(forward>=4f&&forward/physical.gross>.38f,
            "controlled identical living body cannot yield a recordable true motor skill");

        SymbioteMotorSkillMemoryV169 bank=new SymbioteMotorSkillMemoryV169();
        actualBody(action,proven,bank,true,false);
        ok(bank.candidatesCount()==1&&bank.verifiedCount()==0,
            "single trained movement was prematurely called a proven skill");
        float[] mutated=proven.clone();mutated[0]+=.4f;mutated[84]+=.32f;
        mutated[404]+=.4f;
        ok(bank.recoverIfStalled(action,mutated,new MotorBrain.Body(),
            new Morphology.Surface(new MotorBrain.Body()))==null,
            "unverified movement illegally restored the muscles");
        bank.newLife();
        actualBody(action,proven,bank,false,true);
        ok(bank.verifiedCount()==0&&bank.measuredHeldouts()==1,
            "first heldout was insufficient to prove distinct-life reliability");
        bank.newLife();
        actualBody(action,proven,bank,false,true);
        ok(bank.verifiedCount()==1&&bank.confirmedCount()==1,
            "two independent heldout lives failed to preserve real movement");
        ok(bank.isProvenFor(action)&&!bank.isProvenFor((action+1)%16),
            "motor-skill evidence was incorrectly transferred to other actions");
        String saved=bank.encode();
        SymbioteMotorSkillMemoryV169 copied=new SymbioteMotorSkillMemoryV169();
        ok(copied.restore(saved)&&saved.equals(copied.encode()),
            "verified skills failed byte-equivalent independent save restoration");
        ok(!copied.restore(saved+"tamper")&&saved.equals(copied.encode()),
            "malformed skill data corrupted an already-verified archive");
        ok(!copied.restore("ms169|999|1|0|0|0|0|1|0|bad")&&
            saved.equals(copied.encode()),
            "invalid neural skill snapshot was accepted");

        for(int i=0;i<4;i++){
            copied.begin(action,mutated);
            // No physical displacement; no fabricated success.
            copied.finish(false,false,0f);
        }
        MotorBrain.Body failedBody=new MotorBrain.Body();
        Morphology.Surface failedSkin=new Morphology.Surface(failedBody);
        ok(copied.recoverIfStalled((action+1)%16,mutated,
            failedBody,failedSkin)==null,
            "skill recall overrode an unrelated brain-selected direction");
        float[] recalled=copied.recoverIfStalled(action,mutated,
            failedBody,failedSkin);
        ok(recalled!=null&&Arrays.equals(recalled,proven)&&
            copied.recoveryCount()==1,
            "four actual stalls could not safely recall the proven motor cortex");
        ok(copied.recoverIfStalled(action,mutated,
            failedBody,failedSkin)==null,
            "recalled skill was repeatedly applied on every decision");
        copied.newLife();
        ok(copied.verifiedCount()==1&&copied.isProvenFor(action),
            "new life erased physically verified motor skill");

        // Real Android cortex contains all three motor layers; no skill is
        // recovered into RecurrentCoreV22 or the brain's 16 action values.
        SymbioteMotorHeadV1 head=new SymbioteMotorHeadV1();
        String oldHead=head.encode(),oldLocal=head.encodeLocal(),
            oldFast=head.encodeFast();
        String virgin=head.encodeMemory();
        ok(head.restoreMemory(virgin)&&head.skillMemory().verifiedCount()==0,
            "fresh profile illegally inherited an existing symbiote skill");
        ok(head.restoreMemory(saved)&&head.skillMemory().verifiedCount()==1,
            "full cortex could not restore a skill into optional save slot");
        ok(head.encode().equals(oldHead)&&head.encodeLocal().equals(oldLocal)&&
           head.encodeFast().equals(oldFast),
            "restoring memory silently changed live motor synapses");
        String remembered=head.encodeMemory();
        head.resetLife();
        ok(head.skillMemory().verifiedCount()==1&&
           !remembered.equals(head.encodeMemory()),
            "life advance did not preserve the learned skill history");
        System.out.printf(Locale.US,
            "ZERO169 PASS real 96+74 gel forward=%.3f gross=%.3f ratio=%.3f; "+
            "2 physically independent heldout lives, 1 proven motor skill, "+
            "4 failed attempts before action-specific recall, cooldown, "+
            "corruption rejection, separate profile and retained life memory%n",
            forward,physical.gross,forward/physical.gross);
    }
}
