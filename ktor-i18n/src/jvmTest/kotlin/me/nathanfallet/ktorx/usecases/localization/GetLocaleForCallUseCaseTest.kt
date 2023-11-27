package me.nathanfallet.ktorx.usecases.localization

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import me.nathanfallet.ktorx.plugins.I18n
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GetLocaleForCallUseCaseTest {

    private fun installApp(application: ApplicationTestBuilder, useOfCookie: Boolean = false): HttpClient {
        application.install(I18n) {
            this.supportedLocales = listOf("en", "fr").map(Locale::forLanguageTag)
            this.useOfCookie = useOfCookie
        }
        return application.createClient {
            followRedirects = false
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(kotlinx.serialization.json.Json)
            }
            install(HttpCookies)
        }
    }

    @Test
    fun testLocaleDefault() = testApplication {
        val client = installApp(this)
        routing {
            get {
                call.respond(GetLocaleForCallUseCase()(call).language)
            }
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("en", response.body())
    }

    @Test
    fun testLocaleAcceptHeader() = testApplication {
        val client = installApp(this)
        routing {
            get {
                call.respond(GetLocaleForCallUseCase()(call).language)
            }
        }
        val response = client.get("/") {
            header(HttpHeaders.AcceptLanguage, "fr")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("fr", response.body())
    }

    @Test
    fun testLocaleCookie() = testApplication {
        val client = installApp(this, useOfCookie = true)
        routing {
            get {
                call.respond(GetLocaleForCallUseCase()(call).language)
            }
        }
        val response = client.get("/") {
            cookie("locale", "fr")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("fr", response.body())
    }

    @Test
    fun testLocaleCookieDisabled() = testApplication {
        val client = installApp(this, useOfCookie = false)
        routing {
            get {
                call.respond(GetLocaleForCallUseCase()(call).language)
            }
        }
        val response = client.get("/") {
            cookie("locale", "fr")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("en", response.body())
    }

    @Test
    fun testLocaleCookieWritten() = testApplication {
        val client = installApp(this, useOfCookie = true)
        routing {
            get {
                call.respond(GetLocaleForCallUseCase()(call).language)
            }
        }
        val response = client.get("/") {
            header(HttpHeaders.AcceptLanguage, "fr")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("fr", response.body())
        val cookie = client.cookies("localhost").first()
        assertEquals("locale", cookie.name)
        assertEquals("fr", cookie.value)
    }

}
