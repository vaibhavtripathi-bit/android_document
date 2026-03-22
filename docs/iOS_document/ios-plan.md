# iOS L6 Interview Prep — Comprehensive Documentation Plan

This mirrors the Android project's depth (13 series, ~100 docs, ~650 interview questions) but adapted for iOS/Apple platform internals. Target: **Google L6 / Staff-level iOS domain interview**.

---

## Series 1: `app-lifecycle/` — App & Scene Lifecycle (8-9 docs, ~60 questions)

| Doc | Topics |
|-----|--------|
| **01-app-delegate-evolution** | `UIApplicationDelegate` history: subclass of `UIResponder` (why?), evolution from iOS 2 → iOS 13 scene lifecycle → iOS 14+ `@main` + `App` protocol in SwiftUI. `UIApplicationMain` vs `@main`. `willFinishLaunching` vs `didFinishLaunching` ordering. Pre-main time (`dyld`, `+load`, `+initialize`). |
| **02-scene-lifecycle** | `UIScene` / `UISceneSession` / `UISceneDelegate` / `UIWindowScene`. Multi-window on iPad. Scene connection/disconnection vs app termination. `UISceneConfiguration` from Info.plist vs runtime. State restoration with `NSUserActivity`. |
| **03-process-lifecycle** | Background execution model. `beginBackgroundTask`. Background modes. `BGTaskScheduler` (BGAppRefreshTask, BGProcessingTask). Jetsam (iOS OOM killer) — no `didReceiveMemoryWarning` guarantee. Process suspension vs termination. |
| **04-state-restoration** | `NSCoder`-based (legacy) vs `NSUserActivity`-based vs SwiftUI `@SceneStorage`. `restorationIdentifier` chain. Why state restoration broke in scene-based apps. Codable state. |
| **05-deep-links-universal-links** | URL Schemes vs Universal Links vs App Clips. `apple-app-site-association` (AASA) file. `NSUserActivity` for Handoff. `UIOpenURLContext`. Deferred deep links. Routing architecture at scale. |
| **06-extensions-lifecycle** | App Extension lifecycle (Today Widget, Share, Notification Content/Service, Intents). Memory limits (40-120MB). No shared address space. App Groups for data sharing. Extension vs containing app process model. |
| **07-interview-qa** | 60+ L6 questions covering edge cases, API evolution, process death, multi-scene, background execution. |

**Key evolution to cover**: `UIApplicationDelegate` was a subclass of `UIResponder` — why? (responder chain integration for remote notifications, motion events). Scene-based lifecycle broke many singleton patterns.

---

## Series 2: `uikit-view-system/` — UIKit View Internals (9-10 docs, ~70 questions)

