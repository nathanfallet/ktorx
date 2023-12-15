package me.nathanfallet.ktorx.models.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

interface IAPIClient {

    suspend fun request(
        method: HttpMethod,
        url: String,
        builder: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse

}
