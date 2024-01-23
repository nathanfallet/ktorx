package me.nathanfallet.ktorx.extensions

import me.nathanfallet.ktorx.routers.base.RouteType

val RouteType.Companion.login: RouteType
    get() = RouteType("login")

val RouteType.Companion.register: RouteType
    get() = RouteType("register")

val RouteType.Companion.registerCode: RouteType
    get() = RouteType("registerCode".lowercase())

val RouteType.Companion.authorize: RouteType
    get() = RouteType("authorize")
