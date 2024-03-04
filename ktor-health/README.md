# ktor-health

Health check for Ktor projects.

## Installation

Add dependency to your `build.gradle(.kts)` or `pom.xml`:

```kotlin
api("me.nathanfallet.ktorx:ktor-health:2.2.3")
```

```xml

<dependency>
    <groupId>me.nathanfallet.ktorx</groupId>
    <artifactId>ktor-health-jvm</artifactId>
    <version>2.2.3</version>
</dependency>
```

## Usage

Install the plugin, and optionally add some checks:

```kotlin
install(KtorHealth) {
    // Add a check on `/healthz` endpoint
    healthCheck("database") {
        // Check your database connection and return a boolean
    }
    // You can also create checks on `/readyz` with `readyCheck(...)`
    // Or a custom endpoint with `customCheck("/custom", "database") { ... }`
}
```
