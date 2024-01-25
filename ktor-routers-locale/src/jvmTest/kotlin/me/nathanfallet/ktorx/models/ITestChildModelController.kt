package me.nathanfallet.ktorx.models

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.annotations.*

interface ITestChildModelController :
    IChildModelController<TestChildModel, Long, TestCreatePayload, TestUpdatePayload, TestModel, Long> {

    @APIMapping
    @TemplateMapping(template = "basic")
    @Path("GET", "/basic")
    fun basic(call: ApplicationCall, @ParentModel("testmodelId") parent: TestModel): String

    @APIMapping
    @TemplateMapping(template = "list")
    @ListModelPath
    fun list(call: ApplicationCall, @ParentModel("testmodelId") parent: TestModel): List<TestChildModel>

    @APIMapping
    @TemplateMapping(template = "create")
    @CreateModelPath
    fun create(
        call: ApplicationCall,
        @ParentModel("testmodelId") parent: TestModel,
        payload: TestCreatePayload,
    ): TestChildModel

    @APIMapping
    @TemplateMapping(template = "get")
    @GetModelPath
    fun get(call: ApplicationCall, @ParentModel("testmodelId") parent: TestModel, @Id id: Long): TestChildModel

    @APIMapping
    @TemplateMapping(template = "update")
    @UpdateModelPath
    fun update(
        call: ApplicationCall,
        @ParentModel("testmodelId") parent: TestModel,
        @Id id: Long,
        payload: TestUpdatePayload,
    ): TestChildModel

    @APIMapping
    @TemplateMapping(template = "delete")
    @DeleteModelPath
    fun delete(call: ApplicationCall, @ParentModel("testmodelId") parent: TestModel, @Id id: Long)

}
