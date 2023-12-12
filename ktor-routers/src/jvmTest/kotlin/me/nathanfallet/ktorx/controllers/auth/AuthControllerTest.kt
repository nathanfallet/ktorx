package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.auth.*
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.users.ISessionPayload
import me.nathanfallet.usecases.users.IUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthControllerTest {

    @Test
    fun testLogin() = runBlocking {
        val loginUseCase = mockk<ILoginUseCase<TestLoginPayload>>()
        val createSessionForUserUseCase = mockk<ICreateSessionForUserUseCase>()
        val setSessionForCallUseCase = mockk<ISetSessionForCallUseCase>()
        val call = mockk<ApplicationCall>()
        val loginPayload = TestLoginPayload("email", "password")
        val user = mockk<IUser>()
        val sessionPayload = mockk<ISessionPayload>()
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            loginUseCase, mockk(), createSessionForUserUseCase, setSessionForCallUseCase,
            mockk(), mockk(), mockk(), mockk(), mockk()
        )
        coEvery { loginUseCase(loginPayload) } returns user
        every { createSessionForUserUseCase(user) } returns sessionPayload
        every { setSessionForCallUseCase(call, sessionPayload) } returns Unit
        controller.login(call, loginPayload)
        verify { setSessionForCallUseCase(call, sessionPayload) }
    }

    @Test
    fun testLoginInvalidCredentials() = runBlocking {
        val loginUseCase = mockk<ILoginUseCase<TestLoginPayload>>()
        val call = mockk<ApplicationCall>()
        val loginPayload = TestLoginPayload("email", "password")
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            loginUseCase, mockk(), mockk(), mockk(), mockk(),
            mockk(), mockk(), mockk(), mockk()
        )
        coEvery { loginUseCase(loginPayload) } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.login(call, loginPayload)
        }
        assertEquals(HttpStatusCode.Unauthorized, exception.code)
        assertEquals("auth_invalid_credentials", exception.key)
    }

    @Test
    fun testRegisterPayload() = runBlocking {
        val registerUseCase = mockk<IRegisterUseCase<TestRegisterPayload>>()
        val createSessionForUserUseCase = mockk<ICreateSessionForUserUseCase>()
        val setSessionForCallUseCase = mockk<ISetSessionForCallUseCase>()
        val call = mockk<ApplicationCall>()
        val registerPayload = TestRegisterPayload("email", "password")
        val user = mockk<IUser>()
        val sessionPayload = mockk<ISessionPayload>()
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), registerUseCase, createSessionForUserUseCase, setSessionForCallUseCase,
            mockk(), mockk(), mockk(), mockk(), mockk()
        )
        every { createSessionForUserUseCase(user) } returns sessionPayload
        every { setSessionForCallUseCase(call, sessionPayload) } returns Unit
        coEvery { registerUseCase(call, registerPayload) } returns user
        controller.register(call, registerPayload)
        coVerify {
            registerUseCase(call, registerPayload)
        }
        verify { setSessionForCallUseCase(call, sessionPayload) }
    }

    @Test
    fun testRegisterPayloadError() = runBlocking {
        val registerUseCase = mockk<IRegisterUseCase<TestRegisterPayload>>()
        val call = mockk<ApplicationCall>()
        val registerPayload = TestRegisterPayload("email", "password")
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), registerUseCase, mockk(), mockk(),
            mockk(), mockk(), mockk(), mockk(), mockk()
        )
        coEvery { registerUseCase(call, registerPayload) } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.register(call, registerPayload)
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

    @Test
    fun testAuthorize() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getClientUseCase = mockk<IGetClientUseCase>()
        val call = mockk<ApplicationCall>()
        val user = TestUser("id")
        val client = TestClient("cid")
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), requireUserForCallUseCase,
            getClientUseCase, mockk(), mockk(), mockk()
        )
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getClientUseCase("cid") } returns client
        assertEquals(ClientForUser(client, user), controller.authorize(call, "cid"))
    }

    @Test
    fun testAuthorizeNoClient() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getClientUseCase = mockk<IGetClientUseCase>()
        val call = mockk<ApplicationCall>()
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), requireUserForCallUseCase,
            getClientUseCase, mockk(), mockk(), mockk()
        )
        coEvery { requireUserForCallUseCase(call) } returns TestUser("id")
        coEvery { getClientUseCase("cid") } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.authorize(call, "cid")
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("auth_invalid_client", exception.key)
    }

    @Test
    fun testAuthorizeWithClient() = runBlocking {
        val createAuthCodeUseCase = mockk<ICreateAuthCodeUseCase>()
        val call = mockk<ApplicationCall>()
        val client = ClientForUser(TestClient("cid"), TestUser("id"))
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), mockk(),
            mockk(), mockk(), createAuthCodeUseCase, mockk()
        )
        coEvery { createAuthCodeUseCase(client) } returns "code"
        assertEquals("app://redirect?code=code", controller.authorize(call, client))
    }

    @Test
    fun testToken() = runBlocking {
        val getAuthCodeUseCase = mockk<IGetAuthCodeUseCase>()
        val generateAuthTokenUseCase = mockk<IGenerateAuthTokenUseCase>()
        val call = mockk<ApplicationCall>()
        val client = ClientForUser(TestClient("cid"), TestUser("id"))
        val request = AuthRequest(client.client.clientId, client.client.clientSecret, "code")
        val token = AuthToken("token", "refresh")
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), mockk(),
            mockk(), getAuthCodeUseCase, mockk(), generateAuthTokenUseCase
        )
        coEvery { getAuthCodeUseCase("code") } returns client
        coEvery { generateAuthTokenUseCase(client) } returns token
        assertEquals(token, controller.token(call, request))
    }

    @Test
    fun testTokenInvalidClientId() = runBlocking {
        val getAuthCodeUseCase = mockk<IGetAuthCodeUseCase>()
        val call = mockk<ApplicationCall>()
        val client = ClientForUser(TestClient("cid"), TestUser("id"))
        val request = AuthRequest("otherClientId", client.client.clientSecret, "code")
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), mockk(),
            mockk(), getAuthCodeUseCase, mockk(), mockk()
        )
        coEvery { getAuthCodeUseCase("code") } returns client
        val exception = assertFailsWith(ControllerException::class) {
            controller.token(call, request)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("auth_invalid_code", exception.key)
    }

    @Test
    fun testTokenInvalidClientSecret() = runBlocking {
        val getAuthCodeUseCase = mockk<IGetAuthCodeUseCase>()
        val call = mockk<ApplicationCall>()
        val client = ClientForUser(TestClient("cid"), TestUser("id"))
        val request = AuthRequest(client.client.clientId, "otherSecret", "code")
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), mockk(),
            mockk(), getAuthCodeUseCase, mockk(), mockk()
        )
        coEvery { getAuthCodeUseCase("code") } returns client
        val exception = assertFailsWith(ControllerException::class) {
            controller.token(call, request)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("auth_invalid_code", exception.key)
    }

    @Test
    fun testTokenInvalidCode() = runBlocking {
        val getAuthCodeUseCase = mockk<IGetAuthCodeUseCase>()
        val call = mockk<ApplicationCall>()
        val client = ClientForUser(TestClient("cid"), TestUser("id"))
        val request = AuthRequest(client.client.clientId, client.client.clientSecret, "code")
        val controller = AuthController<TestLoginPayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), mockk(),
            mockk(), getAuthCodeUseCase, mockk(), mockk()
        )
        coEvery { getAuthCodeUseCase("code") } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.token(call, request)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("auth_invalid_code", exception.key)
    }

}
