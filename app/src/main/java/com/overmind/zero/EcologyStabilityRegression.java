package com.overmind.zero;

import java.util.ArrayList;

/** Deterministic no-brain-tampering regression: spatial recovery and ecology load. */
public final class EcologyStabilityRegression {
    private static void require(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    private static int count(ArrayList<UnknownWorld.Entity> all,int law){
        int n=0;for(UnknownWorld.Entity e:all)if(e.physicalLaw()==law)n++;return n;
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SpatialPerception map=new SpatialPerception();
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        map.observeEnvironment(body,skin,null,empty);
        String good=map.encode(),bad=good.replaceFirst("sp1","sp0");
        require(SpatialCheckpoint.mayRecover(bad,good),
            "Valid spatial checkpoint could not be recovered from backup");
        require(!SpatialCheckpoint.mayRecover(good,bad),
            "A healthy checkpoint unnecessarily triggered rollback");

        // The first sandbox offers real inert material. Thereafter the game
        // does not introduce a fresh FULL-BODY rigid obstacle every 13s.
        ArrayList<UnknownWorld.Entity> wave1=UnknownWorld.newHabitat(1,0,0,false);
        require(count(wave1,6)==1,"Virgin ZERO lost all building opportunities");
        for(int episode=2;episode<=4;episode++){
            ArrayList<UnknownWorld.Entity> wave=
                UnknownWorld.newHabitat(episode,0,0,false);
            require(count(wave,6)==0,"Procedural construction clutter not bounded");
            require(count(wave,5)==(episode%3!=0?1:0),
                "Reducing material clutter suppressed historic energy covers");
        }
        ArrayList<UnknownWorld.Entity> wave5=UnknownWorld.newHabitat(5,0,0,false);
        require(count(wave5,6)==1&&count(wave5,5)==1,
            "Future genuine construction and finite-energy covers disappeared");

        // Swept pruning skips truly distant rigid objects, but NOT long
        // membrane edges crossing a thin plate or fast crossing motions.
        UnknownWorld.Entity distant=new UnknownWorld.Entity(5000,5000,2,6);
        float[] oldX=skin.x.clone(),oldY=skin.y.clone();
        float[] bodyX=body.x.clone(),bodyY=body.y.clone();
        require(!PanelPhysics.near(body,skin,distant,oldX,oldY,bodyX,bodyY),
            "Far panel failed the fast conservative broad phase");
        PanelPhysics.Contact absent=PanelPhysics.constrain(body,skin,distant,
            .025f,oldX,oldY,bodyX,bodyY);
        require(absent.broadphaseSkipped&&absent.points==0&&
            absent.solidNodes==0&&distant.angle==0,
            "Unreachable plate changed physical tissue or velocity");

        UnknownWorld.Entity crossing=new UnknownWorld.Entity(0,0,2,6);
        for(int i=0;i<Morphology.POINTS;i++){
            skin.x[i]=1000+i;skin.y[i]=500;
        }
        skin.x[0]=-100;skin.x[1]=100;
        skin.y[0]=skin.y[1]=0;
        require(PanelPhysics.near(body,skin,crossing,null,null,null,null),
            "Membrane segment spanning a thin plate was incorrectly culled");
        skin.x[0]=oldX[0];skin.y[0]=oldY[0];
        System.out.println("ZERO 0127 PASS: spatial last-good recovery, "+
            "75% fewer newly spawned panels, same rare energy covers, "+
            "and safe swept-near/long-edge collision retention");
    }
}
