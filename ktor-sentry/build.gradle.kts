plugins {
    kotlin("multiplatform")
    `maven-publish`
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

    val coroutinesVersion = "1.7.3"
    val ktorVersion = "2.3.6"
    val sentryVersion = "6.32.0"
    val usecasesVersion = "1.2.1"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.sentry:sentry:$sentryVersion")
                implementation("io.sentry:sentry-kotlin-extensions:$sentryVersion")
                implementation("me.nathanfallet.usecases:usecases:$usecasesVersion")
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
