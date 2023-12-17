package me.nathanfallet.ktorx.models.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import me.nathanfallet.ktorx.usecases.api.IGetTokenUseCase

interface IAPIClient {

    val baseUrl: String
    val getTokenUseCase: IGetTokenUseCase?

    suspend fun request(
        method: HttpMethod,
        path: String,
        builder: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse

}
