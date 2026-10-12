package com.overmind.zero;

import java.util.Random;

/** Independent regression for ZERO 2.0 gates B and C. */
public final class Zero20HierarchicalRegression {
    private static void yes(boolean ok,String why){
        if(!ok)throw new AssertionError(why);
    }
    private static float[] seen(float shift){
        float[] result=new float[SensorimotorMind.S];
        result[0]=.60f;
        for(int i=6;i<result.length;i++)result[i]=.12f+i*.003f+shift;
        return result;
    }
    private static void physicalSequence(TransferSkillsV20 skill,float shift,
                                         float realEnergy){
        float[] current=seen(shift);
        // Twelve real selected decisions, not an imagined multi-step plan.
        int[] actions={0,8,1,9,2,10,3,11,4,12,5,13};
        for(int action:actions)skill.chosen(action,current);
        skill.energyOutcome(realEnergy);
    }
    private static void transferTests(){
        TransferSkillsV20 skills=new TransferSkillsV20();
        yes(skills.confirmed()==0&&skills.observeSuggestion(seen(0))==null,
            "Virgin skill memory guessed a long plan");
        for(int life=1;life<=4;life++){
            skills.resetLife();
            physicalSequence(skills,0,22f);
        }
        yes(skills.candidates()>=9&&skills.confirmed()>0&&
            skills.longestConfirmed()>=10,
            "Independent real-life/heldout corroboration not learned");
        TransferSkillsV20.Advice a=skills.observeSuggestion(seen(0));
        yes(a!=null&&a.length>=3&&a.confidencePermille>0,
            "Verified long option cannot be recognized in lived context");
        yes(skills.observeSuggestion(seen(.8f))==null,
            "Unseen world context caused memorized strategy replay");
        long unchanged=skills.decisions();
        skills.energyOutcome(0);
        skills.energyOutcome(Float.NaN);
        yes(skills.decisions()==unchanged&&skills.realBadEvents()==0,
            "Neutral or imaginary energy manufactured confidence");
        int baselineTrust=a.confidencePermille;
        for(int i=0;i<12;i++){
            skills.resetLife();
            physicalSequence(skills,0,-7);
        }
        TransferSkillsV20.Advice harmful=skills.observeSuggestion(seen(0));
        yes(harmful==null||harmful.confidencePermille<baselineTrust,
            "Repeated actually harmful outcomes failed to devalue strategy");
        String stored=skills.encode();
        TransferSkillsV20 restored=new TransferSkillsV20();
        yes(restored.restore(stored)&&restored.encode().equals(stored),
            "Cross-life evidence failed roundtrip persistence");
        restored.resetLife();
        yes(restored.decisions()==skills.decisions(),
            "Death erased previous learned sequence experience");
        String before=restored.encode();
        yes(!restored.restore(before+"|oops")&&restored.encode().equals(before),
            "Corrupted option storage partly overwrote intact brain");
        yes(!restored.restore(before.replaceFirst("tv20","tv19"))&&
            before.equals(restored.encode()),"Wrong option schema accepted");
        String[] fields=before.split("\\|",-1);
        String[] motif=fields[8].split(",",-1);
        motif[4]="NaN"; // Guaranteed corruption of a REAL energy field.
        fields[8]=String.join(",",motif);
        String corrupted=String.join("|",fields);
        yes(!corrupted.equals(before)&&!restored.restore(corrupted)&&
            before.equals(restored.encode()),"Invalid motif corrupted profile");
        TransferSkillsV20 resume=new TransferSkillsV20();
        resume.resetLife();
        resume.chosen(1,seen(0));
        resume.chosen(2,seen(0));
        resume.resumeLife();
        resume.energyOutcome(30);
        yes(resume.candidates()==0,"Resume attributed energy to unseen action");
        System.out.println("ZERO20 B: 12-action crosslife holdout corroboration, negative outcome devaluation, context generalization guard, schema rollback PASS");
    }
    private static PredictiveWorldV20 learnedWorld(){
        PredictiveWorldV20 world=new PredictiveWorldV20();
        Random rng=new Random(202020);
        for(int i=0;i<14000;i++){
            int action=i%2==0?2:7;
            float[] now=seen(rng.nextFloat()*.1f);
            float[] later=now.clone();
            for(int j=6;j<later.length;j++)
                later[j]+=(action==2?.14f:-.12f)*(j%2==0?1f:.8f);
            world.chosen(action,now);
            world.consequence(later,action,i%7!=0,i%7==0);
        }
        yes(world.validations()>1000&&world.confidence()>.10f,
            "Real-sense shadow model cannot validate accurate predictions");
        return world;
    }
    private static void planningTests(){
        FuturePlannerV20 plan=new FuturePlannerV20();
        PredictiveWorldV20 virgin=new PredictiveWorldV20();
        FuturePlannerV20.Value values=(s,a)->(a==2?.9f:(a==7?.65f:0f))+s[6]*.1f;
        plan.evaluate(seen(0),virgin,values,null);
        yes(plan.checks()==1&&plan.eligible()==0&&plan.abstentions()==1&&
            plan.shadowSuggestion()==-1,
            "Untrained model fabricated forward decisions");
        PredictiveWorldV20 trained=learnedWorld();
        String before=trained.encode();
        for(int i=0;i<10;i++)plan.evaluate(seen(i*.002f),trained,values,null);
        yes(plan.checks()==11&&plan.eligible()==10&&
            plan.shadowSuggestion()>=0&&plan.shadowSuggestion()<16&&
            plan.latestConfidence()>.10f&&plan.latestMargin()>=0,
            "Validated world failed to produce conservative 5-step forecast");
        yes(trained.encode().equals(before),
            "Imagined transitions were incorrectly learned as physical experience");
        String saved=plan.encode();
        FuturePlannerV20 loaded=new FuturePlannerV20();
        yes(loaded.restore(saved)&&loaded.encode().equals(saved)&&
            loaded.shadowSuggestion()==-1,
            "Planner persisted previous imagined action as actual intention");
        String stable=loaded.encode();
        yes(!loaded.restore(stable+"|NaN")&&stable.equals(loaded.encode()),
            "Corrupt planner data damaged learning state");
        yes(!loaded.restore(stable.replaceFirst("fp20","broken"))&&
            stable.equals(loaded.encode()),"Wrong planner schema accepted");
        float[] invalid=seen(0);invalid[12]=Float.NaN;
        loaded.evaluate(invalid,trained,values,null);
        yes(loaded.abstentions()==2&&loaded.shadowSuggestion()==-1,
            "Nonfinite reality triggered imagined motor action");
        System.out.println("ZERO20 C: 5-decision pessimistic model rollouts, abstention, read-only imagination, atomic stats PASS");
    }
    private static void brainParity(){
        SensorimotorMind baseline=new SensorimotorMind();
        SensorimotorMind v2=new SensorimotorMind();
        PredictiveWorldV20 prediction=new PredictiveWorldV20();
        TransferSkillsV20 skills=new TransferSkillsV20();
        FuturePlannerV20 planning=new FuturePlannerV20();
        v2.attachPredictiveWorldV20(prediction);
        v2.attachTransferSkillsV20(skills);
        v2.attachFuturePlannerV20(planning);
        float[] initial=seen(0);
        baseline.resetEpisode(initial);v2.resetEpisode(initial);
        baseline.choose(initial);v2.choose(initial);
        yes(baseline.action==v2.action,"New branch changed virgin action");
        Random rng=new Random(550021);
        for(int i=0;i<440;i++){
            float[] senses=seen(rng.nextFloat()*.5f-.25f);
            if(i%47==0){
                baseline.contactOutcome(i%94==0?22f:-7f,0);
                v2.contactOutcome(i%94==0?22f:-7f,0);
            }
            baseline.observe(senses,.71f);
            v2.observe(senses,.71f);
            yes(baseline.action==v2.action&&
                baseline.serialize().equals(v2.serialize()),
                "New planning/options changed original motor choice or model at "+i);
        }
        yes(skills.decisions()>400&&planning.checks()>40&&
            prediction.trained()>300,
            "New shadow hierarchy did not observe real decisions");
        System.out.println("ZERO20 B/C: 440 identical baseline vs hierarchy policy choices and original brain hashes PASS");
    }
    public static void main(String[] args){
        transferTests();
        planningTests();
        brainParity();
        System.out.println("ZERO20 B/C HIERARCHICAL RESEARCH REGRESSIONS PASS");
    }
}
