# ZERO 3.79 — Training 1, unica stanza personalizzabile (22 ottobre 2026)

## Scopo
Una sola stanza con terreno chiuso e un editor touch per gemme blu (law 0), rosse (law 1), viola (law 3), casse fisiche (law 5) e muro fisso (nuova law 7, 24×24 unità). Nessun nuovo cervello, politica motoria, target, ricompensa o impulso artificiale. Stanze successive potranno essere aggiunte come nuovi layout/snapshot isolati, ma **non esistono in questa versione**.

## Utilizzo
- In GameActivity, quando TEST MAX è spento, pulsante **TRAINING** in alto a sinistra.
- All'apertura: arresta fisica, salva esplorazione originale, crea una nuova vita di TRAINING 1 usando **esattamente lo stesso oggetto neurale Zero3OriginalBrainV356** e pesi appresi; il cervello non è ricreato.
- Editor: seleziona BLU / ROSSA / VIOLA / CASSA / MURO / GOMMA, tocca per creare o cancellare; trascina per tracciare pareti.
- **AVVIA** continua col solito ×1/×5/×20/TEST MAX e le leggi reali del mondo originale. Da **MODIFICA** torna in editor, **ESCI** ripristina la scena fisica originale.
- **PULISCI** rimuove soltanto oggetti dell'editor dopo conferma, non conoscenze del cervello. Il perimetro fisico della stanza non è cancellabile.

## Contratto di persistenza
- `training_room_1_layout_v379`: layout JSON versionato di massimo 120 oggetti manuali.
- `training_room_1_active_v379`: modalità correntemente attiva.
- `training_room_1_scene_v379`: snapshot fisico training, **non** cervello.
- `training_room_1_free_scene_v379`: checkpoint fisico dell'esplorazione libera quando si entra.
- `zero3_original_brain_only_v356`: unico archivio neurale già esistente, invariato (pesi condivisi).
- `physical_scene_v100`: scena libera già esistente, non sovrascritta durante il Training.
- I dati del profilo/personalità, l'identità Android, firma, updater e OTA non sono modificati.
- Entrata in Training avvia una nuova **vita fisica** senza chiamare la terminale per la vita libera; la memoria e i pesi non vengono resettati. Uscita interrompe l'azione incompleta senza credito fittizio e reimposta solo contesto ricorrente a breve termine.

## Fisica, sensori e limiti
- Le pareti sono `UnknownWorld.Entity(law=7)`, immobili e opache alle letture ottiche, con collisioni autentiche su membrana, nucleo e oggetti mobili. Sono escluse dalla firma luminosa delle gemme, ma visibili ai raggi di distanza e al tatto.
- Il mondo Training non genera automaticamente nuovi oggetti/stanze e non introduce tempeste; fame fisiologica e costi muscolari restano reali, come gli effetti energetici dei contatti.
- Perimetro rettangolare largo 480 e alto 640 unità. L'editor offre vista completa e il gioco una camera che segue ZERO.
- Limite snapshot oggetti esteso in modo controllato a 256 per 92 pareti perimetrali + 120 oggetti manuali.
- Nuovo solutore di pareti richiede validazione di topologia membrana, collisioni multiple e prestazioni su Galaxy S21; il debug compile non è una prova scientifica.

## Collaudi e rilascio
- Branch `feature/zero379-single-training-room` derivato dal vero sorgente OTA378 `fix/zero378-physical-inertial-credit-20261020`.
- Android instrumentation `Zero379SingleTrainingRoomAndroidTest`: layout, ottica, contatto parete, persistenza e transizione senza reset dei pesi.
- Workflow `zero379-training-qa.yml`: assembleDebug + assembleDebugAndroidTest + emulator tests, **non pubblica OTA**.
- NON dichiarare APK firmato o OTA pubblicata finché workflow, firma e manifest siano stati verificati.
