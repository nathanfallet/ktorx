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

    val coroutinesVersion = "1.7.3"
    val ktorVersion = "2.3.6"
    val usecasesVersion = "1.2.3"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-freemarker:$ktorVersion")
                implementation("me.nathanfallet.usecases:usecases:$usecasesVersion")
                implementation("com.github.aymanizz:ktor-i18n:2.0.0")
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
