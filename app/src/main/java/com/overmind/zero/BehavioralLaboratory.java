package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/**
 * ZERO 0.11.3: independent, reproducible PHYSICAL behavioral experiments.
 *
 * This harness uses the SAME Body/Surface/ScarcityLab/real-contact sensor
 * and chosen SensorimotorMind actions as the main game. It does not move
 * ZERO on rails, teleport it toward targets, award synthetic energy, or
 * teach labels/goal coordinates to any learner.
 *
 * Coordinates of targets are known ONLY to the external examiner for
 * measurement. The paired A/B run starts from IDENTICAL cloned brain
 * weights, learned histories, body, scenario and independent random seed.
 * Control A disables ONLY instrumental navigation's choice bias; sensory
 * recording, outcomes, other networks and learning remain active.
 *
 * It intentionally never reads/writes Android preferences or real games.
 * Caller supplies read-only snapshot, benchmark report is a separate result.
 */
public final class BehavioralLaboratory {
    public static final int ENERGY=0,HARM=1,GATE=2,CASES=3,SEEDS=3;
    public static final float DT=.035f,SECONDS=32f;
    public static final int FRAMES=(int)(SECONDS/DT);
    public static final int[] VARIANTS={7,19,31};
    private static final String[] NAMES={"ENERGIA","PERICOLO","CANCELLO"};
    private BehavioralLaboratory(){}
    public interface Progress { void step(int finished,int total,String label); }
    public interface Cancel { boolean stopped(); }

    public static final class Profile {
        public float[] motor=MotorBrain.virgin(new Random(918273));
        public float[] hands=ContactLearning.virgin();
        public float[] membrane=Morphology.virgin(new Random(14790));
        public String world="",navigation="",outcome="",diagnostics="",planning="",
            memory="",temporal="",skills="",body="",spatial="",causal="";
        public Profile copy(){
            Profile r=new Profile();
            r.motor=motor.clone();r.hands=hands.clone();r.membrane=membrane.clone();
            r.world=world;r.navigation=navigation;r.outcome=outcome;r.planning=planning;
            r.diagnostics=diagnostics;r.memory=memory;r.temporal=temporal;
            r.skills=skills;r.body=body;r.spatial=spatial;r.causal=causal;
            return r;
        }
    }

    public static final class Trial {
        public final int scenario,seed;
        public final boolean instrumental;
        public int energeticContacts,damagingContacts,gatePassages,gateOpenings;
        public float gain,damage,firstContact=Float.NaN,bestDistance,
            endDistance,traveled,gateTravel,endingReserve;
        public long actualDecisions,learnedNavigationChoices;
        Trial(int scene,int trialSeed,boolean enabled){
            scenario=scene;seed=trialSeed;instrumental=enabled;
        }
        public String describe(){
            return String.format(Locale.US,
                "%s #%d %s: +%.1f / -%.1f energia, tocchi +%d -%d, "+
                "vicinanza min %.1f, tempo contatto %s, "+
                "cancello %.1f U, passaggi %d, decisioni %d, nav %d",
                NAMES[scenario],seed,instrumental?"ATTIVA":"CONTROLLO",
                gain,damage,energeticContacts,damagingContacts,bestDistance,
                Float.isNaN(firstContact)?"mai":String.format(Locale.US,"%.1f s",firstContact),
                gateTravel,gatePassages,actualDecisions,learnedNavigationChoices);
        }
    }

