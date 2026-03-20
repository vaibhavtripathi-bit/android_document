# Compose Custom Views

A showcase Android app demonstrating custom view development in Jetpack Compose, progressing from simple composables to expert-level custom layouts and Canvas drawing.

## Project Structure

```
compose-custom-views/
├── app/src/main/java/com/example/customviews/
│   ├── MainActivity.kt
│   ├── ui/
│   │   ├── theme/
│   │   │   ├── Color.kt
│   │   │   └── Theme.kt
│   │   ├── catalog/
│   │   │   └── CatalogApp.kt          # Navigation + home catalog
│   │   └── views/
│   │       ├── BadgeView.kt            # Level 1: Simple
│   │       ├── AnimatedToggle.kt       # Level 2: Basic
│   │       ├── StarRatingBar.kt        # Level 3: Intermediate
│   │       ├── CircularProgress.kt     # Level 4: Intermediate+
│   │       ├── BarChart.kt             # Level 5: Advanced
│   │       ├── ColorPicker.kt          # Level 6: Advanced
│   │       ├── SignaturePad.kt         # Level 7: Complex
│   │       ├── AnimatedGauge.kt        # Level 8: Complex
│   │       ├── FlowLayout.kt          # Level 9: Expert
│   │       └── NodeGraph.kt           # Level 10: Expert
```

## Custom Views (by difficulty)

### Simple
| View | Concepts |
|------|----------|
| **Custom Badge** | `Surface`, `Shape`, color theming, text overlay, `animateColorAsState` |

### Basic
| View | Concepts |
|------|----------|
| **Animated Toggle** | `animateDpAsState`, spring physics, `MutableInteractionSource`, shape morphing |

### Intermediate
| View | Concepts |
|------|----------|
| **Star Rating Bar** | `Canvas`, `drawPath`, star geometry, `clipRect` for partial fill, `detectHorizontalDragGestures`, half-star precision |
| **Circular Progress** | `Canvas.drawArc`, `Brush.sweepGradient`, `animateFloatAsState`, `rememberInfiniteTransition`, stroke caps |

### Advanced
| View | Concepts |
|------|----------|
| **Bar Chart** | `Canvas`, `Animatable` with staggered launch, `detectTapGestures`, `nativeCanvas` text, `CornerRadius` |
| **Color Picker Wheel** | `Brush.sweepGradient`, `Brush.radialGradient`, HSV math, `detectDragGestures` on circular region, `atan2` |

### Complex
| View | Concepts |
|------|----------|
| **Signature Pad** | `Path`, `awaitPointerEventScope`, stroke rendering with `StrokeCap.Round`, undo/redo stack, pen color/width |
| **Animated Gauge** | Arc segments, tick marks with trigonometry, needle rotation, spring animation, `nativeCanvas` text |

### Expert
| View | Concepts |
|------|----------|
| **Flow Layout** | `Layout` composable, custom `MeasurePolicy`, row wrapping algorithm, `Constraints`, `placeRelative` |
| **Node Graph Editor** | `mutableStateListOf`, `detectDragGestures` + `detectTapGestures`, Bezier curves (`cubicTo`), grid background, node CRUD |

## How to Build

1. Open `compose-custom-views/` in Android Studio
2. Sync Gradle
3. Run on device/emulator (API 24+)

## Tech Stack
- Kotlin 2.0.0
- Jetpack Compose (BOM 2024.10.00)
- Material3 with dynamic color
- Navigation Compose
- No external dependencies
