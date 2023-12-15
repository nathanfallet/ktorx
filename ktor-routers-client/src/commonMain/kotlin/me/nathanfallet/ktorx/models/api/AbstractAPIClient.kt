package me.nathanfallet.ktorx.models.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.nathanfallet.ktorx.models.exceptions.APIException
import me.nathanfallet.ktorx.usecases.api.IGetTokenUseCase

abstract class AbstractAPIClient(
    private val baseUrl: String,
    private val getTokenUseCase: IGetTokenUseCase? = null,
    json: Json? = null,
    engine: HttpClientEngine? = null,
) : IAPIClient {

    private val httpClient = run {
        val block: HttpClientConfig<*>.() -> Unit = {
            expectSuccess = true
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    val clientException = exception as? ClientRequestException
                        ?: return@handleResponseExceptionWithRequest
                    val error = clientException.response.body<Map<String, String>>()["error"]
                        ?: return@handleResponseExceptionWithRequest
                    throw APIException(clientException.response.status, error)
                }
            }
            install(ContentNegotiation) {
                json(json ?: Json)
            }
        }
        engine?.let { HttpClient(it, block) } ?: HttpClient(block)
    }

    override suspend fun request(
        method: HttpMethod,
        url: String,
        builder: HttpRequestBuilder.() -> Unit,
    ): HttpResponse {
        return httpClient.request(baseUrl + url) {
            this.method = method
            getTokenUseCase?.invoke()?.let { token ->
                header("Authorization", "Bearer $token")
            }
            builder()
        }
    }

}
