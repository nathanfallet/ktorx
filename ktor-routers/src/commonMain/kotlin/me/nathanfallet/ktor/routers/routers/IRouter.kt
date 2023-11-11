package me.nathanfallet.ktor.routers.routers

import io.ktor.server.routing.*

interface IRouter {

    fun createRoutes(root: Route)

}