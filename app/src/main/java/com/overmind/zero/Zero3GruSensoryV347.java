package com.overmind.zero;

/**
 * 26 real physically measured inputs to the EXISTING 64-GRU G4 prototype.
 * No hidden world coordinates, object class, box benefit, target or scripted
 * intent. PRE and POST only accept a completed actual V323 metabolic ledger.
 */
final class Zero3GruSensoryV347 {
    private Zero3GruSensoryV347(){}
    static float clamp(float x){return Math.max(-1f,Math.min(1f,x));}
    static float[] measured(Zero3SensoryFrameV315 frame,
                            float reserve,float priorCompletedDamage){
        if(frame==null||!frame.intact()||
           !Float.isFinite(reserve)||reserve<0f||reserve>100f||
           !Float.isFinite(priorCompletedDamage)||
           priorCompletedDamage<0f)
            throw new IllegalArgumentException(
                "V347 requires actual bounded physical sensory/vital data");
        float[] result=new float[SensorimotorMind.S];
        float[] body=Zero3AutonomousLoopV317.encode(frame);
        if(body.length!=20||result.length!=26)
            throw new IllegalStateException("unexpected GRU sensory contract");
        System.arraycopy(body,0,result,0,body.length);
        result[20]=clamp(reserve/50f-1f);
        result[21]=clamp(priorCompletedDamage/40f);
        float[] proprio=frame.globalProprioception();
        result[22]=clamp(proprio[0]);
        result[23]=clamp(proprio[1]);
        float[] actual=frame.lastExecuted64();
        float average=0f;
        if(actual!=null){
            for(float v:actual)average+=Math.abs(v);
            average/=64f;
        }
        result[24]=clamp(average);
        result[25]=clamp(frame.gelAreaError);
        for(float f:result)
            if(!Float.isFinite(f))
                throw new IllegalStateException("nonfinite real 26D sensory");
        return result;
    }
    static float[] before(Zero3SensoryFrameV315 pre,
                          Zero3CausalVitalBoundaryV344.Before vital){
        if(pre==null||vital==null||!pre.preAction||
           pre.episodeId!=vital.episode||
           pre.frameId!=vital.decision||pre.hash!=vital.sensoryHash)
            throw new IllegalStateException("V347 future/foreign PRE input");
        return measured(pre,vital.reserve,vital.previousDamage);
    }
    static float[] after(Zero3SensoryFrameV315 post,
                         Zero3CausalVitalBoundaryV344.After vital){
        if(post==null||vital==null||post.preAction||
           post.episodeId!=vital.episode||
           post.frameId!=(long)vital.completedDecision+1||
           post.hash!=vital.afterSensorHash)
            throw new IllegalStateException("V347 POST outside actual physics");
        return measured(post,vital.afterReserve,vital.actualDamage);
    }

    /** V351: one immutable provenance-preserving view of the existing senses. */
    static final class Unified {
        static final int WIDTH=194;
        static final String SOURCE="V315+V344+Senses.spectral8+firstSolidDistance(room)";
        final long episode,decision,mechanicalHash,ticks;
        final boolean preAction;
        final boolean previousCommandsAvailable;
        private final float[] inputs;
        private final long checksum;
        private Unified(Zero3SensoryFrameV315 frame,MotorBrain.Body body,
                        Morphology.Surface skin,ScarcityLab room,
                        java.util.ArrayList<UnknownWorld.Entity> objects,
                        float reserve,float previousCompletedDamage){
            if(frame==null||!frame.intact()||body==null||skin==null||room==null||
               objects==null||!Float.isFinite(reserve)||reserve<0||reserve>100||
               !Float.isFinite(previousCompletedDamage)||previousCompletedDamage<0)
                throw new IllegalArgumentException("V351 incomplete physical sensory sources");
            episode=frame.episodeId;decision=frame.frameId;
            mechanicalHash=frame.hash;ticks=frame.solverTicks;preAction=frame.preAction;
            previousCommandsAvailable=frame.lastExecutedAvailable;
            inputs=new float[WIDTH];
            System.arraycopy(Zero3AutonomousLoopV317.encode(frame),0,inputs,0,20);
            float[] spectral=SensorimotorMind.Senses.spectral8(body,skin,room,objects);
            if(spectral.length!=24)throw new IllegalStateException("V351 optical topology");
            System.arraycopy(spectral,0,inputs,20,24);
            for(int ray=0;ray<16;ray++){
                float angle=(float)(2d*Math.PI*ray/16);
                float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
                float[] eye=MaterialGeometry.eye(skin,body.cx,body.cy,dx,dy);
                float distance=SensorimotorMind.Senses.firstSolidDistance(room,objects,
                    eye[0],eye[1],dx,dy,SpatialPerception.RANGE);
                inputs[44+ray]=Float.isFinite(distance)?
                    Math.max(0f,Math.min(1f,1f-distance/SpatialPerception.RANGE)):0f;
            }
            int[] channels={Zero3SensoryFrameV315.TOUCH_SOLID,
                Zero3SensoryFrameV315.TOUCH_IMPULSE,Zero3SensoryFrameV315.GRIP_HELD,
                Zero3SensoryFrameV315.EXTENSION};
            for(int group=0;group<4;group++)for(int sector=0;sector<16;sector++){
                float value=0f;
                for(int patch=sector*6;patch<sector*6+6;patch++)
                    value+=frame.patch(patch,channels[group]);
                inputs[60+group*16+sector]=clamp(value/6f);
            }
            float[] executed=frame.lastExecuted64();
            if(executed!=null)System.arraycopy(executed,0,inputs,124,64);
            inputs[188]=reserve/50f-1f;
            inputs[189]=clamp(previousCompletedDamage/40f);
            inputs[190]=clamp(frame.gelAreaError);
            inputs[191]=clamp(frame.bodyVelX/130f);
            inputs[192]=clamp(frame.bodyVelY/130f);
            inputs[193]=previousCommandsAvailable?1f:0f;
            for(int i=0;i<inputs.length;i++){
                if(!Float.isFinite(inputs[i]))throw new IllegalStateException("V351 nonfinite sense");
                inputs[i]=clamp(inputs[i]);
            }
            checksum=hash();
        }
        float[] neural(){if(!intact())throw new IllegalStateException("V351 modified frame");return inputs.clone();}
        boolean intact(){return checksum==hash();}
        private long hash(){
            long h=mechanicalHash^episode^decision^ticks^(preAction?351L:352L);
            for(float x:inputs)h=(h^Float.floatToIntBits(x))*1099511628211L;
            return h;
        }
    }
    static Unified unifiedBefore(Zero3SensoryFrameV315 pre,
            Zero3CausalVitalBoundaryV344.Before vital,MotorBrain.Body b,
            Morphology.Surface skin,ScarcityLab room,
            java.util.ArrayList<UnknownWorld.Entity> objects){
        before(pre,vital); // existing causal guard, not a second source of physiology
        return new Unified(pre,b,skin,room,objects,vital.reserve,vital.previousDamage);
    }
    static Unified unifiedAfter(Zero3SensoryFrameV315 post,
            Zero3CausalVitalBoundaryV344.After vital,MotorBrain.Body b,
            Morphology.Surface skin,ScarcityLab room,
            java.util.ArrayList<UnknownWorld.Entity> objects){
        after(post,vital);
        return new Unified(post,b,skin,room,objects,vital.afterReserve,vital.actualDamage);
    }
}
