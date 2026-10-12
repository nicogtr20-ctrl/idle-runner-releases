package com.overmind.zero;

/**
 * Independent brain checkpoint recovery keys. Uses the EXISTING private
 * working profile; no extra permission, network, migration or file format.
 * SpatialPerception.restore() validates/checkpoints weights atomically.
 */
public final class SpatialCheckpoint {
    public static final String BACKUP="spatial_perception_last_good_v127";
    public static final String QUARANTINE="spatial_perception_unreadable_v127";
    private SpatialCheckpoint(){}
    public static boolean mayRecover(String raw,String fallback){
        if(raw==null||raw.isEmpty()||fallback==null||fallback.isEmpty())return false;
        SpatialPerception check=new SpatialPerception();
        return !check.restore(raw)&&check.restore(fallback);
    }
}
