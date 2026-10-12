package com.overmind.zero;

import java.util.ArrayList;

/** Deterministic structural physics and persistence checks, without oracle. */
public final class ShelterEcologyRegression {
    static void yes(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ShelterEcology ecology=new ShelterEcology();
        ArrayList<UnknownWorld.Entity> field=new ArrayList<>();
        yes(!ecology.storm(59)&&ecology.warning(59)&&ecology.storm(60)&&
            ecology.storm(93)&&!ecology.storm(94)&&
            ecology.wind(70)==1&&ecology.wind(220)==-1,
            "Weather cycle and directional warning incorrect");
        yes(ShelterEcology.exposed(0,0,1,field,null)==1,
            "There should be no phantom shelter in the empty world");
        UnknownWorld.Entity material=new UnknownWorld.Entity(-75,0,2,6);
        field.add(material);
        yes(material.isBox()&&material.isConstruction()&&
            material.physicalLaw()==6&&!material.isRare()&&
            material.touch(0,0)==0f,"Blank construction material gave free energy");
        yes(ShelterEcology.exposed(0,0,1,field,null)<.1f,
            "An opaque material must cast an authentic wind shadow");
        yes(ShelterEcology.exposed(0,0,-1,field,null)==1f,
            "Wind shadow should vanish after physical wind reversal");
        yes(ShelterEcology.exposed(0,55,1,field,null)==1f,
            "A block must not protect an unrelated region");
        material.consumed=true;
        yes(ShelterEcology.exposed(0,0,1,field,null)==1f,
            "Removed blockers must not provide phantom shelter");
        material.consumed=false;
        ShelterEcology bare=new ShelterEcology(),sheltered=new ShelterEcology();
        float bareLoss=0,shieldLoss=0;
        for(int t=0;t<20;t++){
            ShelterEcology.Exposure a=bare.step(body,new Morphology.Surface(body),
                new ArrayList<UnknownWorld.Entity>(),null,70+t,1f);
            ShelterEcology.Exposure b=sheltered.step(body,new Morphology.Surface(body),
                field,null,70+t,1f);
            bareLoss+=a.energyLoss;shieldLoss+=b.energyLoss;
        }
        yes(shieldLoss<bareLoss&&sheltered.integrity>bare.integrity,
            "Real material shelter failed to reduce energetic/structural exposure");
        ShelterEcology.Exposure feel=bare.step(body,skin,
            new ArrayList<UnknownWorld.Entity>(),null,70,.02f);
        yes(feel.active&&feel.energyLoss>0&&skin.touchPulse[0]>.1f,
            "Wind should be experienced through real membrane pressure");
        yes(skin.touchSense[0]==0,"Atmospheric pressure invented solid contact");
        yes(bare.stormEnergyCost>0&&bare.stormSeconds>0&&
            bare.stormDamage>0,"Storm should have measured physical consequences");
        // A touched but UNMOVED box is not an achievement or construction.
        ecology.capture(field);
        yes(ecology.placedCount()==0,"Unmoved spawn counted as a built shelter");
        // A moved box has the same physical original identity and no magic reward.
        material.x+=28;material.vy=3;material.everCollided=true;
        ecology.capture(field);
        yes(ecology.placedCount()==1,"Real moved material not archived");
        String serialized=ecology.encode();
        ShelterEcology resumed=new ShelterEcology();
        yes(resumed.restore(serialized)&&resumed.placedCount()==1,
            "Lost moved shelter after named-slot checkpoint");
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        yes(resumed.rehydrate(empty,0,0,null)==1,
            "Returning ZERO did not find existing built material");
        yes(empty.size()==1&&empty.get(0).x==material.x&&
            empty.get(0).vy==material.vy&&empty.get(0).isConstruction(),
            "Reloaded material altered its physical state or identity");
        yes(resumed.rehydrate(empty,0,0,null)==0,
            "Streaming created duplicate shelter material");
        empty.clear();
        yes(resumed.rehydrate(empty,1000,1000,null)==0,
            "Distant structures must not fill the active simulation");
        float previous=resumed.integrity;
        yes(!resumed.restore(serialized+"|garbage")&&
            resumed.integrity==previous&&resumed.placedCount()==1,
            "Malformed persistence partially overwrote live structures");
        resumed.newLife();
        yes(resumed.placedCount()==1&&resumed.integrity==100,
            "Death erased real constructions or retained old tissue wounds");
        // ZERO 0127: NEVER force the energy cover out of an overlapping
        // plate merely because it is a square. A no-grip collision must
        // produce neither a displacement nor a revealed energy reward.
        UnknownWorld.Entity left=new UnknownWorld.Entity(0,0,2,6);
        UnknownWorld.Entity right=new UnknownWorld.Entity(25,0,2,5);
        ArrayList<UnknownWorld.Entity> pair=new ArrayList<>();
        pair.add(left);pair.add(right);
        yes(ShelterEcology.separateBoxes(pair)==0&&right.x==25f&&
            left.x==0f&&!SquareGripPuzzle.uncovered(right),
            "Passive panel collision moved or rewarded old energy cover");
        left.boxGripHold[0]=Morphology.GRIP_HOLD;
        left.panelHeldNow=true;
        yes(ShelterEcology.separateBoxes(pair)==1&&
            PanelPhysics.separate(pair)==0&&right.x==25f&&
            ShelterEcology.separateBoxes(pair)==0,
            "Only RED-gripped panel may separate without moving fixed cover");
        // Two legacy energy covers also require their OWN confirmed
        // opposing adhesion; touching or an overlapping pair is not enough.
        UnknownWorld.Entity fixed=new UnknownWorld.Entity(0,0,2,5);
        UnknownWorld.Entity moving=new UnknownWorld.Entity(25,0,2,5);
        ArrayList<UnknownWorld.Entity> covers=new ArrayList<>();
        covers.add(fixed);covers.add(moving);
        yes(ShelterEcology.separateBoxes(covers)==0&&
            fixed.x==0f&&moving.x==25f,
            "Legacy crate-crate solver moved an ungripped cover");
        moving.bilateralHeldNow=true;
        yes(ShelterEcology.separateBoxes(covers)==1&&
            fixed.x==0f&&moving.x>25f&&
            ShelterEcology.separateBoxes(covers)==0,
            "Legitimately held crate must separate from an ungripped crate");
        // Standard spawn yields raw construction material in same world.
        ArrayList<UnknownWorld.Entity> spawned=UnknownWorld.newHabitat(1,0,0,false);
        int blank=0;for(UnknownWorld.Entity e:spawned)if(e.isConstruction())blank++;
        yes(blank>0,"No inert material is available for independent building");
        // Only REAL storm energy cost, not placing/moving a cube, should
        // enter the learned action reward. No colored source is credited.
        SensorimotorMind calm=new SensorimotorMind();
        SensorimotorMind windy=new SensorimotorMind();
        float[] sensed=new float[SensorimotorMind.S];
        sensed[0]=.75f;
        calm.resetEpisode(sensed);windy.resetEpisode(sensed);
        calm.observe(sensed,.7f);windy.observe(sensed,.7f);
        windy.ambientDamage(.3f);
        calm.observe(sensed,.7f);windy.observe(sensed,.7f);
        yes(windy.lastReward<calm.lastReward-.06f,
            "Weather damage was not learned as an actual action consequence");
        yes(windy.positiveContactCredits()==0&&windy.negativeContactCredits()==0,
            "Atmospheric damage was falsely attributed to colored objects");
        System.out.println("ZERO-0122 SHELTER PASS: real atmosphere-linked action loss, no synthetic source credit");
        System.out.println("ZERO-0122 SHELTER PASS: active weather, correct opaque direction, "+
            "real tissue pressure, lower damage behind moved solids, "+
            "no free energy, material streaming, save/death persistence, "+
            "one-time SAT solid material collisions and blank procedural supplies");
    }
}