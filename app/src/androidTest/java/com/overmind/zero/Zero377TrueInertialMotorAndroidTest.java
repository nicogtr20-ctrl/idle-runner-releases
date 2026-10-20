package com.overmind.zero;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.util.Arrays;
import static org.junit.Assert.*;

/** This rejects the balloon error: coasting cannot validate voluntary motion.
 * No synthetic push is injected in the original physics; this tests only
 * mathematical credit based on authentic PRE velocity and actual POST travel.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero377TrueInertialMotorAndroidTest {
    private float[] perception(float vx,float vy){
        float[] p=new float[194];p[191]=vx/130f;p[192]=vy/130f;
        p[20+4]=.9f;p[28]=.9f;p[188]=-.5f;
        return p;
    }
    private float[] action(float x){return new float[]{x,0f,.5f,.4f,.3f};}
    private static byte[] bytes(Zero3InstrumentalCreditV371 m)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        m.write(new DataOutputStream(b));return b.toByteArray();
    }
    @Test public void passiveBalloonCoastingDoesNotProduceCertifiedMuscleSkill(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        float[] before=perception(10f,0f),cmd=action(-1f);
        for(int i=0;i<12;i++){
            boolean held=i>=6;
            m.begin(before,cmd,held);
            m.finish(3f,0f,.30f,held); // 10 U/s * 0.3s, no motor correction
        }
        assertFalse("old V371 zero-motion baseline incorrectly passed coast",m.reliable(before,cmd));
        assertNull(m.expected(before,cmd));
        assertEquals(0,m.reliableBanks());
    }
    @Test public void verifiedBrakePredictsPhysicalTurnNotRequestedDirection(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        float[] prior=perception(10f,0f),west=action(-1f);
        for(int i=0;i<11;i++){
            boolean held=i>=6;
            m.begin(prior,west,held);
            m.finish(.5f,0f,.30f,held); // strong braking, still moving EAST
        }
        assertTrue("braking beats inertial coast on independent physical trials",m.reliable(prior,west));
        assertEquals(.5f,m.expected(prior,west)[0],.025f);
        assertTrue("braking cannot be reported as instant reversal",
            m.expected(prior,west)[0]>0f);
        assertNull("speed-conditioned prediction not invented at rest",
            m.expected(perception(0f,0f),west));
    }
    @Test public void originalV371CodecRemainsPrefixAndColdRestartKeepsRealEvidence()throws Exception{
        Zero3InstrumentalCreditV371 source=new Zero3InstrumentalCreditV371();
        float[] prior=perception(10f,0f),west=action(-1f);
        for(int i=0;i<10;i++){
            boolean held=i>=6;
            source.begin(prior,west,held);
            source.complete(.5f,0f,.30f,true,held);
        }
        byte[] current=bytes(source);
        // Original V371 prefix, exactly as already installed in OTA375.
        int tail=4+16*(4+4+4+4+8+8);
        byte[] old=Arrays.copyOf(current,current.length-tail);
        Zero3InstrumentalCreditV371 legacy=Zero3InstrumentalCreditV371.read(
            new DataInputStream(new ByteArrayInputStream(old)));
        assertArrayEquals(old,Arrays.copyOf(bytes(legacy),old.length));
        Zero3InstrumentalCreditV371 recovered=Zero3InstrumentalCreditV371.read(
            new DataInputStream(new ByteArrayInputStream(current)));
        assertArrayEquals("V377 new evidence survives cold brain restore",current,bytes(recovered));
        assertTrue(recovered.verifiedInertialResponseV377()>0);
        assertEquals(0,legacy.verifiedInertialResponseV377());
    }
    @Test public void independentStormFramesCannotTeachFictitiousBraking(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        for(int i=0;i<30;i++){
            boolean held=i%4==3;
            m.begin(perception(10f,0f),action(-1f),held);
            m.complete(-80f,0f,.3f,false,held); // external gust/correction
        }
        assertEquals(0,m.verifiedInertialResponseV377());
    }
}