pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "android-client-server-samples"

include(":ipc_service_aidl:server")
include(":ipc_service_aidl:client")
include(":content_provider:server")
include(":content_provider:client")
include(":shared_storage:server")
include(":shared_storage:client")
