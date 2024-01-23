package me.nathanfallet.ktorx.models.routes

import io.ktor.http.*
import kotlin.reflect.KFunction

data class ControllerRoute(
    val type: RouteType,
    val path: String?,
    val method: HttpMethod?,
    val function: KFunction<*>,
)
