# ZERO OTA161 — Audit fisica e controllo corticale del simbionte
15 ottobre 2026. **CANDIDATO, NON DISTRIBUITO FINCHÉ QA + APK FIRMATA NON PASSANO.** Branch `fix/zero161-symbiote-physics-capability`, base OTA160 `feature/zero22-active-recurrent-ota117`. Conservare cervello 2.2, rete ricorrente, neurogenesi, memorie storiche, 12 slot, firma e package `com.overmind.zero.ota`.

## Richiesta reale e diagnosi
L'utente osserva nel video Android `1000107558.mp4` un organismo spesso fusiforme che oscilla/pivota pur deformandosi, raramente macchia con più appendici. HUD: grande percorso cumulativo rispetto allo spostamento netto; la telecamera segue il baricentro, quindi il video non quantifica da solo la traslazione nel mondo. L'audit del codice rivela che la corteccia **condivide gli stessi 84 pesi fra 16 settori**: l'unico segnale spaziale semplice correlato alla direzione è `cos(a-goal),sin(a-goal)`; ogni intervallo ricalcola 64 spinte e rumore. OTA160 apprende la durata dell'azione astratta, NON garantisce identica contrazione muscolare.

## Test fisico obbligatorio con elettrodi SOLO DI LABORATORIO
`Zero161PhysicalCapabilityRegression` esegue la VERA classe di fisica `SymbioteMatterV1` senza codice Android né ricompensa del cervello:
- Corpo quieto per 8 secondi: deriva globale, massimo salto per step, integrità, conservazione area, contorno non incrociato.
- Tre settori 0/5/10 stimolati con forze normali reali: tre picchi locali superiori alle valli, vera sagoma 96 punti e volume conservato, non un rendering prefabbricato.
- Una delle tre protrusioni volontariamente contratta con gli altri comandi mantenuti: variazione misurabile del suo raggio reale.
- Crawl bidirezionale con segnali di prova alternati: percorso cumulativo, avanzamento netto, rapporto; NESSUN generatore del ritmo incluso nell'app.
- TUTTI i vecchi regression sulla fisica materiale, membrane, collisioni e cervello restano gates.

**Evidenza su originale OTA160** (Actions `38772620171`): quietNet .033U / quietMaxStep .027U, 3 protrusioni prodotte realmente (picchi 87.69/82.22/87.55U, valli 32.24/28.75/17.56U), locomozione prova ~13.73U su 16s, percorso gross ~152.06U. Dunque **le tre appendici sono possibili in fisica**: la loro assenza nel comportamento autonomo NON prova un blocco strutturale permanente.

Il primo test di retrazione 0.96→1.0 era metodologicamente sbagliato (incremento quasi nullo). La controprova `extend=-.92` su un solo tip conferma in OTA160 quasi nessuna retrazione (delta tip ~-.20U), mentre il forte attrito da contrazione `traction=.95, drag≈.2/step` frena il materiale che vorrebbe ritirarsi. Abbassare ovunque l'attrito ha ottenuto ~-3.08U di retrazione ma peggiorato lo spostamento a 3.41U: fix globale SCARTATO. Una frizione anisotropa costante retraeva -2.84U ma spostamento 6.29U: NON dichiarare miglioramento. L'esperimento successivo libera radialmente SOLO una protrusione realmente estesa che sta ricevendo il comando volontario di contrazione, lasciando la presa ordinaria intatta. La metrica netto deve essere confrontata con i 13.73U baseline identici, non solo con una soglia permissiva.

## Motore neurale reale aggiornato
`SymbioteMotorHeadV1` conserva integralmente i vecchi **84 pesi** del formato `smh1` senza toccare dimensioni, pesi del 2.2, reward core o decision selector. Nuovo `LOCAL_KEY=symbiote_cortex_local_v161`: **320 pesi regionali opzionali inizializzati TUTTI A ZERO**, ciascuno per 16 settori × 4 comandi × 5 letture (bias, forma locale, ottica locale, due armoniche angolari terze). Una base spaziale permette rappresentare più lobuli ma NON impone tre arti, un obiettivo, un lato o un'andatura. Pesi locali imparati SOLO dal medesimo REINFORCE su energia/danno/costo muscolare reali, heldout isolato, nessuna ricompensa positiva per la distanza, nessun target cibo nascosto. Controlli nuovi smussati nel tempo SOLO se la stessa azione 2.2 persiste, nuove azioni applicate immediatamente. Save/restore opzionale atomico, 12 slot preservati, reset MEMBRANE include nuova chiave, reset altri domini la preserva. L'atlante Corpo/Parametri mostra read-only vecchi 84 e nuovi 320, veri valori/salvataggio/contatori.

## Gate di accettazione
1. `Zero161PhysicalCapabilityRegression`: quiete, tre protrusioni reali, contrazione indipendente, avanzamento netto, integrità topologica e massa; confronto matched originale.
2. `Zero161CortexRegression`: attivazione settore senza perdita altrove, smooth action, legacy 84 byte/pesi persistiti, nuovi 320 restore opzionale/corrotto, heldout escluso, training da vera conseguenza e morte senza perdita.
3. Pre-release JVM + invarianti storici + `assembleDebug` PASS; publisher signer: pin SHA1 Git per OGNI sorgente modificato, nuovo test, tutti i gates storici, release firmata e manifest OTA161.
4. Dopo installazione Galaxy S21: 25 vite pre vs 25 post, distanza NETTO/VIA, rumore dei comandi, varietà della sagoma rilevata fisicamente, tasso blu/rosso a pari minuti; distinguere apprendimento insufficiente da problema codice. Il successo del test a elettrodi NON implica competenza autonoma.

**Nessun reset del cervello e nessuna simulazione di movimento verso energia.** Scrivere lo stato finale qui e in HANDOFF solo DOPO il vero QA.