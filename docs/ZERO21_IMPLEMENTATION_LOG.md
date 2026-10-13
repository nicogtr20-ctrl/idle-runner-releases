# ZERO 2.1 — Implementation ledger and executable source contract
**Date:** 13 October 2026. **Live production baseline:** 0.11.6-ota.112, versionCode 1112 (Android package com.overmind.zero.ota). **Research branch:** feature/zero21-unified-cognition based on feature/overmind-inapp-updater. **Release status:** NEVER published from this branch. Android profile/12 slots on the actual phone have not been touched.

## Source of truth and sequence
1. User requested a big, integrated research effort replacing piecemeal disconnected modules, and detailed GitHub papers that another chat can use.
2. Read repository AGENTS, OVERMIND_START_HERE, main and stable documentation, existing experiment papers 0095–0127 and ZERO_20_INTELLIGENCE_RESEARCH. Stable versus feature/zero-20-hierarchical-intelligence are *diverged*. Do NOT wholesale merge old experimental branch.
3. Retrieved latest published metadata from main/updater/public/latest.json at start: versionName 0.11.6-ota.112, versionCode 1112, APK SHA256 35829304a1245931eef55fd494170d03ca7760a6c00f697581601b497d32ce61, URL https://overmind-zero-updates.vercel.app/apk/overmind-zero-1112-35829304a124.apk. This is the reference release, not a NEW build.
4. Isolated new development branch feature/zero21-unified-cognition from existing stable Android source. No branch rewrite, Android release action or user save modification.
5. Added executable shared evidence kernel, legacy-integrated event feeds, optional persistence, scoped reset, dedicated regression and full Android/JVM staging workflow.
6. Initial dedicated QA run 38451441375 FAILURE at javac because the copied historical CI class list omitted *existing* ObjectExperienceV20.java and its regression; no runtime source failure was established from that run. Fixed class list without dropping existing tests in commit 0e6b6bb2d30e14ac47f3f5de009dc9209bc382e5. New run 38451873419 was launched by GitHub. Recheck its final status and log; CI green must NOT be assumed from workflow existence. After fixing any reported failure, rerun on the EXACT FINAL CODE SHA.

## Actual new code, not roadmap
### UnifiedCognitionV21.java
Path: app/src/main/java/com/overmind/zero/UnifiedCognitionV21.java
Pure Java, no Android API. Public KEY unified_cognition_v21, FORMAT uc21. Belief snapshot contains actually observed reserve fraction, actual material touch and held-grip fraction, nearest sensed obstruction, uncertainty and confidence, and a *tentative* object-hypothesis ID. Four coarse contexts (metabolic reserve <0.35 or not x actual tactile/near-solid evidence) times 16 action choices, 64 bounded action-outcome tables. Stores action opportunities, measured external effects, actual positive/negative clamped-contact events, and net actual energy over a completed action interval. Conservative observational statistics, NOT causality per instance.

Main APIs: chosen(action,26senses,16observedRaysOrNull,hypothesis,worldConfidence,objectConfidence); energyContact(realDelta); consequence(afterSenses,actualMeasuredExternalMovement,train,holdout); resetLife/resumeLife; encode/restore; confidence; summary. Trained cells are never updated by heldout intervals. Confidence 0 unless at least 80 heldout, 12 genuine external-motion events, baseline nontrivial and >8% heldout Brier improvement. Even if confidence positive, this source has NO policy score API. Every chosen/consequence pair is a single observed interval. Pending action intentionally not saved or replayed. No hidden object ID, ground truth position, category-specific physics, privileged resource occupancy or pretrained expert behavior.

Serialization: UTF-8 uc21 delimited representation of bounded arrays/counters, strict parse of finite floats/doubles/counters; atomic replace on valid full document only. Missing optional key = old profile, empty virgin model. Cross-life learned cells persist; on death only transient action and unverified identity reset.

### SensorimotorMind.java
Path: app/src/main/java/com/overmind/zero/SensorimotorMind.java
Added a *transient reference* to UnifiedCognitionV21, attachUnifiedCognitionV21, energyContact hook using actual clamped contactOutcome, chosen hook placed AFTER original action scoring+random exploration+final choice, and real consequence hook after original physical observation with the SAME LearningDiagnostics.shouldHoldout split. Before choosing next action, completed interval is recorded exactly once. Calls resetLife when real death begins and resumeLife after restart. Original learned model serialization stays intact; no new score term, reward term or physical actuation, no additional RNG use for action selection. Keep the original enableZero20ExperimentalAdvice(false) device boundary.

