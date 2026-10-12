# ZERO 2.0 — private signed preview build request

Explicitly request a read-only, non-publishing, owner-private APK build for supervised manual Android upgrade testing.

Locked stable reference: OTA99 com.overmind.zero.ota, versionCode 1099, SHA256 02c6670305764209eb6d3daff9805a33a131a264e2bc2a917925b1f8f19376f6.
The preview must match **the exact stable signing certificate**, package ID and versionCode, and the learning code must be shadow-only. Failure of any verification blocks artifact upload.

**Forbidden:** automatic OTA publication, Github release, repository merging, modifying the current user device/profile/slot, importing user brain in APK, faking signing, using a temporary/self-signed key or making ZERO 2.0 influence body motion.

Only upload the final APK as a private 3-day GitHub Actions artifact if all gates pass. On-phone safety and performance checks remain required before any stable rollout.
