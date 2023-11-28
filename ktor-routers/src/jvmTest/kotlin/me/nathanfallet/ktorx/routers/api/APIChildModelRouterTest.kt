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
import me.nathanfallet.ktorx.controllers.base.IChildModelController
import me.nathanfallet.ktorx.controllers.base.IModelController
import me.nathanfallet.ktorx.models.TestChildModel
import me.nathanfallet.ktorx.models.TestCreatePayload
import me.nathanfallet.ktorx.models.TestModel
import me.nathanfallet.ktorx.models.TestUpdatePayload
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.usecases.models.UnitModel
import kotlin.test.Test
import kotlin.test.assertEquals

class APIChildModelRouterTest {

    private val mock = TestModel(1, "string")
    private val childMock = TestChildModel(2, 1, "string")
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

    private fun createChildRouter(
        controller: IChildModelController<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, TestModel, Long>,
        parentRouter: APIChildModelRouter<TestModel, Long, *, *, *, *>,
        mapping: APIMapping = APIMapping(),
        route: String? = null,
        prefix: String? = null
    ): APIChildModelRouter<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, TestModel, Long> {
        return APIChildModelRouter(
            TestChildModel::class,
            TestCreatePayload::class,
            TestUpdatePayload::class,
            controller,
            parentRouter,
            mapping,
            route,
            null,
            prefix
        )
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
        val childController =
            mockk<IChildModelController<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, TestModel, Long>>()
        val router = createChildRouter(childController, createRouter(controller))
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        coEvery { childController.list(any(), mock) } returns listOf(childMock)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels/1/testchildmodels")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf(childMock), response.body())
    }

    @Test
    fun testAPIGetRouteCustomRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val childController =
            mockk<IChildModelController<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, TestModel, Long>>()
        val router = createChildRouter(childController, createRouter(controller), route = "childs")
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        coEvery { childController.list(any(), mock) } returns listOf(childMock)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels/1/childs")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf(childMock), response.body())
    }

    @Test
    fun testAPIGetRouteCustomPrefix() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val childController =
            mockk<IChildModelController<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, TestModel, Long>>()
        val router = createChildRouter(childController, createRouter(controller), route = "childs", prefix = "/api/v1")
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        coEvery { childController.list(any(), mock) } returns listOf(childMock)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/v1/testmodels/1/childs")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf(childMock), response.body())
    }

    @Test
    fun testAPIGetIdRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val childController =
            mockk<IChildModelController<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, TestModel, Long>>()
        val router = createChildRouter(childController, createRouter(controller))
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        coEvery { childController.get(any(), mock, 2) } returns childMock
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels/1/testchildmodels/2")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(childMock, response.body())
    }

    @Test
    fun testAPIGetRouteNotMapped() = testApplication {
        val client = installApp(this)
        val router = createChildRouter(mockk(), createRouter(mockk()), APIMapping(listEnabled = false))
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels/1/testchildmodels")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testAPIGetIdRouteNotMapped() = testApplication {
        val client = installApp(this)
        val router = createChildRouter(mockk(), createRouter(mockk()), APIMapping(getEnabled = false))
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/api/testmodels/1/testchildmodels/2")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testAPICreateRouteNotMapped() = testApplication {
        val client = installApp(this)
        val router = createChildRouter(mockk(), createRouter(mockk()), APIMapping(createEnabled = false))
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/api/testmodels/1/testchildmodels")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testAPIUpdateRouteNotMapped() = testApplication {
        val client = installApp(this)
        val router = createChildRouter(mockk(), createRouter(mockk()), APIMapping(updateEnabled = false))
        routing {
            router.createRoutes(this)
        }
        val response = client.put("/api/testmodels/1/testchildmodels/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testAPIDeleteRouteNotMapped() = testApplication {
        val client = installApp(this)
        val router = createChildRouter(mockk(), createRouter(mockk()), APIMapping(deleteEnabled = false))
        routing {
            router.createRoutes(this)
        }
        val response = client.delete("/api/testmodels/1/testchildmodels/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

}
