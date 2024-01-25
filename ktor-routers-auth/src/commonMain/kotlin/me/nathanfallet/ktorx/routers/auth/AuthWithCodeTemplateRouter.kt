package me.nathanfallet.ktorx.routers.auth

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.extensions.register
import me.nathanfallet.ktorx.extensions.registerCode
import me.nathanfallet.ktorx.extensions.registerCodeRedirect
import me.nathanfallet.ktorx.models.annotations.TemplateMapping
import me.nathanfallet.ktorx.models.routes.ControllerRoute
import me.nathanfallet.ktorx.models.routes.RouteType
import kotlin.reflect.KClass

open class AuthWithCodeTemplateRouter<LoginPayload : Any, RegisterPayload : Any, RegisterCodePayload : Any>(
    loginPayloadTypeInfo: TypeInfo,
    registerPayloadTypeInfo: TypeInfo,
    val registerCodePayloadTypeInfo: TypeInfo,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    errorTemplate: String? = null,
    redirectUnauthorizedToUrl: String? = null,
    redirectTemplate: String? = null,
    controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
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
        val mapping = controllerRoute.annotations.firstNotNullOfOrNull { it as? TemplateMapping } ?: return
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
                        invokeControllerRoute(call, controllerRoute)
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
                        val codePayload = invokeControllerRoute(call, controllerRoute, mapOf("code" to code))
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
                        invokeControllerRoute(call, controllerRoute, mapOf("code" to code))
                        call.respondRedirect(call.request.queryParameters["redirect"] ?: "/")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            else -> super.createControllerRoute(root, controllerRoute, openAPI)
        }
    }

    private suspend fun register(call: ApplicationCall, code: String): Any? {
        return controllerRoutes.singleOrNull { it.type == RouteType.registerCode }?.let {
            invokeControllerRoute(call, it, mapOf("code" to code))
        }
    }

}
