package me.nathanfallet.ktorx.routers.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthAPIRouterTest {

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
        controller: IAuthController<*, *>,
    ) = AuthAPIRouter(controller, IAuthController::class)

    @Test
    fun testPostTokenRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<*, *>>()
        val router = createRouter(controller)
        val authRequest = AuthRequest("cid", "secret", "code")
        val authResponse = AuthToken("token", "refresh")
        coEvery { controller.token(any(), authRequest) } returns authResponse
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/api/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(authResponse, response.body())
    }

    @Test
    fun testPostTokenRouteInvalidBody() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<*, *>>()
        val router = createRouter(controller)
        routing {
            router.createRoutes(this)
        }
        val response = client.post("/api/auth/token") {
            setBody("invalid")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(mapOf("error" to "error_body_invalid"), response.body())
    }

    @Test
    fun testAPIPostRouteOpenAPI() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController<*, *>>()
        val router = createRouter(controller)
        val openAPI = OpenAPI()
        val authRequest = AuthRequest("cid", "secret", "code")
        val authResponse = AuthToken("token", "refresh")
        coEvery { controller.token(any(), authRequest) } returns authResponse
        routing {
            router.createRoutes(this, openAPI)
        }
        client.post("/api/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }
        val post = openAPI.paths["/api/auth/token"]?.post
        assertEquals("createToken", post?.operationId)
        assertEquals(listOf("Auth"), post?.tags)
        assertEquals("Create a token", post?.description)
        assertEquals(
            "#/components/schemas/${AuthRequest::class.qualifiedName}",
            post?.requestBody?.content?.get("application/json")?.schema?.`$ref`
        )
        assertEquals(2, post?.responses?.size)
        assertEquals(
            "#/components/schemas/${AuthToken::class.qualifiedName}",
            post?.responses?.get("201")?.content?.get("application/json")?.schema?.`$ref`
        )
        assertEquals(
            Schema<String>().type("string").example("error_body_invalid"),
            post?.responses?.get("400")?.content?.get("application/json")?.schema?.properties?.get("error")
        )
    }

}
