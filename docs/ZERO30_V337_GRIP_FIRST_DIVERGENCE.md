# ZERO 3.0 — V3.37 | Prima divergenza bilaterale e ablation dei vincoli

**Pre-registrazione 17 ottobre 2026, prima di avviare i nuovi esperimenti.** Leggere [master](ZERO30_MASTER_BLUEPRINT.md), [contratto](ZERO30_IMPLEMENTATION_CONTRACT.md), [V3.36](ZERO30_V336_POINTWISE_TOPOLOGY_GUARD.md). Ramo `feature/zero30-foundation-g0-g3-20261016` PR #87 **DRAFT**; OTA172, 12 brain legacy, fisica gameplay e publisher **immutati**.

## Osservazione confermata (non reinterpretare a posteriori)

V3.36 su ordinal 645, 168 decisioni V317 ×12 substep e frozen V319 ha mantenuto `baselineBilateral=1192`, `faceOnlyBilateral=0`, `facePlusLinkBilateral=0`; tutte e tre le varianti `boxGrip=2014`. Il candidato annulla tutti i crossing ma distrugge capacità manipolativa. Le decisioni provengono dal mondo BASE e vengono copiate identiche sulle altre due fisiche: evidenza meccanica matched, non controllo chiuso indipendente e non prova di gait imparato.

## Domande V3.37

1. **Quando** compare il primo scostamento nella presa? Individuare primo frame diverso per geometria della shell, `boxGripHold`, contatti, facce sinistra/destra/sopra/sotto, lato/indice `gripCommand`, bilateral, box XY/VXY, gel work, `selfCrossings`. Non scambiare "prima divergenza di posizione" per causa sufficiente del successivo outcome.
2. **Ablazione link-only (nuova, non equivalente alla V3.36):** utilizzare la routine originale di pressione/eiezione BOX **invariata**, ma abilitare solo la ricerca di faccia alternativa quando una correzione `repairLinks` *crea* un auto-incrocio. Questo è `MODE_3_LINK_ONLY` lab-only, diverso dai bracci già misurati BASE `0`, FACE_ONLY `1`, FACE_PLUS_LINK `2`. La fisica ordinaria deve mantenere opzioni default FALSE.
3. Confronto preregistrato fra 4 bracci (stesso nastro di 64 comandi reali V317+V319): ordinal 337 e 645, **168×12=2016 substep per scena e braccio**. Nascite V333 già note usate ESCLUSIVAMENTE per diagnosi, non nuovi holdout. Ampiezza somma degli attuatori >0.1.
4. **Gate veto indipendenti:** 0 crossing POST-WORLD del candidato, 0 links through BOX, 0 patch incorporate, `gel.invalid=0`, lavoro gel `<=1.10×baseline + 1e-6`, nessuna falsa presa senza comando o owner misattribuiti e frame bilaterali `>=80%` di baseline **solo se baseline li mostra**; baseline con 0=NOT_TESTED, **non** PASS. I quattro controlli vecchi e vera regressione MultiObject + V319 devono mantenersi, e la compilazione Android Debug resta SOLO compile. Una soluzione che supera solo questi scenari già visti resta LAB, non G3.
5. Se LINK_ONLY continua a perdere grip, non introdurre patch esplicite sui seed/ordinals, o rimappare artificialmente i lati: documentare FAIL e progettare un risolutore simultaneo con stato legale di contatto/membrana e vincoli sulle prese *non inventate*. Nuovi seed indipendenti e 2.000 decisioni autentiche addestrato/congelato solo **dopo** un gate meccanico sufficiente.

## Analisi difetto collaterale da verificare

Nella V3.36 gli 0 bilaterali di FACE_ONLY mostrano che la modifica della proiezione sulla faccia influisce sulla futura dinamica del solido anche quando le prese generiche restano 2014; **non si deve dedurre** che siano stati i soli timer grip ad azzerarsi. La V3.37 misura distintamente appoggio sulle quattro facce, segni del contatto, timer associati alla BOX, dispersione degli indici e velocità/dislocazione BOX. Questo evita di confondere numero di patch tenute con reale opposto lato e trazione.

**Fino a nuova CI verificata: V3.37 è soltanto protocollo, `G3/G5=BLOCKED`, nessun OTA/merge.**
