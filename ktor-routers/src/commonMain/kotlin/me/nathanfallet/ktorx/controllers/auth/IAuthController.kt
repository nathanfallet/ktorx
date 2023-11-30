package me.nathanfallet.ktorx.controllers.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.auth.ILoginPayload
import me.nathanfallet.ktorx.models.auth.IRegisterPayload

interface IAuthController : IUnitController {

    suspend fun login(call: ApplicationCall, payload: ILoginPayload)
    suspend fun register(call: ApplicationCall, payload: IRegisterPayload)

}
