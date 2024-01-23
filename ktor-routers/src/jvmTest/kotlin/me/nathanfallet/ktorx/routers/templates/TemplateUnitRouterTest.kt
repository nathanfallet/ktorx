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
import io.swagger.v3.oas.models.OpenAPI
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.controllers.base.UnitController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateUnitRouterTest {

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

    @Test
    fun testUnitPage() = testApplication {
        val client = installApp(this)
        val router = object : TemplateUnitRouter(TemplateMapping(""), { template, _ ->
            respond(template)
        }, UnitController, IUnitController::class) {
            override fun createRoutes(root: Route, openAPI: OpenAPI?) {
                root.get("/test") {
                    call.respondTemplate("test", mapOf())
                }
            }
        }
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("test", response.body())
    }

}
