plugins {
    kotlin("multiplatform").version("1.9.20").apply(false)
    kotlin("plugin.serialization").version("1.9.20").apply(false)
    id("convention.publication")
}

allprojects {
    group = "me.nathanfallet.ktorx"
    version = "1.2.1"

    repositories {
        mavenCentral()
    }

    dependencies {
        configurations
            .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
            .forEach {
                add(it.name, "io.mockative:mockative-processor:2.0.1")
            }
    }
}
