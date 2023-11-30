package me.nathanfallet.ktorx.controllers.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.ILoginPayload
import me.nathanfallet.ktorx.models.auth.IRegisterPayload

interface IAuthController {

    suspend fun login(call: ApplicationCall, payload: ILoginPayload)
    suspend fun register(call: ApplicationCall, payload: IRegisterPayload)

}
