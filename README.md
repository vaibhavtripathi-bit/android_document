# Android & iOS Interview Preparation

A comprehensive collection of deep-dive documentation covering Android and iOS internals for senior-level (L5/L6) interview preparation.

---

## Repository Structure

```
android_document/
├── android/                      # Android deep-dive documentation
│   ├── 01-background-work/
│   ├── 02-coroutines-and-threading/
│   ├── 03-compose-internals/
│   ├── 04-rendering-pipeline/
│   ├── 05-security/
│   ├── 06-ipc-binder/
│   ├── 07-memory-performance/
│   ├── 08-lifecycle-navigation/
│   ├── 09-testing-di/
│   ├── 10-system-services/
│   ├── 11-view-system/
│   ├── 12-modularization-build/
│   └── engineering-problems/
│
├── ios/                          # iOS deep-dive documentation
│   ├── 01-app-lifecycle/
│   ├── 02-uikit-view-system/
│   ├── 03-swiftui-internals/
│   ├── 04-concurrency-threading/
│   ├── 05-memory-performance/
│   ├── 06-networking-data/
│   ├── 07-rendering-graphics/
│   ├── 08-system-frameworks/
│   ├── 09-security-privacy/
│   ├── 10-testing-di/
│   ├── 11-architecture-patterns/
│   ├── 12-build-toolchain/
│   └── 13-api-evolution-history/
│
├── code-samples/                 # Runnable sample projects
│   ├── ipc-binder/
│   │   └── android-client-server-samples/
│   ├── compose/
│   │   └── compose-custom-views/
│   └── background-work/
│       └── workmanager-samples/
│
└── interview-prep/               # Calibration questions & study guides
    ├── onboarding.md
    ├── services.md
    └── ios-utility-libraries.md
```

---

## Android Topics

| # | Topic | Focus Areas |
|---|-------|-------------|
| 01 | Background Work | WorkManager, Services, JobScheduler, Foreground Services |
| 02 | Coroutines & Threading | Coroutine internals, Flow, Dispatchers, Structured Concurrency |
| 03 | Compose Internals | Recomposition, Slot Table, LayoutNode, State management |
| 04 | Rendering Pipeline | Choreographer, VSync, GPU/CPU pipeline, Jank prevention |
| 05 | Security | Keystore, Biometrics, Certificate pinning, ProGuard |
| 06 | IPC & Binder | Binder protocol, AIDL, Messenger, ContentProvider internals |
| 07 | Memory & Performance | GC, Memory leaks, Profiling, ANR debugging |
| 08 | Lifecycle & Navigation | Activity/Fragment lifecycle, Navigation Component, back stack |
| 09 | Testing & DI | Unit/Integration/UI testing, Hilt, Dagger internals |
| 10 | System Services | AMS, WMS, PMS, Context internals |
| 11 | View System | Measure/Layout/Draw pass, RecyclerView internals, custom views |
| 12 | Modularization & Build | Gradle internals, dynamic features, modularization strategies |
| — | **Engineering Problems** | Rate limiting, module communication, crash resilience, caching, remote config, network-aware uploads, PII protection, multi-process safety, concurrency, product flavors |

---

## Engineering Problems (Android)

Real-world production problems with deep-dive solutions — designed for L5/L6 system design rounds.

| # | Problem | Key Concepts |
|---|---------|--------------|
| 01 | Rate Limiting & Server Protection | Token bucket, leaky bucket, client-side throttling |
| 02 | Module Communication via Dagger Maps | Dagger multibindings, decoupled module events |
| 03 | Report Caching & Threshold Sending | Batching strategies, local persistence, flush triggers |
| 04 | Ordered Module Initialization | Dependency graphs, startup sequencing, AppStartup |
| 05 | Crash Resilience & ANR Prevention | Watchdog, uncaught exception handlers, StrictMode |
| 06 | Cross-Module Event Communication | EventBus, SharedFlow, decoupled pub/sub |
| 07 | Remote Configuration Management | Feature flags, A/B testing, Firebase Remote Config |
| 08 | Network-Aware & Roaming-Safe Uploads | ConnectivityManager, WorkManager constraints |
| 09 | Data Anonymization & PII Protection | Hashing, encryption, data masking strategies |
| 10 | Multi-Process Safety & Background Work | Locking, IPC, WorkManager in multi-process apps |
| 11 | Concurrency & Thread Safety | Mutex, atomic ops, coroutine synchronization |
| 12 | Product Flavor & Modular Builds | Build variants, flavor dimensions, conditional modules |

---

## iOS Topics

| # | Topic | Focus Areas |
|---|-------|-------------|
| 01 | App Lifecycle | UIApplicationDelegate, Scene lifecycle, state transitions |
| 02 | UIKit View System | Responder chain, Auto Layout, CALayer |
| 03 | SwiftUI Internals | View diffing, `@State`/`@Binding`/`@ObservableObject` |
| 04 | Concurrency & Threading | GCD, OperationQueue, async/await, actors |
| 05 | Memory & Performance | ARC, retain cycles, Instruments, memory pressure |
| 06 | Networking & Data | URLSession, Codable, CoreData, CloudKit |
| 07 | Rendering & Graphics | Core Animation, Metal, offscreen rendering |
| 08 | System Frameworks | Notifications, background modes, extensions |
| 09 | Security & Privacy | Keychain, App Transport Security, privacy manifests |
| 10 | Testing & DI | XCTest, UI testing, dependency injection patterns |
| 11 | Architecture Patterns | MVC, MVVM, TCA, Coordinator |
| 12 | Build Toolchain | Xcode build system, Swift Package Manager, Instruments |
| 13 | API Evolution & History | Swift evolution, deprecation strategies, backward compatibility |

---

## Code Samples

| Folder | Topic | Description |
|--------|-------|-------------|
| `code-samples/ipc-binder/` | IPC & Binder | Android client/server communication examples |
| `code-samples/compose/` | Compose | Custom composables and layout examples |
| `code-samples/background-work/` | Background Work | WorkManager and scheduling examples |

---

## How to Use This Repo

1. **Topic review** — Navigate to the numbered folder for the topic you want to study.
2. **Run samples** — Open any folder under `code-samples/` in Android Studio.
3. **Interview prep** — Check `interview-prep/` for curated calibration questions.
4. **Navigation** — The numbered folders follow a logical learning order but can be studied independently.
