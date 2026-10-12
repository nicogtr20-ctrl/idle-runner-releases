package com.overmind.zero;

import java.util.Arrays;
import java.util.Random;

/** Falsifiable 96-site real-self affordance readouts. No object/category oracle. */
public final class GripAffordanceV20Regression {
    private static void check(boolean yes,String why){
        if(!yes)throw new AssertionError(why);
    }
    private static Morphology.Surface skin(int[] sites,int rotate,boolean held,boolean extended){
        Morphology.Surface s=new Morphology.Surface(new MotorBrain.Body());
        Arrays.fill(s.touchSense,0);
        Arrays.fill(s.gripCommand,0);
        Arrays.fill(s.gripHold,0);
        Arrays.fill(s.extension,0);
        for(int k:sites){
            int i=(k+rotate)%Morphology.POINTS;
            s.touchSense[i]=.9f;
            s.gripCommand[i]=.75f;
            if(held)s.gripHold[i]=.15f;
            if(extended)s.extension[i]=50f;
        }
        return s;
    }
    private static void observe(GripAffordanceV20 g,int action,
                                Morphology.Surface self,float movement,
                                boolean training,boolean holdout){
        g.chosen(action,self);
        g.consequence(action,movement,training,holdout);
    }
    public static void main(String[] argv){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface virgin=new Morphology.Surface(body);
        Morphology.Surface touchOnly=skin(new int[]{0,1,2},0,false,false);
        Morphology.Surface cluster=skin(new int[]{1,2,3,4},0,true,false);
        Morphology.Surface extended=skin(new int[]{1,2,3,4},0,true,true);
        Morphology.Surface spread=skin(new int[]{0,48},0,true,false);
        Morphology.Surface spreadRotated=skin(new int[]{0,48},27,true,false);
        check(GripAffordanceV20.selfState(virgin)==0,
            "Virgin body invented a contact");
        check(GripAffordanceV20.selfState(touchOnly)==2,
            "Adhesion COMMAND without actual hold must not be called a grasp");
        check(GripAffordanceV20.selfState(cluster)==3&&
              GripAffordanceV20.selfState(extended)==4,
            "Morphological stretch information lost");
        check(GripAffordanceV20.selfState(spread)==5&&
              GripAffordanceV20.selfState(spreadRotated)==5,
            "Circular hold dispersion depends on world rotation");
        GripAffordanceV20 model=new GripAffordanceV20();
        check(model.confidence()==0&&!model.restore(""),
            "Legacy/vacant model must abstain");
        String virginSaved=model.encode();
        observe(model,2,touchOnly,0,true,false);
        check(model.motionEvents()==0&&model.realHeld()==0,
            "Touch or red command alone fabricated a moved body");
        observe(model,2,cluster,0,true,false);
        check(model.motionEvents()==0,
            "Concentrated grip without displacement fabricated world change");
        // Same bodily shape; different truly executed action, externally
        // measured movement, and no fictitious energy reward.
        for(int i=0;i<200;i++){
            observe(model,2,spreadRotated,1,true,false);
            observe(model,7,spread,0,true,false);
            if(i%5==0)model.resetLife(); // trained data survives across lives
        }
        float b=model.forecast(2,spread),c=model.forecast(7,spread);
        check(b>c+.70f,"Own body and action failed to predict real movement");
        check(model.forecast(2,cluster)<model.forecast(2,spread),
            "Held distribution ignored in transfer of learned experience");
        long trained=model.trained(),moves=model.motionEvents();
        for(int i=0;i<240;i++)
            observe(model,i%2==0?2:7,
                    i%2==0?spread:spreadRotated,i%2==0?1f:0f,
                    false,true);
        check(model.trained()==trained&&
              model.forecast(2,spread)==b&&model.forecast(7,spread)==c,
            "Heldout observations leaked into learned grip predictors");
        check(model.validations()==240&&model.confidence()>.10f&&
              model.heldoutError()<model.baselineError()*.8f,
            "Physical grip predictor failed independent same-configuration baseline");
        int prior=(int)model.observed();
        model.chosen(2,spread);
        model.consequence(7,1f,true,false);
        check(model.observed()==prior&&model.invalid()>0,
            "Outcome for a DIFFERENT motor action trained previous grip");
        Morphology.Surface broken=skin(new int[]{0,48},0,true,false);
        broken.extension[2]=Float.NaN;
        model.chosen(2,broken);
        model.consequence(2,1f,true,false);
        check(model.trained()==trained,"Nonfinite membrane data fabricated a memory");
        String checkpoint=model.encode();
        GripAffordanceV20 read=new GripAffordanceV20();
        check(read.restore(checkpoint)&&checkpoint.equals(read.encode()),
            "96-site model did not survive named profile serialization");
        read.resetLife();
        check(read.trained()==trained,"Death erased confirmed learned grip outcomes");
        read.chosen(2,spread);
        read.resumeLife();
        long old=read.observed();
        read.consequence(2,1f,true,false);
        check(read.observed()==old,"Restored uncompleted actuation invented real effect");
        String priorValid=read.encode();
        check(!read.restore(priorValid+"|0")&&read.encode().equals(priorValid),
            "Trailing corruption modified prior intact profile");
        check(!read.restore(priorValid.replaceFirst("ga21","ga19"))&&
              read.encode().equals(priorValid),"Unsupported version accepted");
        String[] fields=priorValid.split("\\|",-1);
        fields[8]="NaN";
        check(!read.restore(String.join("|",fields))&&
              read.encode().equals(priorValid),
              "Nonfinite validation metric erased learning");
        check(!read.restore(virginSaved.substring(0,virginSaved.length()/2))&&
              read.encode().equals(priorValid),"Truncated old save overwrote model");
        // A single 35ms real grip must survive an otherwise unheld action.
        GripAffordanceV20 fleeting=new GripAffordanceV20();
        fleeting.chosen(2,touchOnly);
        fleeting.physicsFrame(spread);
        fleeting.physicsFrame(touchOnly);
        fleeting.consequence(2,1f,true,false);
        check(fleeting.physicsSamples()==2&&fleeting.physicsHeldFrames()==1&&
              fleeting.physicsSpreadFrames()==1&&fleeting.fleetingSpreadIntervals()==1,
              "Brief physically valid hold lost between decisions");
        // Never pretend that two individually held points separated in time
        // constituted ONE distributed grip at the same physical instant.
        GripAffordanceV20 disjoint=new GripAffordanceV20();
        disjoint.chosen(2,touchOnly);
        disjoint.physicsFrame(skin(new int[]{0},0,true,false));
        disjoint.physicsFrame(skin(new int[]{48},0,true,false));
        disjoint.consequence(2,0f,true,false);
        check(disjoint.physicsHeldFrames()==2&&disjoint.physicsSpreadFrames()==0&&
              disjoint.fleetingSpreadIntervals()==0,
              "Different-time holds became a fake simultaneous grasp");
        GripAffordanceV20 commandOnly=new GripAffordanceV20();
        commandOnly.chosen(2,touchOnly);
        for(int i=0;i<20;i++)commandOnly.physicsFrame(touchOnly);
        commandOnly.consequence(2,0f,true,false);
        check(commandOnly.physicsHeldFrames()==0&&
              commandOnly.physicsSpreadFrames()==0,
              "Repeated grip commands counted as real adhesive holds");
        // Newly encoded ga21 must accept a real old ga20 model payload.
        String[] newParts=model.encode().split("\\|",-1);
        StringBuilder legacy=new StringBuilder("ga20");
        for(int i=1;i<newParts.length-5;i++)legacy.append('|').append(newParts[i]);
        GripAffordanceV20 upgraded=new GripAffordanceV20();
        check(upgraded.restore(legacy.toString())&&
              upgraded.trained()==model.trained()&&upgraded.physicsSamples()==0,
              "ga20 backward migration loses learning or invents microframes");
        fleeting.chosen(2,touchOnly);
        fleeting.physicsFrame(spread);
        fleeting.resumeLife();
        long priorTrain=fleeting.trained();
        fleeting.consequence(2,1f,true,false);
        check(fleeting.trained()==priorTrain,"Resume invented a completed micro-grip");
        String microSaved=fleeting.encode();
        GripAffordanceV20 recovered=new GripAffordanceV20();
        check(recovered.restore(microSaved)&&microSaved.equals(recovered.encode()),
              "Microframe counters failed roundtrip");
        String[] malformed=microSaved.split("\\|",-1);
        malformed[malformed.length-1]="999999";
        check(!recovered.restore(String.join("|",malformed))&&
              microSaved.equals(recovered.encode()),
              "Microcounter overflow corrupted previously saved learning");
        // Pausing learning cannot silently create unlabeled experience
        // that makes a valid save fail to reload.
        GripAffordanceV20 stopped=new GripAffordanceV20();
        stopped.chosen(2,touchOnly);
        stopped.physicsFrame(spread);
        stopped.consequence(2,1f,false,false);
        GripAffordanceV20 reloaded=new GripAffordanceV20();
        check(stopped.observed()==0&&
              reloaded.restore(stopped.encode()),
              "Paused learning produced non-restorable shadow outcomes");
        // Exact body and exact old motor weights: the optional researcher
        // module must not change a SINGLE selected action or learned neuron.
        SensorimotorMind oldMind=new SensorimotorMind(),newMind=new SensorimotorMind();
        GripAffordanceV20 observer=new GripAffordanceV20();
        newMind.attachGripAffordanceV20(observer);
        float[] first=new float[SensorimotorMind.S];
        first[0]=.7f;
        oldMind.resetEpisode(first);newMind.resetEpisode(first);
        oldMind.choose(first);newMind.choose(first);
        Random r=new Random(2020020);
        for(int i=0;i<440;i++){
            float[] senses=new float[SensorimotorMind.S];
            senses[0]=.62f;
            for(int j=6;j<senses.length;j++)senses[j]=r.nextFloat()*.8f;
            Morphology.Surface own=i%3==0?spread:
                    (i%3==1?cluster:touchOnly);
            oldMind.observe(senses,.70f,0f,body,own);
            newMind.observe(senses,.70f,0f,body,own);
            check(oldMind.action==newMind.action&&
                    oldMind.serialize().equals(newMind.serialize()),
                    "Shadow grip learner interfered with live motor choice "+i);
        }
        check(observer.observed()>400,
            "Actual physical membrane snapshot not linked to decision boundary");
        System.out.println("ZERO20 F PASS: 96 self sites, dispersed-vs-clustered grips, rotation, heldout, safe resumes, corrupt profiles and 440 old motor choices; "+observer.summary());
    }
}
