package com.overmind.zero;

/**
 * Converts *previously measured consequences* of spectral signatures into
 * a weak attraction/aversion bias for actions pointing toward visible cues.
 * This is NOT an object target selector: it sees only the 26 sensor readings,
 * observed (unknown at birth) per-signature contact values, and energy.
 *
 * No color, position, gate, food or danger label is hardwired.
 * The policy's learned value, episodic recall and stochastic action
 * exploration remain active: this contributes a finite preference only.
 */
public final class SensoryValenceGuide {
    private static final float[] SECTOR_ANGLE={
        -(float)(3*Math.PI/4),-(float)(Math.PI/4),
        (float)(Math.PI/4),(float)(3*Math.PI/4)
    };
    public int guidedChoices=0,avoidanceChoices=0;
    public float lastPositiveBias=0,lastNegativeBias=0;
    private static float clamp(float v,float lo,float hi){
        return Math.max(lo,Math.min(hi,v));
    }
    /**
     * Compare projected heading with observations in four broad sectors.
     * Never read object coordinates, hidden physical law, nor true color.
     */
    public float bias(float[] senses,int action,float[] observedValue,int[] observedCount){
        if(senses==null||senses.length!=SensorimotorMind.S||
           observedValue==null||observedCount==null||
           observedValue.length<3||observedCount.length<3)return 0;
        float angle=(float)((action%8)*Math.PI/4);
        float hunger=clamp(1-senses[0],0,1);
        float total=0;
        for(int signature=0;signature<3;signature++){
            if(observedCount[signature]<1)continue;
            float val=clamp(observedValue[signature]/22f,-1,1);
            if(!Float.isFinite(val))continue;
            float best=0;
            for(int sector=0;sector<4;sector++){
                float brightness=clamp(senses[14+signature*4+sector],0,1);
                if(brightness<.06f)continue;
                float projection=(float)Math.cos(angle-SECTOR_ANGLE[sector]);
                // Food motivation grows when reserve is low. Damage is
                // discouraged at every reserve level. Neither is an oracle.
                float association=val>0?
                    val*(.20f+1.8f*hunger):
                    val*(.60f+.9f*hunger);
                float influence=association*brightness*projection;
                if(Math.abs(influence)>Math.abs(best))best=influence;
            }
            total+=best;
        }
        return clamp(total,-2.1f,2.1f);
    }
    public void chosen(float[] state,int action,float[] values,int[] counts){
        float v=bias(state,action,values,counts);
        if(v>.15f)guidedChoices++;
        if(v<-.15f)avoidanceChoices++;
        lastPositiveBias=Math.max(0,v);
        lastNegativeBias=Math.max(0,-v);
    }
    public void restoreCounts(int guided,int averted){
        guidedChoices=Math.max(0,guided);
        avoidanceChoices=Math.max(0,averted);
    }
    public static void main(String[] args){
        SensoryValenceGuide g=new SensoryValenceGuide();
        float[] s=new float[SensorimotorMind.S];
        s[0]=.12f;
        s[14+2]=.9f; // arbitrary wavelength in sector 2
        float[] learned={22,0,0};int[] n={3,0,0};
        float east=g.bias(s,0,learned,n),ne=g.bias(s,1,learned,n),west=g.bias(s,4,learned,n);
        if(!(ne>east&&east>west))
            throw new AssertionError("Learned visual heading association incorrect");
        if(g.bias(s,1,learned,new int[]{0,0,0})!=0)
            throw new AssertionError("No prior encounter must mean no innate food");
        learned[0]=-7;
        if(!(g.bias(s,1,learned,n)<g.bias(s,4,learned,n)))
            throw new AssertionError("Harmful signature should discourage heading toward it");
        learned[0]=22;s[0]=.99f;
        if(!(g.bias(s,1,learned,n)<ne))
            throw new AssertionError("Urgency from low energy not represented");
        g.chosen(s,1,learned,n);
        if(g.guidedChoices<1)throw new AssertionError("No guided choice telemetry");
        System.out.println("ZERO-009.8 generic spectral-value steering, danger avoidance, hunger dependence and zero prior labels pass");
    }
}
