package me.nathanfallet.ktorx.models.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.auth.AbstractAuthController
import me.nathanfallet.ktorx.models.annotations.*
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken

class TestAuthController(
    loginUseCase: ILoginUseCase<TestLoginPayload>,
    registerUseCase: IRegisterUseCase<TestRegisterPayload>,
    createSessionForUserUseCase: ICreateSessionForUserUseCase,
    setSessionForCallUseCase: ISetSessionForCallUseCase,
    requireUserForCallUseCase: IRequireUserForCallUseCase,
    getClientUseCase: IGetClientUseCase,
    getAuthCodeUseCase: IGetAuthCodeUseCase,
    createAuthCodeUseCase: ICreateAuthCodeUseCase,
    deleteAuthCodeUseCase: IDeleteAuthCodeUseCase,
    generateAuthTokenUseCase: IGenerateAuthTokenUseCase,
) : AbstractAuthController<TestLoginPayload, TestRegisterPayload>(
    loginUseCase,
    registerUseCase,
    createSessionForUserUseCase,
    setSessionForCallUseCase,
    requireUserForCallUseCase,
    getClientUseCase,
    getAuthCodeUseCase,
    createAuthCodeUseCase,
    deleteAuthCodeUseCase,
    generateAuthTokenUseCase,
) {

    @TemplateMapping("login")
    @LoginPath
    override suspend fun login(call: ApplicationCall, @Payload payload: TestLoginPayload) {
        super.login(call, payload)
    }

    @TemplateMapping("register")
    @RegisterPath
    override suspend fun register(call: ApplicationCall, @Payload payload: TestRegisterPayload) {
        super.register(call, payload)
    }

    @TemplateMapping("authorize")
    @AuthorizePath
    override suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser {
        return super.authorize(call, clientId)
    }

    @TemplateMapping("authorize")
    @AuthorizeRedirectPath
    override suspend fun authorize(call: ApplicationCall, client: ClientForUser): String {
        return super.authorize(call, client)
    }

    @APIMapping
    @TokenPath
    override suspend fun token(call: ApplicationCall, @Payload request: AuthRequest): AuthToken {
        return super.token(call, request)
    }

}
