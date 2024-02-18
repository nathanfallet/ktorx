package me.nathanfallet.ktorx.models.responses

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.io.OutputStream

data class StreamResponse(
    val stream: suspend OutputStream.() -> Unit,
    val contentType: ContentType = ContentType.Application.OctetStream,
    val httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
) : ControllerResponse {

    override suspend fun respond(call: ApplicationCall) =
        call.respondOutputStream(contentType, httpStatusCode, stream)

}
