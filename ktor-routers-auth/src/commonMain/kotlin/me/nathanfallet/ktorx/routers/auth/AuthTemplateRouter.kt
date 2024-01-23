package me.nathanfallet.ktorx.routers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.extensions.authorize
import me.nathanfallet.ktorx.extensions.login
import me.nathanfallet.ktorx.extensions.register
import me.nathanfallet.ktorx.models.annotations.TemplateMapping
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.routers.base.ControllerRoute
import me.nathanfallet.ktorx.routers.base.RouteType
import me.nathanfallet.ktorx.routers.templates.TemplateUnitRouter
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
open class AuthTemplateRouter<LoginPayload : Any, RegisterPayload : Any>(
    val loginPayloadTypeInfo: TypeInfo,
    val registerPayloadTypeInfo: TypeInfo,
    val authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    errorTemplate: String? = null,
    redirectUnauthorizedToUrl: String? = null,
    override val controller: IAuthController<LoginPayload, RegisterPayload>,
    controllerClass: KClass<out IAuthController<*, *>>,
    route: String? = "auth",
    prefix: String? = null,
) : TemplateUnitRouter(
    respondTemplate,
    errorTemplate,
    redirectUnauthorizedToUrl,
    controller,
    controllerClass,
    route,
    prefix
) {

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        val mapping = controllerRoute.function.annotations
            .firstNotNullOfOrNull { it as? TemplateMapping } ?: return
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
                        val payload = ModelAnnotations.constructPayloadFromStringLists(
                            loginPayloadTypeInfo.type as KClass<LoginPayload>, call.receiveParameters().toMap()
                        ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                        ModelAnnotations.validatePayload(payload, loginPayloadTypeInfo.type as KClass<LoginPayload>)
                        controller.login(call, payload)
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
                        val payload = ModelAnnotations.constructPayloadFromStringLists(
                            registerPayloadTypeInfo.type as KClass<RegisterPayload>, call.receiveParameters().toMap()
                        ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                        ModelAnnotations.validatePayload(
                            payload,
                            registerPayloadTypeInfo.type as KClass<RegisterPayload>
                        )
                        controller.register(call, payload)
                        call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.authorize -> {
                root.get("$fullRoute/authorize") {
                    try {
                        val client = controller.authorize(call, call.parameters["client_id"])
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
                root.post("$fullRoute/authorize") {
                    try {
                        val client = controller.authorize(call, call.parameters["client_id"])
                        val redirect = controller.authorize(call, client)
                        authMapping.redirectTemplate?.let {
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

}
