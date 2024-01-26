package me.nathanfallet.ktorx.models

import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.annotations.APIMapping
import me.nathanfallet.ktorx.models.annotations.Path
import me.nathanfallet.ktorx.models.annotations.TemplateMapping

interface ITestUnitController : IUnitController {

    @APIMapping
    @TemplateMapping("hello.ftl")
    @Path("GET", "/hello")
    suspend fun hello(): String

}
