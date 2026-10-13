package com.overmind.zero;
public class Zero22LabCheck {
 public static void main(String[] args){
  BehavioralLaboratory.Profile p=new BehavioralLaboratory.Profile();p.activeCoreComparison=true;
  p.recurrent=new RecurrentCoreV22().encode();String saved=p.recurrent;
  BehavioralLaboratory.Trial a=BehavioralLaboratory.simulate(p,0,7,false,null);
  BehavioralLaboratory.Trial b=BehavioralLaboratory.simulate(p,0,7,false,null);
  if(a.gain!=b.gain||a.traveled!=b.traveled||a.actualDecisions!=b.actualDecisions)throw new AssertionError("same starting state not reproducible");
  BehavioralLaboratory.Trial active=BehavioralLaboratory.simulate(p,0,7,true,null);
  if(active==null||active.actualDecisions==0||!saved.equals(p.recurrent))throw new AssertionError("clone altered profile or did not execute");
  System.out.println("PASS independent Android-lab backend: identical control replay, active real physics, source checkpoint immutable");
 }
}