    public static final class Result {
        public final Trial[][][] trials=new Trial[CASES][SEEDS][2];
        public int finished=0;
        public int total(){return CASES*SEEDS*2;}
        public String report(){
            StringBuilder b=new StringBuilder(
                "LABORATORIO ZERO — A/B FISICO, SENZA SCRIPT\n"+
                "Tre scenari, tre varianti ciascuno, 32 secondi SIMULATI per prova.\n"+
                "Stesso cervello iniziale e stessa scena per ogni coppia; "+
                "varia solo l'influenza della navigazione strumentale.\n"+
                "Le coordinate degli oggetti sono usate SOLO per misurare i risultati.\n\n");
            for(int s=0;s<CASES;s++){
                b.append(NAMES[s]).append("\n");
                double[] sums=new double[10];
                int valid=0,navMore=0,controlMore=0,equal=0;
                for(int j=0;j<SEEDS;j++){
                    Trial ctrl=trials[s][j][0],on=trials[s][j][1];
                    if(ctrl==null||on==null)continue;
                    valid++;
                    double a=quality(ctrl),v=quality(on);
                    if(v>a+.0001)navMore++;else if(a>v+.0001)controlMore++;else equal++;
                    sums[0]+=on.gain-ctrl.gain;
                    sums[1]+=on.damage-ctrl.damage;
                    sums[2]+=on.bestDistance-ctrl.bestDistance;
                    sums[3]+=on.gateTravel-ctrl.gateTravel;
                    sums[4]+=on.gatePassages-ctrl.gatePassages;
                    sums[5]+=on.energeticContacts-ctrl.energeticContacts;
                    sums[6]+=on.damagingContacts-ctrl.damagingContacts;
                    sums[7]+=on.actualDecisions;
                    sums[8]+=on.learnedNavigationChoices;
                    sums[9]+=ctrl.learnedNavigationChoices;
                }
                if(valid==0){b.append("Nessun esperimento completato.\n\n");continue;}
                b.append(String.format(Locale.US,
                    "A/B %d coppie — migliore ATTIVA %d, CONTROLLO %d, PARITA' %d\n",
                    valid,navMore,controlMore,equal));
                if(s==ENERGY)b.append(String.format(Locale.US,
                    "Differenze medie ATTIVA − CONTROLLO: energia +%.1f, "+
                    "contatti benefici %+.2f, distanza minima %+.1f U (minore è meglio).\n",
                    sums[0]/valid,sums[5]/valid,sums[2]/valid));
                if(s==HARM)b.append(String.format(Locale.US,
                    "Differenze medie ATTIVA − CONTROLLO: danno %+.1f, "+
                    "contatti nocivi %+.2f, distanza minima %+.1f U.\n",
                    sums[1]/valid,sums[6]/valid,sums[2]/valid));
                if(s==GATE)b.append(String.format(Locale.US,
                    "Differenze medie ATTIVA − CONTROLLO: movimento vero cancello "+
                    "%+.1f U, attraversamenti %+.2f, energia ritrovata %+.1f.\n",
                    sums[3]/valid,sums[4]/valid,sums[0]/valid));
                b.append(String.format(Locale.US,
                    "Scelte influenzate (ATTIVA / CONTROLLO): %.1f / %.1f; "+
                    "decisioni attive %.1f per prova.\n",
                    sums[8]/valid,sums[9]/valid,sums[7]/valid));
                if(sums[8]<1)b.append(
                    "NOTA: modello strumentale probabilmente non ancora pronto "+
                    "o stimoli non rilevanti. Parità non dimostra inefficacia.\n");
                for(int j=0;j<SEEDS;j++){
                    Trial a=trials[s][j][0],c=trials[s][j][1];
                    if(a!=null)b.append("  ").append(a.describe()).append('\n');
                    if(c!=null)b.append("  ").append(c.describe()).append('\n');
                }
                b.append('\n');
            }
            b.append("VERDETTO: singole vittorie non sono prova di "+
                "apprendimento causale. Tre coppie sono solo uno screening; "+
                "se i risultati sono inconcludenti servono più prove "+
                "e più esperienze reali. Nessun risultato del laboratorio "+
                "modifica i salvataggi, il corpo o la memoria del ZERO reale.");
            return b.toString();
        }
    }

    private static double quality(Trial x){
        if(x.scenario==ENERGY)
            return x.gain/22.0+x.energeticContacts*.75-
                x.bestDistance/160.0;
        if(x.scenario==HARM)
            return -x.damage/7.0-x.damagingContacts*.50+
                Math.min(1,x.bestDistance/160.0)*.15;
        return x.gatePassages*5+x.gain/22+
            x.gateTravel/175-x.endDistance/650.0;
    }
    private static final class Rig {
        final SensorimotorMind brain;
        final EpisodicMemory episodes;
        final MotorAutonomyGuard guard;
        Rig(SensorimotorMind mind,EpisodicMemory events,MotorAutonomyGuard protection){
            brain=mind;episodes=events;guard=protection;
        }
    }
    private static Rig restore(Profile p,boolean instrument){
        SensorimotorMind brain=new SensorimotorMind();
        brain.restore(p.world);
        brain.restoreOutcomeModel(p.outcome);
        brain.diagnostics.restore(p.diagnostics);
        brain.restorePlanningStats(p.planning);
        brain.setNavigationAdviceEnabled(instrument);
        EpisodicMemory events=new EpisodicMemory();
        events.restore(p.memory);
        brain.attachMemory(events);
        TemporalWorkingMemory temp=new TemporalWorkingMemory();
        temp.restore(p.temporal);brain.attachWorkingMemory(temp);
        TemporalSkillBank skills=new TemporalSkillBank();
        skills.restore(p.skills);brain.attachTemporalSkills(skills);
        BodyProprioception body=new BodyProprioception();
        body.restore(p.body);brain.attachBodySchema(body);
        SpatialPerception map=new SpatialPerception();
        map.restore(p.spatial);brain.attachSpatialPerception(map);
        CausalExperiments causal=new CausalExperiments();
        causal.restore(p.causal);brain.attachCausalExperiments(causal);
        InstrumentalNavigation nav=new InstrumentalNavigation();
        nav.restore(p.navigation);brain.attachNavigation(nav);
        MotorAutonomyGuard guard=new MotorAutonomyGuard();
        brain.attachMotorGuard(guard);
        brain.setLearning(true);
        return new Rig(brain,events,guard);
    }

