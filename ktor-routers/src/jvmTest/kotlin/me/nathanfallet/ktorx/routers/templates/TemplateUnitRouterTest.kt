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
import io.mockk.mockk
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.ITestUnitController
import me.nathanfallet.ktorx.models.TestModel
import me.nathanfallet.ktorx.models.TestUser
import me.nathanfallet.ktorx.models.exceptions.ControllerRedirect
import me.nathanfallet.ktorx.models.templates.TemplateResponse
import me.nathanfallet.ktorx.models.templates.TemplateResponseData
import me.nathanfallet.usecases.models.annotations.ModelKey
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateUnitRouterTest {

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
        controller: IUnitController,
        route: String? = null,
    ) = TemplateUnitRouter(
        controller,
        ITestUnitController::class,
        { template, model ->
            respond(
                TemplateResponse(
                    template, TemplateResponseData(
                        model["route"] as String,
                        model["keys"] as? List<Keys>,
                        model["item"] as? TestModel,
                        model["item"] as? String,
                        model["map"] as? String,
                        model["item"] as? TestUser,
                        model["items"] as? List<TestModel>,
                        model["code"] as? Int,
                        model["error"] as? String
                    )
                )
            )
        },
        "error",
        "redirect={path}",
        route
    )

    @Test
    fun testTemplateBasicRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.hello() } returns "Hello world"
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/hello")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "hello.ftl",
                TemplateResponseData<TestModel, ModelKey>(
                    "",
                    keys = listOf(),
                    itemString = "Hello world",
                )
            ), response.body()
        )
    }

    @Test
    fun testTemplateBasicRouteRedirect() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.hello() } throws ControllerRedirect("/redirect")
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/hello")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testTemplateBasicRouteRedirectPermanent() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = createRouter<ModelKey>(controller)
        coEvery { controller.hello() } throws ControllerRedirect("/redirect", true)
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/hello")
        assertEquals(HttpStatusCode.MovedPermanently, response.status)
    }

    @Test
    fun testTemplateBasicRouteCustomRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<ITestUnitController>()
        val router = createRouter<ModelKey>(controller, "test")
        coEvery { controller.hello() } returns "Hello world"
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/test/hello")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "hello.ftl",
                TemplateResponseData<TestModel, ModelKey>(
                    "test",
                    keys = listOf(),
                    itemString = "Hello world",
                )
            ), response.body()
        )
    }

}
