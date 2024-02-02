# Dependency injection

To use dependency injection in your project, we are going to use [Koin](https://insert-koin.io/).

```kotlin
val koinVersion = "3.5.0"

implementation("io.insert-koin:koin-core:$koinVersion")
implementation("io.insert-koin:koin-ktor:$koinVersion")
```

Then, in your `plugins` package, create a new file called `Koin.kt` and add the following code:

```kotlin
fun Application.configureKoin() {
    install(Koin) {

        modules()
    }
}
```

Don't forget to call this function in your `Application.kt` file:

```kotlin
fun Application.module() {
    // Existing code...

    configureKoin() // Add this
}
```

If you're ready, we can continue by [creating our models](../models/create-a-model.md).
