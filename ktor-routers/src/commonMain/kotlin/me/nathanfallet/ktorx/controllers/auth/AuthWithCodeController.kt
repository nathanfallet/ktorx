package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.IRegisterPayload
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.*

open class AuthWithCodeController(
    loginUseCase: ILoginUseCase,
    private val registerUseCase: IRegisterUseCase,
    private val createSessionForUserUseCase: ICreateSessionForUserUseCase,
    private val setSessionForCallUseCase: ISetSessionForCallUseCase,
    private val createCodeRegisterUseCase: ICreateCodeRegisterUseCase,
    private val getCodeRegisterUseCase: IGetCodeRegisterUseCase,
    private val deleteCodeRegisterUseCase: IDeleteCodeRegisterUseCase
) : AuthController(
    loginUseCase,
    registerUseCase,
    createSessionForUserUseCase,
    setSessionForCallUseCase
), IAuthWithCodeController {

    override suspend fun register(call: ApplicationCall, payload: IRegisterPayload) {
        createCodeRegisterUseCase(call, payload) ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_register_email_taken"
        )
    }

    override suspend fun register(call: ApplicationCall, code: String): IRegisterPayload {
        return getCodeRegisterUseCase(call, code) ?: throw ControllerException(
            HttpStatusCode.NotFound, "auth_code_invalid"
        )
    }

    override suspend fun register(call: ApplicationCall, code: String, payload: IRegisterPayload) {
        val user = registerUseCase(call, payload) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
        deleteCodeRegisterUseCase(call, code)
    }

}
