# ZERO-006 / OVERMIND v0.6 — Evoluzione strutturale e schermata Cervello

## Intento
Conservare il cervello decisionale appreso in v0.5, consentire la crescita graduale dei neuroni e visualizzare in una schermata tipo abilità di videogame **numeri veri**, non statistiche fittizie.

## Cervello decisionale dinamico
- 7 ingressi, inizialmente 10 neuroni nascosti e 1 uscita: **91 parametri** iniziali.
- Massimo sperimentale 24 neuroni nascosti (**217 parametri**) per sicurezza sul Galaxy S21, non per un presunto limite dell'IA.
- Quando l'errore recente non scende, il cervello può provare ad aggiungere 1 neurone (+9 pesi).
- La nuova uscita parte a **zero**, quindi il nuovo neurone non altera l'attuale comportamento prima della sperimentazione.
- La prova allena solo la nuova unità su un piccolo buffer di esperienze, lasciando invariati i pesi esistenti.
- Il candidato è accolto solo se migliora l'errore su un quarto degli esempi non usati nella prova. Se non migliora, la rete resta identica.
- Gli esperimenti non assicurano che la rete cresca: se 10 neuroni sono sufficienti o il test fallisce, il numero rimane 91.
- Stato v2 serializzato con i pesi originali e attuali, numero neuroni, tentativi, successi; migrazione da v1 senza rimuovere i pesi v0.5.
- Il buffer temporaneo di 48 esperienze è ricostruito dopo riavvio; non viene salvato. Le preferenze/pesi appresi e i contatori invece sì.

## Nuova pagina CERVELLO
Pulsante nel pannello superiore, vicino ad AGGIORNA. Si apre una Activity Android separata, compatibile con il tasto indietro e con gli aggiornamenti OTA.

- **ABILITÀ**: crescita totale 171 → 171+N, numero decisioni, esperienze, tentativi motori, contatti, dischi mossi, tentativi crescita e errore corrente.
- **PARAMETRI**: scelta / corpo / manipolazione con elenco scrollabile di **ogni peso**, valore originale, valore corrente e differenza.
- **EVOLUZIONE**: schema della rete decisionale con neuroni aggiunti evidenziati, tentativi vs crescita effettiva, qualità della previsione e regole di accettazione.
- Il profilo legge i dati persistiti dall'app al momento dell'apertura. Per vedere i nuovi numeri chiudere e riaprire la pagina.
- Le barre hanno scale puramente grafiche, **non certificano livelli di intelligenza**.

## Stato e limiti
Si tratta ancora di un contextual bandit con criteri di crescita impostati nel codice, non di una IA che può autonomamente inventare qualsiasi architettura o ragionare come una persona. Il cervello motorio (62) e quello di manipolazione (18) restano fissi in questa fase, mentre quello delle scelte può crescere.

## Test e distribuzione
- JVM: migrazione serializzazione v1/v2, conservazione funzione nell'aggiunta di neurone, separazione esiti, fisica motorio/contatto.
- Test di codice: presenza della pagina e vecchie chiavi di memoria.
- Build APK staging, poi branch OTA firmato con stesso appId e stessa chiave Android.
- Documentare sul repository e testare sul Galaxy S21 effettivo.
