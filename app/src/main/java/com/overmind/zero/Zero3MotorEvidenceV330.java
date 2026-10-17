package com.overmind.zero;

/**
 * V3.30 strictly observational motor causal-readiness audit.
 *
 * All inputs are either an actor's real chosen intention, the actual 64
 * efferences BEFORE each gel step, local surface geometry, or the REAL
 * Coordinator.Experience completed by the solver. No object oracle,
 * target direction, COM impulse, synthetic gain or altered reward.
 *
 * The initial-momentum extrapolation is ONLY an INERTIA PROXY, not a paired
 * passive-world counterfactual. The muscle cancellation index is similarly
 * descriptive, not an identified causal force on the COM.
 */
public final class Zero3MotorEvidenceV330 {
    private Zero3MotorEvidenceV330(){}
    public static final class Measure {
        public final long episode,decision;
        public final float directionX,directionY;
        public final float startVX,startVY;
        public final float dx,dy,gross,work,impulse,seconds;
        public final float signedAlongIntent,netOverGross;
        public final float inertiaProxyAlongIntent,residualBeyondInertiaProxy;
        public final float cancellationProxy,meanMotorMagnitude;
        public final int executedSubsteps,gripFrames,bilateralFrames;
        public final boolean selectedMovement,censored,physical;
        private Measure(Accumulator a,Zero3LearningFoundation.Experience r,
                        int holds,int bilateral){
            episode=a.episode;decision=a.decision;
            directionX=a.x;directionY=a.y;
            startVX=a.vx;startVY=a.vy;
            dx=r.deltaX;dy=r.deltaY;gross=r.gross;work=r.work;
            impulse=r.activeImpulse;seconds=r.seconds;
            signedAlongIntent=dx*a.x+dy*a.y;
            inertiaProxyAlongIntent=(a.vx*a.x+a.vy*a.y)*seconds;
            residualBeyondInertiaProxy=signedAlongIntent-inertiaProxyAlongIntent;
            netOverGross=(float)Math.hypot(dx,dy)/Math.max(1e-6f,gross);
            cancellationProxy=a.sumTotal>1e-6?
                1f-(float)(a.sumCoherent/a.sumTotal):0f;
            meanMotorMagnitude=a.count>0?(float)(a.sumTotal/a.count):0f;
            executedSubsteps=a.count;gripFrames=holds;bilateralFrames=bilateral;
            selectedMovement=(a.x!=0f||a.y!=0f)&&r.intent.effort>.01f;
            censored=r.censored||r.externalCorrection;
            physical=r.physicalEvidence;
            if(!Float.isFinite(signedAlongIntent)||
               !Float.isFinite(residualBeyondInertiaProxy)||
               !Float.isFinite(cancellationProxy)||
               !Float.isFinite(netOverGross)||
               !Float.isFinite(meanMotorMagnitude)||
               cancellationProxy<-1e-4f||cancellationProxy>1.0001f)
                throw new IllegalStateException("V330 invalid measured motor outcome");
        }
    }
    public static final class Accumulator {
        final long episode,decision;
        final float x,y,vx,vy;
        int count;
        double sumTotal,sumCoherent;
        public Accumulator(long episode,long decision,float intentX,float intentY,
                           float initialVX,float initialVY){
            if(episode<=0||decision<0||
               !Float.isFinite(intentX)||!Float.isFinite(intentY)||
               !Float.isFinite(initialVX)||!Float.isFinite(initialVY)||
               Math.hypot(intentX,intentY)>1.0001)
                throw new IllegalArgumentException("V330 invalid physical PRE");
            this.episode=episode;this.decision=decision;
            x=intentX;y=intentY;vx=initialVX;vy=initialVY;
        }
        /** Only the commands ACTUALLY about to be consumed by gel.step. */
        public void motorSubstep(float[] command,Morphology.Surface skin,
                                 MotorBrain.Body body){
            if(command==null||command.length!=64||skin==null||body==null||
               count>=Zero3RuntimeV325.SUBSTEPS)
                throw new IllegalArgumentException("V330 wrong actuator step");
            double vectorX=0,vectorY=0,magnitude=0;
            for(int sector=0;sector<16;sector++){
                int mid=sector*6+3;
                double ux=skin.x[mid]-body.cx,uy=skin.y[mid]-body.cy;
                double len=Math.hypot(ux,uy);
                if(len<1e-5)continue;
                ux/=len;uy/=len;
                int j=sector*4;
                for(int k=0;k<4;k++)
                    if(!Float.isFinite(command[j+k])||
                       Math.abs(command[j+k])>1.00001f)
                        throw new IllegalArgumentException("V330 false executed command");
                // Radial+tangential local *actuator demand* proxy only.
                double fx=command[j]*ux-command[j+1]*uy;
                double fy=command[j]*uy+command[j+1]*ux;
                vectorX+=fx;vectorY+=fy;
                magnitude+=Math.hypot(fx,fy);
            }
            sumTotal+=magnitude;
            sumCoherent+=Math.min(magnitude,Math.hypot(vectorX,vectorY));
            count++;
        }
        public Measure complete(Zero3LearningFoundation.Experience result,
                                int gripFrames,int bilateralFrames){
            if(result==null||result.intent.episodeId!=episode||
               result.intent.decisionId!=decision||
               count!=Zero3RuntimeV325.SUBSTEPS||
               result.frames!=count||gripFrames<0||bilateralFrames<0||
               gripFrames>count||bilateralFrames>gripFrames||
               Float.floatToRawIntBits(result.intent.x)!=Float.floatToRawIntBits(x)||
               Float.floatToRawIntBits(result.intent.y)!=Float.floatToRawIntBits(y))
                throw new IllegalStateException("V330 motor credit has wrong physical owner");
            return new Measure(this,result,gripFrames,bilateralFrames);
        }
    }
}
