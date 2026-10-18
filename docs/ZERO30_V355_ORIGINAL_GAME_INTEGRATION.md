# ZERO 3.0 V355 — ripristino del gioco originale (correzione di requisito)
18 ottobre 2026 · Ramo `fix/zero30-original-game-v355` · **CANDIDATA IN VERIFICA, NON OTA**.

## Priorità assoluta: richiesta esplicita dell'utente
ZERO deve mostrare **esattamente il gioco già esistente**, non una nuova schermata «ZERO 3.0 / PROFILO». Lo sviluppo riguarda il nuovo cervello 3.0 e la sostituzione delle sole schermate/impostazioni neurali 2.2. Non alterare arbitrariamente layout, grafica, HUD, tasto TEMPO, TEST MAX, visibilità degli oggetti, corpo simbionte, logica delle risorse e ambiente fisico.

Questo vincolo **sostituisce** l'interpretazione UI di V354 («dashboard V3 come HOME / gioco nuovo») e i passaggi della vecchia specifica UI che richiedevano una nuova schermata di gioco. La specifica UI rimane valida per **Cervello, Corpo, Neuroni, Abilità, Evoluzione, Percezione, Memoria, Analisi, Impostazioni** soltanto. Non rimettere l'Activity V354 come launcher predefinito.

## Realizzazione in esame
- `GameActivity` è ancora l'Activity launcher e usa **gli originali** `renderWorld`, `renderBody`, `renderHud`, `renderDock`, `renderSettings`, gesti, colori, camera e dimensioni logiche. Il reindirizzamento automatico alla dashboard V354 è stato rimosso.
- Una build candidata usa `zero3_candidate=true` per attivare il percorso 3.0. La branch stabile e i 12 salvataggi storici 2.2 restano intatti. Il percorso 2.2 in build ordinaria rimane originale.
- `Zero3AndroidSessionV325` è l'unico owner. La GRU64 V354, cortex, adesione, 194 sensi, planner, modello predittivo, obiettivi, replay e checkpoint conservano la loro implementazione. Non viene eseguito nessun passo del controller `SensorimotorMind` o `RecurrentCoreV22` in modalità 3.0.
- `CanvasStateV355` espone le referenze reali della V354 **a sola lettura e unicamente a decisione completata sul thread UI**. Non crea uno slime animato falso né un secondo mondo guidato dal 2.2. `Zero3WorldBirthV355` genera il mondo già popolato, poi passa gli stessi oggetti fisici al runtime V354.
- Il menu originale apre soltanto pagine e impostazioni legate ai veri dati V3; `Zero3DashboardActivityV354` resta un pannello secondario cognitivo, mai il gioco principale. Navigazione visibile e pulsanti con palette scura/neon anziché grigi.
- Il profilo V3 si riprende dal checkpoint completo; soltanto uno slot realmente **VUOTO** può generare automaticamente un cervello 3.0 vergine. Stato corrotto: blocco esplicito, mai reset silenzioso. I profili V3 sono separati dai 12 profili 2.2.
- I tre contatori per colore del vecchio HUD **non sono ancora persistiti distintamente** nel ledger V354. Gli orb originali restano, con valore non disponibile (—) anziché conteggi inventati. La misurazione per colore è un requisito ancora aperto e va integrata senza cambiare le leggi fisiche.
- La funzione `salva` 2.2 e `saveScene` sono inibite soltanto quando gira V3, per non sovrascrivere i vecchi neuroni. Il checkpoint verificato V3 viene salvato in pausa/background.
- La velocità segue il tempo causale scelto dal brain V354 senza lanciare la vecchia fisica. Il controllo prestazionale su Galaxy S21 rimane necessario.

## Accettazione OBBLIGATORIA (nessuna scorciatoia)
1. La build candidata con codice 1173 e nome `3.0-candidate.355` apre la **GameActivity originale fullscreen**, non la dashboard V354. Schermata, HUD, dock, particelle, risorse, mondo e stanza visibili.
2. La nuova vita nasce con vero GRU64 attivo e nessun ciclo motore 2.2, 194 ingressi; contatori, decisioni e corpo da V3; nessuna rappresentazione sintetica.
3. Riapertura Activity e processo: stesso cervello 3.0 e checkpoint integro; niente ripartenza vergine automatica, niente due owner.
4. Impostazioni originali raggiungibili direttamente nel dock e pagine neuronali 3.0 autentiche, nessuna schermata 2.2 etichettata 3.0.
5. `gradle assembleDebug assembleDebugAndroidTest`, test nativo GameActivity/Activity recreation, test V354 completo (abilita', osservazione, backup, cervello) e scientific core fino a 10.000 decisioni: **CI da verificare**.
6. Test manuale Galaxy S21: visibilità/sensori, reattività UI x1/x5/x20/TestMAX, memoria dopo force close, termica/batteria, scena identica. Nessun merge su publish/OTA stabile prima dell'approvazione dell'utente.

## Limiti da non nascondere
La V354 ha archivio limitato a 10.000 decisioni per vita, e i vantaggi scientifici della locomozione autonoma e del planning non sono certificati. `GameActivity` usa il painter originale con lo stato fisico del runtime V354: la **parità pixel-perfect e di tutte le regole/sottotipi di mondo della 2.2 non è ancora provata**; è un gate di dispositivo e di confronto. Il ripristino estetico non equivale automaticamente a «solo cambio del cervello» finché questa parità non è controllata. Non dichiarare completato senza questi gate.

Questo documento e la richiesta attuale hanno precedenza sull'interpretazione grafica UI V354. Conservare tutto il lavoro neurale verificato della 3.0.
