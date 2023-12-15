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
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
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
    val mapping: TemplateMapping,
    val respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
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
    route,
    id,
    prefix
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        createTemplateGetRoute(root)
        createTemplateGetCreateRoute(root)
        createTemplatePostCreateRoute(root)
        createTemplateGetIdRoute(root)
        createTemplateGetIdUpdateRoute(root)
        createTemplatePostIdUpdateRoute(root)
        createTemplateGetIdDeleteRoute(root)
        createTemplatePostIdDeleteRoute(root)
    }

    open suspend fun handleExceptionTemplate(
        exception: Exception,
        call: ApplicationCall,
        fromTemplate: String,
    ) {
        when (exception) {
            is ControllerException -> {
                mapping.redirectUnauthorizedToUrl?.takeIf {
                    exception.code == HttpStatusCode.Unauthorized && !it.startsWith(call.request.path())
                }?.let { url ->
                    call.respondRedirect(url.replace("{path}", call.request.uri))
                    return
                }
                call.response.status(exception.code)
                call.respondTemplate(
                    mapping.errorTemplate ?: fromTemplate,
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

    open fun createTemplateGetRoute(root: Route) {
        mapping.listTemplate ?: return
        root.get(fullRoute) {
            try {
                call.respondTemplate(
                    mapping.listTemplate,
                    mapOf(
                        "route" to route,
                        "items" to getAll(call),
                        "keys" to modelKeys
                    )
                )
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.listTemplate)
            }
        }
    }

    open fun createTemplateGetCreateRoute(root: Route) {
        mapping.createTemplate ?: return
        root.get("$fullRoute/create") {
            try {
                call.respondTemplate(
                    mapping.createTemplate,
                    mapOf(
                        "route" to route,
                        "keys" to createPayloadKeys
                    )
                )
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.createTemplate)
            }
        }
    }

    open fun createTemplatePostCreateRoute(root: Route) {
        mapping.createTemplate ?: return
        root.post("$fullRoute/create") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    createPayloadTypeInfo.type as KClass<CreatePayload>, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(payload, createPayloadTypeInfo.type as KClass<CreatePayload>)
                create(call, payload)
                call.respondRedirect("../$route")
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.createTemplate)
            }
        }
    }

    open fun createTemplateGetIdRoute(root: Route) {
        mapping.getTemplate ?: return
        root.get("$fullRoute/{$id}") {
            try {
                call.respondTemplate(
                    mapping.getTemplate,
                    mapOf(
                        "route" to route,
                        "item" to get(call),
                        "keys" to modelKeys
                    )
                )
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.getTemplate)
            }
        }
    }

    open fun createTemplateGetIdUpdateRoute(root: Route) {
        mapping.updateTemplate ?: return
        root.get("$fullRoute/{$id}/update") {
            try {
                call.respondTemplate(
                    mapping.updateTemplate,
                    mapOf(
                        "route" to route,
                        "item" to get(call),
                        "keys" to updatePayloadKeys
                    )
                )
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.updateTemplate)
            }
        }
    }

    open fun createTemplatePostIdUpdateRoute(root: Route) {
        mapping.updateTemplate ?: return
        root.post("$fullRoute/{$id}/update") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    updatePayloadTypeInfo.type as KClass<UpdatePayload>, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                ModelAnnotations.validatePayload(payload, updatePayloadTypeInfo.type as KClass<UpdatePayload>)
                update(call, payload)
                call.respondRedirect("../../$route")
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.updateTemplate)
            }
        }
    }

    open fun createTemplateGetIdDeleteRoute(root: Route) {
        mapping.deleteTemplate ?: return
        root.get("$fullRoute/{$id}/delete") {
            try {
                call.respondTemplate(
                    mapping.deleteTemplate,
                    mapOf(
                        "route" to route,
                        "item" to get(call),
                        "keys" to modelKeys
                    )
                )
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.deleteTemplate)
            }
        }
    }

    open fun createTemplatePostIdDeleteRoute(root: Route) {
        mapping.deleteTemplate ?: return
        root.post("$fullRoute/{$id}/delete") {
            try {
                delete(call)
                call.respondRedirect("../../$route")
            } catch (exception: Exception) {
                handleExceptionTemplate(exception, call, mapping.deleteTemplate)
            }
        }
    }

}
