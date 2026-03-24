# Android Interview Preparation Documentation

A comprehensive collection of Android development documentation covering deep technical topics for senior-level (L5/L6) interview preparation.

## Topics Covered

### Core Android Topics

| Topic | Description |
|-------|-------------|
| **Background Work** | Services, WorkManager, JobScheduler, and power management |
| **Coroutines & Threading** | Threading fundamentals, coroutines, structured concurrency, Flows |
| **Compose Internals** | Compose runtime, recomposition, snapshot system, slot table |
| **Rendering Pipeline** | VSYNC, Choreographer, RenderThread, SurfaceFlinger, Skia |
| **Security** | Permissions, Keystore, R8, app sandbox, secure networking |
| **IPC & Binder** | Binder architecture, AIDL, Zygote, system services |
| **Memory & Performance** | Memory model, GC internals, leaks, ANR, OOM, Baseline Profiles |
| **Lifecycle & Navigation** | Activity/Fragment lifecycle, back stack, Navigation Component |
| **Testing & DI** | Unit testing, Dagger/Hilt, Koin, testing strategies |
| **System Services** | System server boot, AMS, WMS, PMS, platform internals |
| **View System** | View hierarchy, measure/layout/draw, touch system, RecyclerView |
| **Modularization & Build** | Gradle internals, module architecture, build performance |

## Project Structure

```
android_document/
├── docs/
│   ├── android_document/     # Android topic documentation (HTML)
│   └── iOS_document/         # iOS topic documentation
├── questions/                # Calibration questions & answers
└── android_project/          # Sample Android projects
    ├── workmanager-samples/
    ├── compose-custom-views/
    └── android-client-server-samples/
```

## Content Format

- Documentation is in **HTML format** for rich formatting and embedded diagrams
- Each topic includes comprehensive **interview Q&A sections**
- All code examples are in **Kotlin**
- Covers AOSP internals, trade-offs, and production-grade considerations

## How to Use

1. Open the HTML files in a browser to view the documentation
2. Each topic folder contains an `index.html` as the entry point
3. Topics are organized progressively from fundamentals to advanced internals

## Tech Stack

- HTML/CSS for documentation
- Kotlin for code examples
- Mermaid/SVG for diagrams

## License

MIT License