Potential research limitation: externalDisplacement supplied is generic original physicalEffect accumulated across visible simulation; NOT per-object identity; proven grip event may be shorter than a 0.65s decision. Later genuine causal tracking must consume physical-level time-aligned evidence without faking source attribution.

### GameActivity.java
Path: app/src/main/java/com/overmind/zero/GameActivity.java
Adds isolated field UnifiedCognitionV21 unifiedCognitionV21; instantiates/restores it while rebuilding SensorimotorMind from active SharedPreferences; attaches and saves via putString(KEY, encode()). SaveProfileCodec/SaveSlotStore already capture typed preferences and named profiles atomically, so no new slot versionCode/schema rewrite. Other model keys and 96-point physical simulation untouched. On a loaded old save absent key, optional restore(false) leaves a virgin ZERO21 evidence store while prior motor/model memory is left alone.

### ResetDomains.java
Explicit WORLD reset now includes UnifiedCognitionV21.KEY. No second reset domain or overlap. An intentional WORLD reset removes shared hypotheses, whereas death/respawn never removes consolidated learning. Full new ZERO clears everything by existing behavior.

### UnifiedCognitionV21Regression.java
Path: app/src/main/java/com/overmind/zero/UnifiedCognitionV21Regression.java
Stand-alone pure JVM main. Tests 26-channel sensory context separation, real mechanical displacement NOT assumed energetic, real positive and negative contact counts, provisional hypothesis/state, no phantom outcome on resume, exact encode/restore, heldout isolation from training, virgin zero confidence and appropriate abstention, atomic rejection of malformed/NaN/truncated/incompatible serialization, cross-life memory preservation and invalid sensor fail-closed. CI additionally tests existing world, grip, physics and old policy.

### zero21-unified-qa.yml
Path: .github/workflows/zero21-unified-qa.yml
Dedicated GitHub Actions trigger on push to feature/zero21-unified-cognition and manual workflow_dispatch. Based on existing full consolidated shadow QA: builds ALL historical pure Java model sources explicitly, invokes original physics/body/grip/world tests plus new unified model and ObjectExperienceV20 regression, runs static checks proving unified observer never enters choose candidate-scoring function, verifies attaches/persistence/reset and compiles full Gradle Android assembleDebug. No signing, release, manifest or Vercel production write. Artifact upload not required. CI cost limited by concurrency with cancel-in-progress.

## What is NOT coded in this phase
- Multi-view tracking of the same anonymous obstacle, learner-side tracking confidence.
- Per-object tactile/action/energy event attribution and long causal horizons.
- Object-conditional HAND+MEMBRANE technique archives with cross-world validation.
- Complete replacement of independent model scoring with a single trusted arbiter.
- Causal uncertainty estimates robust to extremely rare hidden reward.
- Validated bounded five-step predictive use in LIVE movement.
- Cost/battery/FPS profiling on real S21 and matched long-life uplift.
- Release APK / updater button / signed production distribution.

Do NOT confuse added shared ledger with finished unified cognitive architecture. Paper ZERO21_UNIFIED_COGNITION_MASTER.md contains the engineering contract for G2–G7. The current implementation is a safe first integration and evidence layer, not an AGI completion.

## Known scientific blockers from predecessor
1. Original grip loss could coexist with accepted hand/membrane weight changes, and current grip2 contextual protection is not a fully proven durable object-conditioned skill.
2. v20 shadow world model often lost heldout calibration against unchanged-state baseline; experimental planner abstained all 5,578 times in one recorded device window. Increasing model size or lowering thresholds to force decisions is not a valid solution.
3. Image/ray hypotheses group similar silhouettes but do not ensure single-object identity, particularly near overlapping boxes.
4. Energy under covers is physically hidden/occluded except optical rim clues; even successful cover motion may not provide immediate contact feedback, requiring cautious long-delay event tracing.
5. Some older handoff headings are stale. The actual OTA112 UI code and source must be treated as current baseline.
6. A GitHub CI pass proves build/test invariants; not behavior on real device or retention across 100s of lives.

