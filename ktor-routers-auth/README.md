# ktor-routers-auth

Generic auth routers for Ktor projects.

## Installation

Add dependency to your `build.gradle(.kts)` or `pom.xml`:

```kotlin
api("me.nathanfallet.ktorx:ktor-routers-auth:2.2.2")
```

```xml

<dependency>
    <groupId>me.nathanfallet.ktorx</groupId>
    <artifactId>ktor-routers-auth-jvm</artifactId>
    <version>2.2.2</version>
</dependency>
```

## Usage

Create an interface for an authentication controller that extends `IAuthController` or `IAuthWithCodeController`:
You can only declare the methods you need.

```kotlin
interface MyAuthController : IAuthController<LoginPayload, RegisterPayload> {

    @TemplateMapping("login")
    @LoginPath
    suspend fun login(call: ApplicationCall, @Payload payload: LoginPayload)

    @TemplateMapping("register")
    @RegisterPath
    suspend fun register(call: ApplicationCall, @Payload payload: RegisterPayload)

    @TemplateMapping("authorize")
    @AuthorizePath
    suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser

    @TemplateMapping("authorize")
    @AuthorizeRedirectPath
    suspend fun authorize(call: ApplicationCall, client: ClientForUser): String

    @APIMapping
    @CreateModelPath("/token")
    @DocumentedTag("Auth")
    @DocumentedError(400, "auth_invalid_code")
    @DocumentedError(500, "error_internal")
    suspend fun token(call: ApplicationCall, @Payload request: AuthRequest): AuthToken

}
```

Then create the implementation of this interface using the default implementation or your own:

```kotlin
class TestAuthController(
    loginUseCase: ILoginUseCase<LoginPayload>,
    registerUseCase: IRegisterUseCase<RegisterPayload>,
    createSessionForUserUseCase: ICreateSessionForUserUseCase,
    setSessionForCallUseCase: ISetSessionForCallUseCase,
    requireUserForCallUseCase: IRequireUserForCallUseCase,
    getClientUseCase: IGetClientUseCase,
    getAuthCodeUseCase: IGetAuthCodeUseCase,
    createAuthCodeUseCase: ICreateAuthCodeUseCase,
    deleteAuthCodeUseCase: IDeleteAuthCodeUseCase,
    generateAuthTokenUseCase: IGenerateAuthTokenUseCase,
) : AbstractAuthController<LoginPayload, RegisterPayload>(
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
), MyAuthController {

    override suspend fun login(call: ApplicationCall, @Payload payload: TestLoginPayload) {
        super.login(call, payload)
    }

    override suspend fun register(call: ApplicationCall, @Payload payload: TestRegisterPayload) {
        super.register(call, payload)
    }

    override suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser {
        return super.authorize(call, clientId)
    }

    override suspend fun authorize(call: ApplicationCall, client: ClientForUser): String {
        return super.authorize(call, client)
    }

    override suspend fun token(call: ApplicationCall, @Payload request: AuthRequest): AuthToken {
        return super.token(call, request)
    }

}
```

Finally, create the router that you will add to your Ktor application:

```kotlin
class AuthRouter(
    controller: IAuthController,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : ConcatUnitRouter(
    listOf(
        AuthTemplateRouter<LoginPayload, RegisterPayload>(
            typeInfo<LoginPayload>(),
            typeInfo<RegisterPayload>(),
            { template, model -> respondTemplate(template, model) },
            null,
            "/auth/login?redirect={path}",
            "auth/redirect.ftl",
            controller,
            MyAuthController::class,
        ),
        APIUnitRouter(
            controller,
            MyAuthController::class,
            route = "auth"
        )
    )
)
```
