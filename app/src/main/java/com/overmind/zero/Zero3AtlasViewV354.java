package com.overmind.zero;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.util.Locale;

/** Canvas projection of actual neural units. Layout is illustrative; node/edge data are real. */
public final class Zero3AtlasViewV354 extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private Zero3NeuralGraphV354 graph;
    private float yaw=.3f,pitch=.15f,zoom=1,lastX,lastY,panX,panY;
    private int filter=-1,selected=-1;private boolean dragged;
    private float[] sx=new float[0],sy=new float[0];
    private final ScaleGestureDetector scale;
    public Zero3AtlasViewV354(Context c){super(c);setContentDescription("Atlante dei neuroni reali. Trascina per ruotare, pizzica per zoom, tocca un nodo o collegamento per i pesi.");
        scale=new ScaleGestureDetector(c,new ScaleGestureDetector.SimpleOnScaleGestureListener(){@Override public boolean onScale(ScaleGestureDetector d){zoom=Math.max(.3f,Math.min(4,zoom*d.getScaleFactor()));invalidate();return true;}});}
    public void setGraph(Zero3NeuralGraphV354 g){graph=g;if(g!=null&&sx.length!=g.nodes.length){sx=new float[g.nodes.length];sy=new float[sx.length];}invalidate();}
    public void filter(int f){filter=f;selected=-1;invalidate();}
    private float dp(float value){return value*getResources().getDisplayMetrics().density;}
    private boolean visible(int id){return filter<0||graph.nodes[id].module==filter;}
    @Override protected void onDraw(Canvas c){super.onDraw(c);c.drawColor(0xff070e1c);if(graph==null){label(c,"In attesa del primo snapshot neurale",dp(6),dp(24),0xffb8cfdd);return;}
        float unit=Math.min(getWidth(),getHeight())*.14f*zoom;
        for(Zero3NeuralGraphV354.Node n:graph.nodes){
            float angle=n.index*2.399963f,r=.15f+.08f*(float)Math.sqrt(n.index+1);
            float x=(n.module%4-1.5f)*1.35f+r*(float)Math.cos(angle),y=(n.module/4-1)*1.2f+r*(float)Math.sin(angle),z=(n.index%7-3)*.08f;
            float xx=x*(float)Math.cos(yaw)-z*(float)Math.sin(yaw),zz=x*(float)Math.sin(yaw)+z*(float)Math.cos(yaw);
            float yy=y*(float)Math.cos(pitch)-zz*(float)Math.sin(pitch),depth=y*(float)Math.sin(pitch)+zz*(float)Math.cos(pitch);
            float perspective=3/(3+depth*.15f);sx[n.id]=getWidth()/2f+panX+xx*unit*perspective;sy[n.id]=getHeight()*.45f+panY+yy*unit*perspective;
        }
        p.setStrokeWidth(1);
        for(Zero3NeuralGraphV354.Edge e:graph.edges)if(e.source>=0&&visible(e.source)&&visible(e.target)){
            p.setColor(e.weight>=0?0x5551d4dc:0x55ac7cff);if(e.source==e.target){p.setStyle(Paint.Style.STROKE);c.drawCircle(sx[e.source]+5,sy[e.source]-5,7,p);p.setStyle(Paint.Style.FILL);}else c.drawLine(sx[e.source],sy[e.source],sx[e.target],sy[e.target],p);
        }
        for(Zero3NeuralGraphV354.Node n:graph.nodes)if(visible(n.id)){
            float activation=Float.isFinite(n.activity)?Math.min(1,Math.abs(n.activity)):0;
            p.setColor(n.id==selected?0xffffffff:Float.isFinite(n.activity)?Color.rgb(65+(int)(activation*120),145+(int)(activation*90),205):0xff718096);
            c.drawCircle(sx[n.id],sy[n.id],n.id==selected?8:4+activation*2,p);
        }
        label(c,graph.nodes.length+" unità reali · "+graph.actualSynapses+" pesi/connessioni",dp(6),dp(24),0xffdff8ff);
        label(c,"Peso≥0,04 · max140/modulo · esclusi "+graph.omittedEdges,dp(6),dp(46),0xff96afc2);
        if(selected>=0&&selected<graph.nodes.length){Zero3NeuralGraphV354.Node n=graph.nodes[selected];
            label(c,"#"+n.id+" "+n.name+" · "+graph.modules[n.module],dp(6),getHeight()-dp(70),0xff8ee8ef);
            label(c,"Ingressi "+n.inputs+" · norma matrice locale "+String.format(Locale.ITALY,"%.4f",n.norm),dp(6),getHeight()-dp(46),0xffb8cfdd);
            label(c,"Attività: "+(Float.isFinite(n.activity)?String.format(Locale.ITALY,"%.4f",n.activity):"non acquisita per questa unità"),dp(6),getHeight()-dp(22),0xffb8cfdd);
        }
    }
    private void label(Canvas c,String text,float x,float y,int color){p.setColor(color);p.setTextSize(12*getResources().getDisplayMetrics().scaledDensity);c.drawText(text,x,y,p);}
    @Override public boolean onTouchEvent(MotionEvent e){scale.onTouchEvent(e);
        if(e.getActionMasked()==MotionEvent.ACTION_DOWN){lastX=e.getX();lastY=e.getY();dragged=false;getParent().requestDisallowInterceptTouchEvent(true);return true;}
        if(e.getActionMasked()==MotionEvent.ACTION_MOVE){float dx=e.getX()-lastX,dy=e.getY()-lastY;if(Math.abs(dx)+Math.abs(dy)>3)dragged=true;
            if(!scale.isInProgress()){if(e.getPointerCount()>1){panX+=dx;panY+=dy;}else{yaw+=dx*.008f;pitch+=dy*.008f;}}lastX=e.getX();lastY=e.getY();invalidate();return true;}
        if(e.getActionMasked()==MotionEvent.ACTION_UP){getParent().requestDisallowInterceptTouchEvent(false);if(!dragged&&graph!=null){selected=-1;float best=30*30;
            for(Zero3NeuralGraphV354.Node n:graph.nodes)if(visible(n.id)){float d=(sx[n.id]-e.getX())*(sx[n.id]-e.getX())+(sy[n.id]-e.getY())*(sy[n.id]-e.getY());if(d<best){selected=n.id;best=d;}}
            
            if(selected>=0){Zero3NeuralGraphV354.Node node=graph.nodes[selected];StringBuilder weights=new StringBuilder("Unità "+node.id+" · "+graph.modules[node.module]+"\nIngressi "+node.inputs+" (bias incluso)\n");
                for(int i=0;i<node.parameters.length;i++)weights.append("\n").append(node.module==0?new String[]{"Z","R","N"}[i/node.inputs]+" ":"").append(i%node.inputs).append(": ").append(String.format(Locale.ITALY,"%.6f",node.parameters[i]));
                new android.app.AlertDialog.Builder(getContext()).setTitle(node.name).setMessage(weights.toString()).setPositiveButton("CHIUDI",null).show();
            }else{
                Zero3NeuralGraphV354.Edge nearest=null;float bestEdge=12;
                for(Zero3NeuralGraphV354.Edge edge:graph.edges)if(edge.source>=0&&visible(edge.source)&&visible(edge.target)){
                    float ax=sx[edge.source],ay=sy[edge.source],bx=sx[edge.target],by=sy[edge.target],dx=bx-ax,dy=by-ay;
                    float length=dx*dx+dy*dy;if(length<1)continue;float t=Math.max(0,Math.min(1,((e.getX()-ax)*dx+(e.getY()-ay)*dy)/length));
                    float d=(float)Math.hypot(e.getX()-(ax+t*dx),e.getY()-(ay+t*dy));if(d<bestEdge){bestEdge=d;nearest=edge;}
                }
                if(nearest!=null)new android.app.AlertDialog.Builder(getContext()).setTitle("Connessione reale").setMessage("Origine #"+nearest.source+" → destinazione #"+nearest.target+"\nGate "+nearest.gate+"\nPeso "+nearest.weight).setPositiveButton("CHIUDI",null).show();
            }
            performClick();invalidate();}return true;}return true;
    }
    @Override public boolean performClick(){super.performClick();return true;}
}
