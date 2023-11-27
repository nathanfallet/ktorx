# ktor-sentry

A Sentry plugin for Ktor

## Installation

Add dependency to your `build.gradle` or `pom.xml`:

```groovy
compile 'me.nathanfallet.ktorx:ktor-sentry:1.5.1'
compile 'io.sentry:sentry:6.32.0'
```

```xml

<dependencies>
    <dependency>
        <groupId>me.nathanfallet.ktorx</groupId>
        <artifactId>ktor-sentry-jvm</artifactId>
        <version>1.5.1</version>
    </dependency>
    <dependency>
        <groupId>io.sentry</groupId>
        <artifactId>sentry</artifactId>
        <version>6.32.0</version>
    </dependency>
</dependencies>
```

## Usage

```kotlin
fun Application.configureSentry() {
    Sentry.init {
        it.dsn = "..."
        it.tracesSampleRate = 1.0 // 100% of traces, default is 0.0 (disabled)
    }
    install(KtorSentry)
}
```

In case you are using Koin, you can register and use `ICaptureExceptionUseCase`:

```kotlin
single<ICaptureExceptionUseCase> { CaptureExceptionUseCase() }
```
