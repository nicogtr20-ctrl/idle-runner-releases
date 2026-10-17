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
}
