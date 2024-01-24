package me.nathanfallet.ktorx.extensions

import me.nathanfallet.ktorx.models.routes.RouteType

val RouteType.Companion.login: RouteType
    get() = RouteType("login")

val RouteType.Companion.register: RouteType
    get() = RouteType("register")

val RouteType.Companion.registerCode: RouteType
    get() = RouteType("registerCode".lowercase())

val RouteType.Companion.registerCodeRedirect: RouteType
    get() = RouteType("registerCodeRedirect".lowercase())

val RouteType.Companion.authorize: RouteType
    get() = RouteType("authorize")

val RouteType.Companion.authorizeRedirect: RouteType
    get() = RouteType("authorizeRedirect".lowercase())

val RouteType.Companion.token: RouteType
    get() = RouteType("token")
