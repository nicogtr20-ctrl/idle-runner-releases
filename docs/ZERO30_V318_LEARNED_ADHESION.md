# ZERO 3.0 — V3.18 | Contratto di apprendimento dell'adesione fisica

**Preregistrato prima di scrivere il codice e prima di leggere il primo risultato di V3.18 (17/10/2026).** Letture obbligatorie: [Master](ZERO30_MASTER_BLUEPRINT.md), [Contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [UI](ZERO30_UI_UX_SPEC.md), [V3.17](ZERO30_V317_AUTONOMOUS_MOTOR_LOOP.md). PR #87 sperimentale, nessuna modifica di produzione.

## Problema verificato
V3.17: 2.048 decisioni dell'actor e 2.048 updates cortex; in tutto TRAIN **0** dei comandi adesivi hanno raggiunto `Morphology.GRIP_THRESHOLD=0,28`, anche con 101.980 eventi locali di contatto. I pesi corticali imparano numericamente ma l'azione che abilita la presa non viene mai scelta in ampiezza fisicamente sufficiente. Il vero `SquareGripPuzzle.constrain` ha dimostrato in V3.12 che grip e presa bilaterale sono fisicamente ottenibili, senza trucchi nel solver.

## Esperimento isolato: solo trainable motor adhesion head
- Nuovo `Zero3LearnedAdhesionV318`: 16 neuroni/settori con sei ingressi **soltanto fisici e correnti** (bias, `intent.adhesionPreference` scelto dalla rete sensoriale V3.17, contatto solidi per settore, impulso touch, grip precedentemente rilevato, retina locale). Output `sigmoid(w·features+perturbation)` per settore, `0≤adhesion≤1`, consegnato come canale motore 2 di ciascuno dei 16 settori al solito solver `SymbioteMatterV1.step`. Non scrivere mai `gripCommand`, `gripHold`, coordinate oggetti o posizioni del corpo. Soglia **produzione invariata** `0,28`; `glue` fisica mai finta. Gli altri 48 comandi sono prodotti dalla normale cortex sperimentale V3.17 *congelata*. La sostituzione del canale 2 avviene **prima** dell'invocazione fisica, come output esplicito e verificabile di un nuovo motoneurone trainabile (non correttivo post-solver).
- Unica intenzione da `Zero3AutonomousLoopV317.Encoder` ad ogni decisione (durata 12 frame ×0,025s), una sola `Zero3MotorIntentV316.Authority`. **Encoder/actor e cortex dei restanti 48 attuatori non imparano in questo esperimento**; solo il nuovo motor head riceve gradienti dalle reali conseguenze. Questo separa l'ipotesi adesione dalle altre carenze di locomozione.
- Esplorazione TRAIN con perturbazioni del logit trainabile, correlate tra settori, una distribuzione seed-driven su 12 step, esplicitamente registrata; nessuna fase ritmica o sequenza di attivazioni hardcoded; perturbazioni disattivate nel BLIND. Se un comando supera la soglia, lo fa perché il motoneurone ha prodotto quel valore, non perché il test assegni un grip automatico.
- Segnale di apprendimento **solo dal reale mondo**: frame con `skin.gripHold >= 0,075` con proprietario `box.boxGripHold`, numero di patch e durata della presa, penalità proporzionale al costo motorio reale e ad attivazioni adesive senza contatto. Usa critic baseline corrente, range e gradient-clipping; nessun premio per semplicemente superare `GRIP_THRESHOLD`, nessun reward per toccare se non viene ottenuta la presa. Presa bilaterale registrata soltanto se `box.bilateralHeldNow` è vero. Non rimuovere effetti di contatto dall'allenamento.
- Nessun `physicalLaw`, nome, target o orientamento nascosto come feature di policy; il test può identificare le scene SOLO per confrontare metriche scientifiche.

## Scala, partizioni e criteri fissati
- TRAIN: **32 episodi × 96 intenzioni autonome = 3.072 decisioni** fisiche di 12 tick del gel (**36.864 step gel**), più 96 warmup/episodio. Quattro famiglie FREE, BOX_NEAR, DISK_NEAR, BOX_FAR (8 vite per famiglia). Variazioni di posa, condizioni e distanze reali in famiglia, senza teletrasporti. Una vita BOX rimane nello stesso ambiente per testare accumulo di esperienze nel contesto.
- BLIND indipendente: **16 episodi × 96 decisioni × 2 bracci (learned / same-initial frozen) = 3.072 decisioni fisiche di valutazione** (36.864 step), pose/condizioni/seed e nuove geometrie diverse da TRAIN e V3.17. Gli altri 48 canali e l'actor sono identici nei due bracci; **zero gradienti in entrambe le valutazioni**; rumore esplorativo disattivato. Cambia soltanto la versione del motoneurone di adesione.
- Metriche principali: comandi adesivi sopra soglia e in contatto; `gripHold` fisici veri, **vite BOX con grip** e **frame BOX con presa sostenuta**, `bilateralHeldNow`, costo `gel.work` separato dal movimento spontaneo, attività inutile in FREE/FAR, continuità di owner, invalid/crossings. Osservare tempo di grip e confronto con head frozen. ZERO reward synthetic. Reportare almeno 4 prove BOX indipendenti del BLIND e i contatti reali nel BLIND.
- **Gate esplorazione TRAIN:** almeno 100 frame di vero contatto BOX_NEAR, almeno 1 `gripHold` fisico ottenuto con output neuronale senza scritture sui sensori, e ≥1 modifica reale dei pesi, senza NaN o grip falsi in FREE/FAR. In caso contrario FAIL, senza cambio soglie.
- **Gate scientifico BLIND:** rispetto al **frozen** nei 4 episodi BOX_NEAR, almeno **3 episodi** con grip reale, almeno **25% più frame con grip** (con floor assoluto almeno 25 frame), differenza in prese bilaterali riportata senza attribuzione automatica, e **≤20%** aumento di lavoro fisico in quelle scene. Nelle scene FREE/FAR e DISK, zero `gripHold` vero e duty-cycle adesivo super-soglia **≤30%** del totale settori×frame. Se frozen fa 0 grip la soglia richiede comunque il floor 25; se BLIND ha contatti insufficienti il gate **FAIL**, non viene ridisegnata la prova. Nessun tuning leggendo il BLIND; nuove scene diventano consumate dopo CI.
- **Non-G3:** anche se questa prova passasse, non prova camminata controllabile (V3.17 FAIL), capacità di utilizzare la presa per vivere, retention dopo riavvio, skill memoria o world model; nessun merge, APK o OTA.

## Esito
Risultati reali della CI indicati nella sezione seguente.


## RISULTATI REALI — CI definitiva 39016675337 (17/10/2026)

**[GitHub Actions 39016675337 — SUCCESS tecnico, `scientificGripGate=FAIL`](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39016675337)**. Prima CI 39016521985 già eseguita con la stessa policy e gli stessi semi/split; la seconda ha soltanto corretto la **metrica preregistrata del lavoro** per confrontare i bracci nelle scene BOX, anziché sull'insieme completo delle scene. Nessuna modifica a pesi iniziali, perturbazioni, learning rate, dati, geometria, soglie, sensori o corpo. Considerare il BLIND **consumato**, mai ottimizzare iperparametri su questi episodi.

I due file Java sperimentali `Zero3LearnedAdhesionV318.java` e `Zero3LearnedAdhesionV318Regression.java` e la CI `zero30-v318-adhesion-learning-qa.yml` sono nel ramo Draft PR #87. Il motoneurone effettua il comando **realmente eseguito** nel solo canale muscolare adesione `[sector*4+2]`; `SymbioteMatterV1.step` e tutti i solver di collisione/presa sono quelli di produzione non alterati. I valori `touchSense`, `touchPulse`, `gripCommand`, `gripHold` sono **soltanto letti dal solver**, non iniettati dal test. La comparazione esegue la stessa architettura sensoriale/actor e gli stessi restanti 48 attuatori su entrambi i bracci.

### Numeri verificati

| Misura | TRAIN motoneurone esplorativo | BLIND appreso (senza esplorazione) | BLIND originale congelato |
|---|---:|---:|---:|
| Decisioni scelte dal controller sensorimotor | **3.072** | **1.536** | **1.536** |
| Passi del gel nelle decisioni | **36.864** | **18.432** | **18.432** |
| Frame BOX con tocco fisico | **9.216** | **4.608** | **3.308** |
| Frame BOX con vera presa mantenuta `gripHold` | **8.930** | **4.600** | **0** |
| Episodi BOX con almeno una presa | **8/8** | **4/4** | **0/4** |
| Frame con vera presa bilaterale | **815** | **2.021** | **0** |
| Attivazioni motoneurali settori oltre soglia | **277.704** | **143.602** | **0** |
| Comandi oltre soglia su patch realmente in contatto | **201.446** | **116.829** | **0** |
| Duty-cycle oltre soglia in FREE+DISK+FAR (senza BOX) | **45,21%** | **45,58%** | **0%** |
| Lavoro meccanico vero, su TUTTE le scene | **1,158176408** | **0,574889422** | **0,436685137** |
| Lavoro meccanico vero, SOLO su BOX | — | **0,171031921** | **0,108989737** |
| Primi aggiornamenti del motor head | **3.072 decisioni TRAIN** | **0** | **0** |
| Aggiornamenti con credito positivo da una patch tenuta realmente | **3.165 contributi settoriali** | **0** | **0** |

I veri comandi motori sopra `Morphology.GRIP_THRESHOLD=0,28` esistono ora, e la soglia è rimasta quella di produzione. Dal punto di vista **della possibilità fisica di afferrare e attivare un'uscita motoria trainabile**, il blocco V3.17 (comando sempre sotto soglia, zero grip) è stato superato. Le 2.021 misure bilaterali sono frame, non 2.021 abilità indipendenti e nemmeno 2.021 oggetti spostati.

### Perché la promozione scientifica è stata rifiutata

- **Training coverage PASS**: più di 100 frame BOX in contatto, grip autentico, pesi effettivamente aggiornati.
- **Grip BLIND PASS**: tutte le 4 vite con BOX ottengono prese, per 4.600 frame contro 0 frozen, superando il floor di 25 e il +25% preregistrato.
- **Selettività BLIND FAIL**: con scatola assente (FREE/FAR) o disco mobile che non consente questa presa, il nuovo head **tiene adesione oltre soglia nel 45,582411% dei settori×frame**, molto oltre il massimo preregistrato 30%. FREE: 32.256 settori×frame sopra soglia; DISK: 36.309; FAR: 32.256. In queste scene `gripHold` rimane giustamente **zero**, ma l'organismo spreca l'attuazione.
- **Costo su BOX BLIND FAIL**: lavoro `0,171031921` contro `0,108989737` della baseline, cioè **circa +56,9%**, contro limite +20%. L'aumento complessivo di lavoro nelle 4 famiglie è **circa +31,6%** (0,574889 vs 0,436685). Anche se prende più spesso, questo non prova una presa utile, economica o orientata alla sopravvivenza.
- **Autocontrollo fisico**: zero grip immaginario su FREE/DISK/FAR; eventi `gripHold` consentiti soltanto se comandi neurali sopra soglia e veri owner `box.boxGripHold`. La variazione della traiettoria e del lavoro è una conseguenza **del corpo fisico realmente simulato**, non un aggiustamento nascosto del centro massa.

**Decisione: `trainingCoverage=true`, `blindGripImproved=true`, `blindWorkNotWorse=false`, `blindSelective=false`, `scientificGripGate=FAIL`.** Nessun nuovo modello promosso nel cervello live. **G3 / G5 BLOCKED**.

### Interpretazione tecnica e limiti di validità

Il nuovo head risolve una **lacuna del dominio d'uscita**: la V3.17 non poteva richiedere alcuna adesione, mentre V3.18 può farlo e il mondo conferma le prese. Ma il tipo di apprendimento è un **policy gradient locale di esplorazione/feedback**, con una grossa componente di bias verso adesione quasi universale: non impara ancora un *quando aderire* utile e in funzione della situazione. La ricompensa positiva usa solo `gripHold` reale; la penalità negativa implementata per inattività di presa si basa sulla **somma dei comandi adesivi quando non si avverte contatto** (proxy di costo dell'attuazione), **non è ancora una stima esatta del lavoro muscolare per settore**. Il valore `gel.work` autentico è comunque misurato come metrica indipendente e ha fatto fallire il gate. Questa differenza rispetto alla formulazione «penalità proporzionale al costo motorio reale» del protocollo viene dichiarata, senza modificarne le condizioni di approvazione a posteriori.

