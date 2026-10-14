# ZERO — Symbiote Body 1.0: piano tecnico

**Stato: SPECIFICA DI PROGETTO, NON UN APK O UNA MODIFICA DI FISICA GIÀ PUBBLICATA.**
Data 15 ottobre 2026. Decisione: sostituire il comportamento di rete elastica con massa gelatinosa/fluida realmente governabile dal solo cervello ZERO 2.2 primario.

## Requisito dimostrativo fondamentale
Un solo organismo fisico deve poter concentrare materia in una zona, allungare una pseudopode, toccare un oggetto, eventualmente aderire, ritirarla, mentre la porzione principale può restare quasi ferma. Restare fermi richiede equilibrio di pressioni, forze e/o veri contatti adesivi: niente blocco invisibile del centroide, teletrasporto di massa o azione scriptata «raggiungi la gemma». Lo stesso corpo deve formare due lobi, avvolgere oggetti, passare fra ostacoli e ricompattarsi senza lacerarsi.

## 1. Corpo attuale, verificato nel codice OTA137
- MotorBrain.Body: N=10 masse materiali disposte in anello, molle adiacenti e comando radiale; centroide cx/cy osservato, **non** collider/nucleo fisico.
- Morphology.Surface: POINTS=96 masse perimetrali effettive, con x,y,vx,vy, extension, gripCommand, touchSense, gripHold. Ognuna possiede un attuatore locale, ma la posizione desiderata è ancorata ai dieci nodi interni e le membrane trasmettono elasticamente le reazioni. Questo dà un effetto più simile a molle che a un liquido.
- BodyIntegrity + MaterialContactConstraints impediscono in parte collassi/sovrapposizioni, rispettano contatti/grip reali. MaterialGeometry.touches controlla materia realmente presente (bordo e interno di contorno valido) anziché nucleo/coordinate inventate.
- Il 2.2 attualmente seleziona 16 azioni (otto direzioni con/senza adesione), non dispone di un comando esplicito per «resto del corpo stabile, zona locale in estensione». La mappa DistributedBodySense offre già 96×9 osservazioni locali e una proiezione in 16 settori.
- GameActivity integra fisica con casse a presa bilaterale, lastre, porte, muri, risorse, vento e scenari; GameSceneSnapshot, physical_scene_v100 e SaveSlotStore persistono la scena e i 12 slot.

Quindi un semplice riaddestramento NON può eliminare le limitazioni geometriche/di comando del corpo.

## 2. Nuovo modello di materia: PBF/XPBD in 2D con cortice attivo
Approccio raccomandato: Position Based Fluids / Extended Position Based Dynamics in due dimensioni, con un bordo materiale attivo, più economico sul Galaxy S21 di un solutore 3D completo.

### Particelle e topologia
- Conservare **96 punti perimetrali REALI** con identità materiale lungo la vita, velocità e touch/grip: non sono grafica o braccia prefabbricate.
- Aggiungere inizialmente 64–96 particelle materiali interne: densità, volume/area e scambio di impulsi; calibrare N sulla CPU reale, non far dipendere la correttezza dal conteggio.
- Eliminare le dieci masse come struttura portante radiale rigida; ogni unità interna è materia mobile, non ancora di 96 molle. Nessun punto centrale capace di esercitare un impulso speciale.
- Una sola membrana chiusa nel V1, niente distacchi o divisioni spontanee. Evitare auto-intersezioni, lacune, falsi interni di poligono e spessore zero.

### Fisica
1. Densità interna vincolata (quasi incomprimibile) con compliance XPBD, non lunghezze radiali predeterminate.
2. Conservazione dell'**area** del corpo 2D come misura della massa a spessore nominale costante. Una pendice larga o lunga sottrae area al resto del corpo; non crea tessuto.
3. Pressione/differenze di pressione interne che trasferiscono materialmente volume dove la membrana spinge.
4. Viscosità e smorzamento calibrati per oscillazioni gelatinose dissipative, ma senza fermare forzatamente ogni dinamica.
5. Tensione superficiale e elasticità del cortice che favoriscono coesione e recupero, senza imporre una forma circolare o una direzione utile.
6. Forze e vincoli interni devono trasmettere reazioni coerenti; un contatto esterno col pavimento/oggetto può ancorare una regione solo se realmente avvenuto.
7. Collisioni continue per punti e segmenti di membrana con solidi; nessun allungamento/ritorno attraverso muri.
8. Lavoro muscolare, adesione e viscosità hanno costi energetici reali; una presa non è gratuita e non è garantita dal semplice contatto.

