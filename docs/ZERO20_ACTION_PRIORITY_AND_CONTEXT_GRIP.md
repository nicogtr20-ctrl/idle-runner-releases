# ZERO 2.0 — Decisioni energetiche e recupero contestuale delle prese

**13 ottobre 2026 — OTA successiva alla 109 — lavoro in `feature/zero2-adaptive-priorities-ota110`.**

## Problema osservato, non ipotesi su un cervello ereditato

Il salvataggio **vergine** `20261013-154153-94607215353b` ha 16.836 esperienze sensomotorie, 44 morti e un'identità di inizializzazione motoria differente dal precedente profilo. La memoria non risulta ereditata da un personaggio precedente.

Nell'ultima vita conclusa della registrazione:
- l'azione 8 è stata scelta **245/369 volte (~66%)**;
- erano visibili frequenze già apprese come energia positiva, e ZERO esauriva la riserva senza contatti utili nell'ultimo tratto;
- il credito meccanico dell'azione 8 aveva raggiunto **+1,5**, mentre il consiglio strumentale per quell'azione era **-0,164**;
- `ObjectExperienceV20` aveva fiducia zero e non è la spiegazione di questo comportamento.

**Diagnosi circoscritta:** le abilità di spostamento apprese vengono sommate alle motivazioni energetiche come ricompensa generica anche fuori dalle reali condizioni di manipolazione; un'azione capace può diventare eccessivamente desiderabile. Gli effetti delle altre memorie e della fisica non sono esclusi senza replay controllati.

## Modifiche

### 1. La competenza non è un bisogno biologico

- `ExperienceCredit` conserva **inalterati** i vecchi pesi persistenti `ec2`, la memoria di conseguenze fisiche e quella di firme energetiche; nessun reset.
- Nuovo `mechanicalValue` esplicita il contributo della competenza meccanica; `adaptiveValue` lo ammette nella selezione soltanto se **sensori propri** (ostacoli nei raggi e contatto fisico sulla membrana) rendono plausibile manipolare qualcosa.
- La riserva energetica letta dal corpo riduce ulteriormente l'influenza delle competenze meccaniche quando ZERO sta per esaurirsi. Non contiene etichette «azzurro», «cibo», «cassa», coordinate di target o comandi da eseguire. La valenza visiva resta appresa da veri contatti con fonti energetiche.
- Lo stesso valore condizionato è usato per **scelta immediata, pianificatore a breve termine e consulenza predittiva**, evitando che un pianificatore reintroduca un premio meccanico incondizionato.
- `value` rimane intatto per confronti/migrazione; il comando live usa `adaptiveValue`.

### 2. Recupero di tecniche realmente provate, in situazione simile

- `GripSkillBank` ora può salvare, assieme alla coppia HAND/MEMBRANA, i **26 sensi esistenti** rilevati nell'interazione: contatto/adesione, prossimità e frequenze luminose.
- Durante il recupero sceglie una configurazione abbastanza simile dal punto di vista sensoriale; se tutte le configurazioni con contesto non corrispondono, **non** forza una vecchia tecnica appresa altrove.
- La decisione di **PRESA ON rimane autonoma**, e recupero possibile solo dopo 120 frame di contatto + adesione scelta + nessun punto in presa, fuori dai test delle mutazioni e con cooldown.
- Restano salvati i cinque record originali `grip1` della OTA 109; il nuovo encoder `grip2` importa i vecchi slot senza perdita. Il formato non legge ID oggetto o regole del mondo.
- Il trasferimento contestuale non è ancora riconoscimento perfetto della stessa cassa da angolazioni differenti. Gli stati sensoriali del primo stadio sono quelli all'ultima decisione, non uno snapshot indipendente ad alta frequenza.

### 3. Conservazione della locomozione

- `OnlineBodyLearning`: una mutazione di **mano o membrana** non viene accettata se abbatte la traslazione comparabile oltre un limite netto, **senza** un nuovo miglioramento reale di energia o manipolazione fisica. Resta permesso migliorare azioni stazionarie quando producono veri effetti.
- Il precedente archivio motore e tutti i cervelli nominati rimangono intatti.

## Criteri di verifica e limiti

- Compilazione **JVM di regressioni e Android assembleDebug**, includendo migrazione da `grip1` a `grip2`, recupero fra contesti differenti, divieto premio meccanico lontano da un oggetto, graduazione per riserva.
- Il sistema di navigazione appreso continua ad avere influenza autonoma, senza regole direzionali preprogrammate.
- Non dichiarare maggiore sopravvivenza, ritrovamento dei blu o presa perfetta finché non si osservano i **rapporti live** di nuove vite confrontabili, tenendo conto del cervello vergine e del campione statistico.
- La distribuzione deve utilizzare **la chiave firma originale e il pulsante AGGIORNA**, senza cancellare file prefs né gli slot con nome.
- Un'eventuale memoria episodica di movimenti fisici non deve sovrascrivere i pesi dei motori pericolosamente, come già protetto in OTA 109.

## Restano da affrontare

- Identità multivista dell'oggetto e disambiguazione dei sensori tra oggetti adiacenti.
- Valutazione controllata dell'efficacia del recupero contestuale nel mondo reale, prima di allargare la sostituzione delle tecniche.
- Caso in cui l'organismo apprende una mappa attuatori→spostamenti solo localmente valida: va misurata con esempi condizionali e non affidarsi soltanto alle medie per azione.

**Non è stata introdotta alcuna modifica alla generazione di energia, alla probabilità delle casse, ai loro codici interni o alle firme visive.**
