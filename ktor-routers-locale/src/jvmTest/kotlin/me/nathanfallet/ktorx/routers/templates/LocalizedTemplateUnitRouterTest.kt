package me.nathanfallet.ktorx.routers.templates

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.swagger.v3.oas.models.OpenAPI
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.plugins.I18n
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedTemplateUnitRouterTest {

    private fun installApp(application: ApplicationTestBuilder): HttpClient {
        application.install(I18n) {
            supportedLocales = listOf("en").map(Locale::forLanguageTag)
            useOfUri = true
        }
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

    private fun createRouter(getLocaleForCallUseCase: IGetLocaleForCallUseCase): LocalizedTemplateUnitRouter {
        return object : LocalizedTemplateUnitRouter(
            { template, model ->
                respond(template + ":" + model.map { it.key + ":" + it.value }.joinToString(","))
            },
            getLocaleForCallUseCase = getLocaleForCallUseCase
        ) {
            override fun createLocalizedRoutes(root: Route, openAPI: OpenAPI?) {
                root.get("/test") {
                    call.respondTemplate("test", mapOf())
                }
            }
        }
    }

    @Test
    fun testRedirect() = testApplication {
        val client = installApp(this)
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = createRouter(getLocaleForCallUseCase)
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/test")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testLocaleEnglish() = testApplication {
        val client = installApp(this)
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = createRouter(getLocaleForCallUseCase)
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/en/test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("test:locale:en", response.body())
    }

}
