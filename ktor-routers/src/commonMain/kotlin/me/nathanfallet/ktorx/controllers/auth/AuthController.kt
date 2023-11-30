package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.ILoginPayload
import me.nathanfallet.ktorx.models.auth.IRegisterPayload
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.ICreateSessionForUserUseCase
import me.nathanfallet.ktorx.usecases.auth.ILoginUseCase
import me.nathanfallet.ktorx.usecases.auth.IRegisterUseCase
import me.nathanfallet.ktorx.usecases.auth.ISetSessionForCallUseCase

open class AuthController(
    private val loginUseCase: ILoginUseCase,
    private val registerUseCase: IRegisterUseCase,
    private val createSessionForUserUseCase: ICreateSessionForUserUseCase,
    private val setSessionForCallUseCase: ISetSessionForCallUseCase,
) : IAuthController {

    override suspend fun login(call: ApplicationCall, payload: ILoginPayload) {
        val user = loginUseCase(payload) ?: throw ControllerException(
            HttpStatusCode.Unauthorized, "auth_invalid_credentials"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
    }

    override suspend fun register(call: ApplicationCall, payload: IRegisterPayload) {
        val user = registerUseCase(call, payload) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
    }

}
