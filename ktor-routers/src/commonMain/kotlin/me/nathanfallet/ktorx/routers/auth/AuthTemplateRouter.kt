package me.nathanfallet.ktorx.routers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.templates.TemplateUnitRouter
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass

open class AuthTemplateRouter<LoginPayload : Any, RegisterPayload : Any>(
    val loginPayloadClass: KClass<LoginPayload>,
    val registerPayloadClass: KClass<RegisterPayload>,
    val authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    override val controller: IAuthController<LoginPayload, RegisterPayload>,
    route: String? = "auth",
    prefix: String? = null,
) : TemplateUnitRouter(
    TemplateMapping(authMapping.errorTemplate),
    respondTemplate,
    controller,
    route,
    prefix
) {

    override fun createRoutes(root: Route) {
        createTemplateGetLoginRoute(root)
        createTemplatePostLoginRoute(root)
        createTemplateGetRegisterRoute(root)
        createTemplatePostRegisterRoute(root)
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
                    loginPayloadClass, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(payload, loginPayloadClass)
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
                    registerPayloadClass, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(payload, registerPayloadClass)
                controller.register(call, payload)
                call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, authMapping.registerTemplate)
            }
        }
    }

}
