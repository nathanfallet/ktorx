package me.nathanfallet.ktorx.routers.templates

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.annotations.TemplateMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.ktorx.routers.base.ControllerRoute
import me.nathanfallet.ktorx.routers.base.RouteType
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import me.nathanfallet.usecases.models.annotations.validators.PropertyValidatorException
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
open class TemplateChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    modelTypeInfo: TypeInfo,
    createPayloadTypeInfo: TypeInfo,
    updatePayloadTypeInfo: TypeInfo,
    listTypeInfo: TypeInfo,
    controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    parentRouter: IChildModelRouter<ParentModel, ParentId, *, *, *, *>?,
    controllerClass: KClass<out IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>>,
    val respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    val errorTemplate: String? = null,
    val redirectUnauthorizedToUrl: String? = null,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>(
    modelTypeInfo,
    createPayloadTypeInfo,
    updatePayloadTypeInfo,
    listTypeInfo,
    controller,
    parentRouter,
    controllerClass,
    route,
    id,
    prefix
) {

    open suspend fun handleExceptionTemplate(
        exception: Exception,
        call: ApplicationCall,
        fromTemplate: String,
    ) {
        when (exception) {
            is ControllerException -> {
                redirectUnauthorizedToUrl?.takeIf {
                    exception.code == HttpStatusCode.Unauthorized && !isUnauthorizedRedirectPath(call)
                }?.let { url ->
                    call.respondRedirect(url.replace("{path}", call.request.uri))
                    return
                }
                call.response.status(exception.code)
                call.respondTemplate(
                    errorTemplate ?: fromTemplate,
                    mapOf(
                        "route" to route,
                        "code" to exception.code.value,
                        "error" to exception.key
                    )
                )
            }

            is PropertyValidatorException -> {
                handleExceptionTemplate(
                    ControllerException(
                        HttpStatusCode.BadRequest, "${route}_${exception.key}_${exception.reason}"
                    ), call, fromTemplate
                )
            }

            else -> throw exception
        }
    }

    open fun isUnauthorizedRedirectPath(call: ApplicationCall): Boolean {
        return redirectUnauthorizedToUrl?.startsWith(call.request.path()) == true
    }

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        val mapping = controllerRoute.function.annotations
            .firstNotNullOfOrNull { it as? TemplateMapping } ?: return
        when (controllerRoute.type) {
            RouteType.list -> root.get(fullRoute) {
                try {
                    call.respondTemplate(
                        mapping.template,
                        mapOf(
                            "route" to route,
                            "items" to controllerRoute(call, this@TemplateChildModelRouter),
                            "keys" to modelKeys
                        )
                    )
                } catch (exception: Exception) {
                    handleExceptionTemplate(exception, call, mapping.template)
                }
            }

            RouteType.get -> root.get("$fullRoute/{$id}") {
                try {
                    call.respondTemplate(
                        mapping.template,
                        mapOf(
                            "route" to route,
                            "item" to controllerRoute(call, this@TemplateChildModelRouter),
                            "keys" to modelKeys
                        )
                    )
                } catch (exception: Exception) {
                    handleExceptionTemplate(exception, call, mapping.template)
                }
            }

            RouteType.create -> {
                root.get("$fullRoute/create") {
                    try {
                        call.respondTemplate(
                            mapping.template,
                            mapOf(
                                "route" to route,
                                "keys" to createPayloadKeys
                            )
                        )
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
                root.post("$fullRoute/create") {
                    try {
                        val payload = ModelAnnotations.constructPayloadFromStringLists(
                            createPayloadTypeInfo.type as KClass<CreatePayload>, call.receiveParameters().toMap()
                        ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                        ModelAnnotations.validatePayload(payload, createPayloadTypeInfo.type as KClass<CreatePayload>)
                        controllerRoute(call, this@TemplateChildModelRouter, mapOf("payload" to payload))
                        call.respondRedirect("../$route")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.update -> {
                root.get("$fullRoute/{$id}/update") {
                    try {
                        call.respondTemplate(
                            mapping.template,
                            mapOf(
                                "route" to route,
                                "item" to get(call),
                                "keys" to updatePayloadKeys
                            )
                        )
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
                root.post("$fullRoute/{$id}/update") {
                    try {
                        val payload = ModelAnnotations.constructPayloadFromStringLists(
                            updatePayloadTypeInfo.type as KClass<UpdatePayload>, call.receiveParameters().toMap()
                        ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                        ModelAnnotations.validatePayload(payload, updatePayloadTypeInfo.type as KClass<UpdatePayload>)
                        controllerRoute(call, this@TemplateChildModelRouter, mapOf("payload" to payload))
                        call.respondRedirect("../../$route")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            RouteType.delete -> {
                root.get("$fullRoute/{$id}/delete") {
                    try {
                        call.respondTemplate(
                            mapping.template,
                            mapOf(
                                "route" to route,
                                "item" to get(call),
                                "keys" to modelKeys
                            )
                        )
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
                root.post("$fullRoute/{$id}/delete") {
                    try {
                        controllerRoute(call, this@TemplateChildModelRouter)
                        call.respondRedirect("../../$route")
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }

            else -> root.route(
                "$fullRoute/${controllerRoute.path}",
                controllerRoute.method ?: HttpMethod.Get
            ) {
                handle {
                    try {
                        call.respondTemplate(
                            mapping.template,
                            mapOf(
                                "route" to route,
                                "item" to controllerRoute(call, this@TemplateChildModelRouter),
                                "keys" to modelKeys
                            )
                        )
                    } catch (exception: Exception) {
                        handleExceptionTemplate(exception, call, mapping.template)
                    }
                }
            }
        }
    }

}
