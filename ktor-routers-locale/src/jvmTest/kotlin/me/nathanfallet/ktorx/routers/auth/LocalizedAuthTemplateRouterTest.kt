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
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.models.auth.AuthTemplateResponse
import me.nathanfallet.ktorx.models.auth.TestLoginPayload
import me.nathanfallet.ktorx.models.auth.TestRegisterPayload
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.plugins.I18n
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedAuthTemplateRouterTest {

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
        controller: IAuthController<TestLoginPayload, TestRegisterPayload>,
        getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    ) = LocalizedAuthTemplateRouter(
        typeInfo<TestLoginPayload>(),
        typeInfo<TestRegisterPayload>(),
        AuthMapping(
            loginTemplate = "login",
            registerTemplate = "register",
        ),
        { template, model ->
            respond(
                AuthTemplateResponse(
                    template,
                    (model["locale"] as Locale).language,
                    model["error"] as? String
                )
            )
        },
        null,
        "/auth/login?redirect={path}",
        controller,
        IAuthController::class,
        getLocaleForCallUseCase
    )

    @Test
    fun testRedirect() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk(), mockk())
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
        val router = createRouter(mockk(), getLocaleForCallUseCase)
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

    @Test
    fun testPostLoginRouteInvalidCredentials() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = createRouter(controller, getLocaleForCallUseCase)
        val loginPayload = TestLoginPayload("email", "password")
        coEvery { controller.login(any(), loginPayload) } throws ControllerException(
            HttpStatusCode.Unauthorized,
            "auth_invalid_credentials"
        )
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/en/auth/login") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "email",
                    "password" to "password"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(
            AuthTemplateResponse(
                "login",
                "en",
                error = "auth_invalid_credentials"
            ), response.body()
        )
    }

}
