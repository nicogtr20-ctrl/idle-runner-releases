package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * NON-PRODUCTION benchmark: no learned gait, no actuation/physics changes.
 * Separates tissue capability from the virgin brain's locomotor competence.
 * Summarizes centre net travel, meandering, deformation, action diversity.
 */
public final class ZeroLocomotionCapacityDiagnostic {
    static final float DT=.025f;
    static final int STEPS=1600; // 40 simulated seconds
    static final ArrayList<UnknownWorld.Entity> EMPTY=new ArrayList<>();
    static final class Stats {
        float net,path,shape,areaErr;
        int decisions,distinctActions,bodyInvalid,topology;
        String name;
        @Override public String toString(){
            return String.format(Locale.US,
              "%-18s net=%8.3f path=%9.3f efficiency=%6.3f"+
              " deform=%7.3f areaErr=%7.4f actions=%3d diversity=%2d invalid=%3d folds=%3d",
              name,net,path,net/Math.max(.00001f,path),shape,areaErr,
              decisions,distinctActions,bodyInvalid,topology);
        }
    }
    private static Stats trial(String mode){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,skin);
        RecurrentCoreV22 primary=new RecurrentCoreV22();
        SymbioteMotorHeadV1 head=new SymbioteMotorHeadV1();
        DistributedBodySense distributed=new DistributedBodySense();
        FluidInteroceptionV1 inside=new FluidInteroceptionV1();
        float[] cortex=new float[64];
        float originX=b.cx,originY=b.cy,previousX=b.cx,previousY=b.cy;
        float reference=0;
        for(int i=0;i<Morphology.POINTS;i++)
            reference+=Math.hypot(skin.x[i]-b.cx,skin.y[i]-b.cy);
        reference/=Morphology.POINTS;
        Stats stat=new Stats();stat.name=mode;
        boolean[] chosen=new boolean[16];
        Random random=new Random(17039);
        for(int tick=0;tick<STEPS;tick++){
            if(tick%26==0){
                if(mode.equals("virgin-core")||mode.equals("virgin-explore")||
                    mode.equals("random-actions")){
                    distributed.feed(DistributedBodySense.sense(b,skin,null,EMPTY));
                    inside.feed(FluidInteroceptionV1.sense(b,skin,gel),b,gel);
                    float[] senses=SensorimotorMind.Senses.read(b,skin,null,EMPTY,75f);
                    primary.sense(senses,distributed.localMap(),null,null);
                    float[] scores=primary.primaryScores(new float[16]);
                    int action=0;
                    for(int a=1;a<16;a++)
                        if(scores[a]>scores[action])action=a;
                    if(mode.equals("random-actions")||
                       mode.equals("virgin-explore")&&random.nextFloat()<.25f)
                        action=random.nextInt(16);
                    float explore=mode.equals("virgin-core")?0:.30f;
                    head.chosen(action,distributed.localMap(),primary.state(),
                                explore,inside.summary());
                    System.arraycopy(head.commands(),0,cortex,0,64);
                    primary.chosen(action);
                    chosen[action]=true;
                    stat.decisions++;
                }
            }
            if(mode.equals("lab-cycle")){
                boolean phase=(tick/20)%2==0;
                for(int k=0;k<16;k++){
                    boolean forward=Math.cos((k+.5)*Math.PI/8)>0;
                    cortex[4*k]=forward?(phase?.78f:-.78f)
                                        :(phase?-.43f:.43f);
                    cortex[4*k+1]=0f;
                    cortex[4*k+2]=0f;
                    cortex[4*k+3]=0f;
                }
            }
            gel.step(b,skin,cortex,DT);
            stat.path+=(float)Math.hypot(b.cx-previousX,b.cy-previousY);
            previousX=b.cx;previousY=b.cy;
            if(tick%26==0){
                stat.topology+=MaterialGeometry.selfCrossings(skin);
                if(gel.invalid!=0)stat.bodyInvalid++;
            }
            stat.areaErr=Math.max(stat.areaErr,gel.areaRelativeError(skin));
        }
        stat.net=(float)Math.hypot(b.cx-originX,b.cy-originY);
        for(boolean flag:chosen)if(flag)stat.distinctActions++;
        for(int i=0;i<Morphology.POINTS;i++){
            float stretch=(float)Math.hypot(skin.x[i]-b.cx,skin.y[i]-b.cy);
            stat.shape+=Math.abs(stretch-reference)/Morphology.POINTS;
        }
        return stat;
    }
    public static void main(String[] args){
        System.out.println("ZERO152 DIAGNOSTIC – NO PHYSICS MODIFICATION OR GAMEPLAY REWARD");
        for(String name:new String[]{"passive","virgin-core","virgin-explore",
                                     "random-actions","lab-cycle"}){
            System.out.println(trial(name));
        }
        System.out.println("Interpretation: deform>0 with net/path near zero = undulation; "+
          "lab-cycle shows only physical POSSIBILITY, not learned policy. "+
          "All distances in internal world units; body radius about 27.");
    }
}
