# ZERO OTA154 — six confirmed sensory/motor wiring defects
Date: 15 October 2026. Base: active 2.2 source OTA153 at 1f8c7c1bb82a918d9d3578314f4f95f97030a4c3.

Scope: repair demonstrably incorrect signal pathways without changing physical forces, solver, locomotion reward, policies, model widths, 12 save slots, trained weights, application ID, or signature. This is code correction; independent on-phone competence evidence is still required.

1. `SensorimotorMind.appliedSymbioteControls`: use live `SymbioteMotorHeadV1.measuredSectorAngles` in both efference X/Y channels, sampling real body and membrane in `GameActivity`. Preserve old-body overload.
2. `FluidInteroceptionV1.feed/consequence`: attribute each of 74 physical parcels to nearest real membrane *segment* then its stable six-node sector. Rigid body rotations must preserve cortex-relative pooling. Keep legacy overload and 518/112 features, 84 motor weights and keys. Existing learned interoception parameters persist; changed pooling semantics may require new experience.
3. `InstrumentalNavigation.sense`: accept living membrane and use the same surface-anchored visual ray and solid occlusion as 26D, across birth/resume/action boundaries. Legacy lab overload still available.
4. `SensorimotorMind.Senses.read/spectral8`: evaluate optical range/strength/direction after selecting real tissue eye, including covered-source rim cues; no invisible source identity or target coordinates added to the brain.
5. `choosePrimaryV22`: include `SpatialPerception.choiceBias` precisely once in recurrent `observedMeans`, with the original independent heldout confidence gate and zero virgin bias.
6. `recordTerminal`: share the terminal heldout sample split between the core and cortical consequence before reset. The motor head consumes its pending action exactly once.

Regression `Zero154SensorimotorRegression` and full existing JVM + Android compilation are required to pass before PR merge and signing. Diagnostic differences remain distinct from true gameplay gains. Preserve the SAME current ZERO, never uninstall, reset, or create a new organism for comparing OTA153 vs OTA154. Measure net/gross per life, actual sensor visibility, trained weights, resource encounters and heldout confidence.

Other caveats NOT silently 'fixed': transient tactility vs stable contact represent distinct quantities; XPBD substrate traction mechanics not proven broken; metabolic `work` is a command-integral resource proxy rather than measured force-displacement. Those require separately validated causal experiments.

## Signed release (15 Oct 2026)

Source PR #58 and publisher gate PR #59 merged. QA run 38726087333 SUCCESS (JVM, 96 membrane, 74 fluid, all legacy, Android). Signed publisher run 38727071925 SUCCESS. Official main latest.json: 0.11.6-ota.154 (code1154), signed `https://overmind-zero-updates.vercel.app/apk/overmind-zero-1154-c849b0629e29.apk`, 431364 bytes, SHA256 `c849b0629e29955af69fb40e8d44039cf44cbacb0b2e1f0c2ae0e10683d954ca`. Vercel production dpl_BsTBqVhtRPARgvPebDsXi6ti7grb READY on exact signed main commit. Independent live HTTP byte verification is separate; do not equate build QA or READY with demonstrated intelligent movement on phone.


**Production byte verification completed:** [GitHub Actions LIVE verifier run 38727944242](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/38727944242) **SUCCESS**, 15 Oct 2026 11:31:37 UTC. Independently fetched Vercel production manifest HTTP 200 and APK HTTP 200; confirmed application ID, versionName `0.11.6-ota.154`, versionCode `1154`, exact APK size `431364`, SHA256 `c849b0629e29955af69fb40e8d44039cf44cbacb0b2e1f0c2ae0e10683d954ca`, MIME `application/vnd.android.package-archive`. This establishes LIVE signed distribution, NOT improvement of learned locomotion on Galaxy S21; compare the same existing individual, no reset/uninstall.
