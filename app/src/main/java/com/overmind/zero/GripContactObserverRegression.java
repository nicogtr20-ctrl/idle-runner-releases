package com.overmind.zero;

/** No-training/no-physics-modification regression for the passive grip observer. */
public final class GripContactObserverRegression {
    private static void ok(boolean pass,String msg){
        if(!pass)throw new AssertionError(msg);
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        GripContactObserver monitor=new GripContactObserver();
        monitor.newLife();
        UnknownWorld.Entity box=new UnknownWorld.Entity(110,0,2,5);
        int a=0;
        skin.x[a]=box.x-SquareGripPuzzle.HALF;skin.y[a]=box.y;
        skin.touchSense[a]=1;
        skin.gripCommand[a]=0f;
        monitor.beforeBox(skin,box);
        monitor.boxFrame(skin,box,.82f,1f,.035f);
        ok(monitor.boxTotals().onNoOrangeFrames==1,
            "ON plus contact without actuator must be observable");
        ok(monitor.boxTotals().vetoPoints==1,
            "Local veto of global ON must be observable");
        // A positive actuated hold should satisfy the exact render condition.
        skin.gripCommand[a]=.82f;skin.gripHold[a]=.10f;
        monitor.beforeBox(skin,box);
        monitor.boxFrame(skin,box,0f,1.04f,.035f);
        ok(monitor.boxTotals().offCommandPoints==1,
            "Local grip during global OFF must be counted");
        ok(monitor.boxTotals().orangePoints==1,
            "Actual orange hold must be recorded");
        // Simulate the OUTPUT of a hold invalidation, not the solver itself.
        monitor.beforeBox(skin,box);
        float originalX=skin.x[a],originalY=skin.y[a],originalCommand=skin.gripCommand[a];
        skin.gripHold[a]=0;
        monitor.boxFrame(skin,box,.82f,1.08f,.035f);
        ok(monitor.boxTotals().interruptedHolds==1 &&
           monitor.boxTotals().sameFaceDrops==1,
           "Existing hold lost on same face must be visible");
        ok(skin.x[a]==originalX&&skin.y[a]==originalY&&
           skin.gripCommand[a]==originalCommand&&skin.gripHold[a]==0,
           "Observer must not change physical state or motor commands");
        ScarcityLab gate=new ScarcityLab(0,0);
        skin.x[a]=gate.gateX()-ScarcityLab.DOOR_HALF_W;
        skin.y[a]=gate.gateCenterY();
        skin.touchSense[a]=1;skin.gripHold[a]=.10f;
        monitor.gateFrame(skin,gate,.82f,2f,.035f);
        ok(monitor.gateTotals().orangePoints>=1,
            "Gate counterpart must count confirmed orange independently");
        String report=monitor.encode();
        ok(report.contains("TOTAL,GATE")&&report.contains("TOTAL,BOX")&&
           report.contains("PREVIOUS_LIFE")&&report.contains("S,"),
           "Report must contain both object families and timed events");
        monitor.terminal();monitor.newLife();
        ok(monitor.encode().contains("PREVIOUS_LIFE\nS,"),
           "Last real life trace must survive a new life");
        System.out.println("GripContactObserver PASS: global/local split, face hold, drop, no interference");
    }
}
