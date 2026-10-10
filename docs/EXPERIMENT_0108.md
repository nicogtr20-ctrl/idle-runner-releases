# ZERO Experiment 0108 — Persistenza memoria temporale
10 ottobre 2026. Versione firmata **0.10.8-ota.57**, code 1057.

## Verdetto tecnico
Le idee della 0.10.5 (statistiche su 25 vite recenti vs 25 precedenti e accumulatore storico), 0.10.6 (memoria temporale ricorrente) e 0.10.7 (sequenze di azioni apprese) sono coerenti con l'obiettivo di ZERO: apprendimento autonomo attraverso conseguenze reali, senza script per il cancello. Rimane da dimostrare il miglioramento su dispositivo con esperimenti comparabili. Uno spostamento della porta può essere un segnale positivo senza implicare l'accesso alla risorsa.

## Difetto verificato e correzione
Nella 0.10.7 `GameActivity.load()` leggeva le chiavi `temporal_working_memory_v106` e `temporal_skills_v107`, ma `GameActivity.save()` non le scriveva. La memoria temporale imparata e le abilità consolidate potevano sparire dopo un riavvio o un cambio partita.

La 0.10.8 aggiunge al salvataggio corrente:
```java
.putString("temporal_working_memory_v106",workingMemory.encode())
.putString("temporal_skills_v107",temporalSkillBank.encode())
```
Tutti gli altri sistemi rimangono: memoria episodica em3, corpo, presa, statistiche ls2, progressi e 12 slot nominati. L'identificativo `com.overmind.zero.ota` e la firma sono invariati. La memoria temporale serializzata comprende stato ricorrente e pesi, la banca skill conserva sequenze e conferme. Gli apprendimenti precedenti mai scritti dalla 0.10.6/0.10.7 non si recuperano retroattivamente se l'app era già stata chiusa.

## Test
Nuovo `TemporalPersistenceRegression.java` testa pesi temporali e skill acquisiti, snapshot con lo stesso `SaveProfileCodec` dei 12 slot, ripristino, morte e due ZERO indipendenti. Le pipeline ora impongono controlli espliciti sulle istruzioni effettive di `GameActivity.save()` e `load()`.

- QA staging: GitHub Actions **38067546249** e **38067671221**, entrambe SUCCESS.
- Pubblicazione firmata: **38067752564**, SUCCESS.
- Release: `overmind-zero-v0.10.8-ota.57`.
- VersionCode 1057, APK 150816 byte, SHA256 `50b45e37a1cc7d801408c0800000a0f957ea2c89159afbff4fb0ba22df3eddc3`.
- Vercel `https://overmind-zero-updates.vercel.app/latest.json` e `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1057-50b45e37a1cc.apk`: HTTP 200 verificato, manifest e dimensione corrispondenti.

## Prossimo test
Sul Galaxy S21, installare sopra senza disinstallare o azzerare, salvare uno ZERO, osservare transizioni e skill, chiudere e riaprire l'app, verificare che le capacità persistano e che due slot non si mescolino. La prova manuale è ancora pendente. Prima di aggiungere nuove capacità serve un confronto controllato delle prestazioni reali (cancello, energia, sopravvivenza, danni, esplorazione), senza confondere gli indicatori con intelligenza dimostrata.

## Continuità
Codice Android: `feature/overmind-inapp-updater`; ramo prova `feature/overmind-v108-temporal-persistence`; manifest/artefatti su `main`. Leggere anche `OVERMIND_START_HERE.md`, `AGENTS.md` e `docs/HANDOFF.md`.
