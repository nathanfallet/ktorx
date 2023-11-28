package me.nathanfallet.ktorx.routers.templates

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.controllers.base.IModelController
import me.nathanfallet.ktorx.models.*
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.annotations.ModelKey
import me.nathanfallet.usecases.models.annotations.PayloadKey
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateModelRouterTest {

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
            followRedirects = false
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(Json)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified Keys> createRouter(
        controller: IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>
    ): TemplateModelRouter<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
        return TemplateModelRouter(
            TestModel::class,
            TestCreatePayload::class,
            TestUpdatePayload::class,
            controller,
            TemplateMapping(
                errorTemplate = "error",
                listTemplate = "list",
                getTemplate = "get",
                createTemplate = "create",
                updateTemplate = "update",
                deleteTemplate = "delete",
                redirectUnauthorizedToUrl = "redirect={path}"
            ),
            { template, model ->
                respond(
                    TemplateResponse(
                        template, TemplateResponseData(
                            model["route"] as String,
                            model["keys"] as? List<Keys>,
                            model["item"] as? TestModel,
                            model["items"] as? List<TestModel>,
                            model["code"] as? Int,
                            model["error"] as? String
                        )
                    )
                )
            },
        )
    }

    @Test
    fun testTemplateGetRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.list(any(), UnitModel) } returns listOf(mock)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "list",
                TemplateResponseData(
                    "testmodels",
                    keys = listOf(
                        ModelKey("id", "id", ""),
                        ModelKey("string", "string", "")
                    ),
                    items = listOf(mock),
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.list(any(), UnitModel) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            TemplateResponse(
                "error",
                TemplateResponseData<TestModel, ModelKey>(
                    "testmodels",
                    code = HttpStatusCode.NotFound.value,
                    error = "error_mock",
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetRouteUnauthorized() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.list(any(), UnitModel) } throws ControllerException(
            HttpStatusCode.Unauthorized,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testTemplateGetCreateRoute() = testApplication {
        val client = installApp(this)
        val router = createRouter<PayloadKey>(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/create")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "create",
                TemplateResponseData<TestModel, PayloadKey>(
                    "testmodels",
                    keys = listOf(
                        PayloadKey("string", "string", "", true)
                    )
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplatePostCreateRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<PayloadKey>(controller)
        coEvery { controller.create(any(), UnitModel, TestCreatePayload("string")) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/testmodels/create") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "string" to "string"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, response.status)
        coVerify { controller.create(any(), UnitModel, TestCreatePayload("string")) }
    }

    @Test
    fun testTemplatePostCreateRouteBadRequest() = testApplication {
        val client = installApp(this)
        val router = createRouter<PayloadKey>(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/testmodels/create") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "other" to "string"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            TemplateResponse(
                "error",
                TemplateResponseData<TestModel, ModelKey>(
                    "testmodels",
                    code = HttpStatusCode.BadRequest.value,
                    error = "error_body_invalid",
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetIdRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "get",
                TemplateResponseData(
                    "testmodels",
                    keys = listOf(
                        ModelKey("id", "id", ""),
                        ModelKey("string", "string", "")
                    ),
                    item = mock,
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetIdRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.get(any(), UnitModel, 1) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            TemplateResponse(
                "error",
                TemplateResponseData<TestModel, ModelKey>(
                    "testmodels",
                    code = HttpStatusCode.NotFound.value,
                    error = "error_mock",
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetIdUpdateRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<PayloadKey>(controller)
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1/update")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "update",
                TemplateResponseData(
                    "testmodels",
                    keys = listOf(
                        PayloadKey("string", "string", "", true)
                    ),
                    item = mock,
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetIdUpdateRouteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<PayloadKey>(controller)
        coEvery { controller.get(any(), UnitModel, 1) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1/update")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            TemplateResponse(
                "error",
                TemplateResponseData<TestModel, ModelKey>(
                    "testmodels",
                    code = HttpStatusCode.NotFound.value,
                    error = "error_mock",
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplatePostIdUpdateRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<PayloadKey>(controller)
        coEvery { controller.update(any(), UnitModel, 1, TestUpdatePayload("string")) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/testmodels/1/update") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "string" to "string"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, response.status)
        coVerify { controller.update(any(), UnitModel, 1, TestUpdatePayload("string")) }
    }

    @Test
    fun testTemplatePostIdUpdateRouteBadRequest() = testApplication {
        val client = installApp(this)
        val router = createRouter<ModelKey>(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/testmodels/1/update") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "other" to "string"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            TemplateResponse(
                "error",
                TemplateResponseData<TestModel, ModelKey>(
                    "testmodels",
                    code = HttpStatusCode.BadRequest.value,
                    error = "error_body_invalid",
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetIdRouteDelete() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.get(any(), UnitModel, 1) } returns mock
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1/delete")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "delete",
                TemplateResponseData(
                    "testmodels",
                    keys = listOf(
                        ModelKey("id", "id", ""),
                        ModelKey("string", "string", "")
                    ),
                    item = mock,
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateGetIdRouteDeleteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.get(any(), UnitModel, 1) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels/1/delete")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            TemplateResponse(
                "error",
                TemplateResponseData<TestModel, ModelKey>(
                    "testmodels",
                    code = HttpStatusCode.NotFound.value,
                    error = "error_mock",
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplatePostIdRouteDelete() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.delete(any(), UnitModel, 1) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/testmodels/1/delete")
        assertEquals(HttpStatusCode.Found, response.status)
        coVerify { controller.delete(any(), UnitModel, 1) }
    }

    @Test
    fun testTemplatePostIdRouteDeleteControllerException() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.delete(any(), UnitModel, 1) } throws ControllerException(
            HttpStatusCode.NotFound,
            "error_mock"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/testmodels/1/delete")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            TemplateResponse(
                "error",
                TemplateResponseData<TestModel, ModelKey>(
                    "testmodels",
                    code = HttpStatusCode.NotFound.value,
                    error = "error_mock",
                )
            ), response.body()
        )
    }

}
