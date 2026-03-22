# iOS Utility Libraries - Project Specification

A collection of 13 reusable iOS libraries for common development tasks. Each library is designed to be simple, well-documented, and follows Swift best practices.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Libraries Overview](#libraries-overview)
3. [Project Structure](#project-structure)
4. [Detailed Library Specifications](#detailed-library-specifications)
5. [Demo App Requirements](#demo-app-requirements)
6. [Technical Requirements](#technical-requirements)
7. [Best Practices](#best-practices)

---

## Project Overview

### Goals
- Create reusable, production-ready iOS libraries
- Follow Swift best practices and modern iOS development patterns
- Support both UIKit and SwiftUI
- Maintain clean, testable, and well-documented code
- Provide a demo app showcasing all library features

### Target Platforms
- iOS 15.0+
- Swift 5.9+
- Xcode 15+

---

## Libraries Overview

| # | Library | Description | Android Equivalent |
|---|---------|-------------|-------------------|
| 1 | **PrefsManager** | Type-safe UserDefaults with Keychain support | prefs-manager |
| 2 | **AppLogger** | Flexible logging with multiple outputs | logger |
| 3 | **Analytics** | Unified analytics abstraction | analytics |
| 4 | **AppCache** | Memory + disk caching with expiration | cache |
| 5 | **PermissionManager** | Simplified iOS permissions handling | permissions |
| 6 | **NetworkClient** | HTTP client wrapper with async/await | network |
| 7 | **ConnectivityMonitor** | Network reachability monitoring | connectivity |
| 8 | **Validator** | Input validation library | validator |
| 9 | **SessionManager** | User session & auth state management | session |
| 10 | **FeatureFlags** | Feature toggles for A/B testing | feature-flags |
| 11 | **CrashReporter** | Crash capture and reporting | crash-reporter |
| 12 | **EventBus** | Pub/sub event communication | event-bus |
| 13 | **AppNavigator** | Navigation helper for UIKit & SwiftUI | navigation |

---

## Project Structure

```
iOSUtilityLibraries/
├── Package.swift                    # Swift Package Manager manifest
├── Sources/
│   ├── PrefsManager/
│   │   ├── PrefsManager.swift
│   │   ├── KeychainHelper.swift
│   │   └── PropertyWrappers.swift
│   ├── AppLogger/
│   │   ├── AppLogger.swift
│   │   ├── LogLevel.swift
│   │   ├── LogOutput.swift
│   │   └── FileLogger.swift
│   ├── Analytics/
│   │   ├── Analytics.swift
│   │   ├── AnalyticsProvider.swift
│   │   └── DebugAnalyticsProvider.swift
│   ├── AppCache/
│   │   ├── AppCache.swift
│   │   ├── MemoryCache.swift
│   │   └── DiskCache.swift
│   ├── PermissionManager/
│   │   ├── PermissionManager.swift
│   │   ├── PermissionType.swift
│   │   └── PermissionStatus.swift
│   ├── NetworkClient/
│   │   ├── NetworkClient.swift
│   │   ├── NetworkError.swift
│   │   ├── HTTPMethod.swift
│   │   └── RequestBuilder.swift
│   ├── ConnectivityMonitor/
│   │   ├── ConnectivityMonitor.swift
│   │   └── ConnectionType.swift
│   ├── Validator/
│   │   ├── Validator.swift
│   │   ├── ValidationRule.swift
│   │   └── ValidationResult.swift
│   ├── SessionManager/
│   │   ├── SessionManager.swift
│   │   ├── SessionState.swift
│   │   └── TokenManager.swift
│   ├── FeatureFlags/
│   │   ├── FeatureFlags.swift
│   │   └── FlagSource.swift
│   ├── CrashReporter/
│   │   ├── CrashReporter.swift
│   │   ├── CrashEntry.swift
│   │   └── CrashReporterProvider.swift
│   ├── EventBus/
│   │   ├── EventBus.swift
│   │   └── BusEvent.swift
│   └── AppNavigator/
│       ├── AppNavigator.swift
│       ├── NavigationRouter.swift
│       └── DeepLinkHandler.swift
├── Tests/
│   └── [Unit tests for each library]
├── DemoApp/
│   ├── DemoApp.xcodeproj
│   ├── DemoApp/
│   │   ├── App/
│   │   ├── Screens/
│   │   ├── Views/
│   │   └── Resources/
│   └── DemoAppUITests/
└── README.md
```

---

## Detailed Library Specifications

### 1. PrefsManager - Preferences Manager

**Purpose:** Type-safe UserDefaults wrapper with Keychain support for sensitive data.

**Features:**
- Property wrapper for clean syntax (`@Preference`)
- Keychain storage for sensitive data (`@SecurePreference`)
- Support for Codable objects
- Observable changes with Combine
- Migration support between versions

**API Design:**

```swift
// Property Wrappers
@Preference("user_name", defaultValue: "")
var userName: String

@Preference("login_count", defaultValue: 0)
var loginCount: Int

@SecurePreference("api_token")
var apiToken: String?

// Direct Access
PrefsManager.shared.set("value", forKey: "key")
PrefsManager.shared.get(forKey: "key", defaultValue: "default")

// Codable Objects
PrefsManager.shared.setObject(user, forKey: "current_user")
let user: User? = PrefsManager.shared.getObject(forKey: "current_user")

// Keychain (Secure)
PrefsManager.shared.setSecure("secret", forKey: "api_key")
let secret = PrefsManager.shared.getSecure(forKey: "api_key")

// Observe Changes
PrefsManager.shared.publisher(for: "user_name")
    .sink { newValue in print("Changed to: \(newValue)") }

// Clear
PrefsManager.shared.remove(forKey: "key")
PrefsManager.shared.clearAll()
PrefsManager.shared.clearSecure()
```

**Dependencies:**
- Foundation
- Security (for Keychain)
- Combine

---

### 2. AppLogger - Flexible Logging

**Purpose:** Flexible logging with multiple output targets and formatting options.

**Features:**
- Multiple log levels (verbose, debug, info, warning, error, fatal)
- Multiple outputs (console, file, remote)
- Pretty JSON formatting
- File rotation
- Custom formatters
- Privacy-aware logging (redact sensitive data)

**API Design:**

```swift
// Configuration
AppLogger.configure {
    $0.minLevel = .debug
    $0.outputs = [.console, .file(maxSize: 5_000_000)]
    $0.includeTimestamp = true
    $0.includeLocation = true
}

// Logging
AppLogger.v("Verbose message")
AppLogger.d("Debug message")
AppLogger.i("Info message")
AppLogger.w("Warning message")
AppLogger.e("Error message", error: error)
AppLogger.f("Fatal message")

// With context
AppLogger.d("User action", context: ["screen": "Home", "action": "tap"])

// JSON logging
AppLogger.json(jsonData, level: .debug)

// File access
let logFile = AppLogger.getLogFileURL()
let logs = AppLogger.readLogs()
AppLogger.clearLogs()
```

**Dependencies:**
- Foundation
- OSLog (for unified logging)

---

### 3. Analytics - Unified Analytics

**Purpose:** Abstraction layer for multiple analytics providers.

**Features:**
- Provider-agnostic API
- Support for multiple providers simultaneously
- Automatic screen tracking
- User properties
- Revenue tracking
- Debug mode with local logging

**API Design:**

```swift
// Configuration
Analytics.configure {
    $0.providers = [
        FirebaseAnalyticsProvider(),
        MixpanelProvider(token: "xxx")
    ]
    $0.enableDebugMode = true
    $0.autoTrackScreens = true
}

// Event Tracking
Analytics.track("button_clicked", properties: [
    "button_name": "submit",
    "screen": "checkout"
])

// Screen Tracking
Analytics.trackScreen("HomeScreen")

// User
Analytics.setUserId("user123")
Analytics.setUserProperty("subscription", value: "premium")

// Revenue
Analytics.trackRevenue(
    productId: "premium_plan",
    amount: 9.99,
    currency: "USD"
)

// Session
Analytics.startSession()
Analytics.endSession()

// Debug
let logs = Analytics.getDebugLogs()
```

**Provider Protocol:**

```swift
protocol AnalyticsProvider {
    var name: String { get }
    func track(event: String, properties: [String: Any])
    func trackScreen(name: String, className: String?)
    func setUserId(_ userId: String?)
    func setUserProperty(_ name: String, value: String?)
    func trackRevenue(productId: String, amount: Double, currency: String)
}
```

**Dependencies:**
- Foundation
- UIKit (for auto screen tracking)

---

### 4. AppCache - Simple Caching

**Purpose:** Memory and disk caching with expiration support.

**Features:**
- Two-tier caching (memory + disk)
- Configurable expiration
- LRU eviction for memory cache
- Async API with async/await
- Codable support
- Cache statistics

**API Design:**

```swift
// Configuration
AppCache.configure {
    $0.memoryCacheLimit = 50_000_000 // 50MB
    $0.diskCacheLimit = 200_000_000 // 200MB
    $0.defaultExpiration = .hours(1)
}

// Store and Retrieve
AppCache.shared.set("Hello", forKey: "greeting")
let greeting: String? = AppCache.shared.get(forKey: "greeting")

// With Expiration
AppCache.shared.set(data, forKey: "temp", expiration: .minutes(5))

// Async
let data = await AppCache.shared.getAsync(forKey: "key")

// Get or Create
let value = AppCache.shared.getOrSet(forKey: "computed") {
    computeExpensiveValue()
}

// Codable Objects
AppCache.shared.setObject(user, forKey: "user")
let user: User? = AppCache.shared.getObject(forKey: "user")

// Management
AppCache.shared.remove(forKey: "key")
AppCache.shared.clearMemory()
AppCache.shared.clearDisk()
AppCache.shared.clearExpired()

// Statistics
let stats = AppCache.shared.statistics
print("Memory: \(stats.memoryCount) items, \(stats.memorySize) bytes")
print("Disk: \(stats.diskCount) items, \(stats.diskSize) bytes")
```

**Dependencies:**
- Foundation

---

### 5. PermissionManager - Permissions Handling

**Purpose:** Simplified iOS permissions with status tracking.

**Features:**
- Unified API for all permission types
- Status observation with Combine
- Rationale support
- Settings redirect
- SwiftUI integration

**Supported Permissions:**
- Camera
- Photo Library
- Microphone
- Location (When In Use, Always)
- Notifications
- Contacts
- Calendar
- Reminders
- Motion & Fitness
- Bluetooth
- Face ID / Touch ID

**API Design:**

```swift
// Check Status
let status = PermissionManager.shared.status(for: .camera)

// Request
PermissionManager.shared.request(.camera) { result in
    switch result {
    case .granted:
        // Use camera
    case .denied:
        // Show explanation
    case .restricted:
        // Feature not available
    }
}

// Async/Await
let status = await PermissionManager.shared.request(.camera)

// Multiple Permissions
let results = await PermissionManager.shared.request([.camera, .microphone])

// Observe Status
PermissionManager.shared.statusPublisher(for: .location)
    .sink { status in
        // Handle status change
    }

// Open Settings
PermissionManager.shared.openSettings()

// SwiftUI View Modifier
.requestPermission(.camera, onResult: { result in })
```

**Permission Status:**

```swift
enum PermissionStatus {
    case notDetermined
    case granted
    case denied
    case restricted
    case limited // For photo library
}
```

**Dependencies:**
- Foundation
- AVFoundation (Camera, Microphone)
- Photos
- CoreLocation
- UserNotifications
- Contacts
- EventKit
- CoreMotion
- CoreBluetooth
- LocalAuthentication

---

### 6. NetworkClient - HTTP Client

**Purpose:** Modern HTTP client with async/await and type safety.

**Features:**
- Async/await API
- Request/Response interceptors
- Automatic retry with exponential backoff
- Request timeout
- Multipart form data
- Download/Upload progress
- Response caching
- Certificate pinning

**API Design:**

```swift
// Configuration
NetworkClient.configure {
    $0.baseURL = URL(string: "https://api.example.com")!
    $0.timeout = 30
    $0.defaultHeaders = ["Authorization": "Bearer xxx"]
    $0.enableLogging = true
    $0.retryCount = 3
}

// GET Request
let user: User = try await NetworkClient.shared.get("/users/1")

// POST Request
let response: CreateResponse = try await NetworkClient.shared.post(
    "/users",
    body: newUser
)

// With Query Parameters
let users: [User] = try await NetworkClient.shared.get(
    "/users",
    query: ["page": 1, "limit": 20]
)

// Custom Request
let response = try await NetworkClient.shared.request {
    $0.path = "/upload"
    $0.method = .post
    $0.body = imageData
    $0.headers["Content-Type"] = "image/jpeg"
    $0.timeout = 60
}

// Download with Progress
let fileURL = try await NetworkClient.shared.download(
    url: fileURL,
    progress: { progress in
        print("Downloaded: \(progress * 100)%")
    }
)

// Result Type
let result: Result<User, NetworkError> = await NetworkClient.shared.getResult("/users/1")
result.onSuccess { user in }
result.onFailure { error in }
```

**Error Handling:**

```swift
enum NetworkError: Error {
    case invalidURL
    case noData
    case decodingError(Error)
    case httpError(statusCode: Int, data: Data?)
    case networkError(Error)
    case timeout
    case cancelled
}
```

**Dependencies:**
- Foundation

---

### 7. ConnectivityMonitor - Network Monitoring

**Purpose:** Monitor network connectivity and connection type.

**Features:**
- Real-time connectivity monitoring
- Connection type detection (WiFi, Cellular, Ethernet)
- Expensive connection detection
- Combine publisher
- SwiftUI integration

**API Design:**

```swift
// Start Monitoring
ConnectivityMonitor.shared.startMonitoring()

// Current Status
let isConnected = ConnectivityMonitor.shared.isConnected
let connectionType = ConnectivityMonitor.shared.connectionType
let isExpensive = ConnectivityMonitor.shared.isExpensive

// Observe Changes (Combine)
ConnectivityMonitor.shared.statusPublisher
    .sink { status in
        switch status {
        case .connected(let type):
            print("Connected via \(type)")
        case .disconnected:
            print("Offline")
        }
    }

// Observe Changes (Closure)
ConnectivityMonitor.shared.onStatusChange { status in
    // Handle change
}

// Check Specific Capability
let canReachHost = await ConnectivityMonitor.shared.canReach(host: "api.example.com")

// Stop Monitoring
ConnectivityMonitor.shared.stopMonitoring()

// SwiftUI
@ObservedObject var connectivity = ConnectivityMonitor.shared
// or
@Environment(\.isConnected) var isConnected
```

**Connection Status:**

```swift
enum ConnectionStatus {
    case connected(ConnectionType)
    case disconnected
}

enum ConnectionType {
    case wifi
    case cellular
    case ethernet
    case unknown
}
```

**Dependencies:**
- Network (NWPathMonitor)
- Combine

---

### 8. Validator - Input Validation

**Purpose:** Comprehensive input validation with chainable rules.

**Features:**
- Built-in validators (email, phone, URL, password, credit card)
- Chainable validation rules
- Custom validation rules
- Localized error messages
- Form validation

**API Design:**

```swift
// Quick Validators
let emailResult = Validator.email("test@example.com")
let phoneResult = Validator.phone("+1234567890")
let urlResult = Validator.url("https://example.com")

// Password Validation
let passwordResult = Validator.password("MyPass123!", options: [
    .minLength(8),
    .requireUppercase,
    .requireLowercase,
    .requireDigit,
    .requireSpecialChar
])

// Chained Validation
let result = Validator.validate("username")
    .notEmpty(message: "Username required")
    .minLength(3)
    .maxLength(20)
    .alphanumeric()
    .execute()

if result.isValid {
    // Proceed
} else {
    print(result.errors)
}

// Custom Rule
let result = Validator.validate(value)
    .custom { value in
        value.hasPrefix("@") ? .valid : .invalid("Must start with @")
    }
    .execute()

// Form Validation
let formResult = FormValidator()
    .add("email", value: email, rules: [.required, .email])
    .add("password", value: password, rules: [.required, .minLength(8)])
    .add("confirmPassword", value: confirm, rules: [.matches(password)])
    .validate()

if formResult.isValid {
    // Submit form
} else {
    let emailErrors = formResult.errors(for: "email")
}
```

**Validation Result:**

```swift
struct ValidationResult {
    let isValid: Bool
    let errors: [String]
    var firstError: String? { errors.first }
}
```

**Dependencies:**
- Foundation

---

### 9. SessionManager - Session Management

**Purpose:** Manage user authentication state and tokens.

**Features:**
- Secure token storage (Keychain)
- Session state observation
- Token refresh handling
- User data storage
- Auto logout on token expiry

**API Design:**

```swift
// Configuration
SessionManager.configure {
    $0.tokenStorage = .keychain
    $0.autoRefreshToken = true
    $0.sessionTimeout = 3600 // 1 hour
}

// Login
SessionManager.shared.login(
    userId: "user123",
    accessToken: "jwt_token",
    refreshToken: "refresh_token",
    expiresIn: 3600,
    user: userData
)

// Check Status
let isLoggedIn = SessionManager.shared.isLoggedIn
let userId = SessionManager.shared.userId
let token = SessionManager.shared.accessToken

// Get User Data
let user: User? = SessionManager.shared.getUser()

// Update Token
SessionManager.shared.updateToken(
    accessToken: "new_token",
    refreshToken: "new_refresh"
)

// Observe State
SessionManager.shared.statePublisher
    .sink { state in
        switch state {
        case .loggedIn(let userId):
            // Navigate to home
        case .loggedOut:
            // Navigate to login
        case .tokenExpired:
            // Refresh token
        }
    }

// Logout
SessionManager.shared.logout()

// SwiftUI
@ObservedObject var session = SessionManager.shared
```

**Session State:**

```swift
enum SessionState {
    case unknown
    case loggedIn(userId: String)
    case loggedOut
    case tokenExpired
}
```

**Dependencies:**
- Foundation
- Security (Keychain)
- Combine

---

### 10. FeatureFlags - Feature Toggles

**Purpose:** Feature flag management for A/B testing and gradual rollouts.

**Features:**
- Local default values
- Remote updates
- Debug overrides
- Change observation
- SwiftUI property wrapper

**API Design:**

```swift
// Configuration
FeatureFlags.configure {
    // Define defaults
    $0.define("dark_mode", default: false)
    $0.define("new_checkout", default: false)
    $0.define("max_items", default: 10)
    $0.define("welcome_message", default: "Hello!")
}

// Check Flags
let isDarkMode = FeatureFlags.shared.isEnabled("dark_mode")
let maxItems = FeatureFlags.shared.int("max_items")
let message = FeatureFlags.shared.string("welcome_message")

// Update from Remote
FeatureFlags.shared.update([
    "dark_mode": true,
    "max_items": 20
])

// Debug Overrides
FeatureFlags.shared.setOverride("dark_mode", value: true)
FeatureFlags.shared.removeOverride("dark_mode")
FeatureFlags.shared.clearOverrides()

// Observe Changes
FeatureFlags.shared.publisher(for: "dark_mode")
    .sink { isEnabled in
        // Update UI
    }

// SwiftUI Property Wrapper
@FeatureFlag("dark_mode") var isDarkMode: Bool
@FeatureFlag("max_items") var maxItems: Int

// Conditional View
FeatureFlagView("new_checkout") {
    NewCheckoutView()
} else: {
    OldCheckoutView()
}
```

**Dependencies:**
- Foundation
- Combine

---

### 11. CrashReporter - Crash Reporting

**Purpose:** Capture and report crashes and non-fatal errors.

**Features:**
- Uncaught exception handling
- Signal handling (SIGABRT, SIGSEGV, etc.)
- Non-fatal error logging
- Custom metadata
- Local crash storage
- Multiple reporter backends

**API Design:**

```swift
// Configuration
CrashReporter.configure {
    $0.enableLocalStorage = true
    $0.maxStoredCrashes = 10
    $0.reporters = [
        FirebaseCrashReporter(),
        CustomCrashReporter()
    ]
}

// Set Context
CrashReporter.shared.setUserId("user123")
CrashReporter.shared.setCustomKey("screen", value: "Checkout")
CrashReporter.shared.setCustomKey("cart_items", value: "5")

// Log Non-Fatal Error
CrashReporter.shared.logError(error, context: [
    "action": "checkout",
    "step": "payment"
])

// Log Message
CrashReporter.shared.log("User initiated checkout", level: .info)

// Get Stored Crashes
let crashes = CrashReporter.shared.getStoredCrashes()
for crash in crashes {
    print("\(crash.timestamp): \(crash.message)")
    print(crash.stackTrace)
}

// Clear
CrashReporter.shared.clearStoredCrashes()
```

**Crash Entry:**

```swift
struct CrashEntry: Codable {
    let id: String
    let timestamp: Date
    let isFatal: Bool
    let message: String
    let stackTrace: String
    let deviceInfo: DeviceInfo
    let appInfo: AppInfo
    let customKeys: [String: String]
}
```

**Dependencies:**
- Foundation

---

### 12. EventBus - Event Communication

**Purpose:** Pub/sub event system for decoupled communication.

**Features:**
- Type-safe events
- Sticky events
- One-time subscriptions
- Combine integration
- Main thread delivery option

**API Design:**

```swift
// Define Events
struct UserLoggedIn: BusEvent {
    let userId: String
}

struct CartUpdated: BusEvent {
    let itemCount: Int
}

// Subscribe
let subscription = EventBus.shared.subscribe(UserLoggedIn.self) { event in
    print("User logged in: \(event.userId)")
}

// Subscribe with Options
EventBus.shared.subscribe(
    CartUpdated.self,
    queue: .main,
    receiveSticky: true
) { event in
    updateCartBadge(event.itemCount)
}

// Subscribe Once
EventBus.shared.subscribeOnce(PaymentComplete.self) { event in
    showSuccessMessage()
}

// Publish
EventBus.shared.publish(UserLoggedIn(userId: "123"))

// Publish Sticky
EventBus.shared.publishSticky(CartUpdated(itemCount: 5))

// Get Sticky Event
let cartEvent: CartUpdated? = EventBus.shared.getStickyEvent()

// Remove Sticky
EventBus.shared.removeStickyEvent(CartUpdated.self)

// Unsubscribe
subscription.cancel()
EventBus.shared.unsubscribeAll()

// Combine Publisher
EventBus.shared.publisher(for: UserLoggedIn.self)
    .sink { event in }
```

**Event Protocol:**

```swift
protocol BusEvent {}
```

**Dependencies:**
- Foundation
- Combine

---

### 13. AppNavigator - Navigation Helper

**Purpose:** Simplified navigation for both UIKit and SwiftUI.

**Features:**
- Programmatic navigation
- Deep link handling
- Navigation stack management
- Transition animations
- SwiftUI NavigationStack support

**UIKit API:**

```swift
// Navigate to View Controller
AppNavigator.shared.push(DetailViewController())
AppNavigator.shared.present(ModalViewController(), animated: true)

// With Configuration
AppNavigator.shared.push(DetailViewController()) {
    $0.hidesBottomBarWhenPushed = true
    $0.transition = .fade
}

// Pop
AppNavigator.shared.pop()
AppNavigator.shared.popToRoot()
AppNavigator.shared.popTo(HomeViewController.self)

// Dismiss
AppNavigator.shared.dismiss()
AppNavigator.shared.dismissAll()

// Deep Links
AppNavigator.shared.handleDeepLink(url) { route in
    switch route {
    case .product(let id):
        navigateToProduct(id)
    case .profile(let userId):
        navigateToProfile(userId)
    default:
        return false
    }
    return true
}

// Register Deep Link Routes
AppNavigator.shared.registerRoute("product/:id") { params in
    let id = params["id"]!
    return ProductViewController(id: id)
}
```

**SwiftUI API:**

```swift
// Navigation Router
@ObservedObject var router = NavigationRouter()

NavigationStack(path: $router.path) {
    HomeView()
        .navigationDestination(for: Route.self) { route in
            route.destination
        }
}

// Navigate
router.push(.productDetail(id: "123"))
router.pop()
router.popToRoot()

// Deep Link
router.handleDeepLink(url)

// Environment
@Environment(\.navigator) var navigator
navigator.push(.settings)
```

**Dependencies:**
- Foundation
- UIKit
- SwiftUI

---

## Demo App Requirements

### Features
- Tab-based navigation with one tab per library
- Each tab demonstrates all features of the library
- Console output view showing results
- Settings screen for configuration
- Both UIKit and SwiftUI examples

### Screens
1. **Home** - Overview and quick links
2. **PrefsManager Demo** - Save/load preferences, keychain demo
3. **Logger Demo** - Log messages, view log file
4. **Analytics Demo** - Track events, view debug logs
5. **Cache Demo** - Store/retrieve data, statistics
6. **Permissions Demo** - Request various permissions
7. **Network Demo** - API calls, download progress
8. **Connectivity Demo** - Monitor network status
9. **Validator Demo** - Form validation examples
10. **Session Demo** - Login/logout flow
11. **Feature Flags Demo** - Toggle features
12. **Crash Reporter Demo** - Log errors, view crashes
13. **EventBus Demo** - Publish/subscribe events
14. **Navigation Demo** - Navigation patterns

---

## Technical Requirements

### Swift Package Manager

```swift
// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "iOSUtilityLibraries",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .library(name: "PrefsManager", targets: ["PrefsManager"]),
        .library(name: "AppLogger", targets: ["AppLogger"]),
        .library(name: "Analytics", targets: ["Analytics"]),
        .library(name: "AppCache", targets: ["AppCache"]),
        .library(name: "PermissionManager", targets: ["PermissionManager"]),
        .library(name: "NetworkClient", targets: ["NetworkClient"]),
        .library(name: "ConnectivityMonitor", targets: ["ConnectivityMonitor"]),
        .library(name: "Validator", targets: ["Validator"]),
        .library(name: "SessionManager", targets: ["SessionManager"]),
        .library(name: "FeatureFlags", targets: ["FeatureFlags"]),
        .library(name: "CrashReporter", targets: ["CrashReporter"]),
        .library(name: "EventBus", targets: ["EventBus"]),
        .library(name: "AppNavigator", targets: ["AppNavigator"]),
        // All-in-one
        .library(name: "iOSUtilities", targets: ["iOSUtilities"])
    ],
    targets: [
        // Individual targets...
        .target(name: "iOSUtilities", dependencies: [
            "PrefsManager", "AppLogger", "Analytics", "AppCache",
            "PermissionManager", "NetworkClient", "ConnectivityMonitor",
            "Validator", "SessionManager", "FeatureFlags",
            "CrashReporter", "EventBus", "AppNavigator"
        ])
    ]
)
```

### Code Style
- Follow Swift API Design Guidelines
- Use Swift's native error handling (throws, Result)
- Prefer async/await over completion handlers
- Use Combine for reactive patterns
- Support both UIKit and SwiftUI where applicable
- Include comprehensive documentation comments

### Testing
- Unit tests for all public APIs
- Mock protocols for testability
- UI tests for demo app
- Minimum 80% code coverage

---

## Best Practices

1. **Protocol-Oriented Design** - Use protocols for abstraction
2. **Dependency Injection** - Allow injecting dependencies for testing
3. **Thread Safety** - Use appropriate synchronization
4. **Memory Management** - Avoid retain cycles, use weak references
5. **Error Handling** - Comprehensive error types with context
6. **Documentation** - DocC compatible documentation
7. **Combine Integration** - Reactive APIs where appropriate
8. **SwiftUI Support** - Property wrappers and view modifiers
9. **Async/Await** - Modern concurrency support
10. **Backward Compatibility** - Deprecation warnings for API changes

---

## License

MIT License - Feel free to use in your projects.

---

*Document Version: 1.0*
*Created: March 2026*
