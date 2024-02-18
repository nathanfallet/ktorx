package me.nathanfallet.ktorx.models.responses

import io.ktor.http.*

data class StatusResponse<T : Any>(
    val status: HttpStatusCode,
    val content: T,
)
