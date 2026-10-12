package com.overmind.zero;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

/**
 * PHYSICAL COUNTERFACTUAL REPLAY, NOT A FULL LIFE SIMULATION.
 * Loads independent copies of real physical snapshots and real trained motor,
 * hand and membrane weights; varies only the checked-out release code and the
 * explicitly reported counterfactual controls. No writes to user saves.
 *
 * Directions are an identical, predeclared motor stimulus grid in every build:
 * this tests grasp/physics, NOT learned food-seeking or learned decisions.
 * Never describe this test as a run of the full SensorimotorMind.
 */
public final class ZeroPhysicsReplay {
    static final float DT=MotorBrain.STEP;
    static final int STEPS=440;
    static final float[] OFFSETS={-1.15f,-.35f,.35f,1.15f};
    static final float[] DRIVES={0f,.65f};
    static final String[] SCENES={"mid","latest"};

    static float[] parse(Properties p,String key){
        String raw=p.getProperty(key);
        if(raw==null)throw new IllegalArgumentException("Missing fixture "+key);
        String[] ss=raw.split(",");
        float[] out=new float[ss.length];
        for(int i=0;i<ss.length;i++)out[i]=Float.parseFloat(ss[i]);
        return out;
    }
    static float number(Properties p,String key){return Float.parseFloat(p.getProperty(key));}
    static int count(Properties p,String key){return Integer.parseInt(p.getProperty(key));}
    static void copy(float[] to,float[] from,String name){
        if(to.length!=from.length)throw new AssertionError(name+" length "+from.length+" expected "+to.length);
        System.arraycopy(from,0,to,0,to.length);
    }
    static void optional(UnknownWorld.Entity box,String name,float value){
        try{
            Field f=UnknownWorld.Entity.class.getField(name);
            f.setFloat(box,value);
        }catch(NoSuchFieldException ignored){
            // Deliberate OTA93/94 compatibility: rigid plates were squares,
            // and did not have an orientation field.
        }catch(ReflectiveOperationException error){throw new RuntimeException(error);}
    }
    static Method panelSolver(){
        try{
            return Class.forName("com.overmind.zero.PanelPhysics").getMethod(
                "constrain",MotorBrain.Body.class,Morphology.Surface.class,
                UnknownWorld.Entity.class,float.class,float[].class,float[].class,
                float[].class,float[].class);
        }catch(ClassNotFoundException|NoSuchMethodException ignored){
            return null; // OTA93/94: construction material was a bilateral square.
        }
    }
    static class Measurements{
        String scene,environment,tag;int angleIndex;
        float drive,travel,boxMovement,maxBoxChange,panelMovement;
        int contactFrames,bilateralFrames,activeGripFrames,uncovered,
            oneFaceFrames,activeFrames,invalidFrames;
        String json(){
            return String.format(Locale.US,
                "{\"tag\":\"%s\",\"scene\":\"%s\",\"environment\":\"%s\",\"angle\":%d,\"gripDrive\":%.2f,"+
                "\"frames\":%d,\"contactFrames\":%d,\"bilateralFrames\":%d,"+
                "\"singleFaceFrames\":%d,\"gripFrames\":%d,\"uncovered\":%d,"+
                "\"boxMotion\":%.4f,\"maxBoxMotion\":%.4f,\"panelMotion\":%.4f,"+
                "\"travel\":%.4f,\"invalidFrames\":%d}",
                tag,scene,environment,angleIndex,drive,activeFrames,contactFrames,
                bilateralFrames,oneFaceFrames,activeGripFrames,uncovered,
                boxMovement,maxBoxChange,panelMovement,travel,invalidFrames);
        }
    }
    static Measurements run(Properties p,String name,boolean withPanels,
                            int angleIndex,float drive,String tag,Method panelMethod){
        String prefix="scene."+name+".";
        MotorBrain.Body body=new MotorBrain.Body();
        copy(body.x,parse(p,prefix+"body.x"),"body.x");
        copy(body.y,parse(p,prefix+"body.y"),"body.y");
        copy(body.vx,parse(p,prefix+"body.vx"),"body.vx");
        copy(body.vy,parse(p,prefix+"body.vy"),"body.vy");
        body.time=number(p,prefix+"body.time");
        MaterialGeometry.observe(body);
        Morphology.Surface skin=new Morphology.Surface(body);
        copy(skin.x,parse(p,prefix+"surface.x"),"surface.x");
        copy(skin.y,parse(p,prefix+"surface.y"),"surface.y");
        copy(skin.vx,parse(p,prefix+"surface.vx"),"surface.vx");
        copy(skin.vy,parse(p,prefix+"surface.vy"),"surface.vy");
        copy(skin.extension,parse(p,prefix+"surface.extension"),"surface.extension");
        copy(skin.gripCommand,parse(p,prefix+"surface.command"),"surface.command");
        copy(skin.gripHold,parse(p,prefix+"surface.hold"),"surface.hold");
        copy(skin.touchSense,parse(p,prefix+"surface.sense"),"surface.sense");
        skin.t=number(p,prefix+"surface.t");
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        int objCount=count(p,prefix+"objects");
        for(int i=0;i<objCount;i++){
            String k=prefix+"object."+i+".";
            int law=count(p,k+"law");
            if(law!=5&&law!=6)continue;
            if(law==6&&!withPanels)continue;
            UnknownWorld.Entity box=new UnknownWorld.Entity(
                number(p,k+"sx"),number(p,k+"sy"),2,law);
            box.x=number(p,k+"x");box.y=number(p,k+"y");
            box.vx=number(p,k+"vx");box.vy=number(p,k+"vy");
            box.peakDisplacement=number(p,k+"peak");
            box.everCollided=count(p,k+"collided")!=0;
            box.countedAsMoved=count(p,k+"counted")!=0;
            optional(box,"angle",number(p,k+"angle"));
            optional(box,"angularVelocity",number(p,k+"spin"));
            copy(box.boxGripHold,parse(p,k+"holds"),"holds");
            objects.add(box);
            if(law==5){
                // Exactly the physical hidden-energy source belonging to the
                // cover, not an artificially invented test reward.
                UnknownWorld.Entity hidden=new UnknownWorld.Entity(
                    box.spawnX,box.spawnY,0,4);
                hidden.concealed=true;
                objects.add(hidden);
            }
        }
        float[] motor=parse(p,"model.motor");
        float[] hand=parse(p,"model.hand");
        float[] shape=Morphology.migrate(parse(p,"model.morph"));
        if(motor.length!=MotorBrain.PARAMS)
            throw new AssertionError("Motor profile mismatch");
        float heading=number(p,prefix+"heading")+OFFSETS[angleIndex];
        float dx=(float)Math.cos(heading),dy=(float)Math.sin(heading);
        float initialX=body.cx,initialY=body.cy;
        ArrayList<UnknownWorld.Entity> covers=new ArrayList<>();
        ArrayList<UnknownWorld.Entity> plates=new ArrayList<>();
        for(UnknownWorld.Entity e:objects){
            if(e.physicalLaw()==5)covers.add(e);
            if(e.physicalLaw()==6)plates.add(e);
        }
        float[] original=new float[covers.size()];
        for(int i=0;i<covers.size();i++)original[i]=covers.get(i).displacementFromSpawn();
        float[] panelsOriginal=new float[plates.size()];
        for(int i=0;i<plates.size();i++)panelsOriginal[i]=plates.get(i).displacementFromSpawn();
        Measurements m=new Measurements();
        m.scene=name;m.environment=withPanels?"FULL":"COVERS";
        m.angleIndex=angleIndex;m.drive=drive;m.tag=tag;
        for(int frame=0;frame<STEPS;frame++){
            float[] px=skin.x.clone(),py=skin.y.clone();
            float[] bx=body.x.clone(),by=body.y.clone();
            float oldX=body.cx,oldY=body.cy;
            float tx=body.cx+dx*145f,ty=body.cy+dy*145f;
            body.step(motor,dx,dy,DT,hand,tx,ty);
            skin.step(body,shape,tx,ty,DT,drive);
            // Same ordering as real GameActivity: bilateral covers,
            // construction materials, actual cohesion/tactile composition,
            // object/object collision resolution, then integration.
            boolean hasBoxes=!covers.isEmpty()||!plates.isEmpty();
            if(hasBoxes)java.util.Arrays.fill(skin.touchSense,0);
            int heldThisFrame=0,contactsThisFrame=0,opposites=0;
            for(UnknownWorld.Entity b:covers){
                SquareGripPuzzle.Outcome out=SquareGripPuzzle.constrain(
                    body,skin,b,DT,px,py,bx,by,oldX,oldY);
                heldThisFrame+=out.held;
                contactsThisFrame+=out.contacts;
                if(out.opposingGrip)opposites++;
                if(SquareGripPuzzle.uncover(b,objects))m.uncovered++;
            }
            for(UnknownWorld.Entity plate:plates){
                if(panelMethod==null){
                    // OTA93/94 treated construction material as boxes,
                    // with the same historical bilateral-grip solver.
                    SquareGripPuzzle.Outcome out=SquareGripPuzzle.constrain(
                        body,skin,plate,DT,px,py,bx,by,oldX,oldY);
                    heldThisFrame+=out.held;
                    contactsThisFrame+=out.contacts;
                }else{
                    try{
                        panelMethod.invoke(null,body,skin,plate,DT,px,py,bx,by);
                    }catch(ReflectiveOperationException e){
                        throw new IllegalStateException("Real plate solver failure",e);
                    }
                }
            }
            MaterialGeometry.observe(body);
            GripSurfaceComposition.compose(skin,null,objects);
            ShelterEcology.separateBoxes(objects);
            for(UnknownWorld.Entity o:objects)o.tick(DT);
            m.activeFrames++;
            if(contactsThisFrame>0)m.contactFrames++;
            if(heldThisFrame>0)m.activeGripFrames++;
            if(opposites>0)m.bilateralFrames++;
            if(heldThisFrame>0&&opposites==0)m.oneFaceFrames++;
            if(!Float.isFinite(body.cx)||!Float.isFinite(body.cy)||
                !Float.isFinite(skin.x[0])){m.invalidFrames++;break;}
        }
        m.travel=(float)Math.hypot(body.cx-initialX,body.cy-initialY);
        for(int i=0;i<covers.size();i++){
            float delta=covers.get(i).displacementFromSpawn()-original[i];
            m.boxMovement+=Math.max(0,delta);
            m.maxBoxChange=Math.max(m.maxBoxChange,delta);
        }
        for(int i=0;i<plates.size();i++)
            m.panelMovement+=Math.max(0,plates.get(i).displacementFromSpawn()-panelsOriginal[i]);
        return m;
    }
    public static void main(String[] args)throws Exception{
        if(args.length!=2)throw new IllegalArgumentException("Fixture path and OTA tag required");
        Properties p=new Properties();
        try(FileInputStream in=new FileInputStream(args[0])){p.load(in);}
        Method panel=panelSolver();
        String tag=args[1];
        int n=0;
        for(String scene:SCENES)for(boolean panels:new boolean[]{false,true})
            for(int d=0;d<OFFSETS.length;d++)for(float grip:DRIVES){
                Measurements m=run(p,scene,panels,d,grip,tag,panel);
                System.out.println("ZERO128 "+m.json());
                if(m.invalidFrames>0)throw new AssertionError("Nonfinite physics: "+m.json());
                n++;
            }
        if(n!=32)throw new AssertionError("Incomplete matched replay");
        System.out.println("ZERO128 FINISHED "+tag+" "+n+" matched physical cases");
    }
}
