# ZERO 3.0 V3.5 — CREDITO MOTORIO TEMPORALE CON ASTENSIONE ADATTIVA

**Stato:** prototipo di laboratorio, non collegato al cervello autonomo ZERO 3.0, al gameplay o alla UI. Non pubblicare OTA. Ultima documentazione precedente: [V3.4](ZERO30_MOTOR_V34_DYNAMIC_STATES.md), [master](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [interfaccia richiesta](ZERO30_UI_UX_SPEC.md).

## 1. Evidenze verificate che motivano questo esperimento

- Dopo 2.000 esperienze l'apprendimento online originale non supera il cervello vergine in nessuna delle quattro direzioni testate con il corpo iniziale.
- Il piccolo modello di risposta fisica predice l'effetto immediato e ritardato di perturbazioni localizzate, anche con corpi precondizionati; **NON** predice ancora se una sequenza motoria è utile.
- 13/16 perturbazioni continuano a cambiare sensibilmente il loro esito **oltre 48 frame**, e 2/16 invertono il segno a +360 frame.
- [GitHub 38978753306](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38978753306): il test di **384 frame di credito osservato** migliora la guida ogni decisione (1.280 impulsi → 192) ma **non batte la baseline senza impulsi**. Spostamento firmato baseline +164,83965, guida continua +34,88900, guida con intervallo +163,08493; lavoro 1,248223 / 1,465575 / 1,279668 rispettivamente. 8/16 condizioni migliorano entro costo: gate motorio G3 **BLOCCATO**.

La *pausa temporale* è quindi necessaria a non attribuire prematuramente il risultato, **ma non basta** a decidere quali interventi ripetere.

## 2. ADR ZERO3-M35 — astensione basata esclusivamente su esperienze fisiche vissute

**Sorgenti:** [Zero3MotorAdaptiveGate.java](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorAdaptiveGate.java), [A/B autonomi di laboratorio](https://github.com/nicogtr20-ctrl/idle-runner-releases/blob/feature/zero30-foundation-g0-g3-20261016/app/src/main/java/com/overmind/zero/Zero3MotorAdaptiveFeedbackRegression.java).

- Il cervello/l'intento viene conservato. L'esperimento non aggiunge forze nel mondo, sequenze di passi o obiettivi nascosti.
- All'avvio della vita il corpo viene osservato **senza interventi sperimentali per 384 frame**, usando soltanto delta del centro materiale e lavoro fisico post-solver. Questa finestra è il suo riferimento passivo corrente.
- Il modello preaddestrato e calibrato può proporre un impulso, ma soltanto se la finestra motor-credit è libera. Il nuovo impulso viene registrato con frame/decisionId; dopo **384 frame reali** si valuta la finestra completa, senza episodi immaginati.
- La regola di screening **è stata scelta e congelata prima dei risultati A/B V3.5**: velocità direzionale media della finestra appena conclusa deve superare quella del riferimento osservato della vita (o essere positiva se il riferimento era negativo), e costo per frame non deve superare 1,15× il precedente riferimento (tolleranza numerica). Se fallisce, nessun nuovo impulso esplorativo per il resto della prova; il corpo continua a usare la corteccia volontaria. Se supera, aggiorna dolcemente il riferimento e permette un'altra prova soltanto a fine credito. L'algoritmo NON cambia i pesi del cervello.
- **Limite fondamentale**: una singola vita non contiene il controllo controfattuale realizzato in un'altra vita; il riferimento prima dell'impulso è un *trend osservativo*, non una misura causale separata. La regola può inibire anche azioni buone o approvare azioni cattive per inerzia e drift. Non usarla per il training actor-critic finché non c'è evidenza indipendente.

## 3. Esperimento pre-registrato — 32 casi

Tre bracci a stati iniziali identici dello stesso solver: corteccia normale senza nuovi impulsi, guida a intervallo 384 frame fisso, guida a intervallo 384 frame **con astensione adattiva**; 80 decisioni continue per ciascun braccio, 60 frame per decisione.

**SCREEN** (serve a diagnosticare questa regola, non a dichiararne la generalizzazione): 4 direzioni × pose 0°/45° × stati 0/2 = 16 casi. Sono le condizioni del precedente benchmark.

**UNSEEN_POSE_STATE** (non usati nei precedenti addestramenti di coppie vergini né nella verifica V3.4 originale del ciclo chiuso): 4 direzioni × pose 30°/60° × precondizionamenti fisici 1/3 = 16 casi. Non sono altri mondi né seed completamente indipendenti.

Nessun numero di utilità del set unseen deve cambiare **retroattivamente** la regola di feedback. Un test che termina senza errori non implica un miglioramento motorio. Successo di qualità da richiedere: vantaggio sul controllo *senza impulsi* in più condizioni, spostamento netto aggregato non inferiore, e costo non superiore; deve essere replicato su vita autonoma e più seed prima di promuovere una skill.

**Non confondere** 80 decisioni × 32 casi con 2.000 esperienze in **una sola vita**: questi sono controlli brevi replicati; il benchmark lungo a 2.000 esperienze resta un gate successivo.

## 4. Risultati effettivi

CI avviata: [GitHub Actions ZERO3 motor QA](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions), sul ramo feature, da registrare qui con numeri esatti e PASS/FAIL qualità. **G3 non superato finché non esiste evidenza positiva di vera locomozione autonoma.**
