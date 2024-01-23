package me.nathanfallet.ktorx.routers.base

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.models.ITestModelController
import me.nathanfallet.ktorx.models.TestCreatePayload
import me.nathanfallet.ktorx.models.TestModel
import me.nathanfallet.ktorx.models.TestUpdatePayload
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractModelRouterTest {

    private val mock = TestModel(1, "string")

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

    private fun createRouter(
        route: String?,
        id: String?,
        prefix: String?,
    ): AbstractModelRouter<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
        val controller = object : ITestModelController {
            override suspend fun basic(call: ApplicationCall): String {
                throw NotImplementedError()
            }

            override suspend fun list(call: ApplicationCall): List<TestModel> {
                return emptyList()
            }

            override suspend fun get(call: ApplicationCall, id: Long): TestModel {
                throw NotImplementedError()
            }

            override suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun update(call: ApplicationCall, id: Long, payload: TestUpdatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun delete(call: ApplicationCall, id: Long) {
                throw NotImplementedError()
            }
        }
        return object : AbstractModelRouter<TestModel, Long, TestCreatePayload, TestUpdatePayload>(
            typeInfo<TestModel>(),
            typeInfo<TestCreatePayload>(),
            typeInfo<TestUpdatePayload>(),
            typeInfo<List<TestModel>>(),
            controller,
            ITestModelController::class,
            route,
            id,
            prefix
        ) {
            override fun createRoutes(root: Route, openAPI: OpenAPI?) {
                root.get("$fullRoute/{${this.id}}") {
                    call.respond(mock)
                }
            }

            override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {}
        }
    }

    @Test
    fun testNoArgs() = testApplication {
        val client = installApp(this)
        val router = createRouter(null, null, null)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mock, response.body())
    }

    @Test
    fun testPrefix() = testApplication {
        val client = installApp(this)
        val router = createRouter(null, null, "prefix")
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/prefix/testmodels/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mock, response.body())
    }

    @Test
    fun testId() = testApplication {
        val client = installApp(this)
        val router = createRouter(null, "customId", null)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mock, response.body())
    }

    @Test
    fun testRoute() = testApplication {
        val client = installApp(this)
        val router = createRouter("customRoute", null, null)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/customRoute/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mock, response.body())
    }

    @Test
    fun testAll() = testApplication {
        val client = installApp(this)
        val router = createRouter("customRoute", "customId", "prefix")
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/prefix/customRoute/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mock, response.body())
    }

}
