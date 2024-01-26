package me.nathanfallet.ktorx.models

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.APIMapping
import me.nathanfallet.ktorx.models.annotations.AdminTemplateMapping
import me.nathanfallet.ktorx.models.annotations.ListModelPath
import me.nathanfallet.ktorx.models.annotations.TemplateMapping

interface ITestModelController : IModelController<TestModel, Long, TestCreatePayload, TestUpdatePayload> {

    @APIMapping
    @TemplateMapping(template = "template")
    @AdminTemplateMapping
    @ListModelPath
    suspend fun list(call: ApplicationCall): List<TestModel>

}
