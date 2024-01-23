package me.nathanfallet.ktorx.controllers.auth

import io.ktor.server.application.*

interface IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload> :
    IAuthController<LoginPayload, RegisterPayload> {

    suspend fun register(call: ApplicationCall, code: String): RegisterPayload
    suspend fun register(call: ApplicationCall, code: String, payload: RegisterCodePayload)

}
