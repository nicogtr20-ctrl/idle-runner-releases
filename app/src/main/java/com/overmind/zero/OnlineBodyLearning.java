package com.overmind.zero;

import java.util.Random;

/**
 * Live motor evolution only. Mutation candidates act in ZERO's actual world:
 * no offline fitness, no target oracle and no scripted manipulation.
 * Comparison of sequential 8-action windows is an observational heuristic,
 * NOT a controlled A/B test with matched initial physical states.
 */
public final class OnlineBodyLearning {
    public static final int MOTOR=0,MANIPULATION=1,MEMBRANE=2;
    public static final int WINDOW=8;
    private final Random random=new Random();
    private int domain=0,phase=0,samples=0,failures=0,mobileWindows=0;
    private float pathSum,forwardSum,rewardSum,spentSum,gainSum,dirXSum,dirYSum;
    private int noveltySum,contactsSum;
    private float firstX,firstY,baseScore,baseNet,baseEnergy,baseDirX,baseDirY;
    private float baseNovelty,baseGain,basePhysicalEffect;
    private float externalEffectSum=0;
    private int baseContacts;
    private boolean baseRoom,roomAtStart,roomChanged;
    private float[] candidate;
    private final int[] accepted=new int[3],rejected=new int[3];
    public float lastBaselineScore=0,lastCandidateScore=0;
    public float lastBaselineNet=0,lastCandidateNet=0;
    public boolean lastWasAccepted=false;
    public int lastDomain=-1;
    private float meanTravel=0;

