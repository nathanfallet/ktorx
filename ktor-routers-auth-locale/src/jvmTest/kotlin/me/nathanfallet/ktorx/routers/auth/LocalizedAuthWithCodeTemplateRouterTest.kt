package me.nathanfallet.ktorx.routers.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.models.auth.*
import me.nathanfallet.ktorx.plugins.I18n
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedAuthWithCodeTemplateRouterTest {

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

    private fun createRouter(
        getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    ) = LocalizedAuthWithCodeTemplateRouter<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
        typeInfo<TestLoginPayload>(),
        typeInfo<TestCodePayload>(),
        typeInfo<TestRegisterPayload>(),
        mockk(),
        TestAuthWithCodeController::class,
        { template, model ->
            respond(
                AuthTemplateResponse(
                    template,
                    (model["locale"] as Locale).language,
                    model["error"] as? String
                )
            )
        },
        getLocaleForCallUseCase
    )

    @Test
    fun testRedirect() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/auth/login")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testGetLoginRoute() = testApplication {
        val client = installApp(this)
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = createRouter(getLocaleForCallUseCase)
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/en/auth/login")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            AuthTemplateResponse(
                "login",
                "en"
            ), response.body()
        )
    }

}
