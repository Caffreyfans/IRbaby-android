# Project Guidelines

## Build And Test

- Use the Gradle wrapper from the repository root. On Windows, prefer `./gradlew.bat assembleDebug`, `./gradlew.bat assembleRelease`, and `./gradlew.bat clean`.
- Run `./gradlew.bat test` for local JVM tests when relevant. Run `./gradlew.bat connectedAndroidTest` only when the task requires instrumentation coverage and an emulator or device is available.
- Keep changes compatible with the current Android toolchain unless the task is explicitly an upgrade: Android Gradle Plugin 3.5.3, compileSdkVersion 29, targetSdkVersion 29, minSdkVersion 19.
- The app module has an existing release signing configuration in `app/build.gradle`. Do not change signing settings or embedded credentials unless the task requires it.

## Architecture

- This repository has two Gradle modules: `app` is the Android client, and `web-api` is the iRext API/SDK wrapper used for infrared appliance data.
- In `app/src/main/java/top/caffreyfans/irbaby`, `ui/` contains activities and fragments, `model/` contains persisted data models, `adapter/` contains list/grid adapters, `firmware_api/` contains device communication APIs, and `helper/` contains contracts plus UDP event plumbing.
- `IRApplication` initializes ActiveAndroid, LitePal, guest sign-in for the web API, and the background UDP receive thread during app startup. Treat startup, network, and lifecycle changes carefully.

## Conventions

- Keep changes aligned with the existing Java and AndroidX code style. Do not introduce Kotlin, coroutines, Room, or large architectural rewrites unless the task explicitly asks for them.
- Prefer the existing LitePal-based persistence patterns for `DeviceInfo` and `ApplianceInfo` instead of adding a second persistence approach.
- Reuse existing constants and contracts for intent extras and appliance metadata before adding new keys or duplicated string constants.
- For iRext integration details, link to [web-api/README.md](../web-api/README.md) instead of duplicating SDK setup instructions.

## Pitfalls

- Device discovery and notifications rely on manual threads and global helper classes. Avoid adding more hidden global state, and check lifecycle implications when touching UDP or MQTT flows.
- The manifest currently enables cleartext traffic and includes iRext app metadata. Do not rotate, remove, or rename that configuration unless the task requires it.
- This is a legacy Android codebase with older dependencies and some deprecated patterns. Prefer narrow, compatible fixes over broad modernization by default.

## References

- See [README.md](../README.md) for the top-level project description.
- See [web-api/README.md](../web-api/README.md) for SDK registration and remote-download usage.