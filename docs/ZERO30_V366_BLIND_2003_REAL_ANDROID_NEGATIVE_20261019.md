# ZERO366 — ESITO REALE NEGATIVO: 2003 DECISIONI AUTONOME, NESSUN CONTATTO ENERGETICO

**Data:** 19 ottobre 2026. **Codice sperimentale:** `fix/zero364-block-regressed-ota-and-forensic-20261019` (PR #99 DRAFT). **Produzione:** `3.0-ota.363`, versionCode1181; FIRMA OTA bloccata dal gate PR100. Questo report NON autorizza rilascio.

## Prova originale reale, non laboratorio di direzioni
- Originale `GameActivity`, vero `SymbioteMatterV1` originale, normale `UnknownWorld` e `WorldPlacement.spawn`, griglia corpi/membrane/collisioni originali, sensori PRE194, GRU64 V356 vergine, actor/critic V352, world model V353, executive context V366, vecchia periferica fisica 2.2 riusata **solo a valle dell'unico decisore V3**, vero bilancio energetico, 100/100 alla nascita.
- NESSUNA direzione prescritta, nessun incentivo per blu/rosso, nessuna ricompensa irreale, nessun teletrasporto, nessuna risorsa iniettata. L'emulatore Android ha eseguito 2003 intenzioni autonome (effettivo `Lab.onDraw` con TEST MAX).
- Rendering disabilitato **SOLO PER QA** con `qaSuppressPaintV366` per evitare crash GPU dell'emulatore e accelerare lo stesso ciclo fisico; codice di simulazione, coscienza, reward, save e input non alterati.
- [GitHub Actions 39316508319](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39316508319) FAIL SCIENTIFICO, log fisiologico originale allegato artifact12217848360. Presa decisionale a 2003.

## Dati indipendenti dal testo dell'utente
| Decisioni | Pickup blu | Contatti dannosi | Progressione lungo propria intenzione (U) | Distanza lorda materiale (U) | Efficienza | Spostamento fisico X/Y (U) |
|---:|---:|---:|---:|---:|---:|---|
| 2003 | **0** | **0** | 216.45485 | 2136.19156 | **10.13%** | (+25.8154, −47.9209) |

Diario real-time attraverso 3 checkpoint logcat `ZERO366_REAL`:
- decision0: pick0 harm0 gross0 signed0;
- decision742: pick0 harm0 gross778.389 signed59.848;
- decision1471: pick0 harm0 gross1560.094 signed145.839;
- decision2003: pick0 harm0 gross2136.19156 signed216.45485.

Confronto telefonico precedente **OTA363** (non matched world/seed, quindi NON stima causale rigorosa): decision3100, 0 blu, 30 rossi, signed22.08807U su gross2011.07753U = **1.10%**, sceltaX -138.27065 contro spostamentoX +1144.74303. Prototipo V366 ha una locomozione materialmente più coerente, ma 10.13% < la soglia blind prereg 12%, e NESSUN contatto, neppure iniziale, quindi **non ha appreso valenza energetica da risorse**. Zero danni non dimostra evitamento, visto che zero raccolte+zero contatti significano nessuna interazione fisica con le risorse.

L'emulatore ha richiesto correzioni SOLO al QA: `startActivitySync` attende erroneamente UI idle su gioco con redraw continuo, sostituito col monitor Activity del precedente V362; parsing `LifeStatistics.ls2` corretto per current+history; grafica color buffer GPU instabile in TEST MAX, bypass solo del painting nel QA. La prima vera prova conclusa ha prodotte 2003 decisioni senza script e ha bocciato comportamento.

## Capacità preesistenti da NON toccare e cosa NON dedurre
- [17/17 regression Android CI 39316508292](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/39316508292) VERDE: ereditati V356/361/362 (9), nuovo bridge fisico V364 (3), nuova vista V366/migrazione memoria e costo predittivo V353 (5).
- Significa **funzioni tecniche funzionanti**, NON obiettivo di sopravvivenza raggiunto. Non sostituire V319 grip, V332 local-credit test, V353 12-head, V354 memories, V356 GRU, né salvataggi.
- Il blueprint §9 autorizza curiosità SOLO come riduzione verificata dell'errore predittivo in contesti nuovi, non come sorpresa o reward gratuito. Il mondo originale piazza oggetti visibili a distanze ~130–218U dall'origine (`UnknownWorld.newHabitat`), e la retina spettrale vede entro210U (`SensorimotorMind.Senses.spectral8`). Uno spostamento netto residuo di soli ~54U NON prova che in 2003 scelte il corpo abbia toccato l'area iniziale delle risorse.
- Non assumere il GRU non veda: ha 194 ingressi, spettro con angolo; V354 era aliasato, V366 aggiunge 3 bearing non categoriali e mantiene gli archivi vecchi.
- Non assumere che un modello PRE→POST possa apprendere la valenza di blu/rosso quando su 2003 decisioni HA 0 esempi di contatto fisico. Il costo e l'intenzione possono imparare, la preferenza per risorse rimane senza osservazioni.
- Il precedente 2.2 possedeva un sistema esplorativo/memoria `UnknownWorld.Mind.choose` e sensore `ExperienceCredit`, che nel V356 GRU solo decisore non vengono chiamati. Questa differenza architetturale NON autorizza riattivare il cervello 2.2 come secondo guidatore o programmare direzioni verso gemme.

## Prossimi esperimenti vincolanti (non cambiare cieca forza/attrito e non pubblicare)
1. Strumento QA READ-ONLY per 4 mondi speculari e molte vite: per ogni nascita registrare distanza minima dal **materiale corporeo effettivo** a ciascuna classe fisica di risorsa (solo analisi esterna al cervello) e luminosità spettrale realmente presente nei 194 ingressi PRE; registrare prima dozzina di intenzioni vs delta COM e se il valore GRU/executive usa il contesto.
2. Separare **non percepito** da **percepito ma mai raggiunto**. Non lasciare il GRU con 0 esempi energetici e chiamarlo apprendimento di blu/rosso.
3. Verificare se curiosità **GIÀ IMPLEMENTATA** nel V353/V354/G3 può aiutare ad accumulare esperienze reali rispettando il master; solo se assente nel runtime, presentare ipotesi di integrazione sottoposta a A/B tenendo un unico attore e senza goal hard-coded, con nessuna ricompensa artificiale di movimento.
4. Per promozione: 2k e 10k scelte autonomamente in più habitat non visti, confronto con policy frozen/matched, raccolte blu genuine >0, calo contatti dannosi misurato CON esposizioni effettive, signed intent oltre gate12%, sopravvivenza, ripresa da salvataggio CRC V356. Va mantenuta la PR99 DRAFT finché G3/G4/G5 non superano.

**VERDETTO:** correzione dei collegamenti esiste e i test unitari sono verdi, ma **ZERO3 non è ancora riparato per lo scopo dell'utente**. Non pubblicare V366; OTA363 resta versione pubblica, nessun cervello utente azzerato.
