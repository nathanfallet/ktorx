package me.nathanfallet.ktorx.routers.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.models.ITestUnitController
import kotlin.test.Test
import kotlin.test.assertEquals

class APIUnitRouterTest {

    private fun installApp(application: ApplicationTestBuilder): HttpClient {
        application.application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json(Json)
            }
        }
        return application.createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(Json)
            }
        }
    }

    @Test
    fun testAPIBasicRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = APIUnitRouter(
            controller,
            ITestUnitController::class
        )
        coEvery { controller.hello() } returns "Hello world"
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/hello")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello world", response.body())
    }

    @Test
    fun testAPIQueryParameterRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = APIUnitRouter(
            controller,
            ITestUnitController::class
        )
        coEvery { controller.helloQuery("world") } returns "Hello world"
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/hello/query?name=world")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello world", response.body())
    }

    @Test
    fun testAPIPathParameterRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = APIUnitRouter(
            controller,
            ITestUnitController::class
        )
        coEvery { controller.helloPath("world") } returns "Hello world"
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/hello/path/world")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello world", response.body())
    }

    @Test
    fun testAPIBasicRouteCustomRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = APIUnitRouter(
            controller,
            ITestUnitController::class,
            route = "/test"
        )
        coEvery { controller.hello() } returns "Hello world"
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/test/hello")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello world", response.body())
    }

}
