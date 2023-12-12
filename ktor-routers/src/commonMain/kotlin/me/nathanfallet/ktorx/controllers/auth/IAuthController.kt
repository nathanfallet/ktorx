package me.nathanfallet.ktorx.controllers.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.auth.AuthRequest
import me.nathanfallet.ktorx.models.auth.AuthToken
import me.nathanfallet.ktorx.models.auth.ClientForUser

interface IAuthController<LoginPayload, RegisterPayload> : IUnitController {

    suspend fun login(call: ApplicationCall, payload: LoginPayload)
    suspend fun register(call: ApplicationCall, payload: RegisterPayload)

    suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser
    suspend fun authorize(call: ApplicationCall, client: ClientForUser): String

    suspend fun token(call: ApplicationCall, request: AuthRequest): AuthToken

}
