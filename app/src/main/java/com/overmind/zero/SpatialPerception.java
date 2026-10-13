package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Observed, NON-semantic spatial perception.
 *
 * - The existing 26-sense model is unchanged.
 * - Rays use precisely the same physical solid/occlusion test as the current
 *   eight proximity sensors, with no game object labels or hidden target.
 * - A bounded life-local occupancy memory is registered by observed body
 *   displacement (odometry), not by a prebuilt map or goal coordinates.
 * - A separate learned action predictor/value has zero effect on behavior
 *   until it beats a holdout "no change" prediction baseline.
 *
 * No navigation script, directional reward, knowledge of the door, or
 * autonomous objective has been added.
 */
public final class SpatialPerception {
    public static final String KEY="spatial_perception_v110";
    public static final int RAYS=16, STEPS=8, CELL=36, MAX_CELLS=1024;
    public static final float STEP=22f, RANGE=STEPS*STEP;
    private static final int ACTIONS=SensorimotorMind.ACTIONS;
    private static final int HEAD=4, VALUES=RAYS+2;
    public static final int PARAMETERS=ACTIONS*RAYS*HEAD+ACTIONS*VALUES;
    private static final float PRED_LR=.025f,VALUE_LR=.018f;
    private final float[] prediction=new float[ACTIONS*RAYS*HEAD];
    private final float[] values=new float[ACTIONS*VALUES];
    private final float[] current=new float[RAYS],before=new float[RAYS],eyeOrigins=new float[RAYS*2];
    private final LinkedHashMap<Long,Byte> memory=new LinkedHashMap<>();
    private int pending=-1;
    private float rememberedAhead=0f;
    private float odometryX=0,odometryY=0;
    private float lastBodyX=Float.NaN,lastBodyY=Float.NaN;
    public long transitions=0;
    public int validations=0,choiceInfluences=0,sweeps=0;
    public float error=Float.NaN,baseline=Float.NaN;

