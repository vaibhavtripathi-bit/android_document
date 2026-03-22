# Android Client-Server Samples

A multi-module Android project demonstrating three inter-process communication (IPC) patterns where one app acts as a **server** and another as a **client**.

## Architecture

```
android-client-server-samples/
├── ipc_service_aidl/          # Pattern 1: AIDL Bound Service
│   ├── server/                # Exposes BookManagerService via AIDL
│   └── client/                # Binds to the service, queries & adds books
├── content_provider/          # Pattern 2: ContentProvider
│   ├── server/                # Exposes NoteProvider (SQLite-backed CRUD)
│   └── client/                # Queries, inserts, updates, deletes notes
└── shared_storage/            # Pattern 3: FileProvider + Shared Storage
    ├── server/                # Creates files, shares via FileProvider URIs
    └── client/                # Reads shared files via ContentResolver/SAF
```

Each sub-folder (`server/` and `client/`) is a standalone Android application with its own `applicationId`, manifest, and launcher activity.

## What Each Module Demonstrates

### 1. AIDL IPC (`ipc_service_aidl/`)

| Concept | Detail |
|---|---|
| **AIDL interface** | `IBookManager.aidl` with `Parcelable` `Book` |
| **Bound Service** | `BookManagerService` with `IBookManager.Stub` implementation |
| **Thread safety** | `CopyOnWriteArrayList` for concurrent Binder calls |
| **Custom permission** | `com.example.aidlserver.BIND_BOOK_SERVICE` (normal) |
| **Package visibility** | Client declares `<queries>` for the server package |
| **ServiceConnection** | Client binds/unbinds, handles `onServiceDisconnected` |

### 2. ContentProvider (`content_provider/`)

| Concept | Detail |
|---|---|
| **ContentProvider** | `NoteProvider` with `UriMatcher` (dir + item patterns) |
| **SQLite backend** | `SQLiteOpenHelper` with notes table |
| **Full CRUD** | `query()`, `insert()`, `update()`, `delete()` |
| **Content URIs** | `content://com.example.cpserver.notes/notes` |
| **Observer notification** | `notifyChange()` after mutations |
| **Read/Write permissions** | Separate `READ_NOTES` and `WRITE_NOTES` permissions |
| **Caller logging** | Logs `Binder.getCallingPid()` / `getCallingUid()` |

### 3. Shared Storage (`shared_storage/`)

| Concept | Detail |
|---|---|
| **FileProvider** | Configured with `file_paths.xml` for `filesDir/shared/` |
| **URI permission grants** | `grantUriPermission()` to specific client package |
| **ACTION_SEND** | Share files via chooser intent with `FLAG_GRANT_READ_URI_PERMISSION` |
| **SAF integration** | Client uses `ActivityResultContracts.OpenDocument()` |
| **ContentResolver** | Client reads via `openInputStream(uri)` |
| **SecurityException handling** | Graceful fallback when URI permissions are missing |

## How to Build & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 35 (compileSdk)
- Minimum device/emulator: API 24 (Android 7.0)

### Steps

1. Open `android-client-server-samples/` as a project in Android Studio
2. Wait for Gradle sync to complete
3. For each IPC pattern, install **both** the server and client apps:

```bash
# AIDL
./gradlew :ipc_service_aidl:server:installDebug
./gradlew :ipc_service_aidl:client:installDebug

# ContentProvider
./gradlew :content_provider:server:installDebug
./gradlew :content_provider:client:installDebug

# Shared Storage
./gradlew :shared_storage:server:installDebug
./gradlew :shared_storage:client:installDebug
```

4. Launch the **server** app first, then the **client** app
5. Use the client UI buttons to interact with the server

### Installed Apps

After installation, you'll see 6 apps on the device:
- **AIDL Server** / **AIDL Client**
- **CP Server** / **CP Client**
- **Storage Server** / **Storage Client**

## Key Implementation Details

### Security Model
- AIDL service is protected by a custom `normal` permission
- ContentProvider uses separate read/write permissions
- FileProvider uses URI-based permission grants (no global permissions)
- All clients declare `<queries>` for Android 11+ package visibility

### IPC Mechanisms Under the Hood
- **AIDL**: Binder IPC — proxy/stub pattern, synchronous by default, thread pool on server
- **ContentProvider**: Also Binder IPC — `ContentResolver` → `ContentProviderProxy` → `ContentProvider.Transport`
- **FileProvider**: URI permission grants via `ActivityManagerService`, file access via `ContentResolver.openInputStream()`

### What to Watch in Logcat
```
# AIDL calls
adb logcat -s BookManagerService

# ContentProvider operations
adb logcat -s NoteProvider

# General IPC debugging
adb logcat | grep -E "(Binder|ContentProvider|FileProvider)"
```

## Tech Stack
- **Language**: Kotlin
- **Build**: Gradle Kotlin DSL, AGP 8.7.0
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **UI**: ViewBinding + Material Components
