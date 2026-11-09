package com.overmind.zero;

/** V520 QA-only, compares ONLY original GRU-sampled motor headings.
 * Independent V384 historical train + heldout impasse is mandatory.
 * Currently measured membrane pressure is geometry, not presumed motor
 * efficacy; negative physically learned optical risk remains inviolate.
 */
final class Zero520LiveTactileChoiceGate {
    static final float MIN_FRONT=.23f, MIN_RELIEF=.20f, MAX_NEW_RISK=.012f;
    static final class Audit {
        long assessed,frontContact,reliefCandidates,learnedHarmVetoes,physicalRedirects;
    }
    static int choose(boolean verified,float[] touching,float[] risk,int selected,Audit audit){
        if(!verified)return selected;
        if(touching==null||risk==null||touching.length==0||
           touching.length!=risk.length||selected<0||selected>=touching.length)
            throw new IllegalArgumentException("V520 malformed physical candidates");
        for(int i=0;i<touching.length;i++)
            if(!Float.isFinite(touching[i])||!Float.isFinite(risk[i])||
                touching[i]<0||touching[i]>1||risk[i]<0||risk[i]>1)
                throw new IllegalArgumentException("V520 invalid tactile/risk");
        if(audit!=null)audit.assessed++;
        float original=touching[selected];
        if(original<MIN_FRONT)return selected;
        if(audit!=null)audit.frontContact++;
        int best=selected;
        float bestPressure=original,oldRisk=risk[selected],bestRisk=oldRisk;
        for(int i=0;i<touching.length;i++){
            if(i==selected||original-touching[i]<MIN_RELIEF)continue;
            if(audit!=null)audit.reliefCandidates++;
            // Safety before locomotion: never sacrifice verified actual
            // wavelength harm evidence in the pursuit of a wall shortcut.
            if(risk[i]>oldRisk+MAX_NEW_RISK){
                if(audit!=null)audit.learnedHarmVetoes++;
                continue;
            }
            if(touching[i]<bestPressure-1e-5f||
               (Math.abs(touching[i]-bestPressure)<1e-5f&&risk[i]<bestRisk)){
                best=i;bestPressure=touching[i];bestRisk=risk[i];
            }
        }
        if(audit!=null&&best!=selected)audit.physicalRedirects++;
        return best;
    }
    private Zero520LiveTactileChoiceGate(){}
}
