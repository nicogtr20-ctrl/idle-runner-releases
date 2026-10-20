package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.util.Arrays;
import static org.junit.Assert.*;

/** Regression of the ACTUAL missing pre2.2 causal paths. The synthetic
 * examples check attribution logic only; physical world is not replaced
 * and autonomous survival must still be checked independently.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero371RealMotorEnergyBridgeAndroidTest {
    static float[] sense(boolean flip){
        float[] x=new float[194];
        // No predefined source classes: indices are two observed wavelengths.
        x[20+(flip?4:0)]=.85f;
        x[28+(flip?0:4)]=.85f;
        return x;
    }
    static float[] proposal(float dx,float dy){
        return new float[]{dx,dy,.5f,.5f,.3f};
    }
    static float[] light(int band,int sector){
        float[] p=new float[24];p[band*8+sector]=.9f;return p;
    }
    static void learnDirection(Zero3InstrumentalCreditV371 motor,
             float dx,float dy,float bodyDx,float bodyDy){
        for(int k=0;k<9;k++){
            motor.begin(sense(false),proposal(dx,dy),k>=5);
            motor.complete(bodyDx,bodyDy,.3f,true,k>=5);
        }
    }
    @Test public void intentionVersusMeasuredMovementIsNotConfused(){
        Zero3InstrumentalCreditV371 motor=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 values=new Zero3ContactValenceV369();
        assertEquals(0f,motor.score(sense(false),proposal(1,0),values),0f);
        values.contact(light(0,0),-7);values.contact(light(0,0),-7);
        values.contact(light(1,4),22);values.contact(light(1,4),22);
        // This physical actuator moves left when the actor asks right,
        // exactly the kind of mismatch seen on user's actual phone.
        learnDirection(motor,1,0,-10,0);
        learnDirection(motor,-1,0,10,0);
        assertTrue(motor.motorVerified(0));
        assertTrue(motor.motorVerified(4));
        float chooseRight=motor.score(sense(false),proposal(1,0),values);
        float chooseLeft=motor.score(sense(false),proposal(-1,0),values);
        assertTrue("score actual bodily approach, not actor heading",
            chooseRight>chooseLeft+.10f);
        assertTrue("flip two optical positions and the ranking must flip",
            motor.score(sense(true),proposal(1,0),values)<
            motor.score(sense(true),proposal(-1,0),values)-.10f);
    }
    @Test public void sixRealPriorApproachesAreLinkedOnlyAfterContact(){
        Zero3InstrumentalCreditV371 motor=new Zero3InstrumentalCreditV371();
        for(int i=0;i<6;i++){
            motor.begin(sense(false),proposal(1,0),false);
            motor.complete(11f,0f,.3f,true,false);
        }
        motor.begin(sense(false),proposal(1,0),false);
        motor.contact(light(0,0),-7f,false);
        assertEquals(1L,motor.linkedContacts());
        assertTrue("delayed real red credit assigned to physically approaching action",motor.delayedValue(0,0)<0);
        assertTrue(motor.delayedCount(0,0)>0);
        motor.abort();
        motor.begin(sense(false),proposal(1,0),true);
        motor.contact(light(0,0),-7f,true);
        assertEquals("held-out never becomes learned real harm",1L,motor.linkedContacts());
        motor.abort();
        float[] simultaneous=light(0,0);simultaneous[8+4]=.88f;
        motor.begin(sense(false),proposal(1,0),false);
        motor.contact(simultaneous,-7f,false);
        assertEquals("cannot attribute two equally visible signals",1L,motor.linkedContacts());
        assertEquals(1L,motor.ambiguousContacts());
    }
    @Test public void realMotorMustBeatZeroAndRejectStormFrames(){
        Zero3InstrumentalCreditV371 motor=new Zero3InstrumentalCreditV371();
        for(int i=0;i<40;i++){
            boolean held=i%5==4;
            motor.begin(sense(false),proposal(1,0),held);
            motor.complete(0f,0f,.3f,true,held);
        }
        assertFalse("static body is not a validated movement skill",motor.motorVerified(0));
        int verified=motor.verifiedDirections();
        motor.begin(sense(false),proposal(-1,0),false);
        motor.complete(50f,0,.3f,false,false);
        assertEquals("wind displacement must not be interpreted as voluntary",verified,motor.verifiedDirections());
    }
    @Test public void soleV353DecisionUsesActualMotorDirectionBeforeModelTrusted(){
        Zero3CognitionV353 brain=new Zero3CognitionV353();
        // These are REAL signed physical-contact values, with no body-world
        // target, no hardcoded blue/red or fabricated one-step outcomes.
        brain.genuineContactV369(light(0,0),-7f,false);
        brain.genuineContactV369(light(0,0),-7f,false);
        brain.genuineContactV369(light(1,4),22f,false);
        brain.genuineContactV369(light(1,4),22f,false);
        for(int k=0;k<9;k++){
            boolean hold=k>=5;
            brain.begin(sense(false),proposal(1,0),hold);
            brain.physicalMotorV371(-10f,0f,.3f,true,false,hold);
            brain.abort();
            brain.begin(sense(false),proposal(-1,0),hold);
            brain.physicalMotorV371(10f,0f,.3f,true,false,hold);
            brain.abort();
        }
        assertEquals(2,brain.snapshot().verifiedMotorDirectionsV371);
        assertFalse("V353 model must still have ZERO artificial evidence",brain.trusted(0));
        float[][] proposals={proposal(-1,0),proposal(1,0)};
        assertEquals("The sole trained V353 chooser must act on PHYSICAL motor evidence, not wanted heading",1,brain.select(sense(false),proposals));
        assertEquals("Mirrored world reverses its physical causal choice",
                0,brain.select(sense(true),proposals));
    }
    @Test public void coldResumeKeepsRecentVisualApproachForSubsequentRealContact()throws Exception{
        Zero3InstrumentalCreditV371 brain=new Zero3InstrumentalCreditV371();
        for(int i=0;i<6;i++){
            brain.begin(sense(false),proposal(1,0),false);
            brain.complete(9f,0f,.30f,true,false);
        }
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        brain.write(new DataOutputStream(bytes));
        Zero3InstrumentalCreditV371 resumed=Zero3InstrumentalCreditV371.read(
            new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
        resumed.begin(sense(false),proposal(1,0),false);
        resumed.contact(light(0,0),-7f,false);
        assertEquals("recent PRE→POST history survives cold app restart",
            1L,resumed.linkedContacts());
        resumed.abort();resumed.newLife();
        resumed.begin(sense(false),proposal(1,0),false);
        resumed.contact(light(0,0),-7f,false);
        assertEquals("new life cannot fake past body approach",
            1L,resumed.linkedContacts());
    }
    @Test public void coldCheckpointAndLifeResetRetainEvidenceNotPhantomMotorAction()throws Exception{
        Zero3InstrumentalCreditV371 original=new Zero3InstrumentalCreditV371();
        learnDirection(original,1,0,10,0);
        original.newLife();
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(b);original.write(d);
        byte[] snapshot=b.toByteArray();
        Zero3InstrumentalCreditV371 restored=Zero3InstrumentalCreditV371.read(
            new DataInputStream(new ByteArrayInputStream(snapshot)));
        ByteArrayOutputStream again=new ByteArrayOutputStream();
        restored.write(new DataOutputStream(again));
        assertArrayEquals(snapshot,again.toByteArray());
        assertTrue(restored.motorVerified(0));
        byte[] bad=Arrays.copyOf(snapshot,snapshot.length);
        bad[3]^=0x7f; // mutate nonzero schema byte, not leading zero
        try {
            Zero3InstrumentalCreditV371.read(new DataInputStream(new ByteArrayInputStream(bad)));
            fail("corrupt V371 motor evidence accepted");
        }catch(IOException expected){}
    }
}
