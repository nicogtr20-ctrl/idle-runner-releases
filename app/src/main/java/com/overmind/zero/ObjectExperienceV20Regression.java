package com.overmind.zero;

/** Regression of observational object memory; no game-law or entity lookup. */
public final class ObjectExperienceV20Regression {
    private static void yes(boolean b,String message){
        if(!b)throw new AssertionError(message);
    }
    private static float[] eye(){
        float[] r=new float[16];
        java.util.Arrays.fill(r,1f);
        r[0]=.11f;r[1]=.16f;r[15]=.19f;
        return r;
    }
    private static float[] senses(int signature,float brightness,float reserve){
        float[] s=new float[SensorimotorMind.S];
        s[0]=reserve;
        if(signature>=0)s[14+signature*4+2]=brightness; // ray 0 = east
        return s;
    }
    private static void example(ObjectExperienceV20 m,float[] rays,float[] s,
                                int action,boolean moved,boolean contact,int source,
                                float energy,boolean holdout){
        m.view(rays,s,!holdout);
        m.chosen(action,s);
        if(contact){
            float[] atTouch=senses(source,.94f,s[0]);
            m.energyContact(energy,source,atTouch);
        }
        m.consequence(moved?2f:0,s,rays,!holdout,holdout);
    }
    public static void main(String[] unused){
        float[] rays=eye(),same=senses(0,.16f,.70f),other=senses(1,.85f,.70f);
        ObjectExperienceV20 m=new ObjectExperienceV20();
        m.view(rays,same,true);
        int cat=m.matchedCategory();
        yes(cat>=0,"Real visible obstacle not represented");
        m.chosen(8,same);
        m.energyContact(-7,1,senses(1,0f,.65f));
        m.consequence(3f,same,rays,true,false);
        yes(m.moved(cat)==1,"Real mechanical success lost because energy was negative");
        yes(m.negative(cat)==0,"Unrelated unexpected hazard condemned the object");
        yes(m.ambiguousOutcomes()==0||m.possibleCausalLinks()==0,
            "Accidental unrelated outcome turned into a proven link");
        // Many legitimate empty outcomes must not become evidence of harm or
        // impossibility, nor give unverified advice to the living policy.
        for(int i=0;i<150;i++)example(m,rays,same,8,i%7==0,false,-1,0,false);
        yes(m.negative(cat)==0&&m.positive(cat)==0,
            "Empty boxes acquired fictitious energetic value");
        yes(m.choiceBias(8,.2f)==0,"Unvalidated object memories controlled behavior");
        // Wavelengths distinguish sensory instances even with the same outline.
        m.view(rays,other,true);
        yes(m.matchedCategory()!=cat,"Distinct actually sensed signature was ignored");
        String saved=m.encode();
        ObjectExperienceV20 copy=new ObjectExperienceV20();
        yes(copy.restore(saved)&&copy.encode().equals(saved),"Persistence round trip failed");
        copy.resetLife();
        yes(copy.categories()==m.categories()&&copy.moved(cat)==m.moved(cat),
            "Real memories wiped on death");
        yes(!copy.restore(saved+"|broken")&&copy.encode().equals(saved),
            "Invalid snapshot corrupted valid object memories");
        ObjectExperienceV20 newborn=new ObjectExperienceV20();
        yes(newborn.categories()==0&&newborn.physicalSuccesses()==0,
            "New organism inherited object memories");
        System.out.println("ZERO2 object memory: independent movement, uncertain hazard, rarity, sensory wavelengths, persistence PASS");
    }
}
