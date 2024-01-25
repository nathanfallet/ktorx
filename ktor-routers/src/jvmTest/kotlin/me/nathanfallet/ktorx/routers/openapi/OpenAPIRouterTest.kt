package me.nathanfallet.ktorx.routers.openapi

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.extensions.info
import me.nathanfallet.ktorx.extensions.schema
import me.nathanfallet.ktorx.models.TestModel
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenAPIRouterTest {

    @Test
    fun testOpenAPIRouter() = testApplication {
        val openAPI = OpenAPI().info {
            title = "Test"
            description = "Test description"
            version = "1.0.0"
        }
        openAPI.schema(typeOf<TestModel>())
        val router = OpenAPIRouter()
        routing {
            router.createRoutes(this, openAPI)
        }
        val response = client.get("/docs")
        assertEquals(HttpStatusCode.OK, response.status)
    }

}
