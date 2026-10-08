# Roadmap e criteri di accettazione

## Priorità immediata — updater e documentazione
- [x] Whitepaper, architettura, storico e handoff su GitHub.
- [x] Vercel: endpoint HTTPS stabile `/latest.json` (risponde HTTP 200, manifest disabilitato finché manca signing).
- [x] APK pubbliche firmate via Vercel, versioni 1008 e 1010.
- [x] Firma Android permanente configurata nel workflow GitHub Actions.
- [x] Pulsante **Aggiorna**, download HTTPS e verifica SHA-256 implementati nel branch `feature/overmind-inapp-updater` (build CI riuscita).
- [ ] Verifica effettiva del pulsante su dispositivo e installazione firmata end-to-end.
- [x] Build firmate con applicationId invariato e versionCode crescente (1008 → 1010).
- [x] L'utente conferma aggiornamento in-app funzionante su Galaxy S21 (0.3.7 → 0.3.8).
- [ ] Test separato della persistenza dei dati e regressione dopo ulteriori versioni.
- [x] Manifest su Vercel aggiornato dopo CI firmata con SHA-256.
- [ ] Pipeline aggiorna questo CHANGELOG/HANDOFF ad ogni release.

## ZERO-003 — comportamento fisico reale
- [x] Rimuovere la spinta oggetti "magica" al semplice contatto (0.4.0 CI).
- [x] Collisioni simulate tra segmento della membrana, nucleo e oggetti (0.4.0 CI).
- [x] Controller di 18 pesi vergini che apprende a modulare contrazioni e grip vicino agli oggetti (simulazione e CI).
- [x] Test JVM fisica (contatto/no-contatto) e confronto di manipolazione su direzioni diverse (CI).
- [x] Test 0.4.0 ricevuto: urti contabilizzati, ma reale spostamento non visibile e reset dell'episodio ogni 26s riscontrato da codice.
- [x] Fix 0.4.1 pubblicato con tracce visibili, contatori separati e mondo continuo; test CI superati.
- [ ] Verificare sul Galaxy S21 0.4.1 con screenshot video e contatore DISCHI SPOSTATI dopo 10+ minuti.

## ZERO-004 — estensioni del corpo
- [ ] Pseudopodi dinamici legati alla fisica e ai parametri apprenderibili.
- [ ] Vincoli di materia, energia di deformazione e controllo coesione.
- [ ] Verificare efficacia su spinta, trazione e varchi.

## ZERO-005 — modello del mondo e simulazione persistente
- [ ] Previsioni sensoriali e confronto errore predittivo, curiosità intrinseca.
- [ ] Memoria causale e di posizione più ricca.
- [ ] Energia, rischio, crescita e espansione con regolazione dell'autonomia.
- [ ] XP del giocatore non coincidenti con memoria apprenditiva; interventi indiretti.

## ZERO-006 — UX e gioco
- [ ] Grafica tipo ferrofluido nero credibile con animazioni organiche.
- [ ] Mappa interessante e osservatore invisibile; zero comandi alla creatura.
- [ ] Gameplay bilanciato, eventi emergenti, salvataggi robusti.

## Definizione di "fatto"
**Build CI + test JVM + prova APK su S21 + misure + changelog + nuovo file HANDOFF.** Distinguere sempre "realizzato", "validato CI" e "validato sul dispositivo".

## ZERO-005 — Apprendimento delle scelte (v0.5)
- [x] Rete neurale vergine per valutare quale segnale esplorare (91 pesi, senza preferenze pre-addestrate)
- [x] Rimossa la vecchia selezione con formula fissa dal loop di gioco
- [x] Apprendimento online su conseguenze osservate e memoria persistente
- [x] Test CI e APK Release firmata su GitHub
- [ ] Test reale dell'aggiornamento 0.5 sul Galaxy S21
- [ ] Verificare che Vercel possa pubblicare nuovamente dopo esaurimento del limite giornaliero
- [ ] Navigazione autonoma senza direzione geometrica suggerita