## Evidence and QA checklist for every subsequent commit
- Pin exact source SHA, branch, parent/PR and file paths.
- Name every affected API and check current code before applying patches.
- Run the dedicated ZERO21 QA after all source modifications and check every job's logs on the SAME final head. Never hide a red test by removing it.
- Execute paired real physics A/B with identical original brain, seed, objects and hidden resources; report 0/0/ties honestly.
- Evaluate new habitats and already-effective original grip/locomotion; measure no regressions from protected external-effect records.
- Verify save/load old keys, two independent slots and reset domains, including interrupted action.
- Explicitly state SHADOW_ONLY or what actual bounded bias is enabled and how it can safely be rolled back.
- Ensure no signing key/token/private user-brain raw data in paper or CI logs.
- After empirical uplift, and not before, selectively promote via PR to feature/overmind-inapp-updater with official signed publisher. Use normal AGGIORNA only after manifest SHA and Vercel APK verification. No uninstall and no forced reset.

## Handoff / next concrete development
First finish all new-source CI on exact code SHA, including Android compile and legacy tests. Then implement G2–G4 as a coherent shared event/identity/tracing pipeline (not blind new per-module score additions). Produce evaluator-side novel object rotation/occlusion fixtures and proof of identity continuity before enabling world beliefs. Only then build G5–G7 skill archive and uncertainty-gated advisor, paired headless benchmarks and long-life device QA. Document source, runs, failures, and user observation in this file and docs/ZERO21_HANDOFF_NEXT_CHAT.md.


