package me.nathanfallet.ktorx.models.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.auth.AbstractAuthWithCodeController
import me.nathanfallet.ktorx.models.annotations.RegisterCodePath
import me.nathanfallet.ktorx.models.annotations.RegisterPath
import me.nathanfallet.ktorx.models.annotations.TemplateMapping
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase

class TestAuthWithCodeController(
    loginUseCase: ILoginUseCase<TestLoginPayload>,
    registerUseCase: IRegisterUseCase<TestRegisterPayload>,
    createSessionForUserUseCase: ICreateSessionForUserUseCase,
    setSessionForCallUseCase: ISetSessionForCallUseCase,
    createCodeRegisterUseCase: ICreateCodeRegisterUseCase<TestCodePayload>,
    getCodeRegisterUseCase: IGetCodeRegisterUseCase<TestCodePayload>,
    deleteCodeRegisterUseCase: IDeleteCodeRegisterUseCase,
    requireUserForCallUseCase: IRequireUserForCallUseCase,
    getClientUseCase: IGetClientUseCase,
    getAuthCodeUseCase: IGetAuthCodeUseCase,
    createAuthCodeUseCase: ICreateAuthCodeUseCase,
    deleteAuthCodeUseCase: IDeleteAuthCodeUseCase,
    generateAuthTokenUseCase: IGenerateAuthTokenUseCase,
) : AbstractAuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
    loginUseCase,
    registerUseCase,
    createSessionForUserUseCase,
    setSessionForCallUseCase,
    createCodeRegisterUseCase,
    getCodeRegisterUseCase,
    deleteCodeRegisterUseCase,
    requireUserForCallUseCase,
    getClientUseCase,
    getAuthCodeUseCase,
    createAuthCodeUseCase,
    deleteAuthCodeUseCase,
    generateAuthTokenUseCase,
) {

    @TemplateMapping("register")
    @RegisterPath
    override suspend fun register(call: ApplicationCall, payload: TestCodePayload) {
        super.register(call, payload)
    }

    @TemplateMapping("register")
    @RegisterCodePath
    override suspend fun register(call: ApplicationCall, code: String): TestCodePayload {
        return super.register(call, code)
    }

}
