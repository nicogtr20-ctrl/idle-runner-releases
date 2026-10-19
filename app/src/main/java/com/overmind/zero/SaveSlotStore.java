package com.overmind.zero;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import org.json.JSONObject;
import java.util.*;

/** Manual immutable named save slots plus an automatic working autosave.
 *
 * The live profile remains in the SAME legacy SharedPreferences file, so
 * existing 0.9.x brains migrate without any destructive key changes.
 * User-created named saves are stored in a SECOND file that game resets
 * cannot touch. All switches are synchronous, atomic preference commits.
 */
public final class SaveSlotStore {
    public static final int SLOTS=12;
    public static final String WORKING="zero_motorbrain_001";
    public static final String SAVES="zero_save_slots_v100";
    public static final String PENDING_RELOAD="pending_profile_reload_v100";
    /** One-time V362 protection: explicit NEW GAME must NEVER import a
     * trained V354/V355 experimental brain from a separate preference file.
     * This marker survives process death until a fresh ZERO3 checkpoint and
     * the marker removal are committed together in GameActivity.load(). */
    public static final String FRESH_ZERO3_BIRTH="zero3_explicit_virgin_birth_v362";
    private static final String RECOVERY="emergency_previous_v100";
    private static final String PREFIX="slot_v100_";
    public final SharedPreferences working,slots;

    public static final class Entry {
        public final String name;
        public final long time;
        public final int size;
        private final byte[] snapshot;
        Entry(String name,long when,byte[] data){
            this.name=name;time=when;size=data.length;snapshot=data;
        }
    }

