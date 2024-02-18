package me.nathanfallet.ktorx.models.responses

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

data class BytesResponse(
    val bytes: ByteArray,
    val contentType: ContentType = ContentType.Application.OctetStream,
    val httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
) : ControllerResponse {

    override suspend fun respond(call: ApplicationCall) =
        call.respondBytes(bytes, contentType, httpStatusCode)

}
