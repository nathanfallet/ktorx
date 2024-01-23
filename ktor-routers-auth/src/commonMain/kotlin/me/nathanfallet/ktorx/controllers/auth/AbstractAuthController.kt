package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.annotations.Payload
import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken

abstract class AbstractAuthController<LoginPayload, RegisterPayload>(
    private val loginUseCase: ILoginUseCase<LoginPayload>,
    private val registerUseCase: IRegisterUseCase<RegisterPayload>,
    private val createSessionForUserUseCase: ICreateSessionForUserUseCase,
    private val setSessionForCallUseCase: ISetSessionForCallUseCase,
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val getClientUseCase: IGetClientUseCase,
    private val getAuthCodeUseCase: IGetAuthCodeUseCase,
    private val createAuthCodeUseCase: ICreateAuthCodeUseCase,
    private val deleteAuthCodeUseCase: IDeleteAuthCodeUseCase,
    private val generateAuthTokenUseCase: IGenerateAuthTokenUseCase,
) : IAuthController<LoginPayload, RegisterPayload> {

    open suspend fun login(call: ApplicationCall, @Payload payload: LoginPayload) {
        val user = loginUseCase(payload) ?: throw ControllerException(
            HttpStatusCode.Unauthorized, "auth_invalid_credentials"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
    }

    open suspend fun register(call: ApplicationCall, @Payload payload: RegisterPayload) {
        val user = registerUseCase(call, payload) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
    }

    open suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser {
        val user = requireUserForCallUseCase(call)
        val client = clientId?.let { getClientUseCase(it) } ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_invalid_client"
        )
        return ClientForUser(client, user)
    }

    open suspend fun authorize(call: ApplicationCall, client: ClientForUser): String {
        val code = createAuthCodeUseCase(client) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        return client.client.redirectUri.replace("{code}", code)
    }

    open suspend fun token(call: ApplicationCall, @Payload request: AuthRequest): AuthToken {
        val client = getAuthCodeUseCase(request.code)?.takeIf {
            it.client.clientId == request.clientId && it.client.clientSecret == request.clientSecret
        } ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_invalid_code"
        )
        if (!deleteAuthCodeUseCase(request.code)) throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        return generateAuthTokenUseCase(client)
    }

}
