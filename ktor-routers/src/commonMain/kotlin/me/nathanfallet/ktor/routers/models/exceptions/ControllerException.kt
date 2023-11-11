package me.nathanfallet.ktor.routers.models.exceptions

import io.ktor.http.*

data class ControllerException(
    val code: HttpStatusCode,
    val key: String
) : Exception()