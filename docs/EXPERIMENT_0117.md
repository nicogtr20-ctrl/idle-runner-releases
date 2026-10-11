# OVERMIND ZERO — Experiment 0117: mondo empirico e orizzonte causale (SHADOW)

**Data:** 11 ottobre 2026. **Stato:** prototipo su branch sperimentale, **non pubblicare né installare** finché non supera la QA e una verifica completa sul dispositivo. La 0.11.6 stabile resta invariata. **Motivazione:** ZERO già possiede molte reti sensoriali, previsione a un passo, pianificazione fino a tre azioni e tracce episodiche, ma manca evidenza affidabile che sappia costruire e riutilizzare catene di conseguenze ritardate.

## Ipotesi da verificare

Se un organismo ha assistito a transizioni sensoriali reali, e all'esito energetico reale di alcune azioni, un grafo di stati astratti osservati può valorizzare una **prima azione senza beneficio immediato** perché porta a uno stato da cui altre azioni, già vissute, avevano prodotto energia. Nessuna ricompensa positiva viene assegnata per il solo fatto di spostare un cancello. Questo non è ancora riconoscimento di oggetti, ragionamento simbolico o prova di intelligenza generale.

## Implementazione sperimentale

- Nuova classe Java pura `CausalHorizonProbe`, massimo **48 prototipi percettivi** dai **26 sensori già esistenti**, senza nuove identità, coordinate, leggi del mondo o segnali privilegiati. Il matching confronta tattile, raggi e canali ottici e NON usa riserva energetica e velocità come identità dello stato.
- Ogni coppia **stato percepito + azione fra le 16 disponibili** registra soltanto transizioni fisiche realmente completate, frequenza, accordo sulla destinazione, energia ricevuta/persa e numero di effetti fisici esterni come pura statistica. I **campioni holdout** non allenano il grafo.
- Predizione valutata contro riferimento di **nessun cambiamento sensoriale** sui campioni holdout. **Almeno 30 validazioni** e **almeno 15% di riduzione dell'errore** per proporre una prima azione ipotetica. Ogni collegamento usato nella catena richiede **almeno 3 esperienze** e almeno **75% di accordo** con il suo esito di stato.
- Orizzonte massimo **5 azioni**, ricerca ricorsiva e sconto temporale; nessun premio per movimento, curiosità o miglioramento inventato durante la ricerca. L'azione proposta è una pura **ombra statistica**, non modifica scelta o attuazione e non aggiorna reti Q/esperienze dal futuro immaginato.
- In `SensorimotorMind.choose`, il probe osserva la **decisione già presa** e conta i disaccordi: `causalHorizon.shadowChoice(senses,chosen)`. In `observe`, dopo una vera transizione fisica, riceve solo stato prima/dopo, azione fisica, **actualEnergySinceChoice** e `physicalEffect`. NESSUN termine `causalHorizon` è aggiunto al punteggio di scelta.
- Stato persistito come nuovo `causal_horizon_shadow_v117` in `zero_motorbrain_001` e nei dodici slot nominati senza cambiare i formati precedenti; versione `ch1`, serializzazione delimitata e ripristino atomico se valido. Reset **CAUSAL** elimina anche il nuovo archivio, RESET TUTTO rimane invariato. Morte elimina solo la proposta temporanea, non il grafo.

## Invarianti e limiti

1. Questo esperimento NON deve cambiare comportamento, atti motori o criterio del cancello rispetto alla 0.11.4 conservata dalla 0.11.6. È un osservatore aggiunto, non una nuova rete decisionale. Non abilitarlo nel gioco stabile prima di misure controllate.
2. Il sistema **non riconosce ancora oggetti persistenti**, né ricostruisce world state invisibile. I prototipi possono confondere due scene sensorialmente simili, e nuovi ambienti possono saturare il buffer dei 48 prototipi. Una validazione non garantisce generalizzazione. Il grafo non conosce l'intera sequenza futura, ma concatena esiti empirici già osservati.
3. Il valore deriva dal SOLO delta energetico da contatto effettivo; il contatore fisico NON è ricompensa. Questo evita l'equazione fallace «porta spostata = obiettivo raggiunto». Il problema della morte terminale, già noto dall'audit, rimane separato.
4. Non vi è modifica dell'APK pubblica, del manifest OTA, della firma, delle reti precedenti, dei 16 attuatori, delle leggi fisiche, dei reset di memoria preesistenti o dei vecchi salvataggi.
5. Salvataggi creati dal prototipo possono contenere la nuova chiave opzionale, innocua alle versioni precedenti; comunque **non usare il profilo reale come tester** fino a verifiche approfondite. La prima sperimentazione avviene con copia/slot usa e getta.

## Test automatizzati e piano sperimentale

- `CausalHorizonProbe.main`: neutralità a mente vergine; apprende **stato A -> stato B senza energia** e **B -> C con +22 energia**, preferendo come ipotesi l'azione che raggiunge B rispetto a un'alternativa sterile; prima dei holdout, nessuna proposta; dopo i holdout, proposta statistica; cancellazione temporanea alla morte preserva conoscenza; roundtrip `ch1` e rigetto payload corrotto; holdout contrari neutralizzano un modello errato.
- GitHub Actions `overmind-v117-causal-horizon-qa.yml`: tutte le principali regression JVM, prova senza controllo dei motori, verifica della persistenza per slot, Android `assembleDebug` sul solo ramo.
- Successivo test scientifico (non già superato): creare scenari fisici nei quali una **prima azione cambia l'accessibilità** e il contatto energetico avviene solo più tardi, confrontare due politiche su stesse condizioni iniziali, test non contaminati e intervalli di simulazione sufficientemente lunghi. Una sola prova positiva non conta come generalizzazione.

## Criteri per il passo successivo

Se le previsioni rimangono inaffidabili (confidence zero), non connettere il probe alla policy. Se i risultati sono ripetibili su stati ignoti e benchmark fisici appaiati, valutare un secondo ramo in cui dare un contributo motorio strettamente limitato al probe, sempre misurando regressioni di fame, locomozione, manipolazione e memoria. Ogni passaggio richiede una decisione separata di pubblicazione; **nessuna OTA dall'esperimento 0117**.
