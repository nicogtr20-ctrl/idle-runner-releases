# ZERO Symbiote OTA144 — Avvio dopo «CREA ZERO SIMBIONTE»: crash critico corretto

## Segnalazione
Dopo installazione OTA143 (versionCode 1143) e creazione di un nuovo ZERO Simbionte da «Partite di ZERO», la app si chiude subito; i successivi tentativi di aprirla causano lo stesso arresto. **Non richiedere disinstallazione né azzeramento dati: cancellerebbe i salvataggi del veterano.**

## Radice identificata nel sorgente
`GameActivity.Lab.onDraw` imposta `lastFrame=0` durante una nuova Activity o ripresa. Alla prima chiamata `dt=lastFrame==0?0:...` e successivamente in modalità 1×5×20 `slice=Math.min(dt,.035f)`, quindi `slice=0`.

Per il corpo antico `MotorBrain.Body.step`/ `Morphology.Surface.step` questo primo frame nullo non lanciava l'eccezione. Per ogni nuovo profilo Symbiote OTA143, `SymbioteMatterV1.step` valida `dt<=0` come un errore fisico e lancia `IllegalArgumentException("Invalid symbiote physical input")`. Non essendo intercettato in `onDraw`, provoca crash dell'Activity. L'utente non può riaprire l'app perché il flag `symbiote_body_v1_enabled=true` è salvato nello storage del profilo di lavoro. È un problema deterministico del flusso temporale e **non del livello di intelligenza di ZERO o di un salvataggio compromesso**.

## Patch OTA144
```java
float slice=fullSpeed?TEST_MAX_FIXED_DT:Math.min(dt,.035f);
if(!Float.isFinite(slice)||slice<=0f)continue;
if(fullSpeed)testMaxSimSeconds+=slice;
```
L'integrazione con durata fisica nulla (o non finita) non viene chiamata: salta l'intero step fino a un intervallo positivo. Il guard avviene prima del passo `symMatter.step`, ma anche prima dei sensori, effetti e conseguenze, evitando di attribuire un evento a un intervallo di tempo nullo. Non si modifica alcuna costante fisica, né si riconverte il corpo. Al prossimo frame normale il profilo riprende con cervello e materia salvati.

## Test e release
- PR #48 «CRITICAL OTA144: prevent 100% Symbiote boot crash on zero-duration first frame» merge sul branch ufficiale, source hash `2fff4344052bc5d70bab8be728e5f81eabe80e04`.
- Test QA del branch hotfix: GitHub Actions run `38649252279` SUCCESS; verifica esplicita dell'ordine guard-tempo prima del passo fisico; regressione Symbiote Matter + prove originali e compilazione Android.
- Publisher `38649408621` SUCCESS. Firmata con chiave di `com.overmind.zero.ota`, versione `0.11.6-ota.144`, versionCode `1144`.
- APK `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1144-61f359419902.apk`, **415584 byte**, SHA256 `61f359419902b4a7f5fe808b092b941fdde9df40143f694197539766284d98ff`.
- Verifica esterna di manifesto live e bytes/sha APK nel workflow `Verify LIVE OVERMIND OTA delivery` (vedere il risultato). Non dedurre che sia stato testato manualmente sul Galaxy S21: sono passati test automatizzati JVM/Android e firma.

## Installazione per chi ha app che crasha
1. Aprire il link APK in un browser esterno o questa chat. NON aprire la versione crashante.
2. Installare SOPRA la versione corrente (stesso package + firma). NON disinstallare. NON cancellare dati/app storage.
3. Aprire ZERO: il profilo di lavoro Symbiote deve riprendere dal punto salvato e poter completare i frame iniziali; in seguito usare `Cervello → Partite di ZERO` per accedere al veterano e ai 12 slot nominati.
4. Se ancora crasha, raccogliere `adb logcat -d -s AndroidRuntime:E` oppure log cat tramite ADB sul proprio PC, e allegare lo stacktrace senza azzerare dati, per cercare eventuale secondo bug non rilevato.

## Safety
Nessun reset automatico del profilo o della memoria; no rimozione di `symbiote_body_v1_enabled`; nessun downgrade della versione (firma/versionCode monotono). Il codice stesso scarta frame fisici con dt=0 per tutti gli organismi. Vecchi salvataggi restano su vecchio materiale; nuovi Symbiote vengono preservati, non ricreati.
