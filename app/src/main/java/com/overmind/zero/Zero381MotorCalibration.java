package com.overmind.zero;

import java.util.Locale;

/**
 * ZERO381: low-level, measured response-to-command calibration only.
 *
 * This does NOT choose food, a destination or an AI action. The single V3
 * recurrent brain still chooses its own heading and executes its original
 * 64 channels. The downstream frozen peripheral may correct its interpretation
 * of that heading only after held-out, real physical response proves a
 * repeatable invertible 2D motor map.
 *
 * The origin of each datum is a CLEAN physical decision with observed
 * muscle impulse and net center displacement minus PRE momentum. Disturbed
 * world contacts, held material, mortality, and interruptions are excluded.
 */
final class Zero381MotorCalibration {
    static final String KEY="zero3_physical_response_calibration_v381";
    private final double[] gram=new double[3],xResponse=new double[2],
                           yResponse=new double[2];
    private int trained,heldout;
    private double evalModel,evalNull;
    private double lastBx,lastBy,lastDuration;
    private boolean pending,corrected;
    private float lastChosenX,lastChosenY,lastMappedX,lastMappedY;
    private final float[] answer=new float[2];

    int samples(){return trained;}
    int validations(){return heldout;}
    double validationMse(){return heldout==0?Double.NaN:evalModel/heldout;}
    double baselineMse(){return heldout==0?Double.NaN:evalNull/heldout;}
    boolean usedCorrection(){return corrected;}
    float lastPeripheralX(){return lastMappedX;}
    float lastPeripheralY(){return lastMappedY;}

