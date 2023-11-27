package me.nathanfallet.ktorx.routers.localization

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import me.nathanfallet.ktorx.controllers.base.IModelController
import me.nathanfallet.ktorx.models.*
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.plugins.I18n
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.annotations.ModelKey
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedTemplateModelRouterTest {

    private val mock = TestModel(1, "string")

    private fun installApp(application: ApplicationTestBuilder): HttpClient {
        application.install(I18n) {
            supportedLocales = listOf("en").map(Locale::forLanguageTag)
            useOfUri = true
        }
        application.application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json(kotlinx.serialization.json.Json)
            }
        }
        return application.createClient {
            followRedirects = false
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(kotlinx.serialization.json.Json)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified Keys> createRouter(
        controller: IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>,
        getLocaleForCallUseCase: IGetLocaleForCallUseCase
    ): LocalizedTemplateModelRouter<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
        return LocalizedTemplateModelRouter(
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
                            (model["locale"] as Locale).language,
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
            getLocaleForCallUseCase
        )
    }

    @Test
    fun testRedirect() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val router = createRouter<ModelKey>(controller, mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/testmodels")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testLocaleEnglish() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload>>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = createRouter<ModelKey>(controller, getLocaleForCallUseCase)
        coEvery { controller.getAll(any(), UnitModel) } returns listOf(mock)
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/en/testmodels")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            TemplateResponse(
                "list",
                TemplateResponseData(
                    "en",
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

}
