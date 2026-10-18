package com.overmind.zero;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The ONLY new screen authorized by the user. The live original game pauses
 * and supplies an immutable graph from the ACTUAL active V3 neural modules.
 * This screen NEVER starts physics, a brain, a new life or a separate world.
 */
public final class Zero3OriginalAtlasActivityV356 extends Activity {
    private static volatile Zero3NeuralGraphV354 active;
    static void open(Activity game,Zero3NeuralGraphV354 real){
        if(real==null)throw new IllegalArgumentException("Missing real V3 neural graph");
        active=real;
        game.startActivity(new Intent(game,Zero3OriginalAtlasActivityV356.class));
    }
    private int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12),dp(24),dp(12),dp(16));
        root.setBackgroundColor(0xff06121c);
        TextView header=new TextView(this);
        header.setText("RETE NEURALE 3.0");
        header.setTextSize(19);header.setTextColor(0xffc8fff3);
        header.setPadding(dp(12),dp(8),dp(12),dp(10));
        root.addView(header);
        TextView guidance=new TextView(this);
        guidance.setText("Neuroni e pesi reali · Ruota, ingrandisci e tocca un nodo");
        guidance.setTextSize(12);guidance.setTextColor(0xff95b1bb);
        guidance.setPadding(dp(12),0,dp(12),dp(12));
        root.addView(guidance);
        Zero3AtlasViewV354 chart=new Zero3AtlasViewV354(this);
        chart.setGraph(active);
        root.addView(chart,new LinearLayout.LayoutParams(-1,0,1));
        Button close=new Button(this);
        close.setText("TORNA AL GIOCO");close.setAllCaps(false);
        close.setTextColor(0xffb9fff2);close.setTextSize(14);
        GradientDrawable shape=new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xff123340,0xff091925});
        shape.setCornerRadius(dp(16));shape.setStroke(dp(1),0xff508da0);
        close.setBackground(shape);
        close.setOnClickListener(v->finish());
        LinearLayout.LayoutParams footer=new LinearLayout.LayoutParams(-1,dp(62));
        footer.topMargin=dp(10);root.addView(close,footer);
        setContentView(root);
    }
}
