package me.nathanfallet.ktorx.usecases.auth

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.auth.TestClient
import me.nathanfallet.usecases.models.get.IGetModelUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class GetClientFromModelUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val getModelUseCase = mockk<IGetModelUseCase<TestClient, String>>()
        val useCase = GetClientFromModelUseCase(getModelUseCase)
        val client = TestClient("cid")
        coEvery { getModelUseCase("cid") } returns client
        assertEquals(client, useCase("cid"))
    }

}
