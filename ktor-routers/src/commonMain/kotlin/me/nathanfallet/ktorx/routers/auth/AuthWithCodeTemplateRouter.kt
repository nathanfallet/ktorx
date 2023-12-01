package me.nathanfallet.ktorx.routers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass

open class AuthWithCodeTemplateRouter<LoginPayload : Any, RegisterPayload : Any, RegisterCodePayload : Any>(
    loginPayloadClass: KClass<LoginPayload>,
    registerPayloadClass: KClass<RegisterPayload>,
    val registerCodePayloadClass: KClass<RegisterCodePayload>,
    authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    override val controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
    route: String? = "auth",
    prefix: String? = null
) : AuthTemplateRouter<LoginPayload, RegisterPayload>(
    loginPayloadClass,
    registerPayloadClass,
    authMapping,
    respondTemplate,
    controller,
    route,
    prefix
) {

    override fun createRoutes(root: Route) {
        super.createRoutes(root)
        createTemplateGetRegisterCodeRoute(root)
        createTemplatePostRegisterCodeRoute(root)
    }

    override fun createTemplatePostRegisterRoute(root: Route) {
        authMapping.registerTemplate ?: return
        root.post("$fullRoute/register") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    registerPayloadClass, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                controller.register(call, payload)
                call.respondTemplate(
                    authMapping.registerTemplate,
                    mapOf("success" to "auth_register_code_created")
                )
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call, authMapping.registerTemplate)
            }
        }
    }

    open fun createTemplateGetRegisterCodeRoute(root: Route) {
        authMapping.registerTemplate ?: return
        root.get("$fullRoute/register/{code}") {
            try {
                val code = call.parameters["code"]!!
                val codePayload = controller.register(call, code)
                call.respondTemplate(
                    authMapping.registerTemplate,
                    mapOf("code" to codePayload)
                )
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call, authMapping.registerTemplate)
            }
        }
    }

    open fun createTemplatePostRegisterCodeRoute(root: Route) {
        authMapping.registerTemplate ?: return
        root.post("$fullRoute/register/{code}") {
            try {
                val code = call.parameters["code"]!!
                controller.register(call, code)
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    registerCodePayloadClass, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                controller.register(
                    call,
                    code,
                    payload
                )
                call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call, authMapping.registerTemplate)
            }
        }
    }

}
