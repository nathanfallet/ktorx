package me.nathanfallet.ktorx.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.auth.ISessionPayload
import me.nathanfallet.ktorx.models.auth.TestCodePayload
import me.nathanfallet.ktorx.models.auth.TestLoginPayload
import me.nathanfallet.ktorx.models.auth.TestRegisterPayload
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.usecases.users.IUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthWithCodeControllerTest {

    @Test
    fun testRegister() = runBlocking {
        val createCodeRegisterUseCase = mockk<ICreateCodeRegisterUseCase<TestCodePayload>>()
        val call = mockk<ApplicationCall>()
        val registerPayload = TestCodePayload("code")
        val controller = AuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), createCodeRegisterUseCase, mockk(), mockk()
        )
        coEvery { createCodeRegisterUseCase(call, registerPayload) } returns "code"
        controller.register(call, registerPayload)
        coVerify {
            createCodeRegisterUseCase(call, registerPayload)
        }
    }

    @Test
    fun testRegisterEmailTaken() = runBlocking {
        val createCodeRegisterUseCase = mockk<ICreateCodeRegisterUseCase<TestCodePayload>>()
        val call = mockk<ApplicationCall>()
        val registerPayload = TestCodePayload("code")
        val controller = AuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), createCodeRegisterUseCase, mockk(), mockk()
        )
        coEvery { createCodeRegisterUseCase(call, registerPayload) } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.register(call, registerPayload)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("auth_register_email_taken", exception.key)
    }

    @Test
    fun testRegisterCode() = runBlocking {
        val getCodeRegisterUseCase = mockk<IGetCodeRegisterUseCase<TestCodePayload>>()
        val call = mockk<ApplicationCall>()
        val registerPayload = TestCodePayload("code")
        val controller = AuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), mockk(), getCodeRegisterUseCase, mockk()
        )
        coEvery { getCodeRegisterUseCase(call, "code") } returns registerPayload
        assertEquals(registerPayload, controller.register(call, "code"))
    }

    @Test
    fun testRegisterCodeInvalid() = runBlocking {
        val getCodeRegisterUseCase = mockk<IGetCodeRegisterUseCase<TestCodePayload>>()
        val call = mockk<ApplicationCall>()
        val controller = AuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
            mockk(), mockk(), mockk(), mockk(), mockk(), getCodeRegisterUseCase, mockk()
        )
        coEvery { getCodeRegisterUseCase(call, "code") } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.register(call, "code")
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("auth_code_invalid", exception.key)
    }

    @Test
    fun testRegisterCodePayload() = runBlocking {
        val registerUseCase = mockk<IRegisterUseCase<TestRegisterPayload>>()
        val deleteCodeRegisterUseCase = mockk<IDeleteCodeRegisterUseCase>()
        val createSessionForUserUseCase = mockk<ICreateSessionForUserUseCase>()
        val setSessionForCallUseCase = mockk<ISetSessionForCallUseCase>()
        val call = mockk<ApplicationCall>()
        val registerPayload = TestRegisterPayload("email", "password")
        val user = mockk<IUser>()
        val sessionPayload = mockk<ISessionPayload>()
        val controller = AuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
            mockk(), registerUseCase, createSessionForUserUseCase, setSessionForCallUseCase,
            mockk(), mockk(), deleteCodeRegisterUseCase
        )
        every { createSessionForUserUseCase(user) } returns sessionPayload
        every { setSessionForCallUseCase(call, sessionPayload) } returns Unit
        coEvery { registerUseCase(call, registerPayload) } returns user
        coEvery { deleteCodeRegisterUseCase(call, "code") } returns Unit
        controller.register(call, "code", registerPayload)
        coVerify {
            registerUseCase(call, registerPayload)
            deleteCodeRegisterUseCase(call, "code")
        }
        verify { setSessionForCallUseCase(call, sessionPayload) }
    }

    @Test
    fun testRegisterCodePayloadError() = runBlocking {
        val registerUseCase = mockk<IRegisterUseCase<TestRegisterPayload>>()
        val call = mockk<ApplicationCall>()
        val registerPayload = TestRegisterPayload("email", "password")
        val controller = AuthWithCodeController<TestLoginPayload, TestCodePayload, TestRegisterPayload>(
            mockk(), registerUseCase, mockk(), mockk(),
            mockk(), mockk(), mockk()
        )
        coEvery { registerUseCase(call, registerPayload) } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.register(call, "code", registerPayload)
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

}
