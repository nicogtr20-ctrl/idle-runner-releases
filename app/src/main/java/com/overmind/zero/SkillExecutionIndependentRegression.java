package com.overmind.zero;
import java.util.Arrays;
public class SkillExecutionIndependentRegression {
 static void check(boolean b,String m){if(!b)throw new AssertionError(m);}
 public static void main(String[] args){
  CompetenceMemoryV21 bank=new CompetenceMemoryV21();
  float[] h=new float[ContactLearning.PARAMS],m=new float[Morphology.PARAMS],s=new float[26];
  Arrays.fill(h,.25f);Arrays.fill(m,.35f);s[0]=.5f;s[4]=.4f;
  for(int i=0;i<8;i++)bank.witnessed(h,m,s,true,1,.8f);
  bank.newLife();bank.witnessed(h,m,s,true,1,.8f);
  SkillExecutionV22 runner=new SkillExecutionV22();float[] baselineH=new float[h.length],baselineM=new float[m.length];
  runner.boundary(bank,s,8,0,false);
  check(Arrays.equals(runner.hand(baselineH,true,true,false),h),"consolidated hand not used");
  check(Arrays.equals(runner.membrane(baselineM,true,true,false),m),"consolidated membrane not used");
  check(runner.hand(baselineH,false,true,false)==baselineH,"used without adhesion");
  check(runner.hand(baselineH,true,false,false)==baselineH,"used without contact");
  check(runner.hand(baselineH,true,true,true)==baselineH,"overrode motor experiment");
  String archive=bank.encode();
  for(int i=0;i<3;i++)runner.boundary(bank,s,8,0,false);
  check(runner.hand(baselineH,true,true,false)==baselineH,"failed technique continued");
  check(bank.encode().equals(archive),"failed episode erased durable competence");
  check(Arrays.equals(baselineH,new float[h.length]),"incumbent weights overwritten");
  System.out.println("PASS independent closed-loop skill: contact/adhesion/trial gates, three misses abort, immutable incumbent/archive");
 }
}
