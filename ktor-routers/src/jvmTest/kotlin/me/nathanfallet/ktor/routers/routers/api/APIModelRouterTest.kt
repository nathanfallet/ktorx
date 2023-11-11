package me.nathanfallet.ktor.routers.routers.api

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
import me.nathanfallet.ktor.routers.controllers.base.IModelController
import me.nathanfallet.ktor.routers.models.TestCreatePayload
import me.nathanfallet.ktor.routers.models.TestModel
import me.nathanfallet.ktor.routers.models.TestUpdatePayload
import me.nathanfallet.ktor.routers.models.exceptions.ControllerException
import me.nathanfallet.usecases.models.UnitModel
import kotlin.test.Test
import kotlin.test.assertEquals

class APIModelRouterTest {

    private val mock = TestModel(1, "string")
    private val createMock = TestCreatePayload("string")
    private val updateMock = TestUpdatePayload("string")

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
        controller: IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>
    ): APIModelRouter<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
        return APIModelRouter(
            TestModel::class,
            TestCreatePayload::class,
            TestUpdatePayload::class,
            controller
        )
    }

    @Test
    fun testAPIGetRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.getAll(any(), UnitModel) } returns listOf(mock)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf(mock), response.body())
    }

    @Test
    fun testAPIGetRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.getAll(any(), UnitModel) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(mapOf("error" to "error_mock"), response.body())
    }

    @Test
    fun testAPIGetIdRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mock, response.body())
    }

    @Test
    fun testAPIGetIdRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.get(any(), UnitModel, 1) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(mapOf("error" to "error_mock"), response.body())
    }

    @Test
    fun testAPIPostRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.create(any(), UnitModel, createMock) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/api/testmodels") {
            contentType(ContentType.Application.Json)
            setBody(createMock)
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(mock, response.body())
    }

    @Test
    fun testAPIPostRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.create(any(), UnitModel, createMock) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/api/testmodels") {
            contentType(ContentType.Application.Json)
            setBody(createMock)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(mapOf("error" to "error_mock"), response.body())
    }

    @Test
    fun testAPIPostRouteInvalidBody() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/api/testmodels") {
            setBody("invalid")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(mapOf("error" to "error_body_invalid"), response.body())
    }

    @Test
    fun testAPIPutRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.update(any(), UnitModel, 1, updateMock) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.put("/api/testmodels/1") {
            contentType(ContentType.Application.Json)
            setBody(updateMock)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mock, response.body())
    }

    @Test
    fun testAPIPutRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.update(any(), UnitModel, 1, updateMock) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.put("/api/testmodels/1") {
            contentType(ContentType.Application.Json)
            setBody(updateMock)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(mapOf("error" to "error_mock"), response.body())
    }

    @Test
    fun testAPIPutRouteInvalidBody() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.put("/api/testmodels/1") {
            setBody("invalid")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(mapOf("error" to "error_body_invalid"), response.body())
    }

    @Test
    fun testAPIDeleteRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.delete(any(), UnitModel, 1) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.delete("/api/testmodels/1")
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun testAPIDeleteRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter(controller)
        coEvery { controller.delete(any(), UnitModel, 1) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.delete("/api/testmodels/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(mapOf("error" to "error_mock"), response.body())
    }
}
