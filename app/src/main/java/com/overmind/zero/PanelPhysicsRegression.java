package com.overmind.zero;

import java.util.ArrayList;
import java.util.Random;

/** ZERO 0124: independent physically falsifiable three-step gameplay QA. */
public final class PanelPhysicsRegression {
    static void must(boolean b,String failure){if(!b)throw new AssertionError(failure);}
    public static void main(String[] args){
        UnknownWorld.Entity panel=new UnknownWorld.Entity(110,75,2,6);
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface tissue=new Morphology.Surface(body);
        tissue.step(body,Morphology.virgin(new Random(9)),0,0,MotorBrain.STEP);
        must(panel.isConstruction()&&panel.isBox()&&!panel.isBilateralBox(),
            "Plate still requires a special bilateral square grip");
        must(PanelPhysics.halfLength(panel)>PanelPhysics.HALF_THICKNESS*3,
            "Construction material is not a plate");
        float oldX=panel.x,oldY=panel.y;
        int i=0;
        tissue.x[i]=110;tissue.y[i]=75-PanelPhysics.HALF_THICKNESS+.2f;
        tissue.vx[i]=0;tissue.vy[i]=51;
        float[] px=tissue.x.clone(),py=tissue.y.clone();
        px[i]=110;py[i]=75-PanelPhysics.HALF_THICKNESS-12;
        float[] bx=body.x.clone(),by=body.y.clone();
        // Ordinary collision WITHOUT commanded adhesion must never
        // move/rotate the plate (even when the muscle pushes hard).
        tissue.gripCommand[i]=0f;
        PanelPhysics.Contact beforeRed=PanelPhysics.constrain(body,tissue,panel,.025f,
                                                               px,py,bx,by);
        must(beforeRed.points>0&&beforeRed.pressureImpulse==0f&&
             panel.vx==0f&&panel.vy==0f&&panel.angularVelocity==0f&&
             panel.boxGripHold[i]==0f,
             "An unlit tissue contact spontaneously moved the plate");
        // Exactly ONE point with a genuine accumulated grip timer is
        // sufficient; no second point/opposing side is supplied.
        tissue.x[i]=110;tissue.y[i]=75-PanelPhysics.HALF_THICKNESS+.2f;
        tissue.vx[i]=0;tissue.vy[i]=51;
        tissue.gripCommand[i]=1f;
        panel.boxGripHold[i]=Morphology.GRIP_HOLD-.020f;
        PanelPhysics.Contact result=PanelPhysics.constrain(body,tissue,panel,.025f,
                                                            px,py,bx,by);
        must(result.points>0&&result.pressureImpulse>.1f&&
             PanelPhysics.held(panel,i,tissue),
             "A single confirmed RED membrane point could not move a plate");
        must(panel.vy>0&&panel.vx==0&&panel.angularVelocity==0f,
            "Centered single-side push did not translate thin panel");
        must(panel.x==oldX&&panel.y==oldY,"Collision teleported a rigid plate");
        panel.tick(.03f);
        must(panel.y>oldY,"Real push did not move the plate");
        // Contact far from the center produces actual, bounded rotation.
        UnknownWorld.Entity off=new UnknownWorld.Entity(110,75,2,6);
        Morphology.Surface far=new Morphology.Surface(body);
        far.step(body,Morphology.virgin(new Random(9)),0,0,MotorBrain.STEP);
        float arm=PanelPhysics.halfLength(off)*.72f;
        far.x[3]=off.x+arm;far.y[3]=off.y-PanelPhysics.HALF_THICKNESS+.3f;
        far.vy[3]=56;
        float[] oldSX=far.x.clone(),oldSY=far.y.clone();
        oldSY[3]-=13;
        far.gripCommand[3]=1f;
        off.boxGripHold[3]=Morphology.GRIP_HOLD-.020f;
        PanelPhysics.Contact offResult=PanelPhysics.constrain(body,far,off,.025f,
                   oldSX,oldSY,body.x.clone(),body.y.clone());
        must(offResult.pressureImpulse>0&&Math.abs(off.angularVelocity)>.001f&&
             PanelPhysics.held(off,3,far),
             "A single off-center RED point cannot rotate the plate");
        float previousAngle=off.angle;off.tick(.035f);
        must(off.angle!=previousAngle,"Plate spin not integrated in live physics");
        // No impact + no muscle demand gives zero propulsion.
        UnknownWorld.Entity untouched=new UnknownWorld.Entity(110,75,2,6);
        Morphology.Surface passive=new Morphology.Surface(body);
        passive.step(body,Morphology.virgin(new Random(9)),0,0,MotorBrain.STEP);
        passive.x[3]=untouched.x;passive.y[3]=untouched.y-PanelPhysics.HALF_THICKNESS+.1f;
        passive.vy[3]=0;
        PanelPhysics.constrain(body,passive,untouched,.025f,
                              passive.x.clone(),passive.y.clone(),
                              body.x.clone(),body.y.clone());
        must(untouched.vx==0&&untouched.vy==0&&untouched.angularVelocity==0,
             "Mere touching a plate fabricated propulsion");
        // Grip release must stop BOTH velocity components and spinning.
        tissue.gripCommand[i]=0f;
        panel.vx=38;panel.vy=-6;panel.angularVelocity=1.2f;
        PanelPhysics.constrain(body,tissue,panel,.025f,px,py,bx,by);
        must(panel.boxGripHold[i]==0&&panel.vx==0&&panel.vy==0&&
             panel.angularVelocity==0,
             "Plate continued drifting or spinning after the last RED point released");
        // A new physics tick cannot resurrect unsanctioned loaded momentum.
        panel.vx=50;panel.angularVelocity=.5f;
        panel.tick(.025f);
        must(panel.vx==0f&&panel.angularVelocity==0f,
             "Saved free-spinning construction plate ignored the red-grip rule");
        // Old energy-cover puzzle must retain its hard bilateral rule.
        UnknownWorld.Entity oldBox=new UnknownWorld.Entity(110,75,2,5);
        must(oldBox.isBilateralBox()&&!oldBox.isConstruction(),
             "Historic hidden-energy cover was converted to a pushable plate");
        must(!SquareGripPuzzle.uncovered(oldBox),
             "Touch or plate physics unlocked hidden energy");
        must(PanelPhysics.constrain(body,tissue,oldBox,.025f,px,py,bx,by).points==0,
             "Construction contact code changed physical energy-cover squares");
        // Actual orientation determines whether a plate shields wind.
        UnknownWorld.Entity wall=new UnknownWorld.Entity(-75,0,2,6);
        wall.angle=0;
        must(PanelPhysics.shadow(0,0,1,wall,165),
             "Horizontal panel cannot protect its downstream footprint");
        must(!PanelPhysics.shadow(0,55,1,wall,165),
             "Panel projected a fake huge rectangle from old square graphics");
        wall.angle=(float)Math.PI/2;
        must(PanelPhysics.shadow(0,40,1,wall,165),
             "Rotating a shield does not rotate its physical wind shadow");
        must(!PanelPhysics.shadow(0,80,1,wall,165),
             "A short sheet shields arbitrarily distant vertical space");
        must(!PanelPhysics.shadow(0,0,-1,wall,165),
             "Panel shields wind from the wrong direction");
        // True SAT: a rotated panel and an old crate cannot overlap.
        ArrayList<UnknownWorld.Entity> pair=new ArrayList<>();
        UnknownWorld.Entity plate=new UnknownWorld.Entity(0,0,2,6);
        UnknownWorld.Entity crate=new UnknownWorld.Entity(16,0,2,5);
        pair.add(plate);pair.add(crate);
        float staticX=plate.x,staticY=plate.y;
        must(PanelPhysics.separate(pair)==1&&PanelPhysics.separate(pair)==0&&
             plate.x==staticX&&plate.y==staticY,
             "Passive box overlap translated an ungripped panel");
        // Oblique panel near rigid square with significant tilt.
        plate.angle=.8f;
        crate.x=2;crate.y=1;
        must(PanelPhysics.separate(pair)==1&&PanelPhysics.separate(pair)==0,
             "SAT failed for rotated construction plate against crate");
        // A REAL elastic skin EDGE must not tunnel through a thin plate
        // while both endpoint particles remain outside its opposite sides.
        UnknownWorld.Entity thin=new UnknownWorld.Entity(110,75,2,6);
        Morphology.Surface crossed=new Morphology.Surface(body);
        crossed.step(body,Morphology.virgin(new Random(11)),0,0,MotorBrain.STEP);
        int edge=20,next=edge+1;
        crossed.x[edge]=thin.x;crossed.y[edge]=thin.y-16f;
        crossed.x[next]=thin.x;crossed.y[next]=thin.y+16f;
        float[] sx=crossed.x.clone(),sy=crossed.y.clone();
        PanelPhysics.constrain(body,crossed,thin,.025f,sx,sy,
                               body.x.clone(),body.y.clone());
        float[] l=PanelPhysics.local(thin,crossed.x[edge],crossed.y[edge]);
        float[] h=PanelPhysics.local(thin,crossed.x[next],crossed.y[next]);
        must(!ScarcityLab.crossesInterior(l[0],l[1],h[0],h[1],
                 -PanelPhysics.halfLength(thin),-PanelPhysics.HALF_THICKNESS,
                  PanelPhysics.halfLength(thin),PanelPhysics.HALF_THICKNESS),
             "Skin membrane edge still tunnels through the thin construction plate");
        // A save with orientation and spin must survive normal persistence.
        ShelterEcology archive=new ShelterEcology();
        wall.x+=30f;wall.angularVelocity=.42f;
        ArrayList<UnknownWorld.Entity> items=new ArrayList<>();
        items.add(wall);
        archive.capture(items);
        String data=archive.encode();
        ShelterEcology restored=new ShelterEcology();
        must(restored.restore(data)&&restored.placedCount()==1,
             "Rotating panel not archived");
        items.clear();restored.rehydrate(items,wall.x,wall.y,null);
        must(items.size()==1&&Math.abs(items.get(0).angle-wall.angle)<1e-5f&&
             Math.abs(items.get(0).angularVelocity-wall.angularVelocity)<1e-5f,
             "Saved panel attitude/torque lost on area re-entry");
        must(!restored.restore(data+"|broken")&&restored.placedCount()==1,
             "Malformed orientation overwrote saved materials");
        // The previous OTA 93 se1 archive migrates without discarding locations.
        String oldFormat="se1|100|0|0|0|0|1|-75,0,-45,0,0,0,30,1";
        ShelterEcology legacy=new ShelterEcology();
        must(legacy.restore(oldFormat)&&legacy.placedCount()==1,
             "Legacy square construction profile cannot become a panel");
        ArrayList<UnknownWorld.Entity> oldArr=new ArrayList<>();
        legacy.rehydrate(oldArr,-45,0,null);
        must(oldArr.size()==1&&oldArr.get(0).angle==0f,
             "Old saved construction location was discarded on migration");
        // Blank materials never grant energy or discovery simply by moving.
        must(panel.touch(1,0)==0f&&panel.physicalLaw()==6,
             "One-sided sheet grants synthetic energy");
        System.out.println("ZERO 0126 PASS: only one RED adhesive hold moves plates, off-center spin, "+
            "no free drift, bilateral-cover isolation, orientation-correct "+
            "shadows, rotated SAT solids, angle/velocity persistence and OTA93 migration");
    }
}