    public SpatialPerception(){Arrays.fill(current,1f);Arrays.fill(before,1f);}
    private static float bound(float v,float a,float b){return Math.max(a,Math.min(b,v));}
    private static boolean valid(float[] v){
        if(v==null||v.length!=RAYS)return false;
        for(float x:v)if(!Float.isFinite(x)||x<0||x>1.0001f)return false;
        return true;
    }
    private static long key(int gx,int gy){
        return ((long)gx<<32)^(gy&0xffffffffL);
    }
    private static int cell(float x){return (int)Math.floor(x/CELL);}
    private static float rad(int r){return (float)(r*Math.PI*2/RAYS);}
    private static void require(boolean b,String message){
        if(!b)throw new AssertionError(message);
    }
    /**
     * Physical ray scan from the creature center. Rays stop on the FIRST
     * solid; cells behind it are not observed. No RGB/energy/effect labels.
     */
    public static float[] sense(float cx,float cy,ScarcityLab room,
                                ArrayList<UnknownWorld.Entity> objects){
        float[] result=new float[RAYS];Arrays.fill(result,1f);
        if(!Float.isFinite(cx)||!Float.isFinite(cy))return result;
        for(int r=0;r<RAYS;r++){
            float angle=rad(r);
            float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
            for(int s=1;s<=STEPS;s++){
                float x=cx+dx*s*STEP,y=cy+dy*s*STEP;
                if(SensorimotorMind.Senses.solid(room,x,y,objects)){
                    result[r]=(float)s/(STEPS+1);
                    break;
                }
            }
        }
        return result;
    }
    private void putCell(int x,int y,int sign){
        long k=key(x,y);
        int prior=memory.containsKey(k)?memory.get(k):0;
        int next=Math.max(-3,Math.min(3,prior+sign));
        memory.remove(k);
        // Zero is UNKNOWN occupancy (neutral evidence). Never serialize it
        // as a cell that our own loader would reject on the next startup.
        if(next!=0)memory.put(k,(byte)next);
        if(memory.size()>MAX_CELLS)memory.remove(memory.keySet().iterator().next());
    }
    /** Update only regions actually within line-of-sight, using sensed
     * distances; never look behind the first contact. */
    private void remember(float[] distances){remember(distances,null);}
    private void remember(float[] distances,float[] physicalOrigins){
        // Integrate this ONE simultaneous scan in two phases. A neighboring
        // ray may traverse a cell that contains part of an obstacle, but that
        // free sub-ray must not erase a confirmed hit from the SAME scan.
        // A later scan that no longer sees the obstacle can clear it.
        HashSet<Long> blockedThisScan=new HashSet<>();
        putCell(cell(odometryX),cell(odometryY),-1);
        for(int r=0;r<RAYS;r++){
            float theta=rad(r);
            float dx=(float)Math.cos(theta),dy=(float)Math.sin(theta);
            boolean blocked=distances[r]<.999f;
            int hit=blocked?Math.round(distances[r]*(STEPS+1)):STEPS+1;
            for(int step=1;step<=STEPS;step++){
                if(blocked&&step>hit)break;
                float offsetX=physicalOrigins==null?0:physicalOrigins[2*r];
                float offsetY=physicalOrigins==null?0:physicalOrigins[2*r+1];
                float x=odometryX+offsetX+dx*STEP*step,
                      y=odometryY+offsetY+dy*STEP*step;
                int gx=cell(x),gy=cell(y);
                if(blocked&&step==hit)blockedThisScan.add(key(gx,gy));
                else putCell(gx,gy,-1);
            }
        }
        for(long k:blockedThisScan){
            int x=(int)(k>>32),y=(int)k;
            putCell(x,y,6); // any hit outranks same-scan overlapping free rays
        }
    }
    /** True distributed photoreceptors: sixteen physical membrane patches
     * inspect the exterior. For memory registration preserve EACH patch's
     * offset from the material centroid, not an invented central eye.
     */
    private static float[] senseMaterial(MotorBrain.Body body,Morphology.Surface skin,
                                         ScarcityLab room,ArrayList<UnknownWorld.Entity> objects,
                                         float[] origins){
        float[] result=new float[RAYS];Arrays.fill(result,1f);
        if(body==null||skin==null)return result;
        for(int r=0;r<RAYS;r++){
            float angle=rad(r),dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
            float[] eye=MaterialGeometry.eye(skin,body.cx,body.cy,dx,dy);
            origins[2*r]=eye[0]-body.cx;origins[2*r+1]=eye[1]-body.cy;
            for(int step=1;step<=STEPS;step++){
                float x=eye[0]+dx*step*STEP,y=eye[1]+dy*step*STEP;
                if(SensorimotorMind.Senses.solid(room,x,y,objects)){
                    result[r]=step/(float)(STEPS+1);break;
                }
            }
        }
        return result;
    }
    /** Movement is integrated from the organism's OWN observed displacement.
     * World coordinates are used only by the low-level ray sensor and the
     * body odometry anchor, NOT supplied to the learner as object identity. */
    public void observeEnvironment(MotorBrain.Body body,ScarcityLab room,
                                   ArrayList<UnknownWorld.Entity> objects){
        observeEnvironment(body,null,room,objects);
    }
    public void observeEnvironment(MotorBrain.Body body,Morphology.Surface skin,
                                   ScarcityLab room,ArrayList<UnknownWorld.Entity> objects){
        if(body==null||objects==null)return;
        float x=body.cx,y=body.cy;
        if(Float.isFinite(lastBodyX)&&Float.isFinite(lastBodyY)){
            float dx=x-lastBodyX,dy=y-lastBodyY;
            float distance=(float)Math.hypot(dx,dy);
            if(Float.isFinite(distance)&&distance<250){
                odometryX+=dx;odometryY+=dy;
            }else if(distance>=250){
                // Teleport or inconsistent checkpoint: do not stitch maps.
                memory.clear();odometryX=odometryY=0;
            }
        }
        lastBodyX=x;lastBodyY=y;
        float[] origins=skin==null?null:new float[RAYS*2];
        float[] rays=skin==null?sense(x,y,room,objects):
            senseMaterial(body,skin,room,objects,origins);
        feed(rays);
        if(origins!=null)System.arraycopy(origins,0,eyeOrigins,0,eyeOrigins.length);
        else Arrays.fill(eyeOrigins,0);
        remember(rays,origins);
        sweeps++;
    }
    public void feed(float[] rays){
        if(valid(rays))System.arraycopy(rays,0,current,0,RAYS);
    }
    public float[] ranges(){return current.clone();}
    public float[] eyeOrigins(){return eyeOrigins.clone();}
    public float distance(int ray){
        if(ray<0||ray>=RAYS)throw new IllegalArgumentException("ray");
        return current[ray]>=.999f?RANGE:
            Math.round(current[ray]*(STEPS+1))*STEP;
    }
    public int mappedCells(){return memory.size();}
    public int blockedCells(){
        int n=0;for(byte v:memory.values())if(v>0)n++;return n;
    }
    public int freeCells(){
        int n=0;for(byte v:memory.values())if(v<0)n++;return n;
    }
    public float odometryTravel(){return (float)Math.hypot(odometryX,odometryY);}
    /** Sensor-relative odometry, NOT a privileged absolute-world location.
     * Used for comparing occupancy evidence at the SAME observed location
     * before/after a chosen action rather than confusing self-motion for
     * movement of the surroundings. */
    public float perceivedX(){return odometryX;}
    public float perceivedY(){return odometryY;}
    public int perceivedCellX(){return cell(odometryX);}
    public int perceivedCellY(){return cell(odometryY);}
    public byte observedCell(int cx,int cy){
        Byte evidence=memory.get(key(cx,cy));
        return evidence==null?0:evidence;
    }