    /** Real body/skin solver and ray observations, performed on a clone of
     * one named person. No fabricated progress or simulated sensor changes. */
    public static Trial simulate(Profile input,int caseId,int seed,
                                 boolean active,Cancel cancel){
        if(input==null||caseId<0||caseId>=CASES)
            throw new IllegalArgumentException("scenario/profile");
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface surface=new Morphology.Surface(body);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        ScarcityLab room=null;
        float angle=seed==7?0f:seed==19?(float)(Math.PI/5f):
                                        (float)(-Math.PI/5f);
        if(caseId==GATE){
            room=new ScarcityLab(body.cx,body.cy);
            objects.add(room.food);
        }else{
            float distance=135f;
            float x=(float)Math.cos(angle)*distance;
            float y=(float)Math.sin(angle)*distance;
            objects.add(new UnknownWorld.Entity(x,y,caseId==ENERGY?0:1,
                                                caseId==ENERGY?0:1));
        }
        // Variation is in the PHYSICAL initial condition, equal for both
        // policies in a pair, never in a scripted action or target heading.
        body.cvx=(seed-19)*.11f;body.cvy=(seed==31?.8f:0f);
        Rig rig=restore(input,active);
        SensorimotorMind brain=rig.brain;
        rig.guard.resetLife(body.cx,body.cy);
        float reserve=55f;
        brain.begin(SensorimotorMind.Senses.read(body,surface,room,objects,reserve));
        brain.updateBodySense(body,surface);
        brain.updateSpatialSense(body,room,objects);
        brain.updateNavigationSense(body,room,objects);
        Trial r=new Trial(caseId,seed,active);
        UnknownWorld.Entity stimulus=caseId==GATE?room.food:objects.get(0);
        r.bestDistance=(float)Math.hypot(stimulus.x-body.cx,stimulus.y-body.cy);
        float prevX=body.cx,prevY=body.cy,effect=0f;
        float[] energeticEffects=new float[UnknownWorld.SIGNATURES];
        boolean entered=false,opened=false;
        long initialDecisions=brain.decisions,initialChoices=brain.navigation().choicesInfluenced;
        for(int i=0;i<FRAMES;i++){
            if(cancel!=null&&cancel.stopped())return null;
            float t=i*DT;
            reserve=Math.max(0,reserve-.14f*DT);
            for(UnknownWorld.Entity object:objects)object.tick(DT);
            float mx=brain.motorX(),my=brain.motorY();
            float px=body.cx+mx*145f,py=body.cy+my*145f;
            float effectAtFrameStart=effect;
            body.step(input.motor,mx,my,DT,input.hands,px,py);
            surface.step(body,input.membrane,px,py,DT,brain.adhesion());
            if(room!=null){
                float before=room.doorY;
                room.constrain(body,surface,DT,brain.adhesion());
                effect+=Math.min(2f,Math.abs(room.doorY-before));
                r.gateTravel=Math.max(r.gateTravel,room.maxDoorTravel);
                if(room.openingFree()&&!opened){opened=true;r.gateOpenings++;}
                if(room.openingFree()&&body.cx>room.entranceX()+12&&
                   Math.abs(body.cy-room.oy)<ScarcityLab.OPEN_HALF_HEIGHT&&!entered){
                    entered=true;r.gatePassages++;
                }
                if(!room.reachedEnergy&&room.foodTouchedByCore(body)){
                    float prior=reserve;
                    reserve=Math.min(100,reserve+UnknownWorld.RARE_ENERGY);
                    float gain=reserve-prior;
                    if(gain>.01f){
                        brain.contactOutcome(gain,room.food.signature);
                        energeticEffects[room.food.signature]+=gain;
                        r.gain+=gain;r.energeticContacts++;
                        if(Float.isNaN(r.firstContact))r.firstContact=t;
                    }
                }
            }
            for(UnknownWorld.Entity object:objects){
                if(room!=null&&object==room.food)continue;
                if(object.isMovable()){
                    effect+=Morphology.collide(surface,object,DT)+
                            ContactLearning.collide(body,object,DT);
                }else if(object.ready()){
                    float nearest=(float)Math.hypot(object.x-body.cx,object.y-body.cy);
                    for(int j=0;j<MotorBrain.N;j++)
                        nearest=Math.min(nearest,(float)Math.hypot(object.x-body.x[j],
                                                                   object.y-body.y[j]));
                    for(int j=0;j<Morphology.POINTS;j++)
                        nearest=Math.min(nearest,(float)Math.hypot(object.x-surface.x[j],
                                                                   object.y-surface.y[j]));
                    if(nearest<18+object.radius){
                        float dx=object.x-body.cx,dy=object.y-body.cy;
                        float norm=Math.max(1,(float)Math.hypot(dx,dy));
                        float before=reserve;
                        reserve=Math.max(0,Math.min(100,
                            reserve+object.touch(dx/norm,dy/norm)));
                        float delta=reserve-before;
                        if(Math.abs(delta)>.01f){
                            brain.contactOutcome(delta,object.signature);
                            energeticEffects[object.signature]+=delta;
                            if(delta>0){r.gain+=delta;r.energeticContacts++;}
                            else{r.damage-=delta;r.damagingContacts++;}
                            if(Float.isNaN(r.firstContact))r.firstContact=t;
                        }
                    }
                }
            }
            float shift=(float)Math.hypot(body.cx-prevX,body.cy-prevY);
            if(Float.isFinite(shift)&&shift<200)r.traveled+=shift;
            prevX=body.cx;prevY=body.cy;
            rig.guard.observe(DT,body.cx,body.cy,
                Math.max(0f,effect-effectAtFrameStart));
            r.bestDistance=Math.min(r.bestDistance,
                (float)Math.hypot(stimulus.x-body.cx,stimulus.y-body.cy));
            long priorExperience=brain.experiences;
            int priorAction=brain.action;
            float[] priorSenses=brain.stateCopy();
            boolean actedFromMemory=rig.episodes.physicallyReplaying();
            brain.observe(SensorimotorMind.Senses.read(body,surface,room,objects,reserve),
                DT,effect,body,surface,room,objects);
            // Mirror the live journaling path at the same decision boundary.
            if(brain.experiences>priorExperience){
                rig.episodes.action(priorSenses,priorAction);
                rig.episodes.consequenceSeparated(effect,energeticEffects,
                    input.motor,input.hands,input.membrane,actedFromMemory);
                Arrays.fill(energeticEffects,0f);
                effect=0;
            }
            if(reserve<=0)break;
        }
        r.endDistance=(float)Math.hypot(stimulus.x-body.cx,stimulus.y-body.cy);
        r.endingReserve=reserve;
        r.actualDecisions=brain.decisions-initialDecisions;
        r.learnedNavigationChoices=brain.navigation().choicesInfluenced-initialChoices;
        return r;
    }
    public static Result run(Profile p,Progress progress,Cancel stop){
        if(p==null)throw new IllegalArgumentException("profile");
        Result result=new Result();
        for(int s=0;s<CASES;s++)for(int trial=0;trial<SEEDS;trial++){
            for(int mode=0;mode<2;mode++){
                if(stop!=null&&stop.stopped())return result;
                boolean active=mode==1;
                Trial measured=simulate(p,s,VARIANTS[trial],active,stop);
                if(measured==null)return result;
                result.trials[s][trial][mode]=measured;result.finished++;
                if(progress!=null)progress.step(result.finished,result.total(),
                    NAMES[s]+" • prova "+(trial+1)+" • "+
                    (active?"ATTIVA":"CONTROLLO"));
            }
        }
        return result;
    }
    public static void main(String[] args){
        Profile p=new Profile();
        if(!Arrays.equals(p.motor,p.copy().motor)||
           !Arrays.equals(p.membrane,p.copy().membrane))
            throw new AssertionError("Profile clone changed controllers");
        Trial a=simulate(p,ENERGY,7,false,null);
        Trial b=simulate(p,ENERGY,7,true,null);
        if(a==null||b==null||a.energeticContacts!=b.energeticContacts||
           Math.abs(a.traveled-b.traveled)>.001f||
           a.actualDecisions!=b.actualDecisions)
            throw new AssertionError("Virgin navigation must yield IDENTICAL "+
                "physical outcomes for a controlled A/B pair");
        for(int scenario=0;scenario<CASES;scenario++){
            Trial control=simulate(p,scenario,7,false,null);
            Trial active=simulate(p,scenario,7,true,null);
            if(control==null||active==null||
               control.actualDecisions<10||active.actualDecisions<10||
               !Float.isFinite(active.bestDistance)||
               !Float.isFinite(active.endDistance))
                throw new AssertionError("Invalid physically sampled scenario "+scenario);
        }
        Result result=run(p,null,()->false);
        if(result.finished!=result.total()||!result.report().contains("PARITA'"))
            throw new AssertionError("Full paired report has missing experiments");
        System.out.println(String.format(Locale.US,
            "ZERO 0.11.3 behavioral lab PASS: %d paired genuine physical trials, "+
            "3 scenarios, virgin-control neutrality, no forced goal, real gate "+
            "and sampled consequences",result.finished));
    }
}
