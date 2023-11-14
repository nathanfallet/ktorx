package me.nathanfallet.ktorx.routers

import io.ktor.server.routing.*

interface IRouter {

    fun createRoutes(root: Route)

}
