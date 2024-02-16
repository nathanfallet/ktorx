package me.nathanfallet.ktorx.models

import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.annotations.*

interface ITestUnitController : IUnitController {

    @APIMapping
    @TemplateMapping("hello.ftl")
    @Path("GET", "/hello")
    suspend fun hello(): String

    @APIMapping
    @TemplateMapping("hello.ftl")
    @Path("GET", "/hello/query")
    suspend fun helloQuery(@QueryParameter name: String): String

    @APIMapping
    @TemplateMapping("hello.ftl")
    @Path("GET", "/hello/path/{name}")
    suspend fun helloPath(@PathParameter name: String): String

    @APIMapping
    @TemplateMapping("hello.ftl")
    @Path("POST", "/hello")
    suspend fun postHello(@Payload payload: TestCreatePayload): String

}
