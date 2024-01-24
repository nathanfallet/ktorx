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
            name.set("ktor-routers")
            description.set("Generic routers for Ktor projects.")
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

    val ktorVersion = "2.3.7"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.swagger.codegen.v3:swagger-codegen:3.0.51")
                api("io.swagger.codegen.v3:swagger-codegen-generators:1.0.45")
                api("io.swagger.core.v3:swagger-core:2.2.19")

                api(libs.coroutines)
                api(libs.bundles.ktor.server.api)
                api(libs.usecases)
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
