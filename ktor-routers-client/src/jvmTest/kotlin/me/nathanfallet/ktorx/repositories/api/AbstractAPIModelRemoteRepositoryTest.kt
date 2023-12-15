package me.nathanfallet.ktorx.repositories.api

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.TestCreatePayload
import me.nathanfallet.ktorx.models.TestModel
import me.nathanfallet.ktorx.models.TestUpdatePayload
import me.nathanfallet.ktorx.models.api.AbstractAPIClient
import me.nathanfallet.ktorx.models.exceptions.APIException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

class AbstractAPIModelRemoteRepositoryTest {

    private fun createRepository(
        engine: HttpClientEngine,
    ): AbstractAPIModelRemoteRepository<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
        val client = object : AbstractAPIClient(
            "https://example.com",
            engine = engine
        ) {}
        return object : AbstractAPIModelRemoteRepository<TestModel, Long, TestCreatePayload, TestUpdatePayload>(
            typeInfo<TestModel>(),
            typeInfo<TestCreatePayload>(),
            typeInfo<TestUpdatePayload>(),
            client
        ) {}
    }

    @Test
    fun testGet() = runBlocking {
        val repository = createRepository(MockEngine { request ->
            assertEquals("https://example.com/api/testmodels/1", request.url.toString())
            respond(
                content = """{"id":1,"string":"string"}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        })
        val model = repository.get(1) ?: fail("Model is null")
        assertEquals(1, model.id)
        assertEquals("string", model.string)
    }

    @Test
    fun testGetAPIError() = runBlocking {
        val repository = createRepository(MockEngine { request ->
            assertEquals("https://example.com/api/testmodels/1", request.url.toString())
            respond(
                content = """{"error": "not_found"}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        })
        val exception = assertFailsWith<APIException> {
            repository.get(1)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("not_found", exception.key)
    }

    @Test
    fun testCreate() = runBlocking {
        val repository = createRepository(MockEngine { request ->
            assertEquals("https://example.com/api/testmodels", request.url.toString())
            assertEquals(HttpMethod.Post, request.method)
            assertEquals(ContentType.Application.Json, request.body.contentType)
            assertEquals("""{"string":"string"}""", (request.body as TextContent).text)
            respond(
                content = """{"id":1,"string":"string"}""",
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        })
        val model = repository.create(TestCreatePayload("string")) ?: fail("Model is null")
        assertEquals(1, model.id)
        assertEquals("string", model.string)
    }

    @Test
    fun testUpdate() = runBlocking {
        val repository = createRepository(MockEngine { request ->
            assertEquals("https://example.com/api/testmodels/1", request.url.toString())
            assertEquals(HttpMethod.Put, request.method)
            assertEquals(ContentType.Application.Json, request.body.contentType)
            assertEquals("""{"string":"string"}""", (request.body as TextContent).text)
            respond(
                content = """{"id":1,"string":"string"}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        })
        val model = repository.update(1, TestUpdatePayload("string")) ?: fail("Model is null")
        assertEquals(1, model.id)
        assertEquals("string", model.string)
    }

    @Test
    fun testDelete() = runBlocking {
        val repository = createRepository(MockEngine { request ->
            assertEquals("https://example.com/api/testmodels/1", request.url.toString())
            assertEquals(HttpMethod.Delete, request.method)
            respond(
                content = "",
                status = HttpStatusCode.NoContent,
            )
        })
        assertEquals(true, repository.delete(1))
    }

}
