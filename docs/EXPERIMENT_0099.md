# OVERMIND ZERO — Esperimento 0099: regressione sistematica della presa e locomozione
**Data:** 10 ottobre 2026. **Stato:** codice 0.9.9 in sviluppo/staging, test automatizzati richiesti; conferma su Samsung Galaxy S21 pendente.

## 1. Osservazioni rigorose dell'utente
Nella versione 0.9.4 e nelle precedenti ZERO attivava regolarmente punti arancioni della membrana davanti al cancello; nella 0.9.4 riuscì a spostare il cancello per circa 111 U (soglia per libertà >115 U), senza attraversarlo. Dalla 0.9.5, ripetutamente anche nelle 0.9.6–0.9.8, l'utente riferisce che i punti arancioni non si attivano mai, movimento più lento e tendenza a muoversi nello stesso luogo. Non interpretare la perdita *simultanea* di locomozione e presa come scelta intenzionale o semplice caso senza verificare il codice.

Screenshot della **0.9.8-ota.43**, riferito dall'utente in chat:
- 1.605 TENTATIVI generici, 92.064 ESPERIENZE SENSORIALI, 92.334 DECISIONI.
- NETTO 93U, VIA 5.130U, MAX storico 1.291U, DISCHI SPOSTATI 164.
- 6 abilità motorie archiviate, 24 ricordi (+14/-10), 10.948 richiami.
- **PORTA 0 U; PRESE 268 (storico in precedenti stanze); DUE LATI 0.**
- **TOCCHI 38.259; ADESIONI 0; SENZA PRESA 551** (contatori della porta corrente, per frame, non eventi distinti).
- AZIONE 4/8, PRESA OFF al momento esatto del fermo immagine. Non prova che l'adesione sia sempre OFF.
Interpretazione onesta: la fisica registra molti tocchi ma in questa stanza zero frame nei quali `gripCommand[i] > Morphology.GRIP_THRESHOLD` durante contatto. Non esiste prova che il cancello sia «troppo difficile»: il controllore non arriva nemmeno al livello di azione fisica che ha funzionato in precedenza.

## 2. Confronto diretto fra CODICE v0.9.4 e v0.9.5
Fonte del confronto GitHub sul branch storico `feature/overmind-inapp-updater`: commit **`9b0e716cab4`** (fine 0.9.4) vs **`e5ef6752072`** (0.9.5). Il confronto contiene modifiche `EpisodicMemory.java` (nuova classe), `GameActivity.java`, `SensorimotorMind.java` e `OnlineBodyLearning.java`, **NON `ScarcityLab.java` o `Morphology.java`**. Quindi la geometria, la forza e il requisito di presa della porta non sono stati modificati dalla 0.9.4 alla 0.9.5.

Modifiche tecniche **verificate nel codice 0.9.5**:
1. Dove la 0.9.4 usava `liveBody.weights(0/1/2, ...)`, la 0.9.5 introduceva `lastAppliedMotor/Hand/Membrane=episodic.weights(...,liveBody.weights(...))` in ogni frame. La memoria poteva sostituire **tutti** i controllori fisici con pesi vecchi catturati in un episodio, anche se la postura o il contatto reale erano cambiati.
2. La memoria aggiungeva un bias alle 16 scelte motorie in `SensorimotorMind.choose`; questo può modificare la probabilità di selezionare azioni con presa (azioni 8–15) o senza presa (0–7), ma NON è ancora dimostrato quale effetto concreto abbia avuto sul Galaxy S21 dell'utente.
3. Quando un episodio era richiamato, la 0.9.5 poteva abortire le prove del corpo, riducendo occasioni di aggiornamento dei controllori. La 0.9.7 ha rimosso il replay del motore, ma **consentiva ancora il replay di configurazioni di MANO/MEMBRANA proprio quando `surface.touchSense` indicava un contatto reale**. Questo è un meccanismo plausibilmente dannoso per la presa: un ricordo avrebbe potuto applicare vecchi pesi durante la fase cruciale del contatto.
4. La 0.9.8 ha corretto parte delle regressioni motorie e la conservazione di motor skill, ma non ancora la banca delle tecniche di presa. Un corpo/manipolatore accettato nelle sessioni precedenti potrebbe aver sovrascritto competenze migliori, **non ricostruibili automaticamente** se mai archiviate.