    public static final class Result {
        public final int domain;
        public final boolean accepted;
        public final float[] weights;
        public final float baselineScore,candidateScore;
        Result(int domain,boolean accepted,float[] weights,float baselineScore,float candidateScore){
            this.domain=domain;this.accepted=accepted;this.weights=weights;
            this.baselineScore=baselineScore;this.candidateScore=candidateScore;
        }
    }
    public int accepted(int which){return accepted[which];}
    public int rejected(int which){return rejected[which];}
    public void restoreCounts(int which,int acceptedCount,int rejectedCount){
        accepted[which]=Math.max(0,acceptedCount);rejected[which]=Math.max(0,rejectedCount);
    }
    public float[] weights(int requested,float[] stored){
        return phase==1&&requested==domain&&candidate!=null?candidate:stored;
    }
    public void abortTrial(){
        phase=0;samples=0;candidate=null;
        pathSum=forwardSum=rewardSum=spentSum=gainSum=dirXSum=dirYSum=0;
        externalEffectSum=0;
        noveltySum=contactsSum=0;roomChanged=false;
    }
    public int activeDomain(){return domain;}
    public boolean testingCandidate(){return phase==1;}
    public float movementAverage(){return meanTravel;}
    private float[] mutate(float[] stored,int which,boolean chamber){
        float[] w=stored.clone();
        int count=which==MOTOR?12+random.nextInt(20):3+random.nextInt(8);
        float scale=which==MOTOR?Math.min(1.1f,.34f+.09f*(failures/6)):
                    which==MANIPULATION?.24f:.22f;
        for(int i=0;i<count;i++){
            int k;
            if(which==MOTOR&&random.nextFloat()<.48f)
                k=MotorBrain.INPUT*MotorBrain.HIDDEN+
                  random.nextInt(MotorBrain.PARAMS-MotorBrain.INPUT*MotorBrain.HIDDEN);
            else if(which==MEMBRANE&&chamber&&random.nextFloat()<.58f)
                k=Morphology.LEGACY_PARAMS+random.nextInt(Morphology.GRIP_HEAD);
            else k=random.nextInt(w.length);
            w[k]=Math.max(-3.5f,Math.min(3.5f,
                  w[k]+(float)random.nextGaussian()*scale));
        }
        return w;
    }
    /** Shared physical outcome score, no known goal or object identity. */
    static float evaluate(float path,float net,int novel,int contacts,
                          float energySpent,float energyGained,
                          float forward,float reward){
        return evaluate(path,net,novel,contacts,energySpent,energyGained,forward,reward,0);
    }
    static float evaluate(float path,float net,int novel,int contacts,
                          float energySpent,float energyGained,
                          float forward,float reward,float physicalEffect){
        // Net displacement wins over useless looping. Exploring never-visited
        // cells and obtaining energy/physical contact can ALSO be rewarded,
        // so rotations and stationary manipulation are not prohibited.
        return .006f*Math.min(120,path)
             +.12f*Math.min(150,net)
             +1.8f*Math.min(12,novel)
             +.20f*Math.min(12,contacts)
             +1.4f*Math.min(30,Math.max(0,physicalEffect))
             +.16f*Math.min(60,energyGained)
             -.22f*Math.min(30,energySpent)
             +.02f*Math.max(-40,Math.min(40,forward))
             +.06f*Math.max(-12,Math.min(12,reward))
             +.025f*Math.min(80,net)/Math.max(1,energySpent);
    }
    /**
     * Called only after a genuine sensorimotor action in the live habitat.
     * (startX,startY)->(endX,endY) are actual world coordinates, never targets.
     * New cells come from the observer's visited-grid history.
     */
    public Result observe(float reward,float path,float forward,int contacts,
                          float energyBefore,float energyAfter,int newCells,
                          float startX,float startY,float endX,float endY,
                          boolean room,float dirX,float dirY,
                          float[] motor,float[] hand,float[] shape){
        return observe(reward,path,forward,contacts,energyBefore,energyAfter,newCells,
                       startX,startY,endX,endY,room,dirX,dirY,motor,hand,shape,0);
    }
    public Result observe(float reward,float path,float forward,int contacts,
                          float energyBefore,float energyAfter,int newCells,
                          float startX,float startY,float endX,float endY,
                          boolean room,float dirX,float dirY,
                          float[] motor,float[] hand,float[] shape,float physicalEffect){
        if(!Float.isFinite(reward)||!Float.isFinite(path)||!Float.isFinite(forward)||
           !Float.isFinite(energyBefore)||!Float.isFinite(energyAfter)||
           !Float.isFinite(startX)||!Float.isFinite(startY)||
           !Float.isFinite(endX)||!Float.isFinite(endY)||
           !Float.isFinite(dirX)||!Float.isFinite(dirY)||
           !Float.isFinite(physicalEffect))return null;
        if(samples==0){
            roomAtStart=room;roomChanged=false;
            firstX=startX;firstY=startY;
        }
        if(roomAtStart!=room)roomChanged=true;
        float traveled=Math.max(0,Math.min(50,path));
        meanTravel=.98f*meanTravel+.02f*traveled;
        pathSum+=traveled;
        forwardSum+=Math.max(-15,Math.min(15,forward));
        rewardSum+=Math.max(-3,Math.min(3,reward));
        float energyDelta=energyAfter-energyBefore;
        if(energyDelta<0)spentSum+=-energyDelta;
        else gainSum+=energyDelta;
        noveltySum+=Math.min(6,Math.max(0,newCells));
        contactsSum+=Math.min(6,Math.max(0,contacts));
        externalEffectSum+=Math.min(50,Math.max(0,physicalEffect));
        dirXSum+=dirX;dirYSum+=dirY;
        if(++samples<WINDOW)return null;
        float net=(float)Math.hypot(endX-firstX,endY-firstY);
        float score=evaluate(pathSum,net,noveltySum,contactsSum,
                              spentSum,gainSum,forwardSum,rewardSum,externalEffectSum);
        float energyAfterWindow=energyAfter;
        float avgX=dirXSum/WINDOW,avgY=dirYSum/WINDOW;
        int novel=noveltySum,cont=contactsSum;
        float gain=gainSum,effect=externalEffectSum;
        boolean stable=!roomChanged;
        samples=0;
        pathSum=forwardSum=rewardSum=spentSum=gainSum=dirXSum=dirYSum=0;
        externalEffectSum=0;
        noveltySum=contactsSum=0;
        if(net>5||novel>1)mobileWindows++;
        if(phase==0){
            baseScore=score;baseNet=net;
            baseNovelty=novel;baseContacts=cont;baseGain=gain;
            basePhysicalEffect=effect;
            baseEnergy=energyAfterWindow;baseDirX=avgX;baseDirY=avgY;
            baseRoom=room;phase=1;
            candidate=mutate(domain==MOTOR?motor:domain==MANIPULATION?hand:shape,
                             domain,room);
            return null;
        }
        boolean comparable=stable&&baseRoom==room&&
            Math.abs(baseEnergy-energyAfterWindow)<=30f&&
            Math.hypot(baseDirX-avgX,baseDirY-avgY)<=1.1;
        // Improvement can be exploration OR contact OR energy capture,
        // not movement in one programmed "correct" direction.
        boolean useful=net>baseNet+1.5f ||
                       novel>baseNovelty ||
                       gain>baseGain+3 ||
                       cont>baseContacts+1 ||
                       effect>basePhysicalEffect+2.0f;
        // Preserve locomotor translation while other domains may optimize
        // stationary manipulation; don't accept a motor that slows to zero.
        boolean motorCapable=domain!=MOTOR||net>=Math.max(7f,baseNet*.80f);
        boolean acceptedTrial=comparable&&useful&&motorCapable&&score>baseScore+.30f;
        Result result=new Result(domain,acceptedTrial,
                                 acceptedTrial?candidate:null,baseScore,score);
        lastDomain=domain;lastWasAccepted=acceptedTrial;
        lastBaselineScore=baseScore;lastCandidateScore=score;
        lastBaselineNet=baseNet;lastCandidateNet=net;
        if(acceptedTrial)accepted[domain]++;
        else rejected[domain]++;
        if(domain==MOTOR){
            if(acceptedTrial)failures=0;
            else failures=Math.min(100,failures+1);
        }
        if(domain!=MOTOR||mobileWindows>=4)domain=(domain+1)%3;
        abortTrial();
        return result;
    }

