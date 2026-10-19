#!/usr/bin/env python3
"""V364 anti-regression release gate for ZERO 3.0.
Measure the REAL material-solver's ability to execute ALL four independent
intents without preprogrammed gait, food targets, fake reward or neural resets.

A physical movement in the opposite direction MUST block an OTA, even if the
APK compiles, trained parameters increase, or total movement grows.
This is only a NECESSARY gait check, NOT SUFFICIENT food-avoidance validation.
"""
import re
import subprocess
import sys
from pathlib import Path
root=Path(__file__).resolve().parents[1]
p=subprocess.run([sys.executable,str(root/'tools/zero363_blind_directions_audit.py')],
                 cwd=root,capture_output=True,text=True,timeout=240)
print(p.stdout,flush=True)
if p.stderr:
    print(p.stderr,file=sys.stderr,flush=True)
if p.returncode:
    raise SystemExit("ZERO364: real physical world test could not complete: NO OTA")
pattern=re.compile(r"V363_BLIND_DIR([0-3]) trainForwardBefore=([-+\d.]+) after=([-+\d.]+) frozenForwardBefore=([-+\d.]+) after=([-+\d.]+) trainGrossRatio=([-+\d.]+) trainWorkRatio=([-+\d.]+)")
measure={}
for match in pattern.finditer(p.stdout):
    d=int(match.group(1))
    if d in measure:raise SystemExit("ZERO364 duplicated direction: NO OTA")
    measure[d]={'forward':float(match.group(3)),'frozen':float(match.group(5)),
                 'grossRatio':float(match.group(6)),'workRatio':float(match.group(7))}
if set(measure)!={0,1,2,3}:
    raise SystemExit(f"ZERO364 missing real directional outcome {measure.keys()}: NO OTA")
bad=[]
for direction,metrics in sorted(measure.items()):
    if metrics['forward'] <= 1.0:
        bad.append(f"direction {direction} cannot advance on chosen heading: signed={metrics['forward']:.4f}")
    if metrics['workRatio']>1.1:
        bad.append(f"direction {direction} too costly: workRatio={metrics['workRatio']:.4f}")
if bad:
    raise SystemExit("ZERO364 REAL-MOTOR FAILURE, OTA BLOCKED:\n- "+"\n- ".join(bad))
print("ZERO364_FOUR_WAY_TRUE_MOTOR_ADVANCE_AND_WORK=PASS")
print("LIMITATION: no claim of positive energy pickup or red avoidance without a blind living-brain survival test.")
