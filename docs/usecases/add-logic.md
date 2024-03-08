# Add logic

We're going to use the `usecases` layer to add logic to our application. The goal is to make our logic as reusable
components that can be used in different parts of the application, in controllers but also in other usecases.

As documented in the [usecases library](https://github.com/nathanfallet/usecases), we can create a usecase by extending
the usecase interfaces (`IUseCase`, `ISuspendUseCase`, `IPairUseCase`, ...)

For models, we will directly use usecases defined in the `usecases` library. We will also add a custom usecase to
calculate the progress of all tasks.

In the `usecases` package, create two news file called `ICalculateProgressUseCase.kt` and `CalculateProgressUseCase`:

```kotlin
interface ICalculateProgressUseCase : IUseCase<List<Task>, Double>
```

```kotlin
class CalculateProgressUseCase : ICalculateProgressUseCase {

    override fun invoke(input: List<Task>): Double =
        if (input.isEmpty()) 0.0
        else input.filter { it.completed }.size.toDouble() / input.size.toDouble()

}
```

Now we need to declare usecases in dependency injection to inject them into controllers or other usecases (they can call
between themselves, for example, the compute usecase could call the list usecase, but when we will display the progress
we will also need the list of tasks, so we can pass it as a parameter as we already have it).

In the `Koin.kt` file, add a module for usecases:

```kotlin
val useCaseModule = module {
    single<IListModelSuspendUseCase<Task>>(named<Task>()) {
        ListModelFromRepositorySuspendUseCase(get<ITasksRepository>())
    }
    single<IGetModelSuspendUseCase<Task, Long>>(named<Task>()) {
        GetModelFromRepositorySuspendUseCase(get<ITasksRepository>())
    }
    single<ICreateModelSuspendUseCase<Task, CreateTaskPayload>>(named<Task>()) {
        CreateModelFromRepositorySuspendUseCase(get<ITasksRepository>())
    }
    single<IUpdateModelSuspendUseCase<Task, Long, UpdateTaskPayload>>(named<Task>()) {
        UpdateModelFromRepositorySuspendUseCase(get<ITasksRepository>())
    }
    single<IDeleteModelSuspendUseCase<Task, Long>>(named<Task>()) {
        DeleteModelFromRepositorySuspendUseCase(get<ITasksRepository>())
    }
    single<ICalculateProgressUseCase> { CalculateProgressUseCase() }
}

modules(
    // Existing modules...
    useCaseModule,
)
```
