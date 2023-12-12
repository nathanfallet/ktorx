package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.users.IUser

open class AuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>(
    loginUseCase: ILoginUseCase<LoginPayload>,
    private val registerUseCase: IRegisterUseCase<RegisterCodePayload>,
    private val createSessionForUserUseCase: ICreateSessionForUserUseCase,
    private val setSessionForCallUseCase: ISetSessionForCallUseCase,
    private val createCodeRegisterUseCase: ICreateCodeRegisterUseCase<RegisterPayload>,
    private val getCodeRegisterUseCase: IGetCodeRegisterUseCase<RegisterPayload>,
    private val deleteCodeRegisterUseCase: IDeleteCodeRegisterUseCase,
    requireUserForCallUseCase: IRequireUserForCallUseCase,
    getClientUseCase: IGetClientUseCase,
    getAuthCodeUseCase: IGetAuthCodeUseCase,
    createAuthCodeUseCase: ICreateAuthCodeUseCase,
    generateAuthTokenUseCase: IGenerateAuthTokenUseCase,
) : AuthController<LoginPayload, RegisterPayload>(
    loginUseCase,
    object : IRegisterUseCase<RegisterPayload> {
        override suspend fun invoke(input1: ApplicationCall, input2: RegisterPayload): IUser? {
            return null
        }
    },
    createSessionForUserUseCase,
    setSessionForCallUseCase,
    requireUserForCallUseCase,
    getClientUseCase,
    getAuthCodeUseCase,
    createAuthCodeUseCase,
    generateAuthTokenUseCase,
), IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload> {

    override suspend fun register(call: ApplicationCall, payload: RegisterPayload) {
        createCodeRegisterUseCase(call, payload) ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_register_email_taken"
        )
    }

    override suspend fun register(call: ApplicationCall, code: String): RegisterPayload {
        return getCodeRegisterUseCase(call, code) ?: throw ControllerException(
            HttpStatusCode.NotFound, "auth_code_invalid"
        )
    }

    override suspend fun register(call: ApplicationCall, code: String, payload: RegisterCodePayload) {
        val user = registerUseCase(call, payload) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        setSessionForCallUseCase(call, createSessionForUserUseCase(user))
        deleteCodeRegisterUseCase(call, code)
    }

}