    private static void feed(OnlineBodyLearning b,int count,
                             float reward,float path,float movement,
                             int novelty,int touch,float before,float after,
                             float startX,float endX,float[] m,float[] h,float[] sh){
        for(int i=0;i<count;i++)b.observe(reward,path,movement,touch,
            before,after,novelty,startX,0,endX,0,false,1,0,m,h,sh);
    }
    public static void main(String[] args){
        float[] motor=MotorBrain.virgin(new Random(13));
        float[] hand=ContactLearning.virgin(),shape=new float[Morphology.PARAMS];
        OnlineBodyLearning b=new OnlineBodyLearning();
        if(b.weights(MOTOR,motor)!=motor)throw new AssertionError("Unexpected motor");
        feed(b,WINDOW,0,1,0,0,0,70,70,0,0,motor,hand,shape);
        if(b.weights(MOTOR,motor)==motor)throw new AssertionError("No real mutation");
        OnlineBodyLearning.Result result=null;
        for(int i=0;i<WINDOW;i++)result=b.observe(0,45,0,0,70,70,0,
                    0,0,0,0,false,1,0,motor,hand,shape);
        if(result==null||result.accepted||b.accepted(MOTOR)!=0)
            throw new AssertionError("Circling credited as improvement");
        feed(b,WINDOW,0,1,0,0,0,70,70,0,0,motor,hand,shape);
        for(int i=0;i<WINDOW;i++)result=b.observe(0,15,10,0,70,70,1,
                    0,0,2.1f*(i+1),0,false,1,0,motor,hand,shape);
        if(result==null||!result.accepted||b.accepted(MOTOR)!=1)
            throw new AssertionError("Real net motion+novel area rejected");
        if(b.weights(MOTOR,motor)!=motor)
            throw new AssertionError("Temporary weights not rolled back");
        if(evaluate(200,0,0,0,3,0,0,0)>=
           evaluate(30,30,1,0,3,0,20,0))
            throw new AssertionError("A repeated loop beats efficient exploration");
        if(evaluate(80,0,0,3,4,15,0,0)<=evaluate(80,0,0,0,4,0,0,0))
            throw new AssertionError("Useful local manipulation prohibited");
        b.restoreCounts(MANIPULATION,4,7);
        if(b.accepted(MANIPULATION)!=4||b.rejected(MANIPULATION)!=7||
           b.accepted(MOTOR)!=1)throw new AssertionError("Cross-domain stats");
        System.out.println("ZERO-009.4: reject circling, reward novel net movement or real interaction; independent stats pass");
    }
}