Implementazione con passo fisico deterministico e substep/iterazioni; a x1/x5/x20/TEST MAX cambia il numero di substep reali, non la fisica. Riutilizzo di array e hash spaziale per particelle vicine: evitare allocazioni e O(N²) globali per ogni frame. Trattare overflow, NaN, collasso, normale invertita e intersezione del contorno come errori da testare e registrare, senza resettare silenziosamente il cervello.

## 3. Controllo locale collegato all'UNICO cervello 2.2
L'interfaccia attuale 8 direzioni × adesione è troppo limitata. Il nuovo corpo avrà un'interfaccia muscolare per **16 settori corticali** interpolati sui 96 punti. Per ogni settore, comandi continui:
- espansione/contrazione locale con trasferimento di materia;
- spinta tangenziale per curva/avvolgimento;
- adesione locale [0..1] efficace solo se il nodo tocca una superficie valida;
- rigidità/viscosità locale: permettere base stabile e pendice mobile.

Comandi globali separati per locomozione, freno/controllo della forma; **deve essere possibile locomozione quasi zero e muscoli locali attivi**. La stabilizzazione è un esito fisico appreso; nessuna correzione magica al baricentro.

Aggiungere una **testa motoria allenabile all'interno del percorso del RecurrentCoreV22**, condizionata da stato ricorrente e propriocezione locale, non un secondo cervello concorrente o arbitro. Le 16 azioni esistenti restano segnali di intento/compatibilità durante la migrazione, ma NON vanno codificate come combinazioni predefinite di arti o pseudopodi. Non fornire al controller nomi di cassa, bersagli nascosti, colore premiato, porte 'corrette' o spostamenti vittoriosi. La rete deve apprendere veri effetti dell'azione (schema corporeo, contatto, energia e risultati fisici), anche a costo di un avvio incerto.

I vecchi pesi motori (62/18/82) non sono automaticamente coerenti con il nuovo materiale; non interpretarli come stessi coefficienti fisici dopo conversione. Il cervello di scelta/causalità (rc22 e cs23) resta primario, e i controlli nuovi sono parte del suo sistema motorio.

## 4. Sensori e conseguenze
Mantenere sensori 96×9 e viste reali distribuite, sostituendo le quantità legate alla vecchia estensione radiale con misura di deformazione/pressoione, curvatura, densità, trazione, velocità del flusso, grip desiderato vs posseduto, comando vs movimento effettivo. Pool in settori per 2.2 conservando la mappa completa per attuatori e diagnostica. L'energia entra soltanto da contatto fisico reale: nessun cibo assimilato per prossimità al centroide o interno di un poligono auto-intersecato.

Il motore causale OTA137 deve ricevere effetti esterni realmente osservati, non nuovi 'flag successo'. Segnare nella telemetria intento, attuazione reale, contatto, deformazione, spostamento oggetto, raccolta e tempo di risposta.

## 5. Ordine del lavoro e gate di accettazione

**Fase A — Corpo fluido indipendente dal cervello.**
Nuovo SymbioteMatterV1 + SymbioteXPBDSolver in laboratorio JVM, nessuna modifica alla OTA pubblicata. Test massa/area (target di progetto in assenza di compressione forzata: media errore area <3%), recupero forma, pressione equilibrata, niente nodi concentrati e zero self-crossing stabile. Il centroide resta derivato e non 'attivo'.

**Fase B — Prova dell'unica pendice.**
Controllo muscolare di LABORATORIO, non AI: estendere 1 settore, spostarlo lateralmente, tenerlo, ritrarlo, senza trascinare macro-scopicamente gli altri. Misurare estensione/raggio, drift centroide/raggio, area, strain, energia, collisioni. Test doppia pendice e riposo sotto perturbazione. Specificare se la base poggia/aderisce a un solido, altrimenti un residuo moto del corpo può essere fisicamente corretto.

