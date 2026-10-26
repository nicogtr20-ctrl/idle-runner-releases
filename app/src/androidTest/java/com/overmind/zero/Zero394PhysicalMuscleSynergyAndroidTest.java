package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** LAB: discover a short set of physically cooperative muscles by repeatedly
 * measuring true frozen-S21 170-body counterfactual pairs. Candidates are the
 * 12 independently measured cross-world winners from V393, NOT a live gait;
 * all combinations are selected by this laboratory's observed physical
 * displacement, without changing the real user neural checkpoint. */
@RunWith(AndroidJUnit4.class)
public final class Zero394PhysicalMuscleSynergyAndroidTest {
    static final int[][] V393_OBSERVED_CANDIDATES={
        {14,0,-1},{15,0,-1},{0,0,-1},{8,0,1},
        {13,0,-1},{10,1,-1},{11,1,-1},{13,1,-1},
        {12,1,-1},{14,1,-1},{0,1,-1},{15,1,-1}
    };
    static final class Result {
        final float free,wall;
        Result(float free,float wall){this.free=free;this.wall=wall;}
        float minGain(Result baseline){
            return Math.min(free-baseline.free,wall-baseline.wall);
        }
    }
    private static float along(JSONObject user,boolean wall,int heading,ArrayList<int[]> edits)
        throws Exception{
        int n=edits.size(),sites[]=new int[n],channels[]=new int[n],signs[]=new int[n];
        for(int i=0;i<n;i++){
            sites[i]=edits.get(i)[0];
            channels[i]=edits.get(i)[1];
            signs[i]=edits.get(i)[2];
        }
        Zero392TrueLocalActuatorJacobianAndroidTest.Result r=
            Zero392TrueLocalActuatorJacobianAndroidTest.trialCombination(
                user,wall,heading,sites,channels,signs);
        return r.dx*(heading==0?1f:heading==2?-1f:0f)+
            r.dy*(heading==1?1f:heading==3?-1f:0f);
    }
    private static Result evaluate(JSONObject user,ArrayList<int[]> edits)
        throws Exception{
        return new Result(along(user,false,0,edits),along(user,true,0,edits));
    }
    @Test(timeout=900000)
    public void userOriginalTrainedCortexCanLearnCombinationsWithoutAnAuthoredGait()
        throws Exception {
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        ArrayList<int[]> selected=new ArrayList<>();
        Result baseline=evaluate(user,selected),previous=baseline;
        int stages=0;
        boolean[] used=new boolean[V393_OBSERVED_CANDIDATES.length];
        Log.i("ZERO394_MOTOR_SYNERGY","BASELINE_TRAIN_EAST free="+baseline.free+
            ",wall="+baseline.wall);
        for(int step=0;step<8;step++){
            int winner=-1;
            Result best=null;
            float highest=previous.minGain(baseline);
            for(int candidate=0;candidate<V393_OBSERVED_CANDIDATES.length;candidate++){
                if(used[candidate])continue;
                selected.add(V393_OBSERVED_CANDIDATES[candidate]);
                Result measured=evaluate(user,selected);
                selected.remove(selected.size()-1);
                float val=measured.minGain(baseline);
                if(val>highest+.075f){
                    highest=val;winner=candidate;best=measured;
                }
            }
            if(winner<0)break;
            selected.add(V393_OBSERVED_CANDIDATES[winner]);
            used[winner]=true;
            previous=best;stages++;
            Log.i("ZERO394_MOTOR_SYNERGY","LEARNED_STAGE_"+step+
                ",selected="+Arrays.toString(V393_OBSERVED_CANDIDATES[winner])+
                ",count="+selected.size()+",freeEast="+best.free+
                ",wallEast="+best.wall+",worstGain="+
                best.minGain(baseline));
        }
        assertTrue("Scientifically observed first local improvement must exist",
            stages>=1);
        // Heldout TRUE material pose never used for selecting local motor
        // interventions. Same original saved V388 cortex, different posture.
        JSONObject alternative=new JSONObject(user.toString());
        alternative.put("training_room_1_scene_v379",
            user.getString("training_room_1_free_scene_v379"));
        ArrayList<int[]> none=new ArrayList<>();
        float heldoutEastBaseline=along(alternative,false,0,none);
        float heldoutEastSelected=along(alternative,false,0,selected);
        Log.i("ZERO394_MOTOR_SYNERGY","UNSEEN_POSE_EAST baseline="+
            heldoutEastBaseline+",trainedAdapter="+heldoutEastSelected);
        for(int direction=1;direction<4;direction++){
            float original=along(alternative,false,direction,none);
            float adapted=along(alternative,false,direction,selected);
            Log.i("ZERO394_MOTOR_SYNERGY","UNSEEN_POSE_OTHER_HEADING dir="+
                direction+",baseline="+original+",candidate="+adapted);
        }
        Log.i("ZERO394_MOTOR_SYNERGY",
            "ZERO394_PHYSICAL_SYNERGY_ANALYSIS_COMPLETE=PASS_LAB_ONLY"+
            ",fittedCount="+selected.size()+",trainingFree="+previous.free+
            ",trainingWall="+previous.wall+",unseenEast="+heldoutEastSelected);
    }
}
