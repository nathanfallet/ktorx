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
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.models.auth.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthWithCodeTemplateRouterTest {

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

    private fun createRouter(
        controller: IAuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>,
    ) = AuthWithCodeTemplateRouter(
        typeInfo<TestLoginPayload>(),
        typeInfo<TestCodePayload>(),
        typeInfo<TestRegisterPayload>(),
        controller,
        TestAuthWithCodeController::class,
        { template, model ->
            respond(
                AuthTemplateResponse(
                    template,
                    model["error"] as? String,
                    model["codePayload"] as? TestCodePayload
                )
            )
        },
        redirectUnauthorizedToUrl = "/auth/login?redirect={path}"
    )

    @Test
    fun testGetRegisterRoute() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/auth/register")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            AuthTemplateResponse(
                "register"
            ), response.body()
        )
    }

    @Test
    fun testPostRegisterRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<TestAuthWithCodeController>()
        val router = createRouter(controller)
        val registerPayload = TestCodePayload("code")
        coEvery { controller.register(any(), registerPayload) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "code" to "code"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        coVerify { controller.register(any(), registerPayload) }
    }

    @Test
    fun testPostRegisterRouteInvalidBody() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "other" to "code"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            AuthTemplateResponse(
                "register",
                error = "error_body_invalid"
            ), response.body()
        )
    }

    @Test
    fun testGetRegisterCodeRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<TestAuthWithCodeController>()
        val router = createRouter(controller)
        val codePayload = TestCodePayload("code")
        coEvery { controller.register(any(), "code") } returns codePayload
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/auth/register/code")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            AuthTemplateResponse(
                "register",
                code = codePayload
            ), response.body()
        )
    }

    @Test
    fun testPostRegisterCodeRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<TestAuthWithCodeController>()
        val router = createRouter(controller)
        val codePayload = TestCodePayload("code")
        val registerPayload = TestRegisterPayload("email", "password")
        coEvery { controller.register(any(), "code") } returns codePayload
        coEvery { controller.register(any(), "code", registerPayload) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/register/code") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "email",
                    "password" to "password"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, response.status)
        coVerify { controller.register(any(), "code", registerPayload) }
    }

    @Test
    fun testPostRegisterCodeRouteInvalidBody() = testApplication {
        val client = installApp(this)
        val controller = mockk<TestAuthWithCodeController>()
        val router = createRouter(controller)
        val codePayload = TestCodePayload("code")
        val registerPayload = TestRegisterPayload("email", "password")
        coEvery { controller.register(any(), "code") } returns codePayload
        coEvery { controller.register(any(), "code", registerPayload) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/register/code") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "email"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            AuthTemplateResponse(
                "register",
                error = "error_body_invalid"
            ), response.body()
        )
    }

}
