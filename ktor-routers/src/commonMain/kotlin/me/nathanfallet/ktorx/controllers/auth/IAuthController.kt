package me.nathanfallet.ktorx.controllers.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IUnitController

interface IAuthController<LoginPayload, RegisterPayload> : IUnitController {

    suspend fun login(call: ApplicationCall, payload: LoginPayload)
    suspend fun register(call: ApplicationCall, payload: RegisterPayload)

}