**Diagnosi:** è una regressione architetturale plausibile, supportata da un confronto reale, NON una dimostrazione causale che *solo* il replay episodico spieghi tutti i comportamenti. La verifica finale richiede il test sulla 0.9.9 a cervello conservato e la diagnosi ON/OFF → comando → tenuta fisica.

## 3. Modifica principale 0.9.9: riconquistare indipendenza dei controller
- In `GameActivity`: motore `lastAppliedMotor=liveBody.weights(0,trainer.best)`, mano `lastAppliedHand=liveBody.weights(1,handTrainer.best)`, membrana `lastAppliedMembrane=liveBody.weights(2,shapeTrainer.best)`. **Mai più `episodic.weights()` per uno di questi tre controllori fisici.** Rimane inalterata la memoria sensoriale di episodi e scelte: un ricordo può influenzare un'azione scelta da ZERO, ma non sovrascrivere attuatori appresi.
- Ogni scelta realmente eseguita può ancora essere valutata da `OnlineBodyLearning`, senza cancellazioni di trial indotte da memoria episodica. Se la memoria fisica era fonte del bug, questa è una correzione specifica, e soprattutto testabile, non un incremento artificiale della presa.
- Il cervello delle 16 azioni rimane l'unico a poter selezionare il canale `sensorMind.adhesion()`; non si forza ON a contatto o per fame o perché il cancello è vicino.
- **Fisica invariata:** `GRIP_THRESHOLD=.28f`, `GRIP_HOLD=.075f`; occorrono comando reale sopra soglia e contatto sostenuto. Per spostare porta: due punti indipendenti su facce opposte, velocità tangenziale utile. ZERO può toccare 100.000 volte senza afferrare se non sceglie la presa o il corpo non produce il comando corretto.

## 4. Nuova memoria delle competenze di presa (GripSkillBank)
- Classe `GripSkillBank.java`, archivio fino a **5 coppie (18 parametri mano + 82 parametri membrana)** realmente usate quando la fisica registra la **tenuta mantenuta** di un punto contro la porta, NON soltanto il contatto o la scelta PRESA ON.
- Ogni coppia misura quanti frame sono stati fisicamente mantenuti, quante volte sono stati registrati **due lati contemporaneamente nello stesso frame**, e quanto la porta si è spostata in conseguenza. Le coppie equivalenti consolidano conteggi, la banca preferisce evidenza fisica forte, non premia il riconoscimento del colore arancione da solo.
- Salvataggio `grip_skills_v099` (formato `grip1`, validazione prima di ripristinare); mantiene le versioni storiche delle reti `best`, `hand_brain_v04`, `morph_brain_v07`, `sensorimotor_model_v09`, `episodic_memory_v095` e banca motore `motor_skills_v098`. **Non può ricostruire snapshot di presa della v0.9.4 che non erano stati salvati**. Con un archive vuoto non vi è recupero magico.
- Recupero di una configurazione già verificata, **solo se ZERO ha autonomamente scelto il canale di presa**, ha toccato veramente il cancello per molti frame, non ha mantenuto il contatto e non si sta svolgendo un trial mutante. Non forza l'attivazione della presa, non apre il cancello automaticamente, non sceglie meta di movimento, impone cooldown.
- Le statistiche di banco e di recupero sono disponibili in ANALISI; reset MEMBRANA include `grip_skills_v099`, non resetta gli altri domini.

## 5. Diagnostica indipendente e osservabile nel display
Nuova riga sotto i contatori classici della stanza:
- `PRESA SCELTA`: numero di **frame** in cui ZERO ha autonomamente selezionato adesione e ha realmente toccato il cancello.
- `COMANDO`: frame nei quali un punto a contatto ha emesso un `gripCommand` superiore alla soglia fisica.
- `TENUTA`: frame nei quali almeno un punto ha mantenuto la presa per `>=.075s`.
- `TECNICHE`: numero di configurazioni accettate dopo una tenuta fisica reale.

