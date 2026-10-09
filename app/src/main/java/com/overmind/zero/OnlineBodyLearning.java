package com.overmind.zero;

import java.util.Random;

/**
 * Motor/membrane mutations are evaluated in ZERO's actual running world.
 * No off-screen fitness, no target coordinates, no scripted movement.
 * Two consecutive observational windows are compared; this does not
 * establish experimental causality.
 */
public final class OnlineBodyLearning {
    public static final int MOTOR=0,MANIPULATION=1,MEMBRANE=2;
    public static final int WINDOW=8;
    private final Random rnd=new Random();
    private int domain=0,phase=0,samples=0,failures=0,mobileWindows=0;
    private float scoreSum=0,pathSum=0,energySum=0,dirXSum=0,dirYSum=0;
    private float baseScore,basePath,baseEnergy,baseDirX,baseDirY;
    private float meanTravel=0;
    private boolean baseRoom,roomAtStart,roomChanged;
    private float[] candidate;

    public static final class Result {
        public final int domain;
        public final boolean accepted;
        public final float[] weights;
        Result(int domain,boolean accepted,float[] weights) {
            this.domain=domain;this.accepted=accepted;this.weights=weights;
        }
    }
    public float[] weights(int requested,float[] stored){
        return phase==1&&requested==domain&&candidate!=null?candidate:stored;
    }
    public void abortTrial(){
        phase=0;samples=0;candidate=null;
        scoreSum=pathSum=energySum=dirXSum=dirYSum=0;
        roomChanged=false;
    }
    public boolean testingCandidate(){return phase==1;}
    public float movementAverage(){return meanTravel;}
    public int activeDomain(){return domain;}
    private float[] mutate(float[] stored,int which,boolean chamber){
        float[] w=stored.clone();
        // Early coordinated movement requires much wider experiments than
        // the previous 1-3 tiny mutations (0.06 per weight).
        int count=which==MOTOR?12+rnd.nextInt(20):3+rnd.nextInt(8);
        float scale=which==MOTOR?Math.min(1.1f,.34f+.09f*(failures/6)):
                    which==MANIPULATION?.24f:.22f;
        for(int i=0;i<count;i++){
            int k;
            if(which==MOTOR&&rnd.nextFloat()<.48f)
                k=MotorBrain.INPUT*MotorBrain.HIDDEN+
                  rnd.nextInt(MotorBrain.PARAMS-MotorBrain.INPUT*MotorBrain.HIDDEN);
            else if(which==MEMBRANE&&chamber&&rnd.nextFloat()<.58f)
                k=Morphology.LEGACY_PARAMS+rnd.nextInt(Morphology.GRIP_HEAD);
            else k=rnd.nextInt(w.length);
            w[k]=Math.max(-3.5f,Math.min(3.5f,
                 w[k]+(float)rnd.nextGaussian()*scale));
        }
        return w;
    }
    /**
     * A completed sensory action. Path and forward projection are actually
     * measured nucleus translations in live physical world coordinates.
     * Forward refers to ZERO's own chosen direction, never a known resource.
     */
    public Result observe(float reward,float path,float forward,int contacts,
                          float energy,boolean room,float dx,float dy,
                          float[] motor,float[] hand,float[] shape){
        if(!Float.isFinite(reward)||!Float.isFinite(path)||
           !Float.isFinite(forward)||!Float.isFinite(energy)||
           !Float.isFinite(dx)||!Float.isFinite(dy))return null;
        if(samples==0){roomAtStart=room;roomChanged=false;}
        if(roomAtStart!=room)roomChanged=true;
        float traveled=Math.max(0,Math.min(50,path));
        meanTravel=meanTravel*.98f+traveled*.02f;
        pathSum+=traveled;
        scoreSum+=.15f*reward+.024f*traveled+
           .034f*Math.max(-12,Math.min(12,forward))+
           .055f*Math.min(3,Math.max(0,contacts));
        energySum+=energy;dirXSum+=dx;dirYSum+=dy;
        if(++samples<WINDOW)return null;
        float avgPath=pathSum/WINDOW,score=scoreSum/WINDOW;
        float e=energySum/WINDOW,x=dirXSum/WINDOW,y=dirYSum/WINDOW;
        boolean stable=!roomChanged;
        samples=0;pathSum=scoreSum=energySum=dirXSum=dirYSum=0;
        if(avgPath>.65f)mobileWindows++;
        if(phase==0){
            basePath=avgPath;baseScore=score;baseEnergy=e;
            baseDirX=x;baseDirY=y;baseRoom=room;
            phase=1;
            candidate=mutate(domain==MOTOR?motor:domain==MANIPULATION?hand:shape,
                             domain,room);
            return null;
        }
        boolean comparable=stable&&baseRoom==room&&
             Math.abs(baseEnergy-e)<=30f&&Math.hypot(baseDirX-x,baseDirY-y)<=1.1;
        boolean motorMoved=avgPath>basePath+.12f&&avgPath>.15f;
        boolean accepted=comparable&&score>baseScore+.012f&&
                         (domain!=MOTOR||motorMoved);
        Result out=new Result(domain,accepted,accepted?candidate:null);
        if(domain==MOTOR) {
            if(accepted)failures=0;else failures=Math.min(100,failures+1);
        }
        // A newborn that cannot move should focus on discovering locomotion
        // before spending most trials on the gate or discs.
        if(domain!=MOTOR||mobileWindows>=4)domain=(domain+1)%3;
        abortTrial();
        return out;
    }
    public static void main(String[] args) {
        float[] motor=MotorBrain.virgin(new Random(13));
        float[] hand=ContactLearning.virgin(),shape=new float[Morphology.PARAMS];
        OnlineBodyLearning b=new OnlineBodyLearning();
        if(b.weights(MOTOR,motor)!=motor)throw new AssertionError("not virgin");
        for(int i=0;i<WINDOW;i++)b.observe(0,0,0,0,70,false,1,0,motor,hand,shape);
        float[] trial=b.weights(MOTOR,motor);
        if(trial==motor)throw new AssertionError("mutation was not activated");
        int changes=0;for(int i=0;i<motor.length;i++)if(motor[i]!=trial[i])changes++;
        if(changes<5)throw new AssertionError("motor mutation too weak");
        Result result=null;
        for(int i=0;i<WINDOW;i++)
            result=b.observe(0,0,0,0,70,false,1,0,motor,hand,shape);
        if(result==null||result.accepted||b.activeDomain()!=MOTOR||
           b.weights(MOTOR,motor)!=motor)
            throw new AssertionError("stationary candidate was incorrectly learned");
        for(int i=0;i<WINDOW;i++)
            b.observe(0,0,0,0,70,false,1,0,motor,hand,shape);
        for(int i=0;i<WINDOW;i++)
            result=b.observe(.02f,4,2,0,70,false,1,0,motor,hand,shape);
        if(result==null||!result.accepted||result.domain!=MOTOR||
           result.weights.length!=motor.length)
            throw new AssertionError("physically moving candidate rejected");
        if(b.weights(MOTOR,motor)!=motor)
            throw new AssertionError("tentative model leaked after test");
        System.out.println("ZERO-009.3: stationary rejection, live movement acceptance and rollback passed");
    }
}
