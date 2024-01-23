plugins {
    kotlin("multiplatform")
    id("convention.publication")
    id("org.jetbrains.kotlinx.kover")
    id("com.google.devtools.ksp")
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("ktor-databases")
            description.set("Database extensions for Ktor")
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

    val exposedVersion = "0.46.0"

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.exposed:exposed-core:$exposedVersion")
                api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                api("org.jetbrains.exposed:exposed-dao:$exposedVersion")

                api(libs.coroutines)
                api(libs.bundles.ktor.server.api)
                api(libs.usecases)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.h2database:h2:2.2.224")
            }
        }
    }
}
