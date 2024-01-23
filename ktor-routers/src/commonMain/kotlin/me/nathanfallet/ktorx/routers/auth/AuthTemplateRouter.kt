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
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.templates.TemplateUnitRouter
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
open class AuthTemplateRouter<LoginPayload : Any, RegisterPayload : Any>(
    val loginPayloadTypeInfo: TypeInfo,
    val registerPayloadTypeInfo: TypeInfo,
    val authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    override val controller: IAuthController<LoginPayload, RegisterPayload>,
    controllerClass: KClass<out IAuthController<*, *>>,
    route: String? = "auth",
    prefix: String? = null,
) : TemplateUnitRouter(
    TemplateMapping(
        errorTemplate = authMapping.errorTemplate,
        redirectUnauthorizedToUrl = authMapping.redirectUnauthorizedToUrl
    ),
    respondTemplate,
    controller,
    controllerClass,
    route,
    prefix
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        createTemplateGetLoginRoute(root)
        createTemplatePostLoginRoute(root)
        createTemplateGetRegisterRoute(root)
        createTemplatePostRegisterRoute(root)
        createTemplateGetAuthorizeRoute(root)
        createTemplatePostAuthorizeRoute(root)
    }

    open fun createTemplateGetLoginRoute(root: Route) {
        authMapping.loginTemplate ?: return
        root.get("$fullRoute/login") {
            call.respondTemplate(
                authMapping.loginTemplate,
                mapOf()
            )
        }
    }

    open fun createTemplatePostLoginRoute(root: Route) {
        authMapping.loginTemplate ?: return
        root.post("$fullRoute/login") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    loginPayloadTypeInfo.type as KClass<LoginPayload>, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(payload, loginPayloadTypeInfo.type as KClass<LoginPayload>)
                controller.login(call, payload)
                call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, authMapping.loginTemplate)
            }
        }
    }

    open fun createTemplateGetRegisterRoute(root: Route) {
        authMapping.registerTemplate ?: return
        root.get("$fullRoute/register") {
            call.respondTemplate(
                authMapping.registerTemplate,
                mapOf()
            )
        }
    }

    open fun createTemplatePostRegisterRoute(root: Route) {
        authMapping.registerTemplate ?: return
        root.post("$fullRoute/register") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    registerPayloadTypeInfo.type as KClass<RegisterPayload>, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(payload, registerPayloadTypeInfo.type as KClass<RegisterPayload>)
                controller.register(call, payload)
                call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, authMapping.registerTemplate)
            }
        }
    }

    open fun createTemplateGetAuthorizeRoute(root: Route) {
        authMapping.authorizeTemplate ?: return
        root.get("$fullRoute/authorize") {
            try {
                val client = controller.authorize(call, call.parameters["client_id"])
                call.respondTemplate(
                    authMapping.authorizeTemplate,
                    mapOf(
                        "user" to client.user,
                        "client" to client.client
                    )
                )
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, authMapping.authorizeTemplate)
            }
        }
    }

    open fun createTemplatePostAuthorizeRoute(root: Route) {
        authMapping.authorizeTemplate ?: return
        root.post("$fullRoute/authorize") {
            try {
                val client = controller.authorize(call, call.parameters["client_id"])
                val redirect = controller.authorize(call, client)
                authMapping.redirectTemplate?.let {
                    call.respondTemplate(it, mapOf("redirect" to redirect))
                } ?: call.respondRedirect(redirect)
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, authMapping.authorizeTemplate)
            }
        }
    }

}