    /** Read-only square centered on ZERO's estimated current position.
     * 0 unseen, negative confirmed navigable sightline, positive obstacle.
     * Returns observations, NOT the actual hidden scene. */
    public byte[] localMap(int radius){
        if(radius<1||radius>16)throw new IllegalArgumentException("map radius");
        int n=radius*2+1;
        byte[] result=new byte[n*n];
        int x0=cell(odometryX),y0=cell(odometryY);
        for(int y=-radius;y<=radius;y++){
            for(int x=-radius;x<=radius;x++){
                Byte v=memory.get(key(x0+x,y0+y));
                if(v!=null)result[(y+radius)*n+x+radius]=v;
            }
        }
        return result;
    }

    public void resetLife(){
        memory.clear();odometryX=odometryY=0;
        lastBodyX=lastBodyY=Float.NaN;
        pending=-1;rememberedAhead=0;
        Arrays.fill(current,1f);Arrays.fill(before,1f);Arrays.fill(eyeOrigins,0);
    }
    public void resumeLife(float x,float y){
        // Only transient action history and exact anchor are refreshed;
        // learned predictor and mapped cells of THIS life are retained.
        pending=-1;lastBodyX=x;lastBodyY=y;
    }
    public void chosen(int a){
        if(a<0||a>=ACTIONS)return;
        pending=a;
        System.arraycopy(current,0,before,0,RAYS);
        rememberedAhead=mapAhead(a);
    }
    /** Three grid cells in the proposed ACTUATOR direction, but no
     * hand-coded preference for free/blocked/unknown. Their value is learned
     * from real outcomes just like the ray values. */
    private float mapAhead(int action){
        float angle=(float)((action%8)*Math.PI/4);
        float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
        float result=0;
        for(int j=1;j<=3;j++){
            float x=odometryX+dx*CELL*j,y=odometryY+dy*CELL*j;
            Byte seen=memory.get(key(cell(x),cell(y)));
            if(seen!=null)result+=bound(-seen/3f,-1f,1f);
        }
        return result/3f;
    }
    private float predictedDelta(float[] input,int action,int ray){
        int base=(action*RAYS+ray)*HEAD;
        float p=prediction[base+3]+prediction[base]*input[ray];
        p+=prediction[base+1]*input[(ray+RAYS-1)%RAYS];
        p+=prediction[base+2]*input[(ray+1)%RAYS];
        return bound(p,-1,1);
    }
    private float value(float[] input,int a,float occupancy){
        int base=a*VALUES;
        float v=values[base+RAYS]+values[base+RAYS+1]*occupancy;
        for(int r=0;r<RAYS;r++)v+=values[base+r]*input[r];
        return bound(v,-2,2);
    }
    public float confidence(){
        if(validations<30||!Float.isFinite(error)||!Float.isFinite(baseline)||
           baseline<.025f||error>=baseline*.96f)return 0f;
        return bound((baseline-error)/baseline,0f,.5f);
    }
    public float choiceBias(int action){
        if(action<0||action>=ACTIONS)return 0;
        float trust=confidence();
        if(trust<=0)return 0;
        return bound(value(current,action,mapAhead(action))*trust*.25f,-.19f,.19f);
    }
    public void noteDecision(float peak){
        if(peak>.008f)choiceInfluences++;
    }
    /** A real completed action: held-out samples never train either model
     * or value weights, and the spatial memory is not a goal or reward. */
    public void consequence(int action,float livedReward,
                            boolean learning,boolean heldout){
        if(pending!=action||action<0||action>=ACTIONS)return;
        float err=0,naive=0;
        for(int r=0;r<RAYS;r++){
            float delta=current[r]-before[r];
            float residual=delta-predictedDelta(before,action,r);
            err+=residual*residual;naive+=delta*delta;
            if(learning&&!heldout){
                int base=(action*RAYS+r)*HEAD;
                float g=PRED_LR*bound(residual,-1,1);
                prediction[base]=bound(prediction[base]+g*before[r],-2,2);
                prediction[base+1]=bound(prediction[base+1]+
                    g*before[(r+RAYS-1)%RAYS],-2,2);
                prediction[base+2]=bound(prediction[base+2]+
                    g*before[(r+1)%RAYS],-2,2);
                prediction[base+3]=bound(prediction[base+3]+g,-2,2);
            }
        }
        if(heldout){
            validations++;
            float alpha=validations==1?1f:.09f;
            float e=(float)Math.sqrt(err/RAYS),b=(float)Math.sqrt(naive/RAYS);
            error=validations==1?e:error*(1-alpha)+e*alpha;
            baseline=validations==1?b:baseline*(1-alpha)+b*alpha;
        }
        if(learning&&!heldout&&Float.isFinite(livedReward)){
            int base=action*VALUES;
            float td=bound(livedReward-value(before,action,rememberedAhead),-1f,1f);
            for(int j=0;j<RAYS;j++)
                values[base+j]=bound(values[base+j]+VALUE_LR*td*before[j],-3,3);
            values[base+RAYS]=bound(values[base+RAYS]+VALUE_LR*td,-3,3);
            values[base+RAYS+1]=bound(values[base+RAYS+1]+
                VALUE_LR*td*rememberedAhead,-3,3);
        }
        transitions++;pending=-1;
    }
    public int parameterCount(){return PARAMETERS;}

