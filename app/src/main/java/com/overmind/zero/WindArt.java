package com.overmind.zero;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import java.util.ArrayList;

/**
 * ZERO 0124: immersive 2D atmospheric field. Pure VIEW, never changes wind,
 * tissue, positions, colliders, energy, training or saved experience.
 *
 * Reproducible smooth laminar streamlines are painted in world coordinates.
 * Moving curvilinear filaments, trailing atmospheric motes, soft ribbons and
 * orientation-correct material wakes share the EXACT real storm direction.
 * No frame-random rectangles and no dense noisy grid of dotted lines.
 */
public final class WindArt {
    private WindArt(){}
    private static float fract(float v){return v-(float)Math.floor(v);}
    private static float hash(float x){
        return fract((float)Math.sin(x*127.1f+31.4f)*43758.5453f);
    }
    private static float clamp(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    private static void streamline(Canvas c,Paint p,float x,float y,float width,
                                   float bend,float direction,float alpha,
                                   boolean bright){
        Path path=new Path();
        float dx=direction*width;
        path.moveTo(x,y);
        path.cubicTo(x+dx*.24f,y-bend*.47f,
                     x+dx*.64f,y+bend*.68f,x+dx,y);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(bright?1.1f:2.15f);
        p.setColor(((int)clamp(alpha*255,0,255)<<24)|
                   (bright?0x00bcebe9:0x00467b93));
        c.drawPath(path,p);
        p.setStyle(Paint.Style.FILL);
    }
    public static void draw(Canvas canvas,Paint p,float minX,float maxX,
                            float minY,float maxY,float t,float uiTime,
                            ShelterEcology ecology,
                            ArrayList<UnknownWorld.Entity> environment){
        boolean active=ecology.storm(t),warning=ecology.warning(t);
        if(!active&&!warning)return;
        int dir=ecology.wind(t);
        float energy=active?1f:.28f;
        float width=Math.max(1f,maxX-minX),height=Math.max(1f,maxY-minY);
        float duration=active?ShelterEcology.LENGTH:11f;
        float phase=active?(t-ShelterEcology.START)%ShelterEcology.CYCLE:
                         11f-Math.min(11f,ecology.untilStorm(t));
        float onset=clamp(phase/2.8f,0,1);
        float envelope=energy*(active?Math.min(onset,clamp((duration-phase)/4f,0,1)):1f);
        float gust=.73f+.27f*(float)Math.sin(uiTime*2.4f+.65f)*
                                  (float)Math.sin(uiTime*.84f);
        envelope*=gust;
        p.setShader(null);
        p.setStyle(Paint.Style.FILL);
        // A barely visible broad drifting luminance, never a solid fog
        // hiding the body, red dangers or energies.
        float left=dir>0?minX:minX+width*.12f;
        float right=dir>0?maxX-width*.12f:maxX;
        p.setShader(new LinearGradient(left,minY,right,minY,
            dir>0?new int[]{0x154d99ab,0x04466b7b,0x00466b7b}:
                  new int[]{0x00466b7b,0x04466b7b,0x154d99ab},
            null,Shader.TileMode.CLAMP));
        p.setAlpha((int)clamp(110*envelope,0,255));
        canvas.drawRect(minX,minY,maxX,maxY,p);
        p.setShader(null);p.setAlpha(255);
        // Streamlines are sparse and animated smoothly, with a broad
        // distribution of lengths, sizes and Y lanes (NOT lattice stripes).
        for(int i=0;i<22;i++){
            float seed=hash(i*1.917f);
            float lane=fract((i*.6180339f)+hash(i+42)*.21f);
            float y=minY+lane*height+
                (float)Math.sin(uiTime*.66f+i*1.8f)*9f;
            float len=35f+110f*hash(i+8.27f);
            float velocity=54f+48f*hash(i+19f);
            float travel=fract(hash(i+7f)+uiTime*velocity/Math.max(100,width+len));
            float x=dir>0?minX-len+travel*(width+len):
                            maxX+len-travel*(width+len);
            float wave=10f*(float)Math.sin(uiTime*1.17f+i*2.15f);
            float clarity=.14f+.4f*hash(i+33f);
            streamline(canvas,p,x,y,len,wave,dir,clarity*envelope,false);
            if(i%3==0)
                streamline(canvas,p,x+dir*len*.16f,y-.9f,
                    len*.59f,wave*.7f,dir,clarity*envelope*.85f,true);
        }
        // A few tiny atmospheric motes with graceful ballistic trails.
        // These are VISUAL particles, never objects or physical impulses.
        for(int i=0;i<26;i++){
            float velocity=65f+70f*hash(i+88.42f);
            float xRatio=fract(hash(i+120.42f)+uiTime*velocity/(width+20));
            float x=dir>0?minX+width*xRatio:maxX-width*xRatio;
            float y=minY+hash(i+210.2f)*height+
                (float)Math.sin(uiTime*.8f+i*1.9f)*4f;
            float opacity=envelope*(.13f+.37f*hash(i+311f));
            p.setColor(((int)(opacity*255f)<<24)|0x009fd6de);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(.9f);
            canvas.drawLine(x-dir*3.6f,y+.8f,x+dir*2f,y,p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(((int)(opacity*255f)<<24)|0x00d5f8ed);
            canvas.drawCircle(x+dir*2f,y,.85f,p);
        }
        // Geometry-matched turbulent wake downstream of every real opaque
        // material. Rotating panels orient wakes with their actual outline.
        if(active&&environment!=null){
            for(UnknownWorld.Entity e:environment){
                if(e.consumed||e.concealed||!e.isBox())continue;
                float span=e.isConstruction()?PanelPhysics.halfLength(e):
                           SquareGripPuzzle.HALF;
                float yHalf=e.isConstruction()?
                    Math.abs((float)Math.sin(e.angle))*span+
                    Math.abs((float)Math.cos(e.angle))*PanelPhysics.HALF_THICKNESS:
                    SquareGripPuzzle.HALF;
                for(int i=-1;i<=1;i++){
                    float y=e.y+i*yHalf*.75f;
                    float x=e.x+dir*(e.isConstruction()?
                       span*Math.abs((float)Math.cos(e.angle))+
                       PanelPhysics.HALF_THICKNESS:SquareGripPuzzle.HALF);
                    streamline(canvas,p,x+dir*8f,y,52f+i*11f,
                        (float)Math.sin(uiTime*2+i)*8f,dir,
                        envelope*.22f,true);
                }
            }
        }
        p.setShader(null);p.setStrokeWidth(1f);
        p.setStyle(Paint.Style.FILL);p.setAlpha(255);
    }
}
