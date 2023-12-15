package me.nathanfallet.ktorx.models.exceptions

import io.ktor.http.*

data class APIException(
    val code: HttpStatusCode,
    val key: String,
) : Exception()
