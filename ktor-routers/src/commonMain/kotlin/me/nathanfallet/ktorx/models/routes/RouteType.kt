package me.nathanfallet.ktorx.models.routes

data class RouteType(val value: String? = null) {

    companion object {

        val list = RouteType("list")
        val get = RouteType("get")
        val create = RouteType("create")
        val update = RouteType("update")
        val delete = RouteType("delete")

    }

}
