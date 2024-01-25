package me.nathanfallet.ktorx.models

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.*

interface ITestModelController : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {

    @APIMapping
    @TemplateMapping(template = "basic")
    @Path("GET", "/basic")
    suspend fun basic(call: ApplicationCall): String

    @APIMapping
    @TemplateMapping(template = "list")
    @ListModelPath
    suspend fun list(call: ApplicationCall): List<TestModel>

    @APIMapping
    @TemplateMapping(template = "create")
    @CreateModelPath
    suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel

    @APIMapping
    @TemplateMapping(template = "get")
    @GetModelPath
    suspend fun get(call: ApplicationCall, @Id id: Long): TestModel

    @APIMapping
    @TemplateMapping(template = "update")
    @UpdateModelPath
    suspend fun update(call: ApplicationCall, @Id id: Long, payload: TestUpdatePayload): TestModel

    @APIMapping
    @TemplateMapping(template = "delete")
    @DeleteModelPath
    suspend fun delete(call: ApplicationCall, @Id id: Long)

}
