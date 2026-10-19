> **19/10/2026 — ZERO 3.0 V363 SPEED, SOURCE-TESTED CANDIDATE (not a four-way locomotion certification):** new Galaxy S21 3.0-ota.362 true virgin GRU report 9,793 completed decisions/16 lives, blue pickups0, forward projected595.7 vs gross5927.7, 4,051 disturbed decisions. Real original gel A/B rejects muscle gain2.4→3.0 (+13.3% gross but +0.4% forward, +23.4% work) and lateral1.5 (−7.5% forward). Measured passive substrate baseline0.12→0.08 gives +30.65% gross, +33.52% forward (single orientation), +4.52% gel work; four direction lab: gains3/4, south worsens, so gait **NOT solved**. Neural voluntary true grip ceases being classified external on its own; genuine forced correction and storms remain excluded. The calibration is V3-only in original GameActivity, old V2.2 physics0.12 and V356 saved GRU/weights intact. Trial symmetric initial motor neurons 0/4 and REJECTED. [Full real physical and phone report](docs/ZERO30_V363_SPEED_TRACTION_CAUSAL_PHONE_AUDIT_20261019.md). CI original Android/V361–V363 and signed OTA still must be proven; **do not claim on-phone speed cured before new data**.

> **19/10/2026 — V361 CANDIDATA NON PUBBLICATA:** dopo telemetria OTA360 (43.018 decisioni) e osservazione utente (solo cancello da un lato, ZERO lento, ZERO gemme, nessuna cassa raggiunta), trovato mismatch vista GRU esecutivo `[frequency*8+azimuth]` vs `[azimuth*3+frequency]` e training della corteccia su CH2 sovrascritto dall'adesione V319. Correzioni isolate in `fix/zero361-live-vision-motor-causality-20261019`, checkpoint V356 invariato, opzioni pregresse V354 migrate senza reset tramite contesto legacy marcato e nuova serializzazione nested V361. Telemetria aggiunta per intenzione vs spostamento fisico. [Audit V361](docs/ZERO30_V361_PHONE_VISION_MOTOR_CAUSAL_AUDIT_20261019.md). **NO claim di miglioramento o movimento raggiunto; OTA361 è solo candidata per prova sul telefono, pubblicabile dopo CI Android verde, senza certificazione comportamentale.** Gate fisici e autonomia aperti.

> **18/10/2026 — V354, candidata integrata per prova telefonica.** [Stato corrente, componenti e prove](docs/ZERO30_V354_INTEGRATED_PHONE_CANDIDATE_20261018.md). Habitat completo, correzione fisica congiunta, opzioni/goals/valore contestuale, planning1–3 passi condizionato all’affidabilità, eredità e12 profili V3 separati, backup,10 pagine e atlante reale sono collegati al runtime. **10000 decisioni con cold restart bitwise PASS,20 vite/8000 decisioni fisiche indipendenti PASS, core e build Android in CI PASS.** UI nativa API35 e ricreazione Activity PASS; APK candidato firmato con identità originale verificato e scaricato. L’utente ha autorizzato la candidata per il telefono; questo supera il precedente divieto di produrre APK di laboratorio, NON autorizza merge nel publisher o OTA stabile. Scientificità G3–G7 e Galaxy S21 G9 ancora da dimostrare; nessuna abilità emergente o neurogenesi utile dichiarata. I blocchi V353 e precedenti sotto sono storici, non l’inventario corrente. Non ricreare componenti già integrati; consultare il rapporto e il nuovo ADR V354.

# Idle Runner · Aurora Dash

Native 2D Android endless runner with procedurally rendered graphics, variable-height jumps, reliable double-jump, coins, obstacles, and multiple evolving environments.

## Build APK

Every push to `main` triggers **Actions → Android APK** and uploads `idle-runner-debug.apk` as a workflow artifact. A GitHub release is also created with an APK attachment. Because this repository is private, sign into GitHub to download it.

## Controls

- Short tap: low jump.
- Press and hold: higher jump.
- Second tap in mid-air: double jump.
- Top-right button: pause / resume.

## Tech

Android Java (no game framework), Gradle, drawing with Canvas at device refresh rate. No external graphics dependencies. Target Android 7.0+.
