package me.nathanfallet.ktorx.repositories.auth

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.api.AbstractAPIClient
import me.nathanfallet.usecases.auth.AuthRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AuthAPIRemoteRepositoryTest {

    private fun createRepository(
        engine: HttpClientEngine,
    ): AuthAPIRemoteRepository {
        val client = object : AbstractAPIClient(
            "https://example.com",
            engine = engine
        ) {}
        return AuthAPIRemoteRepository(client)
    }

    @Test
    fun testToken() = runBlocking {
        val repository = createRepository(MockEngine { request ->
            assertEquals("https://example.com/api/auth/token", request.url.toString())
            assertEquals(HttpMethod.Post, request.method)
            assertEquals(ContentType.Application.Json, request.body.contentType)
            assertEquals(
                """{"clientId":"cid","clientSecret":"secret","code":"code"}""",
                (request.body as TextContent).text
            )
            respond(
                content = """{"accessToken":"access"}""",
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        })
        val token = repository.token(AuthRequest("cid", "secret", "code")) ?: fail("Model is null")
        assertEquals("access", token.accessToken)
    }

}
