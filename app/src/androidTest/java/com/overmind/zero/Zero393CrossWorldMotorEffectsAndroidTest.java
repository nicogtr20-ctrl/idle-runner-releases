package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** 96 physically matched one-site intervention effects, true user's entire
 * trained V388 cortex, identical 170 body material, both collision worlds.
 * Each trial starts from the same exact immutable user's original checkpoint,
 * never changing their brain on device, source saved JSON, or original GRU. */
@RunWith(AndroidJUnit4.class)
public final class Zero393CrossWorldMotorEffectsAndroidTest {
    static final class Measure implements Comparable<Measure> {
        final int sector,channel,sign;
        final float free,wall,freeGain,wallGain,minimumGain;
        Measure(int sector,int channel,int sign,float free,float wall,
                float free0,float wall0){
            this.sector=sector;this.channel=channel;this.sign=sign;
            this.free=free;this.wall=wall;
            freeGain=free-free0;wallGain=wall-wall0;
            minimumGain=Math.min(freeGain,wallGain);
        }
        @Override public int compareTo(Measure o){
            return Float.compare(o.minimumGain,minimumGain);
        }
        String csv(){return "sector="+sector+",channel="+channel+",sign="+sign+
           ",free="+free+",wall="+wall+",gainFree="+freeGain+
           ",gainWall="+wallGain+",worstCaseGain="+minimumGain;}
    }
    @Test(timeout=900000)
    public void findOnlyLocalPhysicalCorrectionsThatImproveBothWallAndClearWorld()
        throws Exception {
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero392TrueLocalActuatorJacobianAndroidTest.Result baseFree=
            Zero392TrueLocalActuatorJacobianAndroidTest.trial(user,false,-1,-1,0);
        Zero392TrueLocalActuatorJacobianAndroidTest.Result baseWall=
            Zero392TrueLocalActuatorJacobianAndroidTest.trial(user,true,-1,-1,0);
        Measure[] table=new Measure[96];
        int at=0;
        for(int ch:new int[]{0,1,3})for(int sector=0;sector<16;sector++)
            for(int sign:new int[]{-1,+1}){
                Zero392TrueLocalActuatorJacobianAndroidTest.Result clear=
                    Zero392TrueLocalActuatorJacobianAndroidTest.trial(user,false,sector,ch,sign);
                Zero392TrueLocalActuatorJacobianAndroidTest.Result wall=
                    Zero392TrueLocalActuatorJacobianAndroidTest.trial(user,true,sector,ch,sign);
                table[at++]=new Measure(sector,ch,sign,clear.dx,wall.dx,
                                       baseFree.dx,baseWall.dx);
            }
        Arrays.sort(table);
        int bothBetter=0,wallEast=0,freeEast=0;
        for(Measure v:table){
            if(v.freeGain>.05f&&v.wallGain>.05f)bothBetter++;
            if(v.wall>0)wallEast++;
            if(v.free>0)freeEast++;
        }
        Log.i("ZERO393_CROSSWORLD","S21_REAL_33312_BASELINE,freeEast="+
            baseFree.dx+",wallEast="+baseWall.dx+",bothImprove="+bothBetter+
            ",wallMovesEast="+wallEast+",freeMovesEast="+freeEast+",cases=96");
        for(int i=0;i<Math.min(12,table.length);i++)
            Log.i("ZERO393_CROSSWORLD","ROBUST_MUSCLE_"+i+","+table[i].csv());
        for(int i=table.length-3;i<table.length;i++)
            Log.i("ZERO393_CROSSWORLD","WORST_MUSCLE_"+i+","+table[i].csv());
        assertEquals(96,at);
        assertTrue(Float.isFinite(table[0].minimumGain));
        Log.i("ZERO393_CROSSWORLD",
            "ZERO393_MOTOR_CROSSWORLD_COMPLETE=PASS_MATH_ONLY_NOT_AUTONOMY");
    }
}
