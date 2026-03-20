# WorkManager Samples

A comprehensive Android app demonstrating every major WorkManager use case, from basic one-time tasks to complex chaining, foreground work, and retry strategies.

## Project Structure

```
workmanager-samples/
├── app/src/main/java/com/example/workmanager/
│   ├── WorkManagerApp.kt              # @HiltAndroidApp + custom Configuration.Provider
│   ├── MainActivity.kt                # @AndroidEntryPoint entry
│   ├── di/
│   │   └── AppModule.kt              # Hilt module: WorkManager, Room, DAO
│   ├── data/
│   │   ├── WorkLog.kt                # Room entity for worker execution logs
│   │   ├── WorkLogDao.kt             # DAO with Flow-based queries
│   │   └── AppDatabase.kt            # Room database
│   ├── workers/
│   │   ├── SimpleWorker.kt           # OneTimeWork + input/output Data
│   │   ├── PeriodicSyncWorker.kt     # PeriodicWork + flex interval
│   │   ├── ConstrainedWorker.kt      # All constraint types
│   │   ├── ChainWorkerA/B/C.kt       # Sequential chain with data passing
│   │   ├── RetryWorker.kt            # Result.retry() + backoff
│   │   ├── ExpeditedWorker.kt        # setExpedited() + OutOfQuotaPolicy
│   │   ├── ForegroundWorker.kt       # setForeground() + notification
│   │   ├── ProgressWorker.kt         # setProgress() reporting
│   │   ├── FailingWorker.kt          # Result.failure() + error data
│   │   └── DataProcessingWorker.kt   # Input/output chaining
│   └── ui/
│       ├── theme/Theme.kt            # Material3 dynamic color theme
│       └── screens/
│           ├── WorkManagerNavigation.kt  # NavHost + home catalog
│           ├── SimpleWorkScreen.kt       # OneTimeWork demo
│           ├── PeriodicWorkScreen.kt     # Periodic work demo
│           ├── ConstrainedWorkScreen.kt  # Constraints demo
│           ├── ChainWorkScreen.kt        # Chaining demo
│           ├── UniqueWorkScreen.kt       # Unique work policies
│           ├── RetryWorkScreen.kt        # Retry + backoff demo
│           ├── ExpeditedWorkScreen.kt    # Expedited work demo
│           ├── ForegroundWorkScreen.kt   # Foreground work demo
│           ├── ProgressWorkScreen.kt     # Progress reporting demo
│           ├── InputOutputScreen.kt      # Data passing demo
│           ├── ObserveWorkScreen.kt      # Observation methods demo
│           ├── CancelWorkScreen.kt       # Cancellation demo
│           ├── FailureWorkScreen.kt      # Failure handling demo
│           └── WorkLogsScreen.kt         # Room-backed log viewer
```

## Use Cases Covered

### Basics
| Use Case | Worker | Key APIs |
|----------|--------|----------|
| **OneTimeWorkRequest** | `SimpleWorker` | `OneTimeWorkRequestBuilder`, `setInputData`, `Result.success(outputData)` |
| **PeriodicWorkRequest** | `PeriodicSyncWorker` | `PeriodicWorkRequestBuilder(repeat, flex)`, `enqueueUniquePeriodicWork` |

### Constraints
| Use Case | Worker | Key APIs |
|----------|--------|----------|
| **All Constraint Types** | `ConstrainedWorker` | `Constraints.Builder`, `NetworkType`, `setRequiresCharging`, `setRequiresBatteryNotLow`, `setRequiresStorageNotLow`, `setRequiresDeviceIdle` |

### Chaining
| Use Case | Workers | Key APIs |
|----------|---------|----------|
| **Sequential Chain** | `ChainWorkerA → B → C` | `beginWith().then().then().enqueue()` |
| **Parallel + Combine** | `[A, B] → C` | `beginWith(listOf(a, b)).then(c)` |

### Policies
| Use Case | Key APIs |
|----------|----------|
| **Unique Work** | `enqueueUniqueWork(name, ExistingWorkPolicy.KEEP/REPLACE/APPEND/APPEND_OR_REPLACE)` |

### Priority
| Use Case | Worker | Key APIs |
|----------|--------|----------|
| **Expedited Work** | `ExpeditedWorker` | `setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)` |
| **Foreground Work** | `ForegroundWorker` | `setForeground(ForegroundInfo)`, notification, `foregroundServiceType` |

### Data
| Use Case | Worker | Key APIs |
|----------|--------|----------|
| **Input/Output** | `DataProcessingWorker` | `workDataOf()`, output merging in chains, 10KB limit |

### Observation
| Use Case | Key APIs |
|----------|----------|
| **Progress Reporting** | `setProgress(Data)`, `WorkInfo.progress` |
| **Observe by ID/Tag/Name** | `getWorkInfoByIdFlow`, `getWorkInfosByTagFlow`, `getWorkInfosForUniqueWorkFlow` |
| **WorkInfo States** | `ENQUEUED`, `RUNNING`, `SUCCEEDED`, `FAILED`, `BLOCKED`, `CANCELLED` |

### Error Handling
| Use Case | Worker | Key APIs |
|----------|--------|----------|
| **Retry + Backoff** | `RetryWorker` | `Result.retry()`, `BackoffPolicy.LINEAR/EXPONENTIAL`, `setBackoffCriteria` |
| **Failure** | `FailingWorker` | `Result.failure(outputData)`, error propagation in chains |

### Lifecycle
| Use Case | Key APIs |
|----------|----------|
| **Cancellation** | `cancelWorkById`, `cancelUniqueWork`, `cancelAllWorkByTag`, `cancelAllWork` |

### Debug
| Use Case | Key APIs |
|----------|----------|
| **Work Logs** | Room database logging from every worker, Flow-based UI |

## Architecture

- **DI**: Hilt with `@HiltWorker` + `HiltWorkerFactory` for constructor injection into workers
- **Database**: Room for worker execution logging (observable via Flow)
- **UI**: Jetpack Compose + Navigation Compose + Material3
- **WorkManager**: Custom `Configuration.Provider` on Application class (disables default initialization)

## How to Build

1. Open `workmanager-samples/` in Android Studio
2. Sync Gradle
3. Run on device/emulator (API 26+)

## Tech Stack
- Kotlin 2.0.0, Compose BOM 2024.10.00
- WorkManager 2.10.0
- Hilt 2.51.1 + hilt-work
- Room 2.6.1
- Navigation Compose 2.8.4
