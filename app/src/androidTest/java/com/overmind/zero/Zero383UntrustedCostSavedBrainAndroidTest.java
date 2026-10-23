package com.overmind.zero;

import android.content.Context;
import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** READ ONLY: user V382 saved recurrent cognition with true option predictor,
 * but reduced sensory representations reconstructed from the stored 256-flight
 * maxima only. It NEVER infers actual per-decision override from the
 * reduced frames; measures whether invalid cost output is material. */
@RunWith(AndroidJUnit4.class)
public final class Zero383UntrustedCostSavedBrainAndroidTest {
    @Test public void validateRealCostHeadAndMeasureLiveDecisionScoringSensitivity()
            throws Exception{
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        File f=new File(ctx.getExternalFilesDir(null),"zero383-user382-readonly.json");
        assertTrue(f.isFile());
        JSONObject input=new JSONObject(new String(Files.readAllBytes(f.toPath()),
            StandardCharsets.UTF_8));
        Zero3OriginalBrainV356.Readout readout=
            Zero3OriginalBrainV356.inspectSaved(input.getString("brain"));
        assertEquals(10294,readout.decisions);
        Field coreF=Zero3OriginalBrainV356.Readout.class.getDeclaredField("core");
        coreF.setAccessible(true);
        Object core=coreF.get(readout);
        Field cognitionF=Zero3RecurrentBrain.class.getDeclaredField("cognition");
        cognitionF.setAccessible(true);
        Zero3CognitionV353 cognition=(Zero3CognitionV353)cognitionF.get(core);
        assertTrue("real user head 0 energy predicted heldout",cognition.trusted(0));
        assertTrue("real user head 1 physical harm predicted heldout",cognition.trusted(1));
        assertFalse("real user head 2 physical cost must be rejected",cognition.trusted(2));
        Zero3CognitionV353.Snapshot snapshot=cognition.snapshot();
        float mse=snapshot.mse[2],baseline=snapshot.baselineMse[2];
        assertTrue("Real V382 cost predictor must be provably worse than no model",
            mse>baseline*100f);
        StringBuilder result=new StringBuilder(String.format(Locale.US,
            "REAL_V382_COST_HEAD_UNTRUSTED mse=%.12g baseline=%.12g mse_over_baseline=%.3f cognition_prediction_evaluations=%d%n",
            mse,baseline,mse/Math.max(1e-14f,baseline),snapshot.plannerUses));
        String[] lines=input.getString("flight").trim().split("\n");
        assertEquals(257,lines.length);
        int comparisons=0,substantiveCost=0,costChangesRank=0;
        double costSpanMean=0,costSpanMax=0,headEnergyRangeMean=0;
        for(int i=1;i<lines.length;i++){
            String[] parts=lines[i].split(",");
            assertEquals(28,parts.length);
            float[] observed=new float[194];
            observed[188]=.45f;observed[193]=1f;
            float blueStrength=Float.parseFloat(parts[19]),
                  redStrength=Float.parseFloat(parts[21]);
            int blueBin=Integer.parseInt(parts[18]),
                redBin=Integer.parseInt(parts[20]);
            if(blueBin>=0)observed[20+blueBin]=blueStrength;
            if(redBin>=0)observed[28+redBin]=redStrength;
            observed[191]=Float.parseFloat(parts[5])/130f;
            observed[192]=Float.parseFloat(parts[6])/130f;
            double minCost=Double.MAX_VALUE,maxCost=-Double.MAX_VALUE,
                   minEnergy=Double.MAX_VALUE,maxEnergy=-Double.MAX_VALUE;
            double bestWith=-1e100,bestWithout=-1e100;
            int winnerWith=-1,winnerWithout=-1;
            for(int k=0;k<8;k++){
                float a=k*(float)Math.PI/4f;
                float[] option={(float)Math.cos(a),(float)Math.sin(a),.5f,.5f,.45f};
                float[] predicted=cognition.predictionV354(observed,option);
                double cost=predicted[2],energy=predicted[0]-predicted[1];
                double optical=cognition.realContactPreferenceV374(observed,option);
                minCost=Math.min(minCost,cost);maxCost=Math.max(maxCost,cost);
                minEnergy=Math.min(minEnergy,energy);maxEnergy=Math.max(maxEnergy,energy);
                double confidence=(predicted[12]*1.5);
                double with=optical+energy-cost-confidence;
                double without=optical+energy-confidence;
                if(with>bestWith){bestWith=with;winnerWith=k;}
                if(without>bestWithout){bestWithout=without;winnerWithout=k;}
            }
            double costRange=maxCost-minCost;
            costSpanMean+=costRange;costSpanMax=Math.max(costSpanMax,costRange);
            headEnergyRangeMean+=(maxEnergy-minEnergy);
            if(costRange>.005)substantiveCost++;
            if(winnerWith!=winnerWithout)costChangesRank++;
            comparisons++;
        }
        result.append(String.format(Locale.US,
            "REDUCED_SENSORY_COUNTERFACTUAL n=%d meanCostHeadingRange=%.7f maxCostHeadingRange=%.7f costRangeGreaterThanValenceMargin=%d rankingFlipWithUntrustedCost=%d meanEnergyDamageHeadingRange=%.7f%n",
            comparisons,costSpanMean/comparisons,costSpanMax,substantiveCost,
            costChangesRank,headEnergyRangeMean/comparisons));
        Bundle status=new Bundle();status.putString("stream",result.toString());
        InstrumentationRegistry.getInstrumentation().sendStatus(0,status);
        System.out.print(result);
    }
}