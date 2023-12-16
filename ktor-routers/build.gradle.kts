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

    val coroutinesVersion = "1.7.3"
    val ktorVersion = "2.3.7"
    val usecasesVersion = "1.5.5"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

                api("io.ktor:ktor-server-core:$ktorVersion")
                api("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("io.swagger.codegen.v3:swagger-codegen:3.0.51")
                api("io.swagger.codegen.v3:swagger-codegen-generators:1.0.45")
                api("io.swagger.core.v3:swagger-core:2.2.19")

                api("me.nathanfallet.usecases:usecases:$usecasesVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.mockative:mockative:2.0.1")
                implementation("io.mockk:mockk:1.13.8")
            }
        }
    }
}
