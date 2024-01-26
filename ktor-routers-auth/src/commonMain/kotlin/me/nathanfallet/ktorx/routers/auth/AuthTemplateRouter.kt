package me.nathanfallet.ktorx.routers.auth

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.extensions.authorize
import me.nathanfallet.ktorx.extensions.authorizeRedirect
import me.nathanfallet.ktorx.extensions.login
import me.nathanfallet.ktorx.extensions.register
import me.nathanfallet.ktorx.models.annotations.TemplateMapping
import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.ktorx.models.routes.ControllerRoute
import me.nathanfallet.ktorx.models.routes.RouteType
import me.nathanfallet.ktorx.routers.templates.TemplateUnitRouter
import kotlin.reflect.KClass

open class AuthTemplateRouter<LoginPayload : Any, RegisterPayload : Any>(
    val loginPayloadTypeInfo: TypeInfo,
    val registerPayloadTypeInfo: TypeInfo,
    controller: IAuthController<LoginPayload, RegisterPayload>,
    controllerClass: KClass<out IAuthController<*, *>>,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    errorTemplate: String? = null,
    redirectUnauthorizedToUrl: String? = null,
    val redirectTemplate: String? = null,
    route: String? = null,
    prefix: String? = null,
) : TemplateUnitRouter(
    controller,
    controllerClass,
    respondTemplate,
    errorTemplate,
    redirectUnauthorizedToUrl,
    route ?: "auth",
    prefix
) {

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        val mapping = controllerRoute.annotations.firstNotNullOfOrNull { it as? TemplateMapping } ?: return
        when (controllerRoute.type) {
            RouteType.login -> {
                root.get("$fullRoute/login") {
                    call.respondTemplate(
                        mapping.template,
                        mapOf()
                    )
                }
                root.post("$fullRoute/login") {
                    try {
                        invokeControllerRoute(call, controllerRoute)
                        call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.register -> {
                root.get("$fullRoute/register") {
                    call.respondTemplate(
                        mapping.template,
                        mapOf()
                    )
                }
                root.post("$fullRoute/register") {
                    try {
                        invokeControllerRoute(call, controllerRoute)
                        call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.authorize -> {
                root.get("$fullRoute/authorize") {
                    try {
                        val client = invokeControllerRoute(
                            call, controllerRoute, mapOf("clientId" to call.parameters["client_id"])
                        ) as ClientForUser
                        call.respondTemplate(
                            mapping.template,
                            mapOf(
                                "user" to client.user,
                                "client" to client.client
                            )
                        )
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.authorizeRedirect -> {
                root.post("$fullRoute/authorize") {
                    try {
                        val clientId = call.parameters["client_id"]
                        val client = authorize(call, clientId)
                        val redirect = invokeControllerRoute(
                            call, controllerRoute, mapOf("client" to client)
                        ) as String
                        redirectTemplate?.let {
                            call.respondTemplate(it, mapOf("redirect" to redirect))
                        } ?: call.respondRedirect(redirect)
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            else -> super.createControllerRoute(root, controllerRoute, openAPI)
        }
    }

    private suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser {
        return controllerRoutes.singleOrNull { it.type == RouteType.authorize }?.let {
            invokeControllerRoute(call, it, mapOf("clientId" to clientId))
        } as ClientForUser
    }

}
