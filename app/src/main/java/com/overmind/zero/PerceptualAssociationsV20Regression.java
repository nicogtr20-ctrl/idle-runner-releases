package com.overmind.zero;

import java.util.Arrays;

/** Deterministic falsification tests; these are SENSOR fixtures, not box labels. */
public final class PerceptualAssociationsV20Regression {
    private PerceptualAssociationsV20Regression(){}
    static void yes(boolean b,String why){if(!b)throw new AssertionError(why);}
    static float[] empty(){float[] r=new float[16];Arrays.fill(r,1f);return r;}
    static float[] rotate(float[] src,int shift){
        float[] out=new float[16];
        for(int i=0;i<16;i++)out[(i+shift)%16]=src[i];
        return out;
    }
    static float[] narrow(){
        float[] r=empty();r[3]=.30f;r[4]=.20f;r[5]=.30f;return r;
    }
    static float[] wide(){
        float[] r=empty();
        for(int i=2;i<=8;i++)r[i]=i==5?.20f:.23f;
        return r;
    }
    public static void main(String[] ignored){
        PerceptualAssociationsV20 m=new PerceptualAssociationsV20();
        m.view(empty(),true);
        yes(m.categories()==0&&m.currentCategory()==-1,
            "An empty visible world cannot become an object class");
        float[] a=narrow();
        m.view(a,true);
        yes(m.categories()==1&&m.currentCategory()==0,"First partial shape not learned");
        m.view(rotate(a,7),true);
        yes(m.categories()==1&&m.currentCategory()==0,
            "Same pattern after global viewing-direction rotation must match");
        m.view(wide(),true);
        yes(m.categories()==2&&m.currentCategory()==1,
            "Strongly different sensed contour must not be called identical");
        m.view(a,true);
        m.chosen(8);
        m.consequence(9f,true,false);
        yes(m.evidence(0,8)==1&&m.observedMovements(0,8)==1,
            "Only actual after-action external displacement is positive evidence");
        m.view(a,true);
        m.chosen(8);
        m.consequence(0f,true,false);
        yes(m.evidence(0,8)==2&&m.observedMovements(0,8)==1,
            "Pressing/touching without movement cannot count as success");
        m.view(a,false);
        m.chosen(8);
        int before=m.evidence(0,8);
        m.consequence(0f,false,true);
        yes(m.validations()==1&&m.evidence(0,8)==before,
            "Heldout samples must not train prototypes or effect counts");
        long novel=m.unknown();
        float[] two=empty();two[3]=two[11]=.22f;
        m.view(two,true);
        yes(m.currentCategory()==-1&&m.categories()==2&&m.ambiguous()>0,
            "Two similar separate nearest obstacles must not be fused");
        m.view(empty(),true);
        yes(m.unknown()>novel&&m.categories()==2,"No fabricated shape in empty scene");
        m.actualEnergy(22f);
        yes(m.realEnergyEvents()==1,
            "Actual energy contact is counted without claiming shape caused it");
        String saved=m.encode();
        PerceptualAssociationsV20 restored=new PerceptualAssociationsV20();
        yes(restored.restore(saved)&&restored.encode().equals(saved),
            "Complete bounded model roundtrip must conserve real learning");
        restored.resetLife();
        restored.view(rotate(a,10),false);
        yes(restored.categories()==2&&restored.currentCategory()==0,
            "Category learnt in another life must be usable without training");
        String intact=restored.encode();
        yes(!restored.restore(saved.replaceFirst("pa20\\|","pa19|"))&&
            restored.encode().equals(intact),"Bad version must leave state untouched");
        yes(!restored.restore(saved.replaceFirst("\\|0\\.2", "|NaN"))&&
            restored.encode().equals(intact),"Invalid prototype must be atomic");
        // Actuation choice parity: attaching a purely observational catalogue
        // MUST NOT change motor policy or randomness over 440 choices.
        SensorimotorMind baseline=new SensorimotorMind(), shadow=new SensorimotorMind();
        shadow.attachPerceptualAssociationsV20(new PerceptualAssociationsV20());
        float[] senses=new float[SensorimotorMind.S];
        senses[0]=.6f;
        baseline.begin(senses);shadow.begin(senses);
        for(int i=0;i<440;i++){
            int expected=baseline.choose(senses);
            int actual=shadow.choose(senses);
            yes(expected==actual,"Perceptual observer altered original action at "+i);
        }
        System.out.println("PASS ZERO2 perceptual prototypes, view rotation, uncertainty, real effect association, holdout, persistence and 440 unchanged decisions");
    }
}
