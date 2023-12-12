package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.AuthRequest
import me.nathanfallet.ktorx.models.auth.AuthToken
import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.ktorx.models.auth.IClient
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.ICreateSessionForUserUseCase
import me.nathanfallet.ktorx.usecases.auth.ILoginUseCase
import me.nathanfallet.ktorx.usecases.auth.IRegisterUseCase
import me.nathanfallet.ktorx.usecases.auth.ISetSessionForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase

open class AuthController<LoginPayload, RegisterPayload>(
    private val loginUseCase: ILoginUseCase<LoginPayload>,
    private val registerUseCase: IRegisterUseCase<RegisterPayload>,
    private val createSessionForUserUseCase: ICreateSessionForUserUseCase,
    private val setSessionForCallUseCase: ISetSessionForCallUseCase,
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
) : IAuthController<LoginPayload, RegisterPayload> {

    override suspend fun login(call: ApplicationCall, payload: LoginPayload) {
        val user = loginUseCase(payload) ?: throw ControllerException(
            HttpStatusCode.Unauthorized, "auth_invalid_credentials"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
    }

    override suspend fun register(call: ApplicationCall, payload: RegisterPayload) {
        val user = registerUseCase(call, payload) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
    }

    override suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser {
        val user = requireUserForCallUseCase(call)
        val client = object : IClient {}
        return ClientForUser(client, user)
    }

    override suspend fun authorize(call: ApplicationCall, client: ClientForUser): String {
        // TODO: Gen auth code and return redirect URL
        return ""
    }

    override suspend fun token(call: ApplicationCall, request: AuthRequest): AuthToken {
        // TODO: Check client/code and gen token
        return AuthToken("", "")
    }

}
