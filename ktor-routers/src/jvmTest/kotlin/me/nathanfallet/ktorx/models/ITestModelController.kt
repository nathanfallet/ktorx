package me.nathanfallet.ktorx.models

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.*

interface ITestModelController : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {

    @APIMapping("basic", "Basic test")
    @TemplateMapping(template = "basic")
    @Path("GET", "/basic")
    suspend fun basic(call: ApplicationCall): String

    @APIMapping
    @TemplateMapping(template = "basic")
    @Path("GET", "/basic/map")
    suspend fun basicMap(call: ApplicationCall): Map<String, String>

    @APIMapping
    @TemplateMapping(template = "basic")
    @Path("GET", "/basic/model")
    suspend fun basicModel(call: ApplicationCall): TestUser

    @APIMapping
    @TemplateMapping(template = "list")
    @ListModelPath
    suspend fun list(call: ApplicationCall): List<TestModel>

    @APIMapping
    @TemplateMapping(template = "create")
    @CreateModelPath
    suspend fun create(call: ApplicationCall, @Payload payload: TestCreatePayload): TestModel

    @APIMapping
    @TemplateMapping(template = "get")
    @GetModelPath
    @DocumentedError(404, "testmodels_not_found", "Test model not found")
    suspend fun get(call: ApplicationCall, @Id id: Long): TestModel

    @APIMapping
    @TemplateMapping(template = "update")
    @UpdateModelPath
    suspend fun update(call: ApplicationCall, @Id id: Long, @Payload payload: TestUpdatePayload): TestModel

    @APIMapping
    @TemplateMapping(template = "delete")
    @DeleteModelPath
    @DocumentedType(TestModel::class)
    suspend fun delete(call: ApplicationCall, @Id id: Long)

}
