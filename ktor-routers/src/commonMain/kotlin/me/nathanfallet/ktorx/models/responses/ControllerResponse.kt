package me.nathanfallet.ktorx.models.responses

import io.ktor.server.application.*

interface ControllerResponse {

    suspend fun respond(call: ApplicationCall)

}
