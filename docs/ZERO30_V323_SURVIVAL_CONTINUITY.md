# ZERO 3.0 — V3.23 | Continuità energetica nella stessa vita

Protocollo fissato prima dell'implementazione, 17/10/2026. Riferimenti obbligatori: docs/ZERO30_MASTER_BLUEPRINT.md, docs/ZERO30_IMPLEMENTATION_CONTRACT.md, docs/ZERO30_UI_UX_SPEC.md, docs/ZERO30_V322B_FULL_CAUSAL_REPLAY.md.

Ipotesi: l'archivio comune V322B conserva neuroni, corpo, mondo, RNG di fixture e tracce sensoriali ma non la riserva energetica e i suoi eventi. V323 deve associare nello stesso payload atomico la riserva, il tempo trascorso, lo stato scarsità e una traccia causale dei costi realmente rilevati dal solver e delle interazioni fisiche.

Regole da GameActivity: dreno di base 0.09 unità/sec normalmente e 0.30 in scarsità a partire da 70 secondi; costo motorio min(reserve, max(0, gel.work delta)*8); esiti risorse soltanto quando MaterialGeometry.touches vede contatto reale, con entity.touch e saturazione reserve entro [0,100]. Niente successi sintetici, target direzionali o modifiche al solver.

Prove da eseguire: scene FREE/BOX/DISK/FAR/BOX+DISK con 4 pose ciascuna, 24+24 decisioni, checkpoint e prosecuzione nella stessa vita dopo nuovo processo fino a 96; confronto bytewise contro un processo continuo, 20/20 richiesto. Aggiungere casi controllati di contatto con risorsa e pericolo reali e attraversamento della soglia temporale di scarsità. Variare energia iniziale, mantenere trace PRE/POST per decisione, negare recovery da dati incompleti. Doppia generazione SHA-256, rollback, fault injection, compilazione Android Debug.

Questa verifica resta di laboratorio: non integra automaticamente l'energia nell'encoder V315 o in GameActivity, non completa actor-critic, locomozione G3, world model G5 o persistenza Android. PR Draft, nessuna OTA, profili 2.2 intatti.

## Risultati
Da compilare esclusivamente dai log GitHub Actions.