    public SaveSlotStore(Context c){
        working=c.getSharedPreferences(WORKING,Context.MODE_PRIVATE);
        slots=c.getSharedPreferences(SAVES,Context.MODE_PRIVATE);
    }
    private String key(int slot){
        if(slot<0||slot>=SLOTS)throw new IllegalArgumentException("Slot must be 1–12");
        return PREFIX+slot;
    }
    public static String cleanName(String name){
        if(name==null)return "";
        String t=name.trim().replace('\n',' ').replace('\r',' ');
        return t.length()>36?t.substring(0,36):t;
    }
    private Entry deserialize(String raw){
        if(raw==null)return null;
        try {
            JSONObject record=new JSONObject(raw);
            if(record.getInt("v")!=1)return null;
            String name=cleanName(record.getString("name"));
            long date=record.getLong("created");
            byte[] decoded=Base64.decode(record.getString("snapshot"),Base64.DEFAULT);
            if(name.isEmpty()||date<=0)return null;
            Map<String,Object> validated=SaveProfileCodec.decode(decoded);
            validate(validated);
            return new Entry(name,date,decoded);
        }catch(Exception error){return null;}
    }
    private static void validate(Map<String,Object> profile){
        // All legacy 0.9.x profiles saved from the actual running game carry
        // a valid 62-weight motor and a sensorimotor world model. Reject
        // corrupted/incomplete backups BEFORE changing working preferences.
        Object motor=profile.get("best");
        Object world=profile.get("sensorimotor_model_v09");
        if(!(motor instanceof String)||((String)motor).split(",",-1).length!=MotorBrain.PARAMS||
           !(world instanceof String)||((String)world).length()<100)
            throw new IllegalArgumentException("Missing essential saved brain");
        for(String n:((String)motor).split(",")){
            float x=Float.parseFloat(n);
            if(!Float.isFinite(x)||Math.abs(x)>5)throw new IllegalArgumentException("Motor corrupted");
        }
        Object scene=profile.get("physical_scene_v100");
        if(!(scene instanceof String))
            throw new IllegalArgumentException("Physical scene missing; leave the game and save again");
        try {
            JSONObject parsed=new JSONObject((String)scene);
            if(parsed.getInt("v")!=1 ||
               GameSceneSnapshot.restore(parsed.getJSONObject("physical").toString())==null)
                throw new IllegalArgumentException("Damaged physical scene");
        }catch(Exception error){throw new IllegalArgumentException("Damaged physical scene",error);}
    }
    private Map<String,Object> capture(){
        Map<String,Object> current=new HashMap<>(working.getAll());
        current.remove(ResetDomains.REQUEST);
        current.remove(PENDING_RELOAD);
        current.remove(FRESH_ZERO3_BIRTH);
        validate(current);
        return current;
    }
    private String record(String name,byte[] snapshot){
        return record(name,snapshot,System.currentTimeMillis());
    }
    private String record(String name,byte[] snapshot,long date){
        try {
            JSONObject o=new JSONObject();
            o.put("v",1);o.put("name",cleanName(name));
            o.put("created",date);
            o.put("snapshot",Base64.encodeToString(snapshot,Base64.NO_WRAP));
            return o.toString();
        }catch(Exception e){throw new IllegalStateException("Unable to create save record",e);}
    }
    public Entry get(int slot){return deserialize(slots.getString(key(slot),null));}
    public Entry recovery(){return deserialize(slots.getString(RECOVERY,null));}
    public boolean occupied(int slot){return slots.contains(key(slot));}
    public void save(int slot,String name){
        String cleaned=cleanName(name);
        if(cleaned.isEmpty())throw new IllegalArgumentException("Scegli un nome per ZERO");
        String data=record(cleaned,SaveProfileCodec.encode(capture()));
        if(!slots.edit().putString(key(slot),data).commit())
            throw new IllegalStateException("Salvataggio fallito. Non è stato sovrascritto il personaggio.");
    }
    public void rename(int slot,String name){
        String cleaned=cleanName(name);
        if(cleaned.isEmpty())throw new IllegalArgumentException("Nome non valido");
        Entry e=get(slot);
        if(e==null)throw new IllegalStateException("Salvataggio mancante o danneggiato");
        String data=record(cleaned,e.snapshot,e.time);
        if(!slots.edit().putString(key(slot),data).commit())
            throw new IllegalStateException("Impossibile rinominare lo slot");
    }
    public void delete(int slot){
        if(!slots.edit().remove(key(slot)).commit())
            throw new IllegalStateException("Eliminazione fallita");
    }
    /**
     * The current auto-save gets an explicit rescue copy BEFORE any profile
     * replacement. A broken slot or unavailable storage cannot erase it.
     */
    private void protectCurrent(){
        byte[] current=SaveProfileCodec.encode(capture());
        if(!slots.edit().putString(RECOVERY,record("ZERO precedente",current)).commit())
            throw new IllegalStateException("Backup di sicurezza fallito");
    }
    private void install(Map<String,Object> next){
        validate(next);
        SharedPreferences.Editor e=working.edit().clear();
        for(Map.Entry<String,Object> item:next.entrySet()){
            String k=item.getKey();Object v=item.getValue();
            if(k.equals(ResetDomains.REQUEST)||k.equals(PENDING_RELOAD)||
                k.equals(FRESH_ZERO3_BIRTH))continue;
            if(v instanceof String)e.putString(k,(String)v);
            else if(v instanceof Integer)e.putInt(k,(Integer)v);
            else if(v instanceof Long)e.putLong(k,(Long)v);
            else if(v instanceof Float)e.putFloat(k,(Float)v);
            else if(v instanceof Boolean)e.putBoolean(k,(Boolean)v);
            else if(v instanceof Set){
                HashSet<String> values=new HashSet<>();
                for(Object s:(Set<?>)v)values.add((String)s);
                e.putStringSet(k,values);
            }else throw new IllegalArgumentException("Unknown preference type");
        }
        // GameActivity knows to discard its old in-memory motor/hand/policy
        // before it executes another physics step or background auto-save.
        e.putBoolean(PENDING_RELOAD,true);
        if(!e.commit())throw new IllegalStateException("Impossibile caricare la partita");
    }
    public void load(int slot){
        Entry saved=get(slot);
        if(saved==null)throw new IllegalStateException("Slot vuoto o danneggiato");
        Map<String,Object> next=SaveProfileCodec.decode(saved.snapshot);
        validate(next);
        protectCurrent();
        install(next);
    }
    public void loadRecovery(){
        Entry saved=recovery();
        if(saved==null)throw new IllegalStateException("Nessun personaggio da recuperare");
        Map<String,Object> next=SaveProfileCodec.decode(saved.snapshot);
        validate(next);
        protectCurrent();
        install(next);
    }
    public void newGame(){
        protectCurrent();
        // The OTHER preferences file contains saved named characters and
        // survives. Start with absolutely no inherited weights/stats.
        if(!working.edit().clear()
            .putBoolean(SymbioteMatterV1.ENABLED,true)
            .putBoolean(PENDING_RELOAD,true)
            .putBoolean(FRESH_ZERO3_BIRTH,true).commit())
            throw new IllegalStateException("Nuova partita non avviata");
    }
}
