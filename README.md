# Android, iOS, React JS & React Native Interview Preparation

A comprehensive collection of deep-dive documentation covering Android, iOS, React JS, and React Native internals, plus an Android system design track for senior-level (L5/L6) interview preparation.

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
│   ├── 13-media-streaming/
│   ├── 14-large-screen-foldables/
│   ├── 15-on-device-ml/
│   ├── 16-android-tv/
│   └── engineering-problems/
│
├── android_system_design/        # Android system design prep track
│   ├── interview-tips/
│   ├── 00-prep-plan.html
│   └── 09-case-study-video-player.html
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
├── react-js/                     # React JS web deep-dive documentation
│   ├── 01-react-fundamentals/
│   ├── 02-rendering-reconciliation/
│   ├── 03-hooks-internals/
│   ├── 04-state-management/
│   ├── 05-routing-navigation/
│   ├── 06-performance-optimization/
│   ├── 07-server-side-rendering/
│   ├── 08-testing-patterns/
│   ├── 09-build-toolchain/
│   ├── 10-security-web/
│   ├── 11-architecture-patterns/
│   ├── 12-browser-internals/
│   └── engineering-problems/
│
├── react-native/                 # React Native mobile deep-dive documentation
│   ├── 01-architecture-bridge/
│   ├── 02-new-architecture/
│   ├── 03-rendering-pipeline/
│   ├── 04-navigation-deep-dive/
│   ├── 05-native-modules/
│   ├── 06-performance-optimization/
│   ├── 07-state-management/
│   ├── 08-animations-gestures/
│   ├── 09-platform-specific/
│   ├── 10-testing-debugging/
│   ├── 11-security-storage/
│   ├── 12-build-deployment/
│   └── engineering-problems/
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
| 13 | Media & Streaming | Media3, HLS/DASH, ABR, Widevine DRM, offline playback |
| 14 | Large Screen & Foldables | Window size classes, fold postures, adaptive layouts, multi-window |
| 15 | On-Device ML | TFLite, ML Kit, model optimization, delegates, inference pipelines |
| 16 | Android TV | Leanback UI, D-pad navigation, TV architecture, playback UX |
| — | **Engineering Problems** | Rate limiting, module communication, crash resilience, caching, remote config, network-aware uploads, PII protection, multi-process safety, concurrency, product flavors |

---

## Android System Design Track

The `android_system_design/` folder complements the deep-dive topic notes with system design preparation focused on Staff/L6 interviews.

| Area | Focus Areas |
|---|---|
| Core Architecture | Architecture patterns, offline-first networking, local storage, SDK/library design |
| Platform Systems | Media rendering, background work, security/privacy, observability, release engineering |
| Case Studies | Video player, messaging app, analytics SDK |
| Interview Prep | Android domain tips, system design tips, coding tips, behavioral preparation |

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

## React JS Topics

| # | Topic | Focus Areas |
|---|-------|-------------|
| 01 | React Fundamentals | JSX transformation, Fiber architecture, virtual DOM, reconciliation |
| 02 | Rendering & Reconciliation | Render phases, Scheduler lanes, concurrent features, Suspense, hydration |
| 03 | Hooks Internals | Hooks linked list, useState/useReducer, useEffect lifecycle, React 19 use() |
| 04 | State Management | Redux internals, Zustand/Jotai/Signals, TanStack Query, state machines |
| 05 | Routing & Navigation | History API, React Router v6, data loading, code splitting, Next.js App Router |
| 06 | Performance Optimization | Rendering perf, memoization, virtualization, Web Vitals, memory management |
| 07 | Server-Side Rendering | SSR/SSG/ISR, React Server Components, streaming, Next.js, edge rendering |
| 08 | Testing Patterns | RTL, MSW, Playwright/Cypress, mocking, performance testing, CI infrastructure |
| 09 | Build Toolchain | Module systems, Webpack/Vite/Turbopack, transpilation, TypeScript, CSS solutions |
| 10 | Security & Web | XSS, CSRF, authentication, authorization, dependency security, browser security |
| 11 | Architecture Patterns | Component architecture, design systems, micro-frontends, accessibility |
| 12 | Browser Internals | Rendering engine, V8 internals, event loop, DOM APIs, Web Workers |
| — | **Engineering Problems** | Real-time collaboration, infinite scroll, offline-first, design systems at scale, performance monitoring, micro-frontend migration, search typeahead, image gallery, form builder, error tracking, feature flags, cross-tab state sync |

---

## React Native Topics

| # | Topic | Focus Areas |
|---|-------|-------------|
| 01 | Architecture & Bridge | Bridge architecture, Hermes/JSC, native modules, shadow tree, Yoga, Metro |
| 02 | New Architecture | JSI, Fabric renderer, TurboModules, Codegen, bridgeless mode, concurrent rendering |
| 03 | Rendering Pipeline | Reconciliation to native, view flattening, FlatList/FlashList, image handling |
| 04 | Navigation Deep Dive | React Navigation internals, native stack, deep linking, navigation performance |
| 05 | Native Modules | Native module creation, TurboModules, native UI, platform APIs, Expo Modules |
| 06 | Performance Optimization | JS thread, rendering, memory, startup, animation, network, bundle size |
| 07 | State Management | Redux/Zustand/MobX in RN, persistent storage (MMKV/SQLite/Realm), state sync |
| 08 | Animations & Gestures | Animated API, Reanimated v3, Gesture Handler, Skia graphics, shared elements |
| 09 | Platform-Specific | iOS/Android specifics, responsive design, accessibility, i18n, RN for Web |
| 10 | Testing & Debugging | Jest, Detox E2E, Flipper, profiling, crash reporting, CI/CD mobile |
| 11 | Security & Storage | Keychain/Keystore, certificate pinning, code protection, biometric auth |
| 12 | Build & Deployment | Metro config, native builds, Expo, OTA updates, app signing, monorepo |
| — | **Engineering Problems** | Chat app, offline-first, performance audit, native-to-RN migration, push notifications, image-heavy apps, payments, biometric auth, analytics SDK, multi-app platform, accessibility overhaul, background processing |

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
