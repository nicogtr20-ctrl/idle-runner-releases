# OVERMIND ZERO / ZERO-007 — Corpo plastico e pseudopodi emergenti

## Problema
Il corpo precedente aveva soltanto 10 punti fisici e un anello spesso: ZERO non poteva formare sporgenze morbide sottili simili a braccia senza disegnare braccia predefinite.

## Nuova fisica
- `Morphology.Surface` aggiunge **32 masse elastiche** adiacenti sul contorno di ZERO. Non esistono segmenti catalogati come braccia, zampe o tentacoli.
- Le masse sono collegate fra loro e vincolate elasticamente alla superficie interna già esistente (10 masse + nucleo), quindi il cervello motorio salvato dalla versione precedente resta compatibile.
- Il nuovo controllore di morfologia ha **74 pesi**, condivisi su tutti i punti: ogni punto legge distanza/direzione dello stimolo e variabili fisiche locali, decide se estendersi e se incurvarsi di lato. Non ci sono azioni speciali «estrai braccio».
- All'inizio i pesi d'uscita sono a zero: la rete non nasce con appendici preimpostate. Mutazioni delle connessioni possono far sviluppare estensioni locali.
- Materiale sintetico e lunghezza massima limitati per evitare bolle giganti e artefatti numerici.
- La superficie esterna muove gli oggetti con **collisioni e impulsi fisici calcolati sulle masse**, non con una spinta automatica.
- Rendering: contorno renderizzato realmente sulle 32 posizioni delle masse; i riflessi segnalano allungamenti, senza sprite di braccia prefatte.
- Eredita i pesi di movimento 62, manipolazione 18, decisioni dinamiche 91+, le esperienze, i contatori e il package/firma Android di v0.6.
- Salvataggi aggiunti: `morph_brain_v07`, `morph_attempts_v07`, `morph_accepted_v07`.
- In CERVELLO/ABILITÀ compare la nuova abilità Membrana plastica e in PARAMETRI una quarta tab `MEMBRANA` con tutti i 74 pesi iniziali/attuali/delta.
- Nel gioco indicatori fisici `MEMBRANA 32 PUNTI • ALLUNGAMENTO ... • TEST ...`.

## Apprendimento e limiti
L'estensione NON si attiva per una sequenza manualmente fissata, ma dipende dalla rete evolutiva addestrata sui risultati di simulazioni fisiche. I criteri di fitness (avvicinamento allo stimolo, contatto, spostamento, consumo energetico) sono progettati. Non è ragionamento astratto o inventarsi da zero nuovi sensi.

La presenza di 32 punti non garantisce che ZERO impari a usarli. È necessaria una verifica visiva della crescita delle protrusioni e confronto del comportamento fisico a pesi vergini. Non chiamare appendici vere se la massa resta rotonda nel test.

## Test
- Test JVM: nessun arto prestabilito, controllo allungamenti fisici, collisioni a contatto e assenza di spinte lontane, addestramento senza regressione training.
- Regressioni dei tre vecchi modelli e Android build Debug.
- Solo dopo CI verde: build APK firmata sul ramo updater OTA; se Vercel non si aggiorna, fallback GitHub Release firmata.
