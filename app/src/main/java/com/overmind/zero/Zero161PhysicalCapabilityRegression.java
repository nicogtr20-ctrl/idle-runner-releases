package com.overmind.zero;

import java.util.Locale;

/** OTA161 physics-only acceptance: NEVER invoked by the organism's policy.
 * Laboratory electrodes stimulate real cortex coordinates; no scripted gait
 * or limb generator is shipped to Android, and no learned weights are reset.
 */
public final class Zero161PhysicalCapabilityRegression {
    static void ok(boolean good,String message){
        if(!good)throw new AssertionError("OTA161: "+message);
    }
    static float sectorRadius(MotorBrain.Body b,Morphology.Surface s,int sector){
        float sum=0;
        int width=Morphology.POINTS/SymbioteMatterV1.SECTORS;
        for(int j=0;j<width;j++){
            int k=sector*width+j;
            sum+=(float)Math.hypot(s.x[k]-b.cx,s.y[k]-b.cy);
        }
        return sum/width;
    }
    static float[] drive(int... sectors){
        float[] v=new float[SymbioteMatterV1.SECTORS*SymbioteMatterV1.OUTPUTS];
        for(int s:sectors){
            v[s*4]=.96f;
            v[((s+1)%16)*4]=.47f;
            v[((s+15)%16)*4]=.47f;
        }
        return v;
    }
    static void stable(SymbioteMatterV1 m,Morphology.Surface s){
        ok(m.invalid==0,"nonfinite matter");
        ok(MaterialGeometry.selfCrossings(s)==0,"self-crossing cortex");
        ok(m.areaRelativeError(s)<.16f,"area conservation");
    }
    static float norm(float a,float b){return (float)Math.hypot(a,b);}
    public static void main(String[] args){
        final float dt=.025f;
        MotorBrain.Body calmBody=new MotorBrain.Body();
        Morphology.Surface calmSkin=new Morphology.Surface(calmBody);
        SymbioteMatterV1 calmMatter=new SymbioteMatterV1();
        calmMatter.reset(calmBody,calmSkin);
        float[] still=new float[64];
        float maxQuietStep=0;
        for(int i=0;i<320;i++){
            float x=calmBody.cx,y=calmBody.cy;
            calmMatter.step(calmBody,calmSkin,still,dt);
            maxQuietStep=Math.max(maxQuietStep,norm(calmBody.cx-x,calmBody.cy-y));
        }
        stable(calmMatter,calmSkin);
        float quietNet=norm(calmBody.cx,calmBody.cy);

        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 m=new SymbioteMatterV1();
        m.reset(b,s);
        float[] triple=drive(0,5,10);
        for(int i=0;i<70;i++){m.step(b,s,triple,dt);}
        stable(m,s);
        long repairsAtPeak=m.topologyRepairs;
        float[] radii=new float[16];
        for(int j=0;j<16;j++)radii[j]=sectorRadius(b,s,j);
        float[] peaks={radii[0],radii[5],radii[10]};
        float[] gaps={radii[2],radii[7],radii[13]};
        int strong=0;
        for(int k=0;k<3;k++)if(peaks[k]>gaps[k]+2f && peaks[k]>31f)strong++;
        float before0=radii[0],before5=radii[5],before10=radii[10];
        float[] independent=drive(0,5,10);independent[5*4]=-.92f;
        independent[4*4]=-.52f;independent[6*4]=-.52f;
        float[] retractProgress=new float[4];
        for(int i=0;i<65;i++){
            m.step(b,s,independent,dt);
            if(i==0||i==9||i==29||i==64)retractProgress[i==0?0:i==9?1:i==29?2:3]=sectorRadius(b,s,5)-before5;
        }
        stable(m,s);
        float delta0=sectorRadius(b,s,0)-before0;
        float delta5=sectorRadius(b,s,5)-before5;
        float delta10=sectorRadius(b,s,10)-before10;

        MotorBrain.Body crawl=new MotorBrain.Body();
        Morphology.Surface surface=new Morphology.Surface(crawl);
        SymbioteMatterV1 matter=new SymbioteMatterV1();
        matter.reset(crawl,surface);
        float[] action=new float[64];
        float path=0;
        float end=0;
        for(int tick=0;tick<640;tick++){
            boolean phase=(tick/20)%2==0;
            for(int sector=0;sector<16;sector++){
                boolean forward=Math.cos((sector+.5)*Math.PI/8)>0;
                action[4*sector]=forward?(phase?.78f:-.78f):(phase?-.43f:.43f);
                action[4*sector+2]=forward?(phase?-.95f:.95f):(phase?.95f:-.95f);
            }
            float xx=crawl.cx,yy=crawl.cy;
            matter.step(crawl,surface,action,dt);
            path+=norm(crawl.cx-xx,crawl.cy-yy);
        }
        stable(matter,surface);
        end=norm(crawl.cx,crawl.cy);
        System.out.printf(Locale.US,
            "ZERO161 BASELINE quietNet=%.3f quietMaxStep=%.3f threePeaks=%d peak=[%.2f %.2f %.2f] gaps=[%.2f %.2f %.2f] independentDelta=[%.2f %.2f %.2f] radialProgress=[%.2f %.2f %.2f %.2f] selfContactPairs=%d repairs=[%d +%d] crawledNet=%.2f gross=%.2f efficiency=%.4f%n",
            quietNet,maxQuietStep,strong,peaks[0],peaks[1],peaks[2],gaps[0],gaps[1],gaps[2],delta0,delta5,delta10,retractProgress[0],retractProgress[1],retractProgress[2],retractProgress[3],m.selfContactPairs,repairsAtPeak,m.topologyRepairs-repairsAtPeak,end,path,end/Math.max(1,path));
        ok(quietNet<2f && maxQuietStep<.35f,"passive gel jitters / translates");
        ok(strong==3,"three independently activated pseudopods cannot coexist");
        ok(Math.abs(delta5)>1.2f,"changing one active appendage has no effect");
        ok(end>5f,"known opposite-phase cortex cannot produce meaningful locomotion");
        ok(end/Math.max(1,path)>.01f,"substrate produces excessive useless jitter");
    }
}