Il test separa il motoneurone adesivo dall'actor e dai 48 altri attuatori, che sono congelati: non dimostra ancora che ZERO abbia imparato a camminare, selezionare obiettivi, manipolare intenzionalmente scatole o mantenere una competenza su restart. Anche le geometrie cieche, pur con pose e semi diversi, appartengono alle stesse categorie di oggetti del TRAIN e non sono un mondo completamente inedito.

Il contatore `learnedHeadDeltaL2=8,8871154763` è **radice della somma dei quadrati di tutti gli aggiornamenti successivi**, non distanza finale delle weights dall'inizializzazione; non usarlo come tale. Il segnale motorio selezionato dall'actor rimane una policy sensorimotor con lettura fissa, non un pieno actor-critic goal/survival.

### Prossimo passo motivato — V3.19

Progettare e validare **controllo contestuale dell'adesione**: accoppiare meglio retina locale/tatto/propriocezione con attribuzione settoriale, penalità sul **lavoro fisico effettivo** e costo delle attivazioni in assenza di superfici fisicamente afferrabili; separare grasping temporaneo utile dal tenere semplicemente acceso il collante. Benchmark rigoroso su scene con/ senza superfici grippabili e costo normalizzato per utilizzo effettivo, con **nuovo BLIND** (nessun tuning sulla V3.18). Nessuna scorciatoia al solver, nessuna attivazione automatica di presa o hardcoded `gripCommand`. In seguito tornare al problema G3 originario: locomozione/allineamento e retention reale, critica energia/danno autentici, memoria e UI 3D.

**Stato:** PR #87 ancora Draft, esperimento solo in branch, nessun merge/OTA, ZERO3 non attivo nell'APK, tutti i 12 profili 2.2 intatti.
