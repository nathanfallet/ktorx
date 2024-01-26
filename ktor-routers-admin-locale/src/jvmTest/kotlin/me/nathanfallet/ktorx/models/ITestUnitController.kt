package me.nathanfallet.ktorx.models

import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.annotations.APIMapping
import me.nathanfallet.ktorx.models.annotations.AdminTemplateMapping
import me.nathanfallet.ktorx.models.annotations.Path

interface ITestUnitController : IUnitController {

    @APIMapping
    @AdminTemplateMapping("hello.ftl")
    @Path("GET", "/")
    suspend fun dashboard()

}
