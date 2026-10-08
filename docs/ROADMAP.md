# Roadmap e criteri di accettazione

## Priorità immediata — updater e documentazione
- [x] Whitepaper, architettura, storico e handoff su GitHub.
- [x] Vercel: endpoint HTTPS stabile `/latest.json` (risponde HTTP 200, manifest disabilitato finché manca signing).
- [ ] Distribuzione APK pubblico su Vercel dopo firma stabile e test.
- [ ] Chiave di firma Android persistente custodita in GitHub Actions Secrets, non nel repo.
- [ ] Pulsante **Aggiorna** e download dell'APK dentro l'app, verifica SHA-256, richiesta di installazione ad Android.
- [ ] Build firmate con stesso applicationId/versionCode crescente; verifica di aggiornamento effettivo su Galaxy S21.
- [ ] Pubblicazione release e aggiornamento del manifest Vercel soltanto se tutti i test sono verdi.
- [ ] Pipeline aggiorna questo CHANGELOG/HANDOFF ad ogni release.

## ZERO-003 — comportamento fisico reale
- [ ] Rimuovere la spinta oggetti "magica" al semplice contatto.
- [ ] Collisioni e forze reali fra corpo a membrana, massa centrale e oggetti.
- [ ] ZERO scopre che alcune deformazioni spostano oggetti.
- [ ] Test quantitativo push force / controllore vergine vs addestrato su oggetti nuovi.

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
