package me.nathanfallet.ktorx.models

import io.ktor.server.websocket.*
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.annotations.Path
import me.nathanfallet.ktorx.models.annotations.WebSocketMapping

interface ITestUnitController : IUnitController {

    @WebSocketMapping
    @Path("GET", "/hello")
    suspend fun hello(session: DefaultWebSocketServerSession)

}
