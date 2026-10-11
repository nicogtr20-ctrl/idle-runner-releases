# OVERMIND ZERO — Esperimento 0116.5: indizi ottici veri, vetro e luce nascosta

**Data:** 11 ottobre 2026. **Base:** 0.11.6-ota.71. **Nessuna modifica alla policy, ai pesi neurali o alla fisica.**

## Ipotesi

Prima della modifica, ZERO percepiva la cassa come oggetto fisico e segnale ottico ma il premio da +40 nascosto sotto di essa era completamente invisibile ai sensori, finché la cassa non era stata spostata. La stanza era occlusa otticamente dalle pareti; ZERO poteva raggiungerla e toccare il cancello senza aver visto il premio. La nuova ipotesi è che **l'accesso a indizi ottici fisici deboli e coerenti con una sorgente reale** aumenti l'esplorazione, i contatti utili e la ripetizione di sequenze che cambiano l'ambiente. Non implica che ZERO conosca il significato di scatola, finestra o ricompensa.

## Implementazione

1. **Bagliore perimetrale sotto la cassa 40×40.** L'oggetto energia +40 esiste già, con coordinate fisse, e rimane **non raccoglibile** finché una presa bilaterale reale non sposta il blocco di almeno 55 unità. Finché è coperto, il mondo emette dall'orlo della faccia della cassa più vicina a ZERO una versione attenuata (massimo **20%**) delle sue frequenze luminose preesistenti; si usa esattamente lo stesso sistema di occlusione per muri, cancelli e altri solidi. Nessuna sorgente energetica nuova; solo la frazione di luce che riesce a filtrare. Sullo schermo si disegna un alone ciano sottile dietro il blocco opaco, non l'energia intera.
2. **Finestra nel muro frontale superiore della stanza.** Una porzione ben definita del muro superiore dell'ingresso (coordinate relative al centro stanza `-85..-52` in y) è otticamente trasparente. La geometria `ScarcityLab` è **identica**: la finestra rimane un muro pienamente solido per nucleo, membrana e raggi di ostacolo. Solo la prova d'occlusione dei fotoni ignora la lamina in quella posizione; il cancello resta opaco e continua a bloccare linee di vista che lo attraversano. Il disegno mostra una piccola fascia vetrosa nello stesso punto.
3. Le informazioni restano nei **26 sensori originali** e nelle **24 componenti spettrali a otto settori** già esistenti. Non ci sono segnali tipo `hasHiddenReward`, `goToBox`, `glassNear`, `openDoor`, nuove etichette o nuovi canali di decisione. Frequenza ottica deriva dalla vera sorgente, ma il cervello deve apprenderne da sé le conseguenze. La cassa conserva la sua frequenza originale e può saturare uno dei canali in cui il bagliore rarefatto è presente; il segnale **nuovo e diagnosticamente più utile** è la piccola emissione nel canale associato alla fonte energetica.
4. Modalità `Senses.read(...,cueEnabled=false)` e `spectral8(...,false)` esistono per **test accoppiati** (mondo scuro vs mondo con indizi). Nel gioco reale l'osservazione normale abilita i fotoni; le altre componenti di contatto e decisione non cambiano.

## Invarianti di sicurezza e salvataggio

- Classi `MotorBrain`, `Morphology`, `ScarcityLab`, `SquareGripPuzzle`, `UnknownWorld`, `DecisionBrain`, `InstrumentalNavigation`, `ContextualMotionProbe`, `DecisionBlackbox`, `GameSceneSnapshot`, `SaveSlotStore` e `TelemetryGitHub` **byte-identiche** alla base; nessun cambiamento di forza, ricompensa, alimentazione, cooldown, numero di azioni, matrice dei pesi o reset.
- `SensorimotorMind` cambia solamente il trasporto e l'attenuazione ottica nelle funzioni sensoriali. Il confronto punteggi, l'esplorazione casuale e gli aggiornamenti delle reti Q non cambiano, ma le future esperienze e quindi le scelte possono cambiare **legittimamente** perché ZERO osserva una realtà arricchita.
- Schema degli oggetti e dei salvataggi invariato; i 12 slot nominati sono compatibili. NON disinstallare né azzerare un personaggio per provarlo.

## Validazione e limiti

- `OpticalCueRegression`: una sorgente dietro la finestra è visibile agli stessi sensori solo con fotoni abilitati; il muro rimane fisicamente opaco ai raggi di ostacolo; un cancello chiuso continua a occludere; il bagliore è rilevabile mentre il vero +40 rimane bloccato; un secondo oggetto opaco davanti alla cassa intercetta il bagliore; osservare luce non sposta la cassa né sblocca energia.
- `OpticalCues.main`: coppia sorgente-cassa e attenuazione coerenti, colore debole e nessun doppio premio.
- Suite storica Java, test di non interferenza del predittore shadow, Android assembleDebug, OTA firmata solo dopo CI.
- La promessa non è «ZERO capirà il vetro». Possibili effetti indesiderati: fissazione su una sorgente irraggiungibile, urti ripetuti contro vetri o casse, interferenza con gli apprendimenti esistenti, rischio di segnali schermati da altri oggetti. Servono molte vite reali con telemetria e confronto delle sequenze osservate. In caso di regressione, valutare un successivo esperimento ambientale abilitabile/disabilitabile, senza azzerare il cervello.

L'esperimento 0117 sul pianificatore causale rimane nel suo ramo separato e non è inserito nella versione stabile.