Interpretazione verificabile:
A) TOCCHI molto alti, PRESA SCELTA=0 ⇒ il cervello non usa il canale di adesione durante il contatto; fisica e attuatore non ancora imputabili.
B) PRESA SCELTA>0, COMANDO=0 ⇒ ZERO sceglie il canale, ma il comando fisico finale non supera la soglia.
C) COMANDO>0, TENUTA=0 ⇒ comanda davvero il grip ma non lo mantiene abbastanza a lungo.
D) TENUTA>0, DUE LATI=0 ⇒ ha presa monolaterale ma non sufficiente geometria.
E) DUE LATI>0, PORTA=0 ⇒ serve movimento tangenziale coordinato.
Questo consente di distinguere **decisione, neuroattuazione, contatto persistente e manipolazione geometrica**, anziché supporre che basti più tempo.

## 6. Test automatici e limiti
- JVM `GripSkillBank.main`: non archivia normale tocco, archivia tenuta vera, include bilaterale/distanza, recupera un controller realmente verificato e diverso da quello attuale, serializza/ripristina senza perdere l'archivio in caso di dati corrotti.
- JVM `ScarcityLab.main`: l'adesione su un solo lato NON muove la porta, due semplici tocchi senza comando NON aprono, vera presa coordinata muove la porta. Test dell'integrità fisica dei 96 punti, riparazione di attraversamenti impossibili.
- Regressione statica: nessun `episodic.weights(` negli attuatori del gioco, nessun `abortTrial()` dovuto a un semplice ricordo, criteri di tenuta invariati; Gradle Android + firma stabile su stesso package, controllo digest SHA.
- **Tutti i miglioramenti comportamentali su S21 sono non ancora verificati**. La prima prova deve essere aggiornamento sopra 0.9.8 senza disinstallare/reset; screenshot con nuovo flusso PRESA SCELTA → COMANDO → TENUTA e metriche in tempo equivalente x5. Se PRESA SCELTA resta 0 per migliaia di tocchi, va diagnosticata la policy Q delle 16 scelte e la memoria episodica come *bias decisionale*, non i pesi di grip.
- In assenza di archivio di vecchie prese non c'è garanzia che ZERO recuperi subito il risultato 0.9.4. Inoltre questa non è una prova di autonomia assoluta né un planner che conosce come aprire un cancello.

## 7. Handoff prossima chat
Repo `nicogtr20-ctrl/idle-runner-releases`; codice live `feature/overmind-inapp-updater`; sviluppo 0.9.9 `fix/overmind-v099-physical-grip-retention`; `main` per updater/pubblicazioni, papers. Leggere `OVERMIND_START_HERE.md`, `AGENTS.md`, `docs/HANDOFF.md`, **questo** esperimento, `docs/EXPERIMENT_0098.md`, `docs/ARCHITECTURE.md`, `docs/WHITEPAPER.md`, `docs/ROADMAP.md`, `docs/CHANGELOG.md`. Tutti i dati storici devono essere preservati; per APK firmata verificare release, `updater/public/latest.json` SHA, firma e success GitHub Actions.


### RILASCIO VERIFICATO 0.9.9-ota.46 — 10 ottobre 2026
- GitHub Actions staging #38013890946: **success**. Publisher firmato #38014277535: **success**. GitHub Release checksum verificato #38014422904: **success**.
- App **`com.overmind.zero.ota`**; versionName **`0.9.9-ota.46`**, versionCode **1046**; `OVERMIND-ZERO.apk` **104.528 byte**.
- SHA256: **`f4524305cdea4094e23c2eb12f664e21f6fe07cff6c3f3e949c0c4e035e6ec1e`**, esattamente uguale in release GitHub e `updater/public/latest.json`.
- Link esistente (NON ipotetico): `https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v0.9.9-ota.46/OVERMIND-ZERO.apk`.
- Installare direttamente sopra la vecchia APK senza disinstallare, **senza premere alcun reset**. Nessun test comportamentale sul Galaxy S21 della 0.9.9 ancora disponibile. La conferma necessaria è la catena PRESA SCELTA→COMANDO→TENUTA e la velocità/NETTO.
