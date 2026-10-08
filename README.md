# Idle Runner · Aurora Dash

Native 2D Android endless runner with procedurally rendered graphics, variable-height jumps, reliable double-jump, coins, obstacles, and multiple evolving environments.

## Build APK

Every push to `main` triggers **Actions → Android APK** and uploads `idle-runner-debug.apk` as a workflow artifact. A GitHub release is also created with an APK attachment. Because this repository is private, sign into GitHub to download it.

## Controls

- Short tap: low jump.
- Press and hold: higher jump.
- Second tap in mid-air: double jump.
- Top-right button: pause / resume.

## Tech

Android Java (no game framework), Gradle, drawing with Canvas at device refresh rate. No external graphics dependencies. Target Android 7.0+.
