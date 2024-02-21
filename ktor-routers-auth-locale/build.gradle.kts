plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("convention.publication")
    id("org.jetbrains.kotlinx.kover")
    id("com.google.devtools.ksp")
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("ktor-routers-auth-locale")
            description.set("Locale extensions for ktor-routers-auth.")
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

    val ktorVersion = "2.3.8"

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":ktor-i18n"))
                api(project(":ktor-routers"))
                api(project(":ktor-routers-auth"))
                api(project(":ktor-routers-locale"))
                api(libs.coroutines)
                api(libs.bundles.ktor.server.api)
                api(libs.usecases)
                api(libs.i18n)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.mockk:mockk:1.13.8")
            }
        }
    }
}
