package me.nathanfallet.ktorx.routers.auth

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
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.models.auth.*
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthTemplateRouterTest {

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
        controller: IAuthController<TestLoginPayload, TestRegisterPayload>,
    ) = AuthTemplateRouter(
        TestLoginPayload::class,
        TestRegisterPayload::class,
        AuthMapping(
            loginTemplate = "login",
            registerTemplate = "register",
            authorizeTemplate = "authorize",
            redirectUnauthorizedToUrl = "/auth/login?redirect={path}"
        ),
        { template, model ->
            respond(
                AuthTemplateResponse(
                    template,
                    model["error"] as? String,
                    null,
                    (model["client"] as? TestClient)?.id,
                    (model["user"] as? TestUser)?.id
                )
            )
        },
        controller
    )

    @Test
    fun testGetLoginRoute() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/auth/login")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            AuthTemplateResponse(
                "login"
            ), response.body()
        )
    }

    @Test
    fun testPostLoginRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val router = createRouter(controller)
        val loginPayload = TestLoginPayload("email", "password")
        coEvery { controller.login(any(), loginPayload) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "email",
                    "password" to "password"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, response.status)
        coVerify { controller.login(any(), loginPayload) }
    }

    @Test
    fun testPostLoginRouteInvalidCredentials() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val router = createRouter(controller)
        val loginPayload = TestLoginPayload("email", "password")
        coEvery { controller.login(any(), loginPayload) } throws ControllerException(
            HttpStatusCode.Unauthorized,
            "auth_invalid_credentials"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/login") {
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
                error = "auth_invalid_credentials"
            ), response.body()
        )
    }

    @Test
    fun testPostLoginRouteInvalidBody() = testApplication {
        val client = installApp(this)
        val router = createRouter(mockk())
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "email",
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            AuthTemplateResponse(
                "login",
                error = "error_body_invalid"
            ), response.body()
        )
    }

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
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val router = createRouter(controller)
        val registerPayload = TestRegisterPayload("email", "password")
        coEvery { controller.register(any(), registerPayload) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "email" to "email",
                    "password" to "password"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Found, response.status)
        coVerify { controller.register(any(), registerPayload) }
    }

    @Test
    fun testPostRegisterRouteInvalidBody() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val router = createRouter(controller)
        val registerPayload = TestRegisterPayload("email", "password")
        coEvery { controller.register(any(), registerPayload) } returns Unit
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/register") {
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

    @Test
    fun testGetAuthorizeCodeRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val router = createRouter(controller)
        val clientForUser = ClientForUser(TestClient("cid"), TestUser("uid"))
        coEvery { controller.authorize(any(), "cid") } returns clientForUser
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/auth/authorize?client_id=cid")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            AuthTemplateResponse(
                "authorize",
                client = (clientForUser.client as TestClient).id,
                user = (clientForUser.user as TestUser).id
            ), response.body()
        )
    }

    @Test
    fun testGetAuthorizeCodeRouteNeedsLogin() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val router = createRouter(controller)
        coEvery { controller.authorize(any(), "cid") } throws ControllerException(
            HttpStatusCode.Unauthorized,
            "auth_invalid_credentials"
        )
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/auth/authorize?client_id=cid")
        assertEquals(HttpStatusCode.Found, response.status)
    }

    @Test
    fun testPostAuthorizeRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<TestLoginPayload, TestRegisterPayload>>()
        val router = createRouter(controller)
        val clientForUser = ClientForUser(TestClient("cid"), TestUser("uid"))
        coEvery { controller.authorize(any(), "cid") } returns clientForUser
        coEvery { controller.authorize(any(), clientForUser) } returns "url"
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/auth/authorize?client_id=cid")
        assertEquals(HttpStatusCode.Found, response.status)
        coVerify { controller.authorize(any(), clientForUser) }
    }

}
