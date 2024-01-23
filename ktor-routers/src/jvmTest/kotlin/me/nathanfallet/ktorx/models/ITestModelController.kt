package me.nathanfallet.ktorx.models

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.*
import me.nathanfallet.ktorx.models.auth.TestUser

interface ITestModelController : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {

    @APIMapping
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
    @ListPath
    suspend fun list(call: ApplicationCall): List<TestModel>

    @APIMapping
    @TemplateMapping(template = "create")
    @CreatePath
    suspend fun create(call: ApplicationCall, payload: TestCreatePayload): TestModel

    @APIMapping
    @TemplateMapping(template = "get")
    @GetPath
    suspend fun get(call: ApplicationCall, @Id id: Long): TestModel

    @APIMapping
    @TemplateMapping(template = "update")
    @UpdatePath
    suspend fun update(call: ApplicationCall, @Id id: Long, payload: TestUpdatePayload): TestModel

    @APIMapping
    @TemplateMapping(template = "delete")
    @DeletePath
    suspend fun delete(call: ApplicationCall, @Id id: Long)

}