    /** Atomic own-module serial: learned weights + current-life map only;
     * no old brain/scene key is touched. */
    public String encode(){
        StringBuilder b=new StringBuilder("sp1|").append(transitions).append('|')
            .append(validations).append('|').append(choiceInfluences).append('|')
            .append(sweeps).append('|')
            .append(Float.isFinite(error)?error:-1f).append('|')
            .append(Float.isFinite(baseline)?baseline:-1f).append('|')
            .append(odometryX).append('|').append(odometryY);
        for(float v:current)b.append('|').append(v);
        for(float v:prediction)b.append('|').append(v);
        for(float v:values)b.append('|').append(v);
        b.append('|').append(memory.size());
        for(Map.Entry<Long,Byte> e:memory.entrySet())
            b.append('|').append(Long.toString(e.getKey(),36)).append(':')
                .append(e.getValue());
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>100000||!raw.startsWith("sp1|"))return false;
        try{
            String[] p=raw.split("\\|",-1);
            int fields=9+RAYS+PARAMETERS;
            if(p.length<fields+1)return false;
            long tr=Long.parseLong(p[1]);
            int validationsNew=Integer.parseInt(p[2]),used=Integer.parseInt(p[3]),
                swept=Integer.parseInt(p[4]);
            float e=Float.parseFloat(p[5]),base=Float.parseFloat(p[6]);
            float ox=Float.parseFloat(p[7]),oy=Float.parseFloat(p[8]);
            if(tr<0||validationsNew<0||validationsNew>tr||used<0||swept<0||
               !Float.isFinite(e)||!Float.isFinite(base)||e< -1||base< -1||
               e>5||base>5||!Float.isFinite(ox)||!Float.isFinite(oy)||
               Math.abs(ox)>1e7f||Math.abs(oy)>1e7f)return false;
            float[] rays=new float[RAYS],pred=new float[prediction.length],
                    vals=new float[values.length];
            int ix=9;
            for(int j=0;j<RAYS;j++)rays[j]=Float.parseFloat(p[ix++]);
            if(!valid(rays))return false;
            for(int j=0;j<pred.length;j++){
                pred[j]=Float.parseFloat(p[ix++]);
                if(!Float.isFinite(pred[j])||Math.abs(pred[j])>2.001f)return false;
            }
            for(int j=0;j<vals.length;j++){
                vals[j]=Float.parseFloat(p[ix++]);
                if(!Float.isFinite(vals[j])||Math.abs(vals[j])>3.001f)return false;
            }
            int n=Integer.parseInt(p[ix++]);
            if(n<0||n>MAX_CELLS||p.length!=ix+n)return false;
            LinkedHashMap<Long,Byte> loaded=new LinkedHashMap<>();
            for(int j=0;j<n;j++){
                String[] e2=p[ix++].split(":",-1);
                if(e2.length!=2)return false;
                long k=Long.parseLong(e2[0],36);
                byte v=Byte.parseByte(e2[1]);
                if(v< -3||v>3||loaded.containsKey(k))return false;
                // Repair LEGACY sp1 snapshots containing neutral zero
                // entries generated by putCell() in older OTA builds.
                // All long-term predictors/value weights load intact.
                // Store only non-neutral cells in the canonical new map.
                if(v!=0)loaded.put(k,v);
            }
            System.arraycopy(rays,0,current,0,RAYS);
            System.arraycopy(pred,0,prediction,0,pred.length);
            System.arraycopy(vals,0,values,0,vals.length);
            memory.clear();memory.putAll(loaded);
            transitions=tr;validations=validationsNew;choiceInfluences=used;
            sweeps=swept;error=e<0?Float.NaN:e;
            baseline=base<0?Float.NaN:base;
            odometryX=ox;odometryY=oy;
            lastBodyX=lastBodyY=Float.NaN;
            pending=-1;return true;
        }catch(RuntimeException invalid){return false;}
    }
    public static void main(String[] args){
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        MotorBrain.Body body=new MotorBrain.Body();
        SpatialPerception map=new SpatialPerception();
        float[] empty=sense(body.cx,body.cy,null,objects);
        for(float d:empty)require(d==1f,"Empty world should show open space");
        UnknownWorld.Entity disk=new UnknownWorld.Entity(body.cx+80,body.cy,0,2);
        objects.add(disk);
        float[] seen=sense(body.cx,body.cy,null,objects);
        require(seen[0]<1f&&seen[8]==1f,"Visible obstacle must affect only the correct angular sector");
        map.observeEnvironment(body,null,objects);
        require(map.mappedCells()>0&&map.blockedCells()>0&&map.freeCells()>0,
            "Line of sight must create observed occupied/free memory");
        int oldCells=map.mappedCells();
        require(oldCells<=MAX_CELLS,"Map bounds exceeded");
        // No script and no influence while new; held-out real transitions
        // must show better predictions to enable any advice.
        require(map.confidence()==0&&map.choiceBias(0)==0,
            "Virgin agent had a hardcoded preference");
        float[] before=new float[RAYS],after=new float[RAYS];
        Arrays.fill(before,.85f);Arrays.fill(after,.45f);
        for(int i=0;i<900;i++){
            map.feed(before);map.chosen(1);
            map.feed(after);map.consequence(1,.45f,i%6!=0,i%6==0);
        }
        require(map.validations==150&&map.confidence()>0,
            "Prediction did not earn held-out confidence");
        require(map.choiceBias(1)!=0,
            "Verified learned spatial experience never reached the policy");
        String saved=map.encode();
        SpatialPerception restored=new SpatialPerception();
        require(restored.restore(saved)&&restored.encode().equals(saved),
            "Spatial knowledge/weights not persistent after restart");
        int retained=restored.mappedCells();
        restored.resetLife();
        require(restored.mappedCells()==0&&restored.transitions==map.transitions,
            "Death failed to clear life-local geometry or erased long-term learning");
        require(!new SpatialPerception().restore(saved.replaceFirst("sp1","sp0")),
            "Corrupted map format was accepted");
        // A real older game wrote :0 cells but its own loader rejected them.
        // Verify the now valid migration preserves every predictor coefficient
        // and accumulated validation, rather than silently resetting to zero.
        String legacy= saved+"|"; // Insert exactly one legacy neutral cell.
        int declared=9+RAYS+PARAMETERS;
        String[] tokens=saved.split("\\|",-1);
        int occupied=Integer.parseInt(tokens[declared]);
        tokens[declared]=Integer.toString(occupied+1);
        legacy=String.join("|",tokens)+
            "|"+Long.toString(key(76543,-23421),36)+":0";
        SpatialPerception recovered=new SpatialPerception();
        require(recovered.restore(legacy)&&
                recovered.transitions==map.transitions&&
                recovered.validations==map.validations&&
                recovered.mappedCells()==map.mappedCells()&&
                recovered.encode().equals(saved),
            "Legacy :0 checkpoint reset learned spatial weights");
        SpatialPerception damaged=new SpatialPerception();
        require(!damaged.restore(legacy.replaceFirst(":0$",":99")),
            "Truly corrupt occupancy evidence was accepted");
        require(map.parameterCount()==PARAMETERS&&PARAMETERS>1000,
            "Learned spatial predictor not fully initialized");
        System.out.println(String.format(Locale.US,
            "ZERO-0110 pass: 16 occluding rays, bounded learned occupancy, %d memories, 150 held-out, earned confidence %.3f, persistent weights and life reset",retained,map.confidence()));
    }
}
