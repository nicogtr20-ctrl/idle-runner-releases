package com.overmind.zero;
import java.util.*;
/** Observed action motion conditioned on real posture AND executed motor
 * synapses. No object coordinates or headings are given to the predictor. */
public final class ContextualNavigationV172 {
    public static final String KEY="contextual_navigation_v172";
    private static final int A=16,C=20,PER_ACTION=6;
    private static final float MATCH=.065f;
    private final ArrayList<Cell> cells=new ArrayList<>();
    private float[] context,pending;
    private int pendingAction=-1,epoch=0;
    private float[] motorReference;
    private static final class Cell{
        int action,train,hold,epoch;float x,y,error,zero;
        final float[] context;
        Cell(int action,float[] context){this.action=action;this.context=context.clone();}
    }
    public void context(MotorBrain.Body b,Morphology.Surface s,float[] weights){
        float[] pose=SymbioteMotorSkillMemoryV169.bodyContext(b,s);
        if(pose==null||weights==null||weights.length!=724){context=null;return;}
        float[] c=new float[C];System.arraycopy(pose,0,c,0,10);
        // Eight bounded signed sketches of the actual 724-synapse actuator.
        // Changing the controller changes matching, without deleting history.
        for(int i=0;i<weights.length;i++){
            if(!Float.isFinite(weights[i])){context=null;return;}
            c[10+i%8]+=weights[i]*(((i/8)&1)==0?1f:-1f)/12f;
        }
        if(motorReference==null)motorReference=weights.clone();
        else{
            double delta=0;for(int i=0;i<weights.length;i++){
                double v=weights[i]-motorReference[i];delta+=v*v;
            }
            if(Math.sqrt(delta/weights.length)>.004f){epoch++;motorReference=weights.clone();}
        }
        // Residual TRANSLATIONAL velocity is signed. Speed magnitude alone
        // aliases bodies still travelling in opposite previous directions.
        c[18]=Math.max(-2f,Math.min(2f,b.cvx/8f));
        c[19]=Math.max(-2f,Math.min(2f,b.cvy/8f));
        context=c;
    }
    private static float distance(float[] x,float[] y){
        if(x==null||y==null)return Float.POSITIVE_INFINITY;
        float d=0;for(int i=0;i<C;i++){float a=x[i]-y[i];d+=a*a;}
        return (float)Math.sqrt(d/C);
    }
    private Cell match(int a,float[] c){
        Cell best=null;float d=MATCH;
        for(Cell cell:cells)if(cell.action==a&&cell.epoch==epoch){float e=distance(cell.context,c);
            if(e<d){best=cell;d=e;}}
        return best;
    }
    public void chosen(int action){pending=context==null?null:context.clone();pendingAction=action;}
    public void cancel(){pending=null;pendingAction=-1;}
    public void consequence(int action,float dx,float dy,boolean train,boolean held,boolean clean){
        if(action!=pendingAction||pending==null){cancel();return;}
        if(!clean||!Float.isFinite(dx)||!Float.isFinite(dy)||Math.hypot(dx,dy)>120){cancel();return;}
        Cell cell=match(action,pending);
        if(cell==null&&train&&!held){
            int count=0;Cell weakest=null;
            for(Cell c:cells)if(c.action==action){count++;
                // Old controller epochs cannot guide this policy. Reclaim
                // their bounded capacity before evicting new live evidence.
                if(weakest==null||(c.epoch!=epoch&&weakest.epoch==epoch)||
                    ((c.epoch==epoch)==(weakest.epoch==epoch)&&
                     c.train+c.hold<weakest.train+weakest.hold))weakest=c;}
            if(count>=PER_ACTION)cells.remove(weakest);
            cell=new Cell(action,pending);cell.epoch=epoch;cells.add(cell);
        }
        if(cell!=null){
            if(held){
                float error=(float)Math.hypot(dx-cell.x,dy-cell.y),zero=(float)Math.hypot(dx,dy);
                float alpha=cell.hold++==0?1f:.2f;
                cell.error+=alpha*(error-cell.error);cell.zero+=alpha*(zero-cell.zero);
            }else if(train){float alpha=cell.train++==0?1f:.12f;
                cell.x+=alpha*(dx-cell.x);cell.y+=alpha*(dy-cell.y);}
        }
        cancel();
    }
    private boolean ready(Cell c){return c!=null&&c.train>=6&&c.hold>=3&&
        c.zero>=.3f&&c.error<c.zero*.95f;}
    public boolean verified(int action){return ready(match(action,context));}
    public float x(int action){Cell c=match(action,context);return ready(c)?c.x:0f;}
    public float y(int action){Cell c=match(action,context);return ready(c)?c.y:0f;}
    public int verified(){int n=0;for(int a=0;a<A;a++)if(verified(a))n++;return n;}
    public String encode(){
        StringBuilder s=new StringBuilder("cn172|").append(epoch);
        for(int i=0;i<724;i++)s.append(',').append(motorReference==null?0f:motorReference[i]);
        for(Cell c:cells){s.append('|').append(c.action).append(',').append(c.train).append(',')
            .append(c.hold).append(',').append(c.x).append(',').append(c.y).append(',')
            .append(c.error).append(',').append(c.zero).append(',').append(c.epoch);for(float f:c.context)s.append(',').append(f);}
        return s.toString();
    }
    public boolean restore(String text){
        if(text==null||text.length()>100000||!text.startsWith("cn172"))return false;
        try{String[] parts=text.split("\\|",-1);if(parts.length<2||parts.length>A*PER_ACTION+2||!parts[0].equals("cn172"))return false;
            String[] ref=parts[1].split(",",-1);if(ref.length!=725)return false;
            int newEpoch=Integer.parseInt(ref[0]);if(newEpoch<0)return false;
            float[] reference=new float[724];for(int j=0;j<724;j++){
                reference[j]=Float.parseFloat(ref[j+1]);
                if(!Float.isFinite(reference[j])||Math.abs(reference[j])>2.501f)return false;
            }
            ArrayList<Cell> parsed=new ArrayList<>();int[] counts=new int[A];
            for(int i=2;i<parts.length;i++){String[] p=parts[i].split(",",-1);
                if(p.length!=8+C)return false;
                int action=Integer.parseInt(p[0]),train=Integer.parseInt(p[1]),hold=Integer.parseInt(p[2]);
                if(action<0||action>=A||++counts[action]>PER_ACTION||train<0||hold<0||train>1000000000||hold>1000000000)return false;
                float[] values=new float[p.length-3];for(int j=3;j<p.length;j++){
                    values[j-3]=Float.parseFloat(p[j]);
                    if(!Float.isFinite(values[j-3])||(j!=7&&Math.abs(values[j-3])>250f))return false;}
                if(values[2]<0||values[3]<0)return false;
                Cell c=new Cell(action,Arrays.copyOfRange(values,5,values.length));
                c.epoch=Integer.parseInt(p[7]);if(c.epoch<0||c.epoch>newEpoch)return false;
                c.train=train;c.hold=hold;c.x=values[0];c.y=values[1];c.error=values[2];c.zero=values[3];parsed.add(c);
            }
            cells.clear();cells.addAll(parsed);epoch=newEpoch;motorReference=reference;cancel();return true;
        }catch(RuntimeException malformed){return false;}
    }
}
