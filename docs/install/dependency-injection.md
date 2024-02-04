# Dependency injection

Dependency injection is a software design pattern that allows the removal of hard-coded dependencies and makes it
possible to change them, whether at run-time or compile-time. This pattern is used to create a loosely coupled system
and to simplify testing.

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

This is where you will define your modules. A module is a collection of dependencies that can be injected into your
application. For example, you can create a module for your database, a module for your repositories, a module for your
use cases, etc. We will come to create these modules later as we need them.

Don't forget to call this function in your `Application.kt` file:

```kotlin
fun Application.module() {
    // Existing code...

    configureKoin() // Add this
}
```

To check that everything is working, we will [set up our first router](example-router.md) and get a hello world
response.
