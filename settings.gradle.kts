pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "ktorx"
includeBuild("convention-plugins")
include(":ktor-i18n-freemarker")
include(":ktor-routers")
include(":ktor-sentry")