    private static boolean finite(double v){return Double.isFinite(v);}
    private static float clamp(float x,float min,float max){
        return Math.max(min,Math.min(max,x));
    }
    private double[] columns(){
        // Ridge avoids fictitious inverse mappings when ZERO has only
        // moved along one axis in actual experience.
        double xx=gram[0]+.25,xy=gram[1],yy=gram[2]+.25;
        double determinant=xx*yy-xy*xy;
        if(determinant<.05)return null;
        return new double[]{
            (xResponse[0]*yy-xResponse[1]*xy)/determinant,
            (xResponse[1]*xx-xResponse[0]*xy)/determinant,
            (yResponse[0]*yy-yResponse[1]*xy)/determinant,
            (yResponse[1]*xx-yResponse[0]*xy)/determinant
        };
    }
    boolean trusted(){
        if(trained<256||heldout<64)return false;
        double[] c=columns();
        if(c==null)return false;
        double cross=c[0]*c[3]-c[1]*c[2];
        // Exclude ill-conditioned, mirrored and unidentifiable motors.
        double v0=Math.hypot(c[0],c[2]),v1=Math.hypot(c[1],c[3]);
        if(!finite(cross)||cross<.035||v0<.15||v1<.15||
           v0>25||v1>25||v0/v1>4||v1/v0>4)return false;
        // Prediction really must beat predicting NO active response.
        return evalNull>1e-6&&evalModel<evalNull*.85;
    }
    /**
     * Translate the V3 *already chosen* direction into frozen actuator motor
     * coordinates. For virgin/unverified cases this is mathematically
     * identity. Maximum angular change is 20 degrees, never a reversal.
     */
    float[] begin(float x,float y,float preVx,float preVy){
        if(!finite(x)||!finite(y)||!finite(preVx)||!finite(preVy)||
           Math.hypot(x,y)<.9)throw new IllegalArgumentException("invalid original physical intent");
        lastChosenX=x;lastChosenY=y;
        lastBx=preVx;lastBy=preVy;
        pending=true;corrected=false;lastDuration=0;
        float newX=x,newY=y;
        if(trusted()){
            double[] c=columns();
            double determinant=c[0]*c[3]-c[1]*c[2];
            if(determinant>.035){
                double rawX=(c[3]*x-c[1]*y)/determinant;
                double rawY=(-c[2]*x+c[0]*y)/determinant;
                double scale=Math.hypot(rawX,rawY);
                if(scale>.00001&&finite(scale)){
                    double wantedX=rawX/scale,wantedY=rawY/scale;
                    double cos=x*wantedX+y*wantedY;
                    // Never invert, aim somewhere else, or impose a new
                    // policy. Limit adjustment to empirically learned
                    // physical actuator cross-axis error.
                    if(cos>.30){
                        // V380's eight-way motor rounding meant tiny angles
                        // were physically discarded. V381 transduces a
                        // continuous original-V3 heading; interpolate the
                        // measured inverse response with at most 12 degrees,
                        // even when raw inverse is farther away. Never allow
                        // the inverse to redirect to the opposite hemisphere.
                        double cross=x*wantedY-y*wantedX;
                        double rawAngle=Math.atan2(cross,cos);
                        double appliedAngle=Math.max(-Math.PI/15,
                            Math.min(Math.PI/15,rawAngle*.35));
                        if(Math.abs(appliedAngle)>.0001){
                            double cs=Math.cos(appliedAngle),sn=Math.sin(appliedAngle);
                            newX=(float)(x*cs-y*sn);
                            newY=(float)(x*sn+y*cs);
                            corrected=true;
                        }
                    }
                }
            }
        }
        lastMappedX=newX;lastMappedY=newY;
        answer[0]=newX;answer[1]=newY;
        return answer; // short-lived transient buffer; no neural state
    }
    void complete(float dx,float dy,float seconds,float impulse,
                  boolean clean,boolean heldoutTrial){
        if(!pending)return;
        pending=false;
        if(!clean||!finite(dx)||!finite(dy)||!finite(seconds)||
           seconds<.2||seconds>.8||!finite(impulse)||impulse<=.5)return;
        double sx=dx-lastBx*seconds,sy=dy-lastBy*seconds;
        double denominator=Math.max(.04,seconds*seconds);
        sx/=denominator;sy/=denominator;
        if(!finite(sx)||!finite(sy)||Math.hypot(sx,sy)>20)return;
        // A/B trial reports are calibrated against the actual commands
        // the physical peripheral received, not the untouched brain intent.
        double x=lastMappedX,y=lastMappedY;
        if(heldoutTrial){
            double[] c=columns();
            if(c!=null){
                double ax=c[0]*x+c[1]*y,ay=c[2]*x+c[3]*y;
                evalModel=Math.min(1e10,evalModel+
                    Math.pow(ax-sx,2)+Math.pow(ay-sy,2));
                evalNull=Math.min(1e10,evalNull+sx*sx+sy*sy);
                heldout++;
            }
        }else{
            // Stationary/impulse observations do not count as proofs that
            // intent produced movement. Fit the verified physical response
            // only; calibrated use requires independent held-out advantage.
            double decay=trained>10000?.9999:1d;
            for(int i=0;i<3;i++)gram[i]*=decay;
            for(int i=0;i<2;i++){xResponse[i]*=decay;yResponse[i]*=decay;}
            gram[0]+=x*x;gram[1]+=x*y;gram[2]+=y*y;
            xResponse[0]+=x*sx;xResponse[1]+=y*sx;
            yResponse[0]+=x*sy;yResponse[1]+=y*sy;
            trained++;
        }
    }
    void abort(){pending=false;corrected=false;}
    String encode(){
        return String.format(Locale.US,
            "zmr381|1|%d|%d|%.12g|%.12g|%.12g|%.12g|%.12g|%.12g|%.12g|%.12g|%.12g",
            trained,heldout,evalModel,evalNull,
            gram[0],gram[1],gram[2],
            xResponse[0],xResponse[1],yResponse[0],yResponse[1]);
    }
    boolean restore(String raw){
        if(raw==null||raw.isEmpty())return true;
        try{
            String[] v=raw.split("\\|",-1);
            if(v.length!=13||!v[0].equals("zmr381")||!v[1].equals("1"))return false;
            int nt=Integer.parseInt(v[2]),nh=Integer.parseInt(v[3]);
            if(nt<0||nh<0||nt>10000000||nh>10000000)return false;
            double[] a=new double[v.length-4];
            for(int i=0;i<a.length;i++){
                a[i]=Double.parseDouble(v[i+4]);
                if(!finite(a[i])||Math.abs(a[i])>1e12)return false;
            }
            if(a[0]<0||a[1]<0||a[2]<0||a[4]<0)return false;
            trained=nt;heldout=nh;evalModel=a[0];evalNull=a[1];
            gram[0]=a[2];gram[1]=a[3];gram[2]=a[4];
            xResponse[0]=a[5];xResponse[1]=a[6];
            yResponse[0]=a[7];yResponse[1]=a[8];
            pending=false;corrected=false;
            return true;
        }catch(Exception ignored){return false;}
    }
}
