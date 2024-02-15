# Testing the example router

Finally, we can add a test to check that the startup is successful. Add a test directory alongside the `main` directory
and create a new file called `ApplicationTest.kt` with the following content:

```kotlin
class ApplicationTest {

    @Test
    fun testStartup() = testApplication {
        environment {
            config = ApplicationConfig("application.test.conf")
        }
        application {
            module()
        }
        val response = client.get("/api")
        assertEquals(HttpStatusCode.OK, response.status)
    }

}
```

This test allows us to check that the application starts correctly. For example, if we forget to declare a dependency in
our `Koin.kt` file, the test will fail.

Now let's test our controller. We check that it returns the expected result. Create a new file
called `HelloControllerTest.kt` in a `controllers` package in the test directory:

```kotlin
class HelloControllerTest {

    @Test
    fun testHello() {
        val controller = HelloController()
        assertEquals(
            mapOf("hello" to "world"),
            controller.hello()
        )
    }

}
```

And the last step is to test our router. We check that the route is correctly registered and that it returns the
expected result.

First, we need to add missing dependencies to gradle for client content negotiation, mocking and template testing:

```kotlin
testImplementation("io.ktor:ktor-client-content-negotiation")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jsoup:jsoup:1.16.2")
```

Create a new file called `HelloRouterTest.kt` in the `controllers` package in the test directory. We create a first
method to set up our application for testing that we will use in all our tests:

```kotlin
private fun installApp(application: ApplicationTestBuilder): HttpClient {
    application.environment {
        config = ApplicationConfig("application.test.conf")
    }
    application.application {
        configureSerialization()
        configureTemplating()
    }
    return application.createClient {
        followRedirects = false
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }
}
```

Then we can create a test for API route. We make a request to the `/api` route and check that the response is correct:

```kotlin
@Test
fun testHelloAPI() = testApplication {
        val client = installApp(this)
        val controller = mockk<IHelloController>()
        val router = HelloRouter(controller)
        every { controller.hello() } returns mapOf("hello" to "world")
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mapOf("hello" to "world"), response.body())
    }
```

In that test, we mock the behavior of the controller to return a specific result. We then create a new router and
register its routes to the application. Finally, we make a request to the route and check that the response is the
expected response for a controller with the behavior we defined.

For template testing, we do almost the same thing, but we check that the response is a template response:

```kotlin
@Test
fun testHelloTemplate() = testApplication {
        val client = installApp(this)
        val controller = mockk<IHelloController>()
        val router = HelloRouter(controller)
        every { controller.hello() } returns mapOf("hello" to "world")
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val document = Jsoup.parse(response.bodyAsText())
        assertEquals("Hello world!", document.getElementById("hello-p")?.text())
    }
```

If you're ready to go further, we can continue by [creating our models](../models/create-a-model.md) to make more
complex routers.
