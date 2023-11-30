package me.nathanfallet.ktorx.controllers.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.IRegisterPayload

interface IAuthWithCodeController : IAuthController {

    suspend fun register(call: ApplicationCall, code: String): IRegisterPayload
    suspend fun register(call: ApplicationCall, code: String, payload: IRegisterPayload)

}
