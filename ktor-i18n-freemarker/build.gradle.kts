plugins {
    kotlin("multiplatform")
    id("convention.publication")
    id("org.jetbrains.kotlinx.kover")
    id("com.google.devtools.ksp")
}

repositories {
    maven("https://jitpack.io")
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("ktor-i18n-freemarker")
            description.set("An i18n plugin for Ktor Freemarker")
        }
    }
}

kotlin {
    jvm {
        jvmToolchain(19)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":ktor-i18n"))
                api(libs.coroutines)
                api(libs.bundles.ktor.server.freemarker)
                api(libs.usecases)
                api(libs.i18n)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
