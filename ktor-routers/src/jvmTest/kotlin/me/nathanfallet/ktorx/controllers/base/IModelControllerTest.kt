package me.nathanfallet.ktorx.controllers.base

import io.ktor.server.application.*
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.ktorx.models.TestCreatePayload
import me.nathanfallet.ktorx.models.TestModel
import me.nathanfallet.ktorx.models.TestUpdatePayload
import me.nathanfallet.usecases.models.UnitModel
import kotlin.test.Test
import kotlin.test.assertEquals

class IModelControllerTest {

    @Test
    fun testGetAll() = runBlocking {
        val controller = object : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
            override suspend fun getAll(call: ApplicationCall): List<TestModel> {
                return emptyList()
            }

            override suspend fun get(call: ApplicationCall, id: Long): TestModel {
                throw NotImplementedError()
            }

            override suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun update(call: ApplicationCall, id: Long, payload: TestUpdatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun delete(call: ApplicationCall, id: Long) {
                throw NotImplementedError()
            }
        }
        assertEquals(emptyList(), controller.getAll(mockk(), UnitModel))
    }

    @Test
    fun testGet() = runBlocking {
        val controller = object : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
            override suspend fun getAll(call: ApplicationCall): List<TestModel> {
                throw NotImplementedError()
            }

            override suspend fun get(call: ApplicationCall, id: Long): TestModel {
                return TestModel(1, "string")
            }

            override suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun update(call: ApplicationCall, id: Long, payload: TestUpdatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun delete(call: ApplicationCall, id: Long) {
                throw NotImplementedError()
            }
        }
        assertEquals(TestModel(1, "string"), controller.get(mockk(), UnitModel, 1))
    }

    @Test
    fun testCreate() = runBlocking {
        val controller = object : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
            override suspend fun getAll(call: ApplicationCall): List<TestModel> {
                throw NotImplementedError()
            }

            override suspend fun get(call: ApplicationCall, id: Long): TestModel {
                throw NotImplementedError()
            }

            override suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel {
                return TestModel(1, "string")
            }

            override suspend fun update(call: ApplicationCall, id: Long, payload: TestUpdatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun delete(call: ApplicationCall, id: Long) {
                throw NotImplementedError()
            }
        }
        assertEquals(TestModel(1, "string"), controller.create(mockk(), UnitModel, TestCreatePayload("string")))
    }

    @Test
    fun testUpdate() = runBlocking {
        val controller = object : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
            override suspend fun getAll(call: ApplicationCall): List<TestModel> {
                throw NotImplementedError()
            }

            override suspend fun get(call: ApplicationCall, id: Long): TestModel {
                throw NotImplementedError()
            }

            override suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun update(call: ApplicationCall, id: Long, payload: TestUpdatePayload): TestModel {
                return TestModel(1, "string")
            }

            override suspend fun delete(call: ApplicationCall, id: Long) {
                throw NotImplementedError()
            }
        }
        assertEquals(TestModel(1, "string"), controller.update(mockk(), UnitModel, 1, TestUpdatePayload("string")))
    }

    @Test
    fun testDelete() = runBlocking {
        var called = false
        val controller = object : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {
            override suspend fun getAll(call: ApplicationCall): List<TestModel> {
                throw NotImplementedError()
            }

            override suspend fun get(call: ApplicationCall, id: Long): TestModel {
                throw NotImplementedError()
            }

            override suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun update(call: ApplicationCall, id: Long, payload: TestUpdatePayload): TestModel {
                throw NotImplementedError()
            }

            override suspend fun delete(call: ApplicationCall, id: Long) {
                called = true
            }
        }
        controller.delete(mockk(), UnitModel, 1)
        assertEquals(true, called)
    }

}
