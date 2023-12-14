package me.nathanfallet.ktorx.routers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
open class AuthWithCodeTemplateRouter<LoginPayload : Any, RegisterPayload : Any, RegisterCodePayload : Any>(
    loginPayloadTypeInfo: TypeInfo,
    registerPayloadTypeInfo: TypeInfo,
    val registerCodePayloadTypeInfo: TypeInfo,
    authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    override val controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
    route: String? = "auth",
    prefix: String? = null,
) : AuthTemplateRouter<LoginPayload, RegisterPayload>(
    loginPayloadTypeInfo,
    registerPayloadTypeInfo,
    authMapping,
    respondTemplate,
    controller,
    route,
    prefix
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
        createTemplateGetRegisterCodeRoute(root)
        createTemplatePostRegisterCodeRoute(root)
    }

    override fun createTemplatePostRegisterRoute(root: Route) {
        authMapping.registerTemplate ?: return
        root.post("$fullRoute/register") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    registerPayloadTypeInfo.type as KClass<RegisterPayload>, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(payload, registerPayloadTypeInfo.type as KClass<RegisterPayload>)
                controller.register(call, payload)
                call.respondTemplate(
                    authMapping.registerTemplate,
                    mapOf("success" to "auth_register_code_created")
                )
            } catch (exception: Exception) {
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
                    mapOf("codePayload" to codePayload)
                )
            } catch (exception: Exception) {
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
                    registerCodePayloadTypeInfo.type as KClass<RegisterCodePayload>,
                    call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(
                    payload,
                    registerCodePayloadTypeInfo.type as KClass<RegisterCodePayload>
                )
                controller.register(
                    call,
                    code,
                    payload
                )
                call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, authMapping.registerTemplate)
            }
        }
    }

}
