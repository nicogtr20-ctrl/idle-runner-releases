package com.overmind.zero;
import java.util.*;
/** Physical command latency, uncensored option experiments and live velocity context. */
public final class Zero172MotorResponseRegression {
    private static void check(boolean yes,String why){if(!yes)throw new AssertionError("ZERO172: "+why);}
    private static int samples(MotorDurationV22 d){int n=0;for(int a=0;a<16;a++)for(int k=0;k<MotorDurationV22.OPTIONS;k++)n+=d.trained(a,k)+d.heldout(a,k);return n;}
    private static void duration(){
        MotorDurationV22 d=new MotorDurationV22();float[] ranked=new float[16];
        int chosen=d.select(0,0,ranked,false,.7f,0,0,0),ticks=d.selectedTicks();
        check(ticks>=2,"unmapped body has no multi-tick physical experiment");
        for(int i=0;i<ticks;i++){
            d.interval(chosen,i+1,0,0,0,.01f,0);
            int next=d.select(4,4,ranked,true,.7f,0,i+1,0);
            check(next==(i==ticks-1?4:0),"random proposal interrupted an unfinished physical experiment");
        }
        check(samples(d)==1,"complete option not counted exactly once");
        long before=d.evaluated();int count=samples(d);
        d.resetLife();d.select(0,0,ranked,false,.7f,0,0,0);check(d.selectedTicks()>1,"fixture needs long option");
        d.interval(0,1,0,-7,7,0,0);
        check(d.select(4,4,ranked,false,.7f,7,1,0)==4,"harm failed to interrupt");
        check(d.evaluated()==before&&samples(d)==count,"censored option relabeled a complete duration");
        d.resetLife();d.select(0,0,ranked,false,.7f,0,0,0);ranked[6]=.7f;
        check(d.select(6,6,ranked,false,.7f,0,0,0)==6,"strong current neural alternative blocked");
        String current=d.encode();MotorDurationV22 restored=new MotorDurationV22();
        check(restored.restore(current)&&restored.encode().equals(current),"duration checkpoint roundtrip");
        check(!restored.restore(current+"bad")&&restored.encode().equals(current),"malformed save mutated duration");
        StringBuilder old=new StringBuilder("md165|80|80|40|80");
        for(int a=0;a<16;a++)for(int k=0;k<3;k++)old.append('|').append(a+1).append(",2,.1,.1,1,1,.2,.2");
        check(restored.restore(old.toString()),"old 48-cell duration checkpoint rejected");
        for(int a=0;a<16;a++)for(int k=0;k<MotorDurationV22.OPTIONS;k++){
            check(restored.trained(a,k)==(k<3?a+1:0),"old per-action duration history remapped incorrectly");
            check(restored.directionalSamples(a,k)==0&&restored.directionalHoldouts(a,k)==0,"censored old evidence grants new confidence");
        }
    }
    private static void context(){
        ContextualNavigationV172 n=new ContextualNavigationV172();MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);SymbioteMatterV1 matter=new SymbioteMatterV1();matter.reset(b,s);float[]w=new float[724];b.cvx=3;
        for(int i=0;i<12;i++){n.context(b,s,w);n.chosen(0);n.consequence(0,2,0,i<8,i>=8,true);}
        check(n.verified(0),"matching velocity failed heldout validation");
        String encoded=n.encode();b.cvx=-3;n.context(b,s,w);
        check(!n.verified(0),"opposite residual momentum inherited old prediction");
        check(n.restore(encoded),"current signed-velocity checkpoint rejected");
        // Construct bounded historical epochs, then add five distinct live contexts.
        StringBuilder history=new StringBuilder("cn172|1");for(int i=0;i<724;i++)history.append(",0");
        for(int i=0;i<6;i++){history.append("|0,100,50,1,0,.1,1,0");for(int j=0;j<20;j++)history.append(",0");}
        check(n.restore(history.toString()),"epoch fixture rejected");
        for(int i=0;i<5;i++){b.cvx=-12+6*i;n.context(b,s,w);n.chosen(0);n.consequence(0,1,0,true,false,true);}
        int live=0;for(String cell:n.encode().split("\\|")){String[]v=cell.split(",");if(v.length==28&&v[7].equals("1"))live++;}
        check(live==5,"stale epochs evict current low-count contexts");
        ContextualNavigationV172 restored=new ContextualNavigationV172();String data=n.encode();check(restored.restore(data)&&data.equals(restored.encode()),"context exact roundtrip");
        check(!restored.restore(data+"|bad")&&data.equals(restored.encode()),"malformed context reset learned memory");
        check(Arrays.asList(ResetDomains.keys(ResetDomains.NAVIGATION)).contains(ContextualNavigationV172.KEY),"context selective reset missing");
    }
    private static void physicalLatency(){
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface s=new Morphology.Surface(b);SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(b,s);
        SymbioteMotorHeadV1 h=new SymbioteMotorHeadV1();h.setAdaptationEnabled(false);float[]z=new float[RecurrentCoreV22.Z];z[0]=.7f;
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();int reverse=0;float first=0,last=0,start=b.cx;
        for(int interval=0;interval<16;interval++){
            int a=interval<8?0:4;float x=b.cx;
            h.chosen(a,DistributedBodySense.sense(b,s,null,empty),z,.2f,FluidInteroceptionV1.sense(b,s,gel),SymbioteMotorHeadV1.measuredSectorAngles(b,s),b,s);
            for(int tick=0;tick<39;tick++){h.fastTick(b,s,1f/60);gel.step(b,s,h.commands(),1f/60);}
            float signed=(b.cx-x)*(a==0?1:-1);
            if(interval>=8&&interval<=9&&signed<0)reverse++;
            if(interval==8)first=signed;if(interval==15)last=signed;
            h.consequence(0,0,0,0,false,false);
        }
        check(reverse>0&&last>0,"real material response no longer reproduces transient reversal followed by intended movement");
        check(gel.invalid==0&&MaterialGeometry.selfCrossings(s)==0,"latency fixture invalid material");
        System.out.printf(Locale.US,"REAL NEURAL LATENCY: first reversed-command interval %.3f U; settled interval %.3f U%n",first,last);
    }
    public static void main(String[]args){duration();context();physicalLatency();System.out.println("ZERO172 PASS: complete motor experiments, safe interrupts, honest legacy migration, signed velocity, current epoch capacity and actual material response");}
}