| Doc | Topics |
|-----|--------|
| **01-view-hierarchy-internals** | `UIView` / `CALayer` duality. Why views have layers (AppKit history). `UIWindow` → `UIView` tree. `hitTest(_:with:)` + `point(inside:with:)` — reverse pre-order traversal. `UIView` vs `CALayer` coordinate systems. |
| **02-touch-event-flow** | **Full touch pipeline**: IOKit HID → SpringBoard → `UIApplication.sendEvent()` → `UIWindow.sendEvent()` → hit testing → `UIResponder` chain. `UITouch` lifecycle (began/moved/ended/cancelled). `UIEvent` types. `touchesBegan/Moved/Ended/Cancelled`. Gesture recognizer state machine. `UIGestureRecognizer` internals: `require(toFail:)`, simultaneous recognition, delegate methods. `UIScrollView` delayed touch delivery (150ms). |
| **03-layout-system** | Auto Layout: Cassowary simplex solver. Constraint priorities. `intrinsicContentSize`. `contentHuggingPriority` / `contentCompressionResistancePriority`. `layoutSubviews()` vs `setNeedsLayout()` vs `layoutIfNeeded()`. `updateConstraints()` cycle. Layout passes. `UIStackView` internals. `translatesAutoresizingMaskIntoConstraints`. Performance: constraint churn, `systemLayoutSizeFitting`. |
| **04-rendering-pipeline** | Core Animation render pipeline: commit transaction → render server (backboardd) → GPU. `CATransaction` implicit vs explicit. `CALayer.display()` → `draw(in:)`. Off-screen rendering triggers. `shouldRasterize`. Compositing. Metal backing. `CADisplayLink` + ProMotion (120Hz). Frame pacing. |
| **05-drawing-core-graphics** | `UIGraphicsImageRenderer` vs `UIGraphicsBeginImageContext` (why the old API is bad). `CGContext`. `draw(_:)` vs `draw(in:)`. PDF rendering. `UIBezierPath`. Text rendering with TextKit 1 vs TextKit 2. `NSAttributedString` performance. |
| **06-custom-views** | Building custom UIView subclasses. `intrinsicContentSize` contract. `sizeThatFits` vs Auto Layout. State invalidation patterns. `@IBDesignable`/`@IBInspectable` (and why they're dying). Accessibility: `UIAccessibility` protocol, traits, custom actions. Dynamic Type. |
| **07-animations** | `UIView.animate` (block-based) → `UIViewPropertyAnimator` (interruptible). Core Animation: `CABasicAnimation`, `CAKeyframeAnimation`, `CASpringAnimation`, `CAAnimationGroup`. Presentation layer vs model layer. `UIPercentDrivenInteractiveTransition`. `UIViewControllerTransitioningDelegate`. Hero animations. `UIMotionEffect`. |
| **08-table-collection-views** | `UITableView` / `UICollectionView` internals. Cell reuse queue. `UICollectionViewCompositionalLayout`. `UICollectionViewDiffableDataSource` + `NSDiffableDataSourceSnapshot`. `CellRegistration`. Prefetching (`UICollectionViewDataSourcePrefetching`). Self-sizing cells. Compositional layout: section providers, orthogonal scrolling, decorations. `UICollectionViewListCell`. |
| **09-interview-qa** | 70+ L6 questions on touch flow, rendering, layout performance, animation internals, collection view edge cases. |

**Key deep dives**: The full touch pipeline from hardware to view is a classic Staff-level question. `hitTest` reverse pre-order traversal. Why `UIView` wraps `CALayer` (historical AppKit split). Off-screen rendering triggers.

---

## Series 3: `swiftui-internals/` — SwiftUI Runtime & Internals (8-9 docs, ~60 questions)

| Doc | Topics |
|-----|--------|
| **01-swiftui-runtime** | `AttributeGraph` (AG) — the dependency graph engine. `ViewGraph`. How `body` is called. Structural identity vs explicit identity. `_ViewDebug`. `_printChanges()`. |
| **02-view-identity-diffing** | Structural identity (position in `ViewBuilder`). `id()` modifier. `ForEach` identity. AnyView type erasure cost. `@ViewBuilder` result builder internals. Conditional view identity pitfalls (`if/else` destroys state). |
| **03-state-management** | `@State` (heap-allocated, view-scoped). `@Binding` (projected value). `@StateObject` vs `@ObservedObject` (ownership). `@EnvironmentObject`. `@Observable` macro (iOS 17). `@Bindable`. Observation tracking (`withObservationTracking`). Why `@Published` triggers full recomputation vs `@Observable` per-property tracking. |
| **04-layout-system** | `ProposedViewSize` → `sizeThatFits` → placement. `Layout` protocol. `GeometryReader` (and why it's often misused). `alignmentGuide`. `frame` vs `fixedSize`. `ViewThatFits`. Custom `Layout` protocol (iOS 16). `LayoutValueKey`. |
| **05-rendering-integration** | SwiftUI → Core Animation bridge. `UIHostingController` internals. `_UIHostingView`. Render loop integration with CADisplayLink. Metal backing. `drawingGroup()`. `Canvas` view (iOS 15). `TimelineView`. |
| **06-navigation-evolution** | `NavigationView` (deprecated) → `NavigationStack` / `NavigationSplitView`. `NavigationPath`. `navigationDestination(for:)`. Programmatic navigation. Deep linking. `NavigationLink` value-based vs view-based. Coordinator pattern in SwiftUI. |
| **07-performance** | Lazy containers (`LazyVStack`, `LazyHStack`, `LazyVGrid`). `equatable()` modifier. View identity stability. Reducing `body` recomputation. Instruments: SwiftUI profiling template. `Self._printChanges()`. Task modifier lifecycle. |
| **08-interop** | `UIViewRepresentable` / `UIViewControllerRepresentable`. Coordinator pattern. `UIHostingController` embedding. Incremental adoption strategy. Two-way data flow. Sizing challenges. |
| **09-interview-qa** | 60+ questions on AG internals, identity, state, performance, navigation, interop edge cases. |

---

## Series 4: `concurrency-threading/` — Swift Concurrency & Threading (7-8 docs, ~55 questions)

| Doc | Topics |
|-----|--------|
| **01-gcd-internals** | `libdispatch` internals. Dispatch queues (serial/concurrent). Thread pool (64 thread limit). Quality of Service (QoS) classes. Priority inversion. `DispatchWorkItem`. `DispatchGroup`. `DispatchSemaphore`. `DispatchSource`. Target queue hierarchy. |
| **02-operation-queues** | `OperationQueue` / `Operation`. Dependencies. Cancellation. KVO on `isFinished`/`isExecuting`. `BlockOperation`. When to use over GCD. |
| **03-swift-concurrency** | `async/await`. Structured concurrency (`TaskGroup`, `withTaskGroup`). `Task` vs `Task.detached`. Task cancellation (cooperative). Task priority. `TaskLocal`. Sendable protocol. Actor model. |
| **04-actors-isolation** | `actor` keyword. Actor reentrancy problem. `@MainActor`. Global actors. `nonisolated`. Actor isolation checking. `@Sendable` closures. Data race safety. Swift 6 strict concurrency. |
| **05-async-sequences** | `AsyncSequence` / `AsyncStream` / `AsyncThrowingStream`. `for await`. Backpressure. Bridging Combine → AsyncSequence. `withCheckedContinuation` / `withCheckedThrowingContinuation`. |
| **06-combine-framework** | `Publisher` / `Subscriber` / `Subscription` (backpressure). `CurrentValueSubject` / `PassthroughSubject`. Operators. `@Published`. Combine vs AsyncSequence vs Observation framework. Memory management (`AnyCancellable`, `store(in:)`). |
| **07-main-thread-runloop** | `RunLoop` modes (default, tracking, common). `CFRunLoop` sources. Why `Timer` doesn't fire during scrolling. `RunLoop.main` vs `DispatchQueue.main`. Main actor. UI thread safety. |
| **08-interview-qa** | 55+ questions on GCD internals, actor reentrancy, Sendable, Combine backpressure, RunLoop edge cases. |

---

## Series 5: `memory-performance/` — Memory Management & Performance (8 docs, ~60 questions)

| Doc | Topics |
|-----|--------|
| **01-arc-internals** | ARC (Automatic Reference Counting) vs GC. Retain/release under the hood. Side tables. Weak reference zeroing. `unowned` vs `weak` (crash vs nil). `withExtendedLifetime`. Copy-on-write for value types. Swift object layout (metadata, refcount, fields). |
| **02-retain-cycles** | Closure capture lists. `[weak self]` vs `[unowned self]`. Delegate patterns. `NotificationCenter` observer leaks (pre-iOS 9 vs post). Timer retain cycles. Combine `sink` leaks. `@MainActor` closure captures. |
| **03-memory-regions** | Stack vs heap. Value types vs reference types. `ContiguousArray`. `ManagedBuffer`. `UnsafeBufferPointer`. Autorelease pools (`@autoreleasepool`). ObjC bridging costs. Tagged pointers. `isa` swizzling. |
| **04-jetsam-memory-pressure** | Jetsam (iOS OOM killer). Memory footprint vs dirty memory vs compressed memory. `os_proc_available_memory()`. `didReceiveMemoryWarning` unreliability. Jetsam priority bands. Background memory limits (~50MB). `MetricKit` memory reports. |
| **05-app-startup** | Pre-main: dyld4 (closures, shared cache), `+load` vs `+initialize`, static initializers. Post-main: first frame. `UIScene` connection timing. Instruments: App Launch template. `os_signpost`. `MetricKit` launch diagnostics. Reducing launch time. |
| **06-instruments-profiling** | Time Profiler, Allocations, Leaks, VM Tracker, System Trace, Metal System Trace, Network, Energy. Xcode Memory Graph Debugger. `MXMetricManager`. Custom `os_signpost` intervals. `OSLog` structured logging. |
| **07-energy-thermal** | `ProcessInfo.ThermalState`. CPU/GPU throttling. Background energy budget. `BGTaskScheduler` energy awareness. Location accuracy vs energy. Network coalescing. Push vs poll. `MetricKit` energy metrics. |
| **08-interview-qa** | 60+ questions on ARC edge cases, Jetsam, startup optimization, profiling, energy. |

---

## Series 6: `networking-data/` — Networking & Data Persistence (7-8 docs, ~50 questions)

| Doc | Topics |
|-----|--------|
| **01-urlsession-internals** | `URLSession` architecture. `URLSessionConfiguration` (default/ephemeral/background). `URLSessionTask` hierarchy. HTTP/2 multiplexing. `URLProtocol` interception. `URLCache`. TLS/ATS. Certificate pinning. Background transfers. `URLSessionWebSocketTask`. |
| **02-core-data** | `NSManagedObjectContext` / `NSPersistentStoreCoordinator` / `NSPersistentContainer`. Concurrency: `perform`/`performAndWait`. `NSFetchedResultsController`. Lightweight migration. `NSBatchInsertRequest`/`NSBatchDeleteRequest`. CloudKit sync (`NSPersistentCloudKitContainer`). `NSMergePolicy`. Faulting. |
| **03-swift-data** | `@Model` macro. `ModelContainer` / `ModelContext`. `@Query`. Relationship management. Migration. SwiftData vs Core Data internals (same SQLite backing). Limitations. |
| **04-keychain-security** | Keychain Services API. `kSecAttrAccessible` options. Biometric protection (`SecAccessControl`). Keychain sharing (App Groups). Keychain vs UserDefaults vs file protection. `CryptoKit`. App Attest. Device Check. |
| **05-file-system-sandbox** | App sandbox structure (Bundle, Documents, Library, tmp). File protection classes (`NSFileProtectionComplete` etc.). `FileManager`. App Groups shared container. iCloud document sync. `UIDocument`. |
| **06-serialization** | `Codable` internals (compiler-synthesized). `JSONEncoder`/`JSONDecoder` performance. `PropertyListEncoder`. Custom `encode(to:)`/`init(from:)`. `@propertyWrapper` for decoding. Protocol Buffers / FlatBuffers on iOS. |
| **07-interview-qa** | 50+ questions on URLSession edge cases, Core Data concurrency, Keychain, sandbox, serialization. |

---

## Series 7: `rendering-graphics/` — Rendering Pipeline & Graphics (7 docs, ~45 questions)

| Doc | Topics |
|-----|--------|
| **01-core-animation-pipeline** | Full render pipeline: app process → commit → render server (backboardd) → GPU. `CATransaction`. Implicit animations. `CALayer` tree (model/presentation/render). 60Hz/120Hz ProMotion. `CADisplayLink`. |
| **02-offscreen-rendering** | What triggers off-screen passes: `cornerRadius` + `masksToBounds`, `shadow`, `mask`, `shouldRasterize`, group opacity. Performance cost. How to avoid. |
| **03-metal-gpu** | Metal basics for iOS devs. `MTKView`. Compute shaders. Metal Performance Shaders. Core Image + Metal. GPU frame capture. Metal vs OpenGL ES (deprecated). |
| **04-text-rendering** | TextKit 1 vs TextKit 2. `NSLayoutManager` → `NSTextLayoutManager`. `NSTextStorage`. Custom text layout. `CTFramesetter` (Core Text). Emoji rendering. Dynamic Type. |
| **05-image-handling** | `UIImage` memory cost (decoded bitmap). `ImageIO` for progressive/thumbnail loading. `CGImageSource`. `UIGraphicsImageRenderer`. HEIF/HEIC. `PHImageManager`. Downsampling. `SDWebImage`/`Kingfisher` architecture. |
| **06-scrolling-performance** | 60fps scroll checklist. Cell preparation cost. Image decoding on background thread. Prefetching. `CALayer.shouldRasterize` for complex cells. Instruments: Core Animation template. Blended layers. Misaligned images. |
| **07-interview-qa** | 45+ questions on render pipeline, off-screen rendering, Metal, text, image, scroll performance. |

---

## Series 8: `system-frameworks/` — System Frameworks & Platform Internals (8 docs, ~55 questions)

| Doc | Topics |
|-----|--------|
| **01-darwin-xnu-basics** | XNU kernel. Mach + BSD layers. Mach ports (IPC). `launchd`. `SpringBoard`. `backboardd`. Process model (no swap, Jetsam). Entitlements. Sandbox profiles (`sandbox-exec`). |
| **02-objc-runtime** | `objc_msgSend`. Method dispatch (table vs message). `isa` pointer. Class hierarchy. Method swizzling. Associated objects. `NSProxy`. `NSInvocation`. KVO internals (`isa-swizzling`). KVC. `@dynamic` vs `@synthesize`. |
| **03-swift-runtime** | Swift type metadata. Witness tables (protocol, value). Existential containers. Generic specialization. `@objc` bridging cost. `dynamic` dispatch. Module stability. Library evolution (`@frozen`). |
| **04-notification-push** | APNs architecture. Device token lifecycle. `UNUserNotificationCenter`. Notification Service Extension (modify before display). Notification Content Extension. Silent push. Background push. Push delivery reliability. Token-based auth (JWT). |
| **05-inter-process-communication** | XPC Services. `NSXPCConnection`. Mach ports. `CFMessagePort`. App Groups. `UserDefaults(suiteName:)`. Keychain sharing. `UIDocumentInteractionController`. `UIActivityViewController`. URL schemes. Universal Links. |
| **06-accessibility** | `UIAccessibility` protocol. Traits. Custom actions. `accessibilityElements`. VoiceOver navigation. Dynamic Type. `UIAccessibilityContainer`. Accessibility Inspector. `AXCustomContent`. SwiftUI accessibility modifiers. |
| **07-internationalization** | `Bundle.localizedString`. `String(localized:)`. `Locale`. `DateFormatter` / `FormatStyle`. RTL support. `semanticContentAttribute`. Plural rules. String catalogs (Xcode 15). `CFBundleLocalizations`. |
| **08-interview-qa** | 55+ questions on ObjC runtime, Swift runtime, XNU, push, IPC, accessibility. |

---

## Series 9: `security-privacy/` — Security & Privacy (7 docs, ~50 questions)

| Doc | Topics |
|-----|--------|
| **01-app-sandbox** | iOS sandbox model. Entitlements. Container structure. File protection classes. Data protection API. Jailbreak detection (and why it's unreliable). |
| **02-transport-security** | App Transport Security (ATS). TLS 1.3. Certificate pinning (`URLSessionDelegate`). `SecTrust`. Certificate transparency. |
| **03-keychain-crypto** | Keychain deep dive. `SecKey` for asymmetric crypto. `CryptoKit` (AES-GCM, SHA, HMAC, P256). Secure Enclave. Biometric auth (`LAContext`). |
| **04-privacy-framework** | Privacy manifests (iOS 17). Required reason APIs. Tracking transparency (`ATTrackingManager`). `SKAdNetwork`. Privacy nutrition labels. `PHPhotoLibrary` limited access. Location "approximate" mode. |
| **05-code-signing** | Code signing chain. Provisioning profiles. Entitlements. App Store review. TestFlight. Enterprise distribution. Notarization (macOS). `codesign` internals. |
| **06-runtime-security** | ASLR. Stack canaries. PAC (Pointer Authentication Codes) on A12+. `__DATA_CONST`. `swift_retain`/`swift_release` vs ObjC. Memory tagging (MTE on future chips). |
| **07-interview-qa** | 50+ questions on sandbox, TLS, Keychain, privacy, code signing, runtime protection. |

---

## Series 10: `testing-di/` — Testing & Dependency Injection (7 docs, ~50 questions)

| Doc | Topics |
|-----|--------|
| **01-xctest-fundamentals** | `XCTestCase`. `setUp`/`tearDown` lifecycle. `XCTestExpectation` for async. `XCTAssert` family. Test plans. Parallel testing. `XCTSkip`. `XCTMetric`. |
| **02-mocking-protocols** | Protocol-oriented testing. Manual mocks vs generated (Sourcery, Mockolo). `@testable import`. Internal vs public testing. Dependency injection patterns (constructor, property, environment). |
| **03-ui-testing** | `XCUIApplication` / `XCUIElement`. Accessibility identifiers. Launch arguments/environment. `XCUIElementQuery`. Snapshot testing. Screenshot testing. |
| **04-swift-testing** | Swift Testing framework (Xcode 16). `@Test`. `@Suite`. `#expect`. Parameterized tests. Tags. Traits. Comparison with XCTest. |
| **05-di-patterns** | Constructor injection. Factory pattern. Environment-based DI (SwiftUI `@Environment`). Swinject. `@Dependency` (TCA). Protocol witnesses. Reader monad pattern. |
| **06-ci-cd** | Xcode Cloud. Fastlane. `xcodebuild` CLI. Code coverage (`llvm-cov`). Test result bundles. Merge queues. Build caching (Bazel, Tuist). |
| **07-interview-qa** | 50+ questions on testing strategy, mocking, UI testing, DI patterns, CI/CD. |

---

## Series 11: `architecture-patterns/` — Architecture & Design Patterns (7 docs, ~50 questions)

| Doc | Topics |
|-----|--------|
| **01-mvc-mvvm-evolution** | MVC (Apple's version vs original). Massive View Controller problem. MVVM + Combine/async. VIPER. Clean Architecture. TCA (The Composable Architecture). |
| **02-coordinator-pattern** | Navigation coordination. `UINavigationController` delegate. Coordinator tree. Deep link routing. SwiftUI navigation with coordinators. |
| **03-reactive-architecture** | Combine-based. Observation framework. Unidirectional data flow (TCA, Redux-like). `@Observable` + SwiftUI. State machines. |
| **04-modularization** | Swift Package Manager modules. Framework vs library. `@_exported`. Module boundaries. API design. Tuist / XcodeGen. Bazel for iOS. Mono-repo vs poly-repo. |
| **05-dependency-management** | SPM internals (resolution, Package.resolved). CocoaPods (Podspec, pod install vs update). Carthage. Binary frameworks (XCFramework). Version resolution conflicts. |
| **06-large-scale-patterns** | Feature flags. A/B testing architecture. Analytics abstraction. Logging (`os_log`, `Logger`). Crash reporting. Remote config. |
| **07-interview-qa** | 50+ questions on architecture decisions, modularization, dependency management, scale patterns. |

---

## Series 12: `build-toolchain/` — Build System & Toolchain (6-7 docs, ~45 questions)

| Doc | Topics |
|-----|--------|
| **01-xcode-build-system** | `xcodebuild` pipeline. `xcconfig` files. Build settings inheritance. Derived data. Build phases. Script phases. `SWIFT_COMPILATION_MODE` (whole module vs incremental). |
| **02-swift-compiler** | SIL (Swift Intermediate Language). SIL optimization passes. Whole module optimization. `@inlinable`. `@usableFromInline`. Compile time: generics vs protocols vs existentials. |
| **03-linker-dyld** | Static vs dynamic linking. `dyld4` shared cache. `@rpath`. Framework search paths. `install_name_tool`. Symbol visibility. Dead code stripping. Mergeable libraries (Xcode 15). |
| **04-spm-internals** | Package.swift manifest. Dependency resolution algorithm. Binary targets. Plugins (build tool, command). Resources. Conditional dependencies. |
| **05-binary-size** | App thinning (slicing, bitcode [removed], on-demand resources). Asset catalogs. `__TEXT` segment. Swift metadata size. Link-time optimization. `-Osize` vs `-O`. Strip symbols. |
| **06-debugging-tools** | LLDB. `po` vs `v` vs `p`. Breakpoint actions. Symbolic breakpoints. `chisel`. Address Sanitizer. Thread Sanitizer. Undefined Behavior Sanitizer. Malloc debugging. |
| **07-interview-qa** | 45+ questions on build system, compiler, linker, SPM, binary size, debugging. |

---

## Series 13: `api-evolution-history/` — API Evolution & Platform History (5-6 docs, ~40 questions)

| Doc | Topics |
|-----|--------|
| **01-uikit-evolution** | iOS 2 → iOS 17 major UIKit changes. `UIAppearance`. Size classes. Trait collections. `UIScene`. `UICollectionViewCompositionalLayout`. `UIButton.Configuration`. `UIContentConfiguration`. |
| **02-swift-evolution** | Swift 1 → Swift 6. ABI stability (5.0). Module stability (5.1). Concurrency (5.5). Macros (5.9). Noncopyable types. Typed throws. Each of these with "why it matters for app devs". |
| **03-swiftui-evolution** | SwiftUI 1.0 → 5.0. What was missing each year. `NavigationStack` (4.0). `Observable` (5.0). Charts. `ContentUnavailableView`. `@Bindable`. Adoption strategy by year. |
| **04-deprecated-patterns** | Patterns that died and why: `UIWebView` → `WKWebView`, `UIAlertView` → `UIAlertController`, Storyboard segues → programmatic, `NSURLConnection` → `URLSession`, `UITableViewCell` manual layout → self-sizing, `GCD` patterns → async/await. |
| **05-interview-qa** | 40+ questions on API evolution, migration decisions, deprecation reasoning, platform history. |

---

## Proposed Sample Projects (alongside docs)

| Project | Purpose |
|---------|---------|
| **`ios-touch-responder-demo/`** | Visualizes the full touch → hit test → responder chain flow with colored overlays |
| **`ios-concurrency-samples/`** | GCD, OperationQueue, async/await, actors, Combine — all patterns side by side |
| **`ios-custom-views/`** | UIKit custom views (from simple to complex) + SwiftUI equivalents |
| **`ios-architecture-comparison/`** | Same feature built in MVC, MVVM, TCA — compare code, testability, navigation |
| **`ios-performance-lab/`** | Intentionally janky app with profiling exercises (off-screen rendering, retain cycles, startup) |

---

## Summary Comparison with Android Project

| Dimension | Android (done) | iOS (proposed) |
|-----------|---------------|----------------|
| Series | 13 | 13 |
| Documents | ~100 | ~95-105 |
| Interview Qs | ~650 | ~630-680 |
| Sample Projects | 3 | 5 |
| Language | Kotlin | Swift |
| UI Framework | Compose + View | SwiftUI + UIKit |
| Depth | AOSP internals | Darwin/XNU + ObjC/Swift runtime |

---

## Suggested Execution Order

1. **App Lifecycle** — foundation: AppDelegate evolution, scene lifecycle
2. **UIKit View System** — touch flow, rendering, layout (classic Staff question)
3. **SwiftUI Internals** — AttributeGraph, identity, state, layout
4. **Concurrency & Threading** — GCD, async/await, actors
5. **Memory & Performance** — ARC, Jetsam, startup, profiling
6. **Rendering & Graphics** — Core Animation pipeline, off-screen, Metal
7. **System Frameworks** — ObjC runtime, Swift runtime, XNU, push
8. **Networking & Data** — URLSession, Core Data, SwiftData, Keychain
9. **Security & Privacy** — sandbox, TLS, privacy manifests, code signing
10. **Testing & DI** — XCTest, Swift Testing, mocking, CI
11. **Architecture** — MVC→MVVM→TCA, modularization, coordinators
12. **Build & Toolchain** — Xcode build, Swift compiler, dyld, SPM
13. **API Evolution** — UIKit/Swift/SwiftUI history, deprecated patterns

---

## Status: AWAITING APPROVAL

Please review and confirm (or request changes) before document generation begins.