## Verified final executable QA / pull request — 13 October 2026
- CI [38452161103](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38452161103) on tested CODE commit `7361e67f3f82c45913f7263b3b9fee1eb8f6dfee`: **SUCCESS**. Verified GitHub job 115485537613: full Java compilation; all legacy physics/learning regressions and new ObjectExperienceV20 + UnifiedCognitionV21 regression; static non-interference against 16-action scoring and physical laws; Gradle Android assembleDebug; all check steps green. All later commits on feature/zero21-unified-cognition before the draft PR were Markdown-only handoff/whitepaper/roadmap updates, not source modifications.
- Draft research PR [#39](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/39) opened against `feature/overmind-inapp-updater`. **DO NOT MERGE OR RELEASE.** There is NO new signed APK/OTA/versionCode, no user phone upgrade and no proven cognitive uplift. Signed production remains OTA112.
- A code or workflow change after this timestamp requires NEW final-head QA; a documentation-only commit does not alter the executable snapshot. A successful Android Debug build is NOT a completed signed APK.


## G2–G7 code delivered — 13 October 2026 (research only)
Added source files `ObjectContinuityV21.java`, `CompetenceMemoryV21.java`, `UnifiedArbiterV21.java`, and executable regressions `ObjectContinuityV21Regression.java`, `CompetenceMemoryV21Regression.java`, `UnifiedArbiterV21Regression.java`, `UnifiedPolicyParityV21Regression.java`. Ported selectively from diverged ZERO20 old research `Zero20CoveredEnergyBenchmark.java`, with 12 paired identical physical-cover scenarios and a new `zero21.shadowCompare` switch that disables experimental V20 actuation on both sides. Integrated read-only evidence in original control flow, never in 16-action scoring; new profiles stored as `oc21`, `cm21`, `ua21` under separate optional keys; reset WORLD/SPATIAL/MEMBRANE scopes without changes to existing saved format. `Zero20PassiveSnapshot` includes a derived privacy-safe `zero21` digest on deliberate user share; `BrainToolsActivity` displays the research-only counts.

Observed test evidence: GitHub run [38455325053](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38455325053) **FAILED** at 12-pair arena after **all Java individual model regressions and 440 policy-choice parity PASS**. Root cause was `ObjectContinuityV21.view` allocating a `taken[]` array with the previous tracked-object count while adding multiple newly observed ray clusters in the *same* sweep; `ArrayIndexOutOfBoundsException` at line 99. Fixed by bounded MAX_TRACKS occupancy array (commit `50d032951b548f8e50fec09c3f1e019c7ec07104`); added explicit two-fresh-obstacles regression (commit `d80bcac1258c2addd0583f482560380de724154a`). Earlier run 38454054909 had rejected an artificial 66U correspondence beyond the deliberate 65U model matching gate; the test was changed to a physically trackable 44U shift instead of lowering the safety gate. Additional prior defect fixed: heldout energy-contact sign evidence is now buffered and committed only after original existing validation split, preventing training-data leakage.

**Next:** inspect final CI status after `d80bcac` and subsequent documentation-only commits, check full Android build and exact 12-pair summary; do not claim green until workflow finished successfully. Exact detailed architecture/testing: [ZERO21_G2_G7_INTEGRATION.md](ZERO21_G2_G7_INTEGRATION.md). No full 105-minute phone replay, no ecological survival uplift, no live new cognitive motor policy, no OTA yet.


## 13 ottobre 2026, sera — TEST MAX su Galaxy S21, tasto SEPARATO da x1/x5/x20
Richiesta esplicita dell'utente: testare velocemente sul cellulare per risparmiare i minuti GitHub Actions, con tasto dedicato non accanto al selettore normale perché troppo facile premerlo per sbaglio. **Implementato** sul ramo sperimentale `feature/zero21-unified-cognition` commit `b432254ed924be51c563cedd8eb5e049878bc910`; quindi trasferito selettivamente al ramo Android stabile `feature/overmind-inapp-updater`, SOLO UI+accelerazione del vecchio cervello, commit `3ca84527c5b42f0977ab3efda969594bbf01fd8e` (nessun merge delle capacità non validate ZERO21). La pipeline firmata stabile è partita: GitHub Actions `OVERMIND stable OTA publisher` run [38457970983](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38457970983). **Verificare esito del run, APK firmata, manifest e disponibilità Vercel prima di dire "premi AGGIORNA"; se fallisce, OTA112 rimane invariata.**

Funzione:
- Controllo `AVVIA TEST MAX` disegnato dentro Impostazioni come pulsante separato, lontano da tempo x1/x5/x20 e con AlertDialog di conferma `AVVIA TEST`/`ANNULLA`; velocità ordinaria intatta.
- Durante il test piccolo `TEST MAX xNN • STOP TEST` fluttuante a sinistra sotto gli indicatori energetici; tasto STOP con hitbox separata. Anche Impostazioni permette arresto.
- Simulazione a passaggi FISSI `1/60` secondo simulato ciascuno; budget di lavoro massimo `38ms` a ogni disegno, massimo `1024` passaggi consecutivi; rendering meno frequente non implica perdita di frame simulati, consumo energetico e morte proporzionali al tempo SIMULATO. Velocità effettiva indicativa ricavata da secondi simulati / secondi reali; NON è x costante predeterminato.
- Temperature Android API29+ controllate circa ogni 2s; a livello MODERATE il budget scende a 18ms; a SEVERE il test viene arrestato. Restituzione del controllo all'UI a ogni burst.
- La modalità TEST è SOLO temporanea, non aggiunta al salvataggio scene/nominati; `suspend()` la disabilita; normale velocità 1/5/20 sopravvive. Il caricamento vecchio salva speed 1/5/20, senza migrazioni. Salvataggi apprendimenti esistenti continuano normali.
- Non cambia alcuna fisica, sensore, reward, rete neurale o regola di presa; aumenta quanti passi fisici eseguiamo per secondo REALE. Trattare prestazioni, surriscaldamento, efficienza e opportunità di apprendimento come DATI DA PROVARE sul vero Galaxy S21, non come benchmark già dimostrato.
- I test CI del ramo ricerca sono stati resi `workflow_dispatch` soltanto; NON lanciarli a ogni commit. Una build firmata OTA stabile è necessaria una tantum per installare sul telefono, ma consume minuti GitHub; non duplicare build soltanto per aggiornare paper Markdown sul ramo stabile.

IMPORTANTE: OTA stabile contiene soltanto TEST MAX con cervello preesistente. I moduli cognitivi `UnifiedCognitionV21`, `ObjectContinuityV21`, `CompetenceMemoryV21` e `UnifiedArbiterV21` sono sempre nel ramo ricerca, non installati attraverso la build stabile. Per raccogliere i dati ZERO21 sul cellulare occorrerà una futura build di prova firmata che includa esplicitamente il ramo sperimentale dopo gate e conservazione salvataggi, non confondere il pulsante TEST MAX con l'attivazione dei nuovi algoritmi.


## 13 ottobre 2026, 21:57 CEST — TEST MAX pubblicato ufficialmente come OTA115
**Pubblicazione REALMENTE conclusa** nel ramo stabile `feature/overmind-inapp-updater` senza mergiare ZERO21 sperimentale. L'utente ha chiesto un pulsante TEST MAX separato da TEMPO x1/x5/x20, nascosto in Impostazioni e con conferma e STOP. Il codice contenuto nella OTA stable è stato compilato e firmato nel job GitHub Actions [38458629153](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38458629153) **SUCCESS**, dopo due run falliti su asserzioni statiche obsolete (il primo pretendeva `for(int i=0;i<speed;i++)`, il secondo il SHA blob storico immutabile di `GameActivity.java`). Correzione puntuale: mantenuti i vecchi test di reale `lifeStats.tick(dt)`, aggiunto controllo del passo fisso `1/60`, limite CPU, conferma, STOP e protezione termica; SHA pin di GameActivity aggiornata alla vera impronta `edb82b9f106307ca0722aa78570d21e0e1a22ef1`, invariati tutti gli altri pin e le regole fisiche.

Manifest GitHub `main/updater/public/latest.json` realmente aggiornato: `versionName=0.11.6-ota.115`, `versionCode=1115`, `applicationId=com.overmind.zero.ota`, `sha256=71741340eaa7c8edf81be87843f3cd06776819f83f1d44aa444fa1e30e7a5fc2`, `sizeBytes=342640`, APK `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1115-71741340eaa7.apk`. Tree main contiene il binario con dimensione coerente. Vercel autenticato `get_deployment(overmind-zero-updates.vercel.app)` restituisce **production READY**, commit `e01fec62af29050e5124de4c0a730174c6bf2ad8`, alias corretto, messaggio release `Publish signed OVERMIND 0.11.6-ota.115 OTA build`. Il controllo HTTP pubblico diretto via web/container in questa sessione non era disponibile, ma manifest GitHub, deploy Vercel READY e publisher concluso confermano la pubblicazione. Sul telefono l'utente dovrà premere **AGGIORNA APP** e concedere installazione: non dichiarare installato prima della conferma Android.

TEST MAX è disponibile solo dentro Impostazioni con conferma; durante gioco appare `TEST MAX x... • STOP TEST` a sinistra, modalità non persistente (si ferma alla pausa uscita Android), step fisico fisso `1/60s` simulati, deadline ~38ms per batch, minimo 18ms in thermal moderate, stop automatico a thermal severe. *Solo il numero di substep per secondo REALE cambia.* L'effettiva velocità e temperatura sul Samsung Galaxy S21 richiedono test dell'utente.

**Attenzione critica:** questa OTA115 NON include le nuove classi ZERO21 (continuity, unified cognition, competence, arbiter). Queste rimangono soltanto nel ramo sperimentale `feature/zero21-unified-cognition`, per cui la data collection su telefono con OTA115 usa il cervello e la telemetria consolidati (precedente) e non valida ancora G2–G7 su device. Pubblicare in seguito le classi ZERO21 richiederà un'ulteriore build specifica e verifica dei salvataggi.


## 13 ottobre 2026 — ZERO 2.2 attivo (candidato OTA117)
Richiesta utente: cervello rivisto funzionante e APK, x5/x20 fluidi, TEST MAX circa x29. Nuovo ramo `feature/zero22-active-recurrent-ota117` da `fe6d7497eb5b53055b46c474dd5470028bc97f88`. Leggere **[ZERO22_ACTIVE_CORE](ZERO22_ACTIVE_CORE.md)** per architettura, contratto sensoriale, replay, competenze, ottimizzazione TEST MAX e limitazioni. `RecurrentCoreV22` guida realmente la scelta prima dell'azione; i moduli 2.1 restano osservatori compatibili. Nessun reset del profilo o slot. 71 test JVM locali PASS; release, Android build e prova S21 da verificare separatamente. Non chiamare questo sistema Dreamer completo o intelligenza generale.
