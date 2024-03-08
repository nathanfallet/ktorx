# Example router

Now that we have set up our project and dependency injection, we can create our first router.

A router is the place to route your requests to the right controller.

The base `ktor-routers` module provides two types of routers: `APIRouter` and `TemplateRouter`, but more are available
in other modules, like `LocalizedTemplateRouter` in `ktor-routers-locale` which allows you to localize your content
directly, and you can even create your own.

We'll start by creating a controller to handle our routes, and then we'll create a router to route our requests to this
controller for API and templating.

Create a new file in the `controllers` package called `IHelloController.kt` and add the following code:

```kotlin
interface IHelloController : IUnitController {

    @APIMapping
    @TemplateMapping("hello.ftl")
    @Path("GET", "/")
    fun hello(): Map<String, Any>

}
```

This is the definition of our controller with annotations that will be handled by the router to know what kind of route
to create for our controller. It extends `IUnitController` which is one of the three types of controllers available in
`ktor-routers` (the others are `IModelController` and `IChildModelController`, we will talk about them later when we
will create models and have actions on them).

Now, let's create the controller itself in a new file called `HelloController.kt`:

```kotlin
class HelloController : IHelloController {

    override fun hello(): Map<String, Any> = mapOf("hello" to "world")

}
```

In the `resources`, we will create a `templates` folder and add a `hello.ftl` file with the following content:

```html
<p id="hello-p">
    Hello ${hello}!
</p>
```

And the final step is the router itself. We will create a `HelloRouter.kt` file in the same `controllers` package, and
provide both API and templating capabilities:

```kotlin
class HelloRouter(
    controller: IHelloController,
) : ConcatUnitRouter(
    APIUnitRouter(
        controller,
        IHelloController::class
    ),
    TemplateUnitRouter(
        controller,
        IHelloController::class,
        { template, model ->
            respondTemplate(template, model)
        },
        "error.ftl"
    )
)
```

The `ConcatUnitRouter` is a router that concatenates multiple routers together.

For the API response to be available, we need to add the serialization plugin to Ktor. Create a new file in
the `plugins`
package called `Serialization.kt` and add the following code:

```kotlin
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}
```

For the `respondTemplate` to be available, we will add freemarker, which is the templating engine, to our dependencies
in the `build.gradle.kts` file:

```kotlin
implementation("io.ktor:ktor-server-freemarker")
```

We will add the templating plugin to our application, so that the `respondTemplate` function knows which engine to use.
Create a new file in the `plugins` package called `Templating.kt` and add the following code:

```kotlin
fun Application.configureTemplating() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}
```

As always, don't forget to call those functions in your `Application.kt` file:

```kotlin
fun Application.module() {
    // Existing code...

    configureSerialization() // Add this
    configureTemplating() // And this
}
```

Now that our templating engine is set up, we can add our controllers and routers to dependency injection. Add this
to `Koin.kt`:

```kotlin
val controllerModule = module {
    single<IHelloController> { HelloController() }
}
val routerModule = module {
    single { HelloRouter(get()) }
}

modules(
    // Existing modules...
    controllerModule,
    routerModule,
)
```

Finally, we can add our router to the application. In the `plugins` package, create a new file called `Routing.kt` and
add the following code:

```kotlin
fun Application.configureRouting() {
    install(IgnoreTrailingSlash)
    routing {
        listOf(
            get<HelloRouter>()
        ).forEach {
            it.createRoutes(this)
        }
    }
}
```

As always, don't forget to call this function in your `Application.kt` file:

```kotlin
fun Application.module() {
    // Existing code...

    configureRouting() // Add this
}
```

You can now start your application and go to [http://localhost:8080](http://localhost:8080) (templating router)
and [http://localhost:8080/api](http://localhost:8080/api) (API router) to see the result.

Before continuing, it's important to write some unit tests to ensure that everything is working as expected. You can
check [testing the example router](testing-example-router.md) to see how to write tests for your application, controller
and router.
