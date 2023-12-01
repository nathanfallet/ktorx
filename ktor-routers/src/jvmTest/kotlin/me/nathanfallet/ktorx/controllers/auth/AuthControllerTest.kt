package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.auth.ISessionPayload
import me.nathanfallet.ktorx.models.auth.TestLoginPayload
import me.nathanfallet.ktorx.models.auth.TestRegisterPayload
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.ICreateSessionForUserUseCase
import me.nathanfallet.ktorx.usecases.auth.ILoginUseCase
import me.nathanfallet.ktorx.usecases.auth.IRegisterUseCase
import me.nathanfallet.ktorx.usecases.auth.ISetSessionForCallUseCase
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
            loginUseCase, mockk(), createSessionForUserUseCase, setSessionForCallUseCase
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
            loginUseCase, mockk(), mockk(), mockk()
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
            mockk(), registerUseCase, createSessionForUserUseCase, setSessionForCallUseCase
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
        )
        coEvery { registerUseCase(call, registerPayload) } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.register(call, registerPayload)
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

}
