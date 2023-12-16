plugins {
    kotlin("multiplatform")
    id("convention.publication")
    id("org.jetbrains.kotlinx.kover")
    id("com.google.devtools.ksp")
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("ktor-sentry")
            description.set("A Sentry plugin for Ktor")
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

    val sentryVersion = "6.32.0"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.sentry:sentry:$sentryVersion")
                implementation("io.sentry:sentry-kotlin-extensions:$sentryVersion")

                api(libs.coroutines)
                api(libs.bundles.ktor.server.api)
                api(libs.usecases)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.mockative:mockative:2.0.1")
            }
        }
    }
}
