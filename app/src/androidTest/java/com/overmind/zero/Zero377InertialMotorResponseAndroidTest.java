package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.util.Arrays;
import static org.junit.Assert.*;

/** Physical motor attribution must defeat actual PRE-momentum continuation.
 * Tests use measured COM velocity and physically realized POST displacement,
 * never supply destinations, prelabel rewards or synthetic propulsive forces
 * to the original autonomous GRU. These DO NOT certify blind-world survival.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero377InertialMotorResponseAndroidTest {
    static float[] view(float vx,float vy){
        float[] p=new float[194];
        p[191]=vx/130f;p[192]=vy/130f;p[188]=-.3f;
        p[20+4]=.90f; // true observed band0 WEST
        p[28+0]=.99f; // true observed band1 EAST
        return p;
    }
    static float[] action(float dx){return new float[]{dx,0f,.5f,.5f,.3f};}
    static Zero3ContactValenceV369 consequences(){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(0,+22f);v.materialContact(0,+22f);
        v.materialContact(1,-7f);v.materialContact(1,-7f);
        return v;
    }
    static void learn(Zero3InertialMotorV377 model,float vx,float action,
                      float xActual,boolean clean){
        for(int i=0;i<11;i++){
            boolean heldout=i>=7;
            model.begin(view(vx,0),action(action),heldout);
            model.complete(xActual,0,.30f,clean,heldout);
        }
    }
    @Test public void passiveStraightCoastingIsNeverMistakenForAValidatedSkill(){
        Zero3InertialMotorV377 m=new Zero3InertialMotorV377();
        // 10 world-units/second entering decision, +3 U after .30s,
        // regardless of WEST command. The motion is only momentum.
        learn(m,10f,-1f,3f,true);
        assertFalse("passive travel is not demonstrated motor control",
            m.verified(view(10f,0),action(-1f)));
        assertEquals(0,m.verifiedContexts());
        assertEquals(11L,m.validActions());
    }
    @Test public void brakingButStillGlidingEastIsNotTreatedAsReachingWest(){
        Zero3InertialMotorV377 m=new Zero3InertialMotorV377();
        learn(m,10f,-1f,2f,true); // real WEST intent slows X 3 to X 2
        assertTrue("heldout confirms deviation from inertial projection",
            m.verified(view(10f,0),action(-1f)));
        assertTrue("actual body still heads into EAST harmful light",
            m.opticalScore(view(10,0),action(-1),consequences())<0f);
    }
    @Test public void trueReversalScoresMeasuredBodyRatherThanCommandLabel(){
        Zero3InertialMotorV377 m=new Zero3InertialMotorV377();
        learn(m,10f,-1f,-1f,true);
        assertTrue(m.verified(view(10,0),action(-1)));
        assertTrue("true reversal travels toward physically beneficial WEST",
            m.opticalScore(view(10,0),action(-1),consequences())>0f);
        assertFalse("a different unvalidated heading cannot inherit this skill",
            m.verified(view(10,0),action(+1)));
        assertTrue(m.verifiedContexts()>=1);
    }
    @Test public void weatherAndUncleanOutcomesNeverBecomeGaitSkill(){
        Zero3InertialMotorV377 m=new Zero3InertialMotorV377();
        learn(m,10f,-1f,-50f,false); // wind or external solver
        assertEquals(0,m.verifiedContexts());
        assertEquals(11L,m.contaminatedActions());
        assertFalse(m.verified(view(10,0),action(-1)));
    }
    @Test public void existingV353CheckpointLoadsWithoutNewTailAndFreshRoundTrips()
            throws Exception{
        Zero3CognitionV353 original=new Zero3CognitionV353();
        byte[] all=original.encode();
        int suffix=4+16+32*(4+4+4+4+8+8);
        byte[] old=Arrays.copyOf(all,all.length-suffix);
        Zero3CognitionV353 loaded=Zero3CognitionV353.decode(old);
        assertArrayEquals("V375 pretrained neurons preserved before optional V377",
            old,Arrays.copyOf(loaded.encode(),old.length));
        byte[] re=loaded.encode();
        assertArrayEquals(re,Zero3CognitionV353.decode(re).encode());
        Zero3InertialMotorV377 m=new Zero3InertialMotorV377();
        learn(m,10f,-1f,-1f,true);
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        m.write(new DataOutputStream(bytes));
        byte[] raw=bytes.toByteArray();
        assertArrayEquals(raw,write(Zero3InertialMotorV377.read(
            new DataInputStream(new ByteArrayInputStream(raw)))));
        raw[3]^=0x01;
        try{Zero3InertialMotorV377.read(
            new DataInputStream(new ByteArrayInputStream(raw)));
            fail("Corrupted V377 memory accepted");
        }catch(IOException expected){}
    }
    private static byte[] write(Zero3InertialMotorV377 m)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        m.write(new DataOutputStream(b));return b.toByteArray();
    }
}
