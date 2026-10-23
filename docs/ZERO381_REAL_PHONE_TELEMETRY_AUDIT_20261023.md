# ZERO381 — Dati reali Galaxy S21 23 ottobre 2026, 15:31 Europe/Rome

## Provenienza e integrità
Index immutabile sul branch utente `zero-telemetry`: `zero-telemetry/reports/20261023-133146-3182a05bea513`. Origine ANDROID_EXPLICIT_SHARE, `installedVersionName=3.0-ota.381-motor.1`, versionCode1191, checkpoint V356 **valido**; un solo brain GRU64 di ZERO3. Report `part-002.json` contiene flight recorder originale `zmf381|1|9997`, con esattamente decisioni IDs 9741..9996 (ultime 256). I dati sono read-only: NON modificare save o caricare checkpoint privati in docs.

## Confronto globale tra DUE salvataggi / due upload (non esperimento paired!)
| Metrica | Precedente 380 (22 ott) | Upload 381 (23 ott) |
|---|---:|---:|
| Decisioni nel checkpoint | 16246 | **9997** |
| Cortex V3 updates | 9975 | **5787** |
| Movimento verso intent (>0.2 signed) | 7539 / 16246 = 46.41% | 4829 / 9997 = **48.30%** |
| Movimento opposto (<-0.2 signed) | 5419 / 16246 = 33.36% | 3034 / 9997 = **30.35%** |
| Material signed progress / material gross | 3348.09 / 17504.66 = 19.13% | 2577.20 / 11043.52 = **23.34%** |
| Progress per decisione | 0.2061 | **0.2578** |
| Physical heading banks validated V371 | 0/8 | **2/8** |
| Motor stalled counters / completed | 7610 / 16246 = 46.84% | 4592 / 9997 = 45.93% |
| Externally corrected / completed | 3736 / 16246 = 23.0% | 2769 / 9997 = **27.70%** |
| Contact valence evidence blue/red | 7/2 | **7/2** |

Il minore contatore 9997 rispetto a 16246 prova che NON è un avanzamento monotono del medesimo salvataggio V356; è possibile slot diverso, ripristino precedente oppure reset. Non attribuire differenze statistiche esclusivamente alla build381; il cervello è valido e l'app è operativa ma l'identità seriale di un unico training run NON è verificata. Se utente non ha selezionato uno slot precedente bisogna cercare il motivo del salto dei contatori prima di ulteriori OTA.

## Scatola nera V381: ultime 256 decisioni reali (IDs 9741..9996)
- 210/256 = **82.0%** spostamento nello stesso verso dell'intenzione (signed progress >+0.2U); 31/256 =12.1% verso opposto (<-0.2U); 15/256 =5.9% quasi neutro.
- Totale signed=+282.3493U / gross=408.3055U, rapporto 69.15%. Questo è il frammento **recente**, non la media del checkpoint lungo; include molte intenzioni sostenute nella medesima area.
- Su 255 cambi di intento, 136 cambi inferiori a 20°: progress signed medio +1.4208U per scelta. **33 cambi maggiori di 90°**: signed medio **-0.0822U**; **15 cambi oltre 135°**: signed medio **-0.0119U**. Correzione fisica della nuova direzione è debole su cambi bruschi; il precedente slancio continua a penalizzare proprio le sterzate/inversioni. Il progresso al netto della velocità PRE nei 33 cambi forti medio +0.1103U: esiste reazione misurabile ma non abbastanza da assicurare spostamento concorde entro decisione ~0.4s.
- 256/256 decisioni hanno prova di attività fisica, 77/256 sono marcate **externalCorrection**, 0/256 marcate **solidWallTouch**. 77/256 eventi registrano un danno >0, esattamente gli stessi 77 externalCorrection, somma danni 6.3569U. Corrispondenza 77/77 è un forte indizio di disturbo ambientale/meteo in questo segmento, **non prova** che abbia toccato 77 gemme rosse: tempeste nello stesso codice segnalano sia perturbazione che danno. Il recorder non discrimina causa direttamente.
- In 108/256 scelte la banda blu ha intensità >=0.45. C'è un singolo guadagno energetico osservato nelle ultime 256 decisioni: ID9787, **+15.6303**, intensità blu 0.922, rosso 0.161. Non è prova che segua sempre le gemme; serve la geometria di ogni gemma nel frame.
- Grandezze `mappedPhysicalX,Y` sono UGUALI a `intentX,Y` in **tutte 256** decisioni. Questo è cruciale: il modello motorio inverse-response V381 **non sta effettuando correzioni attive**. La calibrazione salvata ha 3970 training/989 heldout, MSE modello 9370.2932/989=9.475, MSE baseline 9136.0854/989=9.238, cioè peggiora di ~2.6%; gate heldout correttamente blocca l'adattamento. Un eventuale progresso reale viene dal passaggio dell'**angolo continuo V3 ai due strati muscolari**, fisica preesistente, diversa storia neurale, e non da inverse response attiva.

## Diagnosi e priorità successive
1. Il feedback reale dell'utente è coerente con le misure e conferma valore nel correggere la precedente quantizzazione a 45°, ma NON è dimostrazione A/B sulla stessa partita dato il salto checkpoint (16246→9997).
2. **Il problema residuo prioritario resta frenata/reversal a decisioni brevi.** Non forzare cibo/rosso/posizione desiderata nel cervello; serve valutare impulso fisico, velocità PRE e continuità di contrazioni del simbionte in 1–3 secondi.
3. Il gate fisico heldout per `Zero381MotorCalibration` resta giustamente spento. NON abbassare arbitrariamente la soglia per farlo attivare: modello sta predicendo peggio di null. Prendere in considerazione un'identificazione di controllo più robusta con incremento reale di moto comandato vs momentum e perturbazioni meteo.
4. Separare nella scatola nera esplicitamente `externalCorrectionCause=weather/solids/movedObjects` e `damageSource`, anziché inferire il rosso da un danno durante tempesta.
5. Chiedere chiarimento sul cambio di save SOLO se necessario: verificare automaticamente gestione save/load e slot prima di imputare bug al controller.
6. OTA pubblica rimane alla 378; la 381 è prerelease firmata. Evitare di pubblicare nuove OTA senza prove sul corpo appreso, checkpoint intatto e test del dispositivo.

## Riferimenti
Read-only source: `zero-telemetry/current.json`, `zero-telemetry/reports/20261023-133146-3182a05bea513/part-002.json`, precedente `zero-telemetry/reports/20261022-212619-2fdb10b7a9743/part-002.json`. I checkpoint grossi frammentati non sono richiesti per le metriche sopra.
