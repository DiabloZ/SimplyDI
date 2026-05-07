# SimplyDI — Agent Instructions

## Project Overview

Kotlin multi-module DI (dependency injection) library for Android. Published to Maven Central under group `io.github.diabloz`.

Two independent DI frameworks live in this repo:
- **SimplyDI** — Android-focused (modules: `SimplyDICore`, `SimplyDIAndroid`, `SimplyDICompose`, `SimplyDIViewModel`)
- **KDI** — Pure Kotlin/JVM, no Android dependencies

## Module Map

| Module | Type | Role |
|---|---|---|
| `SimplyDICore` | Android library | Core DI engine. Singleton `SimplyDIContainer.instance`. Scopes, dependency registration, lazy wrappers. |
| `SimplyDIAndroid` | Android library | Android lifecycle extensions. Depends on `SimplyDICore` + `SimplyDIViewModel`. |
| `SimplyDIViewModel` | Android library | ViewModel factory integration. Depends on `SimplyDICore`. |
| `SimplyDICompose` | Android library | Compose integration. Depends on `SimplyDICore` + `SimplyDIViewModel`. |
| `KDI` | **Pure JVM** (`java-library` + `kotlin-jvm`) | Standalone DI for non-Android. No Android deps. |
| `app` | Android application | Demo/test app. Uses KDI + published SimplyDI artifacts. |
| `SomeTestModule` | Android library | Test fixture module consumed by `app`. |

Orphaned directories (no `build.gradle.kts`, not in `settings.gradle.kts`):
- `SimplyDIAnnotations/`, `SimplyDIProcessor/` — contain only stale `build/` artifacts. Not active.

## Build

Gradle 8.7, AGP 8.5.0, Kotlin 1.9.22. Java 8 target.

```bash
./gradlew clean build
```

Per-module build:
```bash
./gradlew :SimplyDICore:build
./gradlew :KDI:build
```

## Publish

Credentials live in `gradle.properties` (mavenCentral, signing). All publishable modules use `com.vanniktech.maven.publish` with `SonatypeHost.CENTRAL_PORTAL` and `signAllPublications()`.

```bash
./gradlew clean build publishAllPublicationsToMavenCentralRepository
# or per-module:
./gradlew :KDI:publishMavenPublicationToMavenCentralRepository
```

## Key Conventions

- All source files are `.kt` in `src/main/java/` (not `src/main/kotlin/`).
- SDK modules use `-Xexplicit-api=strict` (public API must be explicitly annotated). The `app` module disables this.
- Versions centralized in `gradle/libs.versions.toml`. Catalog aliases used throughout.
- `app` depends on KDI via `project(":KDI")` and on published SimplyDI artifacts via Maven (catalog refs `simply-di-*`). Uncommented `project(":SimplyDI*")` lines show prior local-dev wiring.
- `.gitignore` excludes `*.properties`, `*.gpg`, `*.asc`, `*.txt` — so `gradle.properties` with credentials is not committed in clean state but is present locally.
- No CI workflows, no pre-commit hooks, no lint/ktlint configuration. No automated test or lint gating.

## Testing

Each module has only stub `ExampleUnitTest` / `ExampleInstrumentedTest` files. No meaningful test suite exists.

Android tests require an emulator or device. JVM tests run with:
```bash
./gradlew :<module>:test
```
