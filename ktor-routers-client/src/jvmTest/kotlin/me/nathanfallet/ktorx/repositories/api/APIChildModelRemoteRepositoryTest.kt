package me.nathanfallet.ktorx.repositories.api

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.TestChildModel
import me.nathanfallet.ktorx.models.TestCreatePayload
import me.nathanfallet.ktorx.models.TestModel
import me.nathanfallet.ktorx.models.TestUpdatePayload
import me.nathanfallet.ktorx.models.api.AbstractAPIClient
import me.nathanfallet.usecases.models.id.RecursiveId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class APIChildModelRemoteRepositoryTest {

    private fun createRepository(
        engine: HttpClientEngine,
    ): APIChildModelRemoteRepository<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, Long> {
        val client = object : AbstractAPIClient(
            "https://example.com",
            engine = engine
        ) {}
        return APIChildModelRemoteRepository(
            typeInfo<TestChildModel>(),
            typeInfo<TestCreatePayload>(),
            typeInfo<TestUpdatePayload>(),
            typeInfo<List<TestChildModel>>(),
            client,
            APIModelRemoteRepository<TestModel, Long, TestCreatePayload, TestUpdatePayload>(
                typeInfo<TestModel>(),
                typeInfo<TestCreatePayload>(),
                typeInfo<TestUpdatePayload>(),
                typeInfo<List<TestModel>>(),
                client
            )
        )
    }

    @Test
    fun testGet() = runBlocking {
        val repository = createRepository(MockEngine { request ->
            assertEquals("https://example.com/api/testmodels/1/testchildmodels/2", request.url.toString())
            respond(
                content = """{"id":2,"parentId":1,"string":"string"}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        })
        val model = repository.get(2, RecursiveId<TestModel, Long, Unit>(1)) ?: fail("Model is null")
        assertEquals(2, model.id)
        assertEquals(1, model.parentId)
        assertEquals("string", model.string)
    }

}
