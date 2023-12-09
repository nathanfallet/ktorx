package me.nathanfallet.ktorx.routers

import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI

interface IRouter {

    fun createRoutes(root: Route, openAPI: OpenAPI? = null)

}
