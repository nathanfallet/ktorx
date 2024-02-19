# Create a new project

Create a new Ktor project (using IntelliJ IDEA or the Ktor website), and add the Ktorx dependency to
the `build.gradle.kts` file.

```kotlin
val ktorxVersion = "2.2.0"

implementation("me.nathanfallet.ktorx:ktor-routers:$ktorxVersion")
```

Create your folder structure to start having a clean project.

```
.
├── Application.kt
├── controllers/
├── database/
├── models/
├── plugins/
│   └── Routing.kt
├── repositories/
└── usecases/
```

If you're ready, we can continue by [setting up dependency injection](dependency-injection.md).

You can also check [extopy-backend](https://github.com/groupeminaste/extopy-backend), which is a real world example of a
complete Ktorx project.