**Fase C — Unificazione contatti e mondo.**
Conservare/rivedere SquareGripPuzzle, SquareCollisionSolver, ScarcityLab, PanelPhysics, MaterialContactConstraints, MaterialGeometry, HabitatContinuity. Il perimetro reale deve afferrare con due regioni davvero distinte, non prendere una cassa da lontano. Test gates, cassa che nasconde energia viola, vento, contatto dannoso, passaggio stretto, collisioni del segmento e punti antiphantom.

**Fase D — Attuazione apprendibile dentro 2.2.**
Integrare SymbioteMotorHeadV1 e nuovi sensori; addestrare da conseguenze effettive con verifiche heldout, senza azione privilegiata 'fai tentacolo'. Test neuro-motorio: comando globale basso + comando settore alto produce vero pseudopode; il 2.2 può *sceglierlo* autonomamente solo dopo esperienza, non per script. Nessun ritorno al vecchio cervello, nessuna eliminazione automatica di memorie.

**Fase E — Salvataggi e APK.**
Versionare corpo e scena fisica (physical_scene v2), nuovi parametri motor head, corpo e velocità. Migrare profili legacy SOLO su una COPIA e con consenso esplicito, salvando il veterano in slot prima della conversione. Un nuovo ZERO vergine può nascere col Symbiote appena passa i test; NON resettare un veterano. Verifica reboot/morte/slot multipli, perf x20 e test MAX su Galaxy S21 e carico CPU/termico; poi APK firmata con OTA, hash, manifest e prova HTTP 200. La grafica smooth usa esclusivamente il confine fisico vero.

## 6. Matrice test: capacità del corpo vs capacità della mente
1. **Fisica sola** (comandi sintetici noti): una pendice emerge, il corpo redistribuisce massa, non si spezza, non penetra nei solidi, può ritrarsi senza esplosioni o teletrasporto.
2. **Contatto solo**: adesione solo su collisione fisica; porta/cassa richiedono prese legali; luce e cibo non sono colliders fantasma.
3. **Interfaccia motoria**: espansione regione A e contrazione regione B avvengono in modo indipendente dalla traslazione; comandi ai 16 settori fanno differenze effettive.
4. **Autonomia 2.2**: dopo esperienze in mondi non usati per addestramento, cerca, impara e sfrutta nuove competenze; confrontare con stesso organismo e stessi seed, non solo due cervelli che hanno vissuto tempi diversi.
5. **Compatibilità**: scene e named saves non distrutti, no schema swap invisibile, determinismo entro tolleranza; x1/x5/x20/test max usano identica fisica, differisce solo tempo simulato.
6. **Performance e stabilità**: NaN=0, intersezioni persistenti=0, monitor di area/pressione/touch, misure FPS/frame time (mediana e p95), no peggioramento termico incompatibile con TEST MAX.

## 7. Codice coinvolto / confini
Nuovi moduli pianificati: SymbioteMatterV1, SymbioteXPBDSolver, SymbioteMotorHeadV1, SymbioteBodyAdapter, SymbioteTelemetryV1 e test Regression dedicati.
Da collegare gradualmente: MotorBrain.Body, Morphology.Surface, BodyIntegrity, MaterialGeometry, GameActivity, DistributedBodySense, BodyProprioception, RecurrentCoreV22, SensorimotorMind, GameSceneSnapshot e SaveProfileCodec. Iniziare su branch dedicato isolato, con test di regressione completi per tutte le regole fisiche. Non sostituire la release stabile OTA137 finché non è dimostrato il contratto fisico.

## 8. Esito desiderato
Un organismo inizialmente goffo ma **fisicamente espressivo**, la cui autonomia può crescere: sposta materia da una zona all'altra, crea pseudopodi senza arti prefabbricati, stabilizza le proprie superfici con reali forze di contatto, vede e sente le conseguenze, apprende dalle esperienze del proprio unico cervello.

**Questo file è il progetto completo, non la dichiarazione che il lavoro di implementazione o la APK siano già stati eseguiti.**
