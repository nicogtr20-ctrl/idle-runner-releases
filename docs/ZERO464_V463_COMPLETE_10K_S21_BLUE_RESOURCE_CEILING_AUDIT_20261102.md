# ZERO464 — Original saved S21 finite blue availability and V463 10k Android audit

2026-11-02. Branch: lab/zero464-original-s21-resource-ceiling-audit. Based exactly on V463 2b70ab5496950de9726ad1e6a0ba02041ac97795. Research only: no APK, OTA, merge, production behavior change or user save edits.

## V463 original Android four-gate results, same SHA
- S21 41355640890: FAIL, 10004 real decisions from 33312, signed 418.065, gross 3254.82184, 12.8445%, 4 new blue, 7/10 >=10%, inverse windows 4 and 9, 2834 cortex updates.
- Wild 41355640937: FAIL, 10002 real decisions, signed 722.10625, gross 3997.52336, 18.0638%, 44 red / 4 blue, first 5000 red 19, last 5000 red 25, 5079 cortex updates.
- Regressions 41355640835: PASS, including the four V319 tests after correcting the stale CI assertion.
- S21 33312 / 512 synapse / V356 CRC 41355640817: PASS.
- V462 original S21 41354518251 and wild 41354518230: same 12.8445%, 4 blue, 44 red, 19→25. V461 wild retry 41350332990: 44 red/4 blue, 18.0694%.

## Ten real S21 windows (bucket: signed/gross; ratio; blue)
1: 58.83582/419.00461; .140418; 0
2: 110.29902/618.12589; .178441; 1
3: 45.54979/227.49985; .200219; 0
4: -48.09434/334.54662; -.143760; 1 (inverse)
5: 71.63014/281.03191; .254883; 0
6: 60.52912/178.89305; .338354; 0
7: 7.29806/440.64977; .016562; 1
8: 50.63037/165.82342; .305327; 0
9: -40.02255/311.36950; -.128537; 1 (inverse)
10: 101.40972/277.76724; .365089; 0

## Ten real wild windows (bucket: signed/gross; ratio; blue/red)
1: 26.32270/391.96997; .067155; 3/0
2: 76.85257/360.19174; .213366; 0/0
3: 64.69965/361.20814; .179120; 0/0
4: 53.55827/415.99357; .128748; 0/10
5: 19.25567/402.67495; .047819; 1/9
6: 75.08127/373.22294; .201170; 0/0
7: 90.63784/412.71469; .219614; 0/5
8: 74.82637/367.82026; .203432; 0/5
9: 78.57837/367.00537; .214107; 0/5
10: 162.39060/544.41888; .298282; 0/10

## Newly confirmed S21 test-feasibility defect
The original user-owned 2026-10-25 telemetry part-002.json currentZero.training_room_1_scene_v379 contains 99 entities: 94 walls, 5 blue. One blue at (60,36) is already consumed=true with cooldown 1000000000. The other four blue are at (-96,0), (-48,-36), (36,-60), (60,-24), and ready. UnknownWorld.Entity.ready() requires !consumed; blue touch() sets consumed and infinite cooldown. Therefore there are only FOUR new pickups available in the exact saved room without a genuine death and room regeneration. The precommitted >=5 new blue criterion is not achievable in one no-death continuation, regardless of locomotion. This does NOT authorize lowering it, adding blue, resetting the original brain or forcing death. GameActivity.resetEpisode() can regenerate the room after actual death, which must be explicitly measured rather than silently credited.

V464 single change is read-only Android test instrumentation: count ready/consumed blue in the untouched fixture and record original physical life serial at start/end of the 10000 real decisions. The four existing acceptance gates remain unchanged. It is NOT a locomotion fix.

## Wild collision evidence
Of 44 actual red contacts: 40 have PRE-impact momentum toward red, 19 have issued intention dot >.3 toward red, 28 have zero nonzero optical-preference candidates, 30 have zero motor-scored candidates, 28 are blocked by the V353 score margin. Offered actor candidates: 40 times seven, twice five, twice three. V371 has zero verified directions; V377 three reliable banks. This supports an unresolved motor/optical causal evidence bottleneck, not a safe preset heading.

## Historical original V421-V423 jobs verified
V421 S21 41149391000 17.1723%, 8/10, 2 inverse, 4 blue; wild 41149393560 87 red/3 blue, red 35→52. Regressions 41149404217 and CRC 41149399737 PASS.
V422 S21 41149758090 11.2881%, 7/10, 2 inverse, 4 blue; wild 41149762948 52 red/5 blue, red 19→33. Regressions 41149771615 and CRC 41149767313 PASS.
V423 S21 41149983685 10.0486%, 7/10, 2 inverse, 4 blue; wild 41149987734 26 red/4 blue, red 14→12. Regressions 41149995431 and CRC 41149992279 PASS.

## Approval
BLOCKED. New V464 Android original S21/wild/regression/CRC tests must be inspected on the exact same commit. No APK, OTA or merge. Original 33312 S21, 512 cortex weights, V356 CRC, CH2 V319, 194 senses, original 170-body solver, 12 saved slots remain untouched.