package com.overmind.zero;
import java.util.Locale;

/** READ-ONLY forensic probe. Lab stimulations ONLY. Never called by Android gameplay. */
public final class Zero162LocomotionForensics {
    static float radius(MotorBrain.Body b,Morphology.Surface s){
        float m=0;
        for(int i=0;i<Morphology.POINTS;i++)
            m=Math.max(m,(float)Math.hypot(s.x[i]-b.cx,s.y[i]-b.cy));
        return m;
    }
    static int uncoupled(MotorBrain.Body b,Morphology.Surface s,SymbioteMatterV1 m){
        int no=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float near=Float.POSITIVE_INFINITY;
            for(int j=0;j<MotorBrain.N;j++)
                near=Math.min(near,(float)Math.hypot(s.x[i]-b.x[j],s.y[i]-b.y[j]));
            for(int j=0;j<SymbioteMatterV1.INNER;j++)
                near=Math.min(near,(float)Math.hypot(s.x[i]-m.x[j],s.y[i]-m.y[j]));
            if(near>=24f)no++;
        }
        return no;
    }
    static void require(boolean b,String msg){if(!b)throw new AssertionError(msg);}
    public static void main(String[] args){
        float dt=.025f;
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 g=new SymbioteMatterV1();g.reset(b,s);
        float[] arm=new float[64];arm[0]=1f;arm[4]=.9f;arm[8]=.65f;
        int peakNoReact=0;float peakRadius=0;
        for(int k=0;k<240;k++){
            g.step(b,s,arm,dt);
            peakNoReact=Math.max(peakNoReact,uncoupled(b,s,g));
            peakRadius=Math.max(peakRadius,radius(b,s));
        }
        float pastSensor=s.peakExtension;
        float[] rest=new float[64];
        for(int k=0;k<520;k++)g.step(b,s,rest,dt);
        float current=Math.max(0,radius(b,s)-28f);
        System.out.printf(Locale.US,"FORENSIC_SENSOR peakEver=%.5f shapeNow=%.5f staleGap=%.5f stretchedAtEnd=%d area=%.6f cross=%d%n",
            s.peakExtension,current,s.peakExtension-current,s.stretchedPoints,g.areaRelativeError(s),MaterialGeometry.selfCrossings(s));
        System.out.printf(Locale.US,"FORENSIC_REACTION tipRadiusPeak=%.3f sitesWithoutNearbyInteriorReaction=%d%n",peakRadius,peakNoReact);
        require(g.invalid==0&&MaterialGeometry.selfCrossings(s)==0,"lab matter invalid");
        require(s.peakExtension>=pastSensor-.0001f,"sensor peak unexpectedly reset");

        int normalAligned=0,reverse=0,nearZero=0;
        float minProjection=Float.POSITIVE_INFINITY,maxProjection=Float.NEGATIVE_INFINITY;
        StringBuilder table=new StringBuilder();
        for(int a=0;a<16;a++){
            MotorBrain.Body living=new MotorBrain.Body();
            Morphology.Surface skin=new Morphology.Surface(living);
            SymbioteMatterV1 gel=new SymbioteMatterV1();
            gel.reset(living,skin);
            SymbioteMotorHeadV1 cortex=new SymbioteMotorHeadV1();
            float[] recurrent=new float[RecurrentCoreV22.Z];recurrent[0]=.7f;
            float[] observation=DistributedBodySense.sense(living,skin,null,null);
            cortex.chosen(a,observation,recurrent,0f,null,
                SymbioteMotorHeadV1.measuredSectorAngles(living,skin));
            float[] cmd=cortex.commands().clone();
            for(int t=0;t<160;t++)gel.step(living,skin,cmd,dt);
            float worldX=(float)Math.cos((a%8)*Math.PI/4);
            float worldY=(float)Math.sin((a%8)*Math.PI/4);
            float proj=living.cx*worldX+living.cy*worldY;
            float net=(float)Math.hypot(living.cx,living.cy);
            float cosine=net<.001f?0:proj/net;
            table.append(String.format(Locale.US," %d:%.3f/%.3f",a,net,cosine));
            if(cosine>.3f)normalAligned++;
            else if(cosine<-.3f)reverse++;
            else nearZero++;
            minProjection=Math.min(minProjection,cosine);
            maxProjection=Math.max(maxProjection,cosine);
            require(gel.invalid==0&&MaterialGeometry.selfCrossings(skin)==0,"action "+a+" invalid contour");
        }
        System.out.printf(Locale.US,"FORENSIC_VIRGIN_ACTIONS headingAligned=%d reverse=%d ambiguous=%d minCos=%.3f maxCos=%.3f FULL=%s%n",
            normalAligned,reverse,nearZero,minProjection,maxProjection,table);
    }
}
