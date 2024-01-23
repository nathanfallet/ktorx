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
import me.nathanfallet.ktorx.extensions.register
import me.nathanfallet.ktorx.extensions.registerCode
import me.nathanfallet.ktorx.extensions.registerCodeRedirect
import me.nathanfallet.ktorx.models.annotations.TemplateMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.routers.base.ControllerRoute
import me.nathanfallet.ktorx.routers.base.RouteType
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
open class AuthWithCodeTemplateRouter<LoginPayload : Any, RegisterPayload : Any, RegisterCodePayload : Any>(
    loginPayloadTypeInfo: TypeInfo,
    registerPayloadTypeInfo: TypeInfo,
    val registerCodePayloadTypeInfo: TypeInfo,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    errorTemplate: String? = null,
    redirectUnauthorizedToUrl: String? = null,
    redirectTemplate: String? = null,
    override val controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
    controllerClass: KClass<out IAuthWithCodeController<*, *, *>>,
    route: String? = "auth",
    prefix: String? = null,
) : AuthTemplateRouter<LoginPayload, RegisterPayload>(
    loginPayloadTypeInfo,
    registerPayloadTypeInfo,
    respondTemplate,
    errorTemplate,
    redirectUnauthorizedToUrl,
    redirectTemplate,
    controller,
    controllerClass,
    route,
    prefix
) {

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        val mapping = controllerRoute.function.annotations
            .firstNotNullOfOrNull { it as? TemplateMapping } ?: return
        when (controllerRoute.type) {
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
                        controllerRoute(call, this@AuthWithCodeTemplateRouter, mapOf("payload" to payload))
                        call.respondTemplate(
                            mapping.template,
                            mapOf("success" to "auth_register_code_created")
                        )
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.registerCode -> {
                root.get("$fullRoute/register/{code}") {
                    try {
                        val code = call.parameters["code"]!!
                        val codePayload = controllerRoute(
                            call, this@AuthWithCodeTemplateRouter, mapOf("code" to code)
                        ) as RegisterCodePayload
                        call.respondTemplate(
                            mapping.template,
                            mapOf("codePayload" to codePayload)
                        )
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.registerCodeRedirect -> {
                root.post("$fullRoute/register/{code}") {
                    try {
                        val code = call.parameters["code"]!!
                        register(call, code)
                        val payload = ModelAnnotations.constructPayloadFromStringLists(
                            registerCodePayloadTypeInfo.type as KClass<RegisterCodePayload>,
                            call.receiveParameters().toMap()
                        ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                        ModelAnnotations.validatePayload(
                            payload,
                            registerCodePayloadTypeInfo.type as KClass<RegisterCodePayload>
                        )
                        controllerRoute(
                            call, this@AuthWithCodeTemplateRouter, mapOf(
                                "code" to code,
                                "payload" to payload
                            )
                        )
                        call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            else -> super.createControllerRoute(root, controllerRoute, openAPI)
        }
    }

    private suspend fun register(call: ApplicationCall, code: String): RegisterCodePayload {
        return controllerRoutes.singleOrNull { it.type == RouteType.registerCode }?.invoke(
            call, this, mapOf("code" to code)
        ) as RegisterCodePayload
    }

}
