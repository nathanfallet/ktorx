# Create a model

Creating a model allows us to represent our data inside our application. Then, we use this model to make operations on
our data, like creating, reading, updating, and deleting.

A model is a data class representing our data, with two payload for creating and updating the data.

In this example, we are going to create a `Task` model for a todo app with a `CreateTaskPayload` and
an `UpdateTaskPayload` to create and update those tasks.

In the `models` package, create your first model as defined in the [usecases](https://github.com/nathanfallet/usecases)
library.

```kotlin
@Serializable
data class Task(
    @ModelProperty("id")
    @Schema("ID of the task", "123")
    override val id: Long,
    @ModelProperty("string")
    @Schema("Name of the task", "A task")
    val name: String,
    @ModelProperty("boolean")
    @Schema("If the task is completed", "false")
    val completed: Boolean,
    @ModelProperty("date")
    @Schema("The date the task was created at", "2024-01-01T01:01:01.000Z")
    val createdAt: Instant,
) : IModel<Long, CreateTaskPayload, UpdateTaskPayload>
```

```kotlin
@Serializable
data class CreateTaskPayload(
    @PayloadProperty("string")
    @Schema("Name of the task", "A task")
    val name: String,
)
```

```kotlin
@Serializable
data class UpdateTaskPayload(
    @PayloadProperty("string")
    @Schema("Name of the task", "A task")
    val name: String,
    @PayloadProperty("boolean")
    @Schema("If the task is completed", "false")
    val completed: Boolean,
)
```

In case you have a warning on the `@Serializable` annotation, you need to add the following plugin to
your `build.gradle.kts` file:

```kotlin
kotlin("plugin.serialization") version "1.9.22" // Use your current Kotlin version
```

I personally recommend to create two gradle modules for your project, one for the backend server containing logic and
controllers, and one for your models and client, so you can share the same models between your server and your client.

Now let's [setup our database](../database/create-a-database.md).
