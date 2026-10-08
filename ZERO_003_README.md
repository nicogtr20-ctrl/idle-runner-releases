# OVERMIND ZERO v0.4 — ZERO-003: fisica e apprendimento della manipolazione

## Obiettivo
Il primo test verificabile di interazione fisica: ZERO deve spostare dischi mobili grazie alle deformazioni della propria massa, NON tramite una spinta assegnata artificialmente al contatto.

## Implementazione
- `ContactLearning.java` introduce collisioni tra ogni segmento del perimetro elastico e dischi mobili, con correzione di sovrapposizione e trasferimento di velocità. Movimento emergente dal contatto, attrito e forze muscolari.
- `UnknownWorld.Entity.touch` per gli oggetti mobili **non** modifica più le velocità.
- Un micro-controllore di 18 pesi, inizialmente tutti a zero, può alterare le contrazioni e l'aderenza dei dieci punti della massa solo vicino agli oggetti. Non predetermina zampe, presa o gesti.
- L'addestramento evolve questa rete con esperimenti fisici ripetuti in tre direzioni e conserva i pesi che ottengono un miglior punteggio; confronta i risultati su tre direzioni diverse.
- Il controllo motorio già addestrato rimane separato. Vecchi dati in `zero_motorbrain_001`, `world_knowledge_v03` e `zero_energy_v03` continuano a essere caricati. I nuovi pesi sono salvati in `hand_brain_v04` e i contatori in chiavi dedicate.
- UI: **CONFRONTA** mostra sia movimento sia manipolazione virgin vs trained. **CONTATTI FISICI** si aggiorna solo quando il corpo ha effettivamente causato movimento.

## Limiti dichiarati
Questo è un simulatore fisico semplificato. ZERO conosce ancora la direzione dello stimolo, possiede motori elementari prestabiliti e riceve fitness esterna nell'allenamento. Il processo è neuroevoluzione di una rete minuscola, non AGI, coscienza, linguaggio o vero ragionamento causale generale.
Una prestazione superiore nel training può non tradursi in vantaggio su direzioni nuove: usare il test **CONFRONTA** per verificarlo.

## Controlli
- **FERMA / ADDESTRA**: ferma/riprende l'addestramento delle due reti.
- **TEMPO**: aumenta la velocità della simulazione in primo piano.
- **CONFRONTA**: verifica anche il cervello di manipolazione su direzioni non viste.
- **AGGIORNA**: controlla Vercel e apre l'installazione Android con conferma.

## Pubblicazione
1. Commit nel branch dedicato `feature/overmind-v04-physical-learning`.
2. CI di test nella stessa branch: test JVM di MotorBrain, UnknownWorld e ContactLearning e compilazione Debug Android.
3. Solo se CI verde, integrare nel branch firmato `feature/overmind-inapp-updater`; il suo workflow firma e pubblica su Vercel lo stesso applicationId `com.overmind.zero.ota`.
4. Aggiornare changelog, handoff e verificare aggiornamento in-app sul Galaxy S21.

## Criteri di accettazione
- [ ] Contatto senza intervento magico; movimento solo quando una parte della massa tocca il disco
- [ ] Test no-contact = nessuna spinta
- [ ] Test fisica e apprendimento Java riusciti
- [ ] APK compilata su CI
- [ ] Manifest Vercel aggiornato con APK firmata
- [ ] Test del funzionamento e qualità grafica in mano all'utente
