pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            library("usecases", "me.nathanfallet.usecases:usecases:1.5.6")
            library("i18n", "me.nathanfallet.i18n:i18n:1.0.10")
            library("surexposed", "me.nathanfallet.surexposed:surexposed:1.0.1")

            version("ktor", "2.3.7")
            library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef("ktor")
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef("ktor")
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef("ktor")
            library("ktor-server-sessions", "io.ktor", "ktor-server-sessions").versionRef("ktor")
            library("ktor-server-freemarker", "io.ktor", "ktor-server-freemarker").versionRef("ktor")
            library("ktor-server-websockets", "io.ktor", "ktor-server-websockets").versionRef("ktor")
            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef("ktor")
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef("ktor")
            library("ktor-client-auth", "io.ktor", "ktor-client-auth").versionRef("ktor")

            bundle(
                "ktor-server-api",
                listOf(
                    "ktor-server-core",
                    "ktor-server-content-negotiation",
                    "ktor-server-sessions",
                    "ktor-serialization-kotlinx-json"
                )
            )
            bundle(
                "ktor-server-freemarker",
                listOf(
                    "ktor-server-core",
                    "ktor-server-freemarker"
                )
            )
            bundle(
                "ktor-server-websockets",
                listOf(
                    "ktor-server-core",
                    "ktor-server-websockets"
                )
            )
            bundle(
                "ktor-client-api",
                listOf(
                    "ktor-client-core",
                    "ktor-client-content-negotiation",
                    "ktor-serialization-kotlinx-json",
                    "ktor-client-auth"
                )
            )
        }
    }
}

rootProject.name = "ktorx"
includeBuild("convention-plugins")
include(":ktor-database-sessions")
include(":ktor-health")
include(":ktor-i18n")
include(":ktor-i18n-freemarker")
include(":ktor-routers")
include(":ktor-routers-websockets")
include(":ktor-routers-admin")
include(":ktor-routers-auth")
include(":ktor-routers-client")
include(":ktor-routers-locale")
include(":ktor-routers-admin-locale")
include(":ktor-routers-auth-locale")
include(":ktor-sentry")
