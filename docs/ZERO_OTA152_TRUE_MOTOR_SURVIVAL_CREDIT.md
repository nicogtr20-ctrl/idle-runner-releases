# OVERMIND ZERO OTA152 — credito neuromuscolare da conseguenze fisiche reali

**Data:** 15 ottobre 2026. Source [PR #56](https://github.com/nicogtr20-ctrl/idle-runner-releases/pull/56), branch correttivo `fix/zero152-real-motor-consequence`, base `feature/zero22-active-recurrent-ota117`. Firmed release: **da verificare con QA finale e main `updater/public/latest.json`**; non indicare firmato prima della conclusione del publisher.

## Domanda empirica dell'utente
Il nuovo ZERO appena nato sembra un serpente che ondeggia nello spazio: si deforma ma avanza poco. Vuole distinguere **fisicamente può / deve imparare / codice impedisce l'apprendimento**, non cambiamenti continui alla fisica. Il benchmark originale `ZeroLocomotionCapacityDiagnostic.java`, QA 38712289045 SUCCESS (prova *separata* non integrata), dimostra che lo **stesso corpo fisico OTA151 può** spostarsi di 295.908 world units con una decisione stabile per 40s, mentre le azioni variabili casuali spostano solo 11.175 world units. Questo indica che ondulare sul posto può essere un fenomeno di controllo inesatto, NON impedimento fisico. Non mostra competenza verso risorse nel gioco.

## Difetto software verificato
Nel percorso decisionale ATTIVO `SensorimotorMind.observe` di ZERO 2.2 veniva chiamato `symbioteHead.consequence(actualEnergySinceChoice, actualContactHarm, 0f, ...)`; in `SymbioteMotorHeadV1` il vantaggio REINFORCE risultava nullo per un neonato che non avesse ancora ottenuto un pickup/danno credibile: gradienti muscolari zero anche dopo migliaia di scelte. L'argomento di lavoro muscolare era sempre **zero**; il reale consumo ambientale non arrivava alla testa motoria. Un contatore `trained` in aumento non prova che i pesi cambino.

## Intervento OTA152: nessun premio per muoversi
1. In `GameActivity`, immediatamente attorno a ogni esecuzione FISICA `symMatter.step`, il lavoro attivo incrementale viene derivato dalla differenza `symMatter.work - workBefore`. Si contabilizza una piccola ma **REALE** spesa metabolica per muscolo `min(reserve, freshWork*8)`, sottraendola dalla medesima riserva energetica reale che governa fame e sopravvivenza e registrandola nella contabilità metabolica. La conversione lavoro → unità di energia è una costante di scala del mondo, non un guadagno o un vettore di locomozione. **Nessun cambiamento** a trazione, attrito, shape solver, viscosità, forze, presa, posizione del corpo o desiderata direzione.
2. In `SensorimotorMind`, la reale spesa muscolare viene accumulata per **azione effettivamente eseguita** in `actualMotorEnergyCostSinceChoice`, separata dal `actualAmbientLossSinceChoice` causato da esposizione a una vera tempesta. L'energia di base dovuta al semplice trascorrere del tempo **NON è attribuita ai singoli comandi muscolari** perché non dipende da essi. Contatori temporanei azzerati a nascita/ripresa/nuova decisione senza toccare i modelli salvati.
3. Il reale esito consegnato a `SymbioteMotorHeadV1.consequence` combina guadagni da risorse per contatto, danni da contatto, perdita di energia per tempesta ed effettivo carburante muscolare debitato. Gli intervalli SENZA alcun esito conservano gradiente **esattamente zero**, perfino se il contatore delle scelte cresce; nessun progresso fittizio dal solo ondeggiare. La versione di chiamata originaria a cinque argomenti rimane disponibile e il formato motor head `smh1` mantiene i 84 pesi della OTA151. I campioni heldout restano esclusi dall'addestramento.
4. Lo stesso costo reale di carburante riduce anche `lastReward` nel modello principale, evitando che l'unico cervello ricorrente venga addestrato a ignorare una risorsa realmente spesa. Non sono state aggiunte ricompense di spostamento, distanza, pseudopodi, novità visiva o contatori.

## Regressione e requisiti QA
La prova `SymbioteMotorConsequenceRegression.java` è compilata ed eseguita sia dalla suite correttiva che dal publisher firmato. Asserzioni:
- Materiale senza comando: lavoro motorio = 0.
- Contrazione concreta: lavoro motorio > 0; il carburante viene REALMENTE sottratto a `reserve`.
- Scelta/ondulazione senza alcuna conseguenza verificata: **nessun cambiamento ai pesi**, anche con esplorazione.
- Esito reale di carburante, danno ambientale e pickup di cibo: i pesi del motore scelto POSSONO aggiornarsi.
- Campioni di verifica heldout: nessuna modifica ai pesi muscolari.
- Formato motoristico e apprendimento sopravvivono alla persistenza.
- Suite pre-esistente di membrana fluida, 74 particelle e sensori + Android assembleDebug deve rimanere verde.
- Tutti i blob approvati SHA e i controlli di firma, package e telemetria nel publisher vengono mantenuti, senza disattivazioni.

## Interpretazione corretta delle nuove esperienze
Questo rende apprendibile un rapporto energetico *vero* fra contrazioni e sopravvivenza, ma **non garantisce automaticamente** che un cervello neonato impari la direzione verso il cibo. In assenza di pickup o danni evitabili può apprendere solo a non sprecare carburante; ciò è diverso dal riconoscere o imparare la locomozione. Il riconoscimento di ostacoli, la sensibilità interna e l'associazione tra movimenti e risorse restano compiti sperimentali di ZERO. La sola oscillazione senza spostamento NON è una prova di bug dopo questa correzione; servono dati reali post-OTA152 sullo stesso working brain, non un nuovo ZERO. Ultimo export visto su `zero-telemetry` è `20261014-234158-fd2799d5a250` e NON riguarda il nuovo organismo OTA151/152.

**Preservazione:** installare APK aggiornato sullo stesso `com.overmind.zero.ota`, senza disinstallare, cancellare dati o ricreare ZERO. Verificare non solo net distance, ma energia/pickup reali, persistenza dei pesi motori, episodi e durata della vita, differenziando esperienza insufficiente da guasto.
