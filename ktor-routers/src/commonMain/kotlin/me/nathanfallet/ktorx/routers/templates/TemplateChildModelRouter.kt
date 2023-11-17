package me.nathanfallet.ktorx.routers.templates

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import me.nathanfallet.ktorx.controllers.base.IChildModelController
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass

open class TemplateChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    parentRouter: IChildModelRouter<ParentModel, ParentId, *, *, *, *>?,
    val mapping: TemplateMapping,
    val respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>(
    modelClass,
    createPayloadClass,
    updatePayloadClass,
    controller,
    parentRouter,
    route,
    id,
    prefix
) {

    override fun createRoutes(root: Route) {
        createTemplateGetRoute(root)
        createTemplateGetCreateRoute(root)
        createTemplatePostCreateRoute(root)
        createTemplateGetIdRoute(root)
        createTemplateGetIdUpdateRoute(root)
        createTemplatePostIdUpdateRoute(root)
        createTemplateGetIdDeleteRoute(root)
        createTemplatePostIdDeleteRoute(root)
    }

    open suspend fun handleExceptionTemplate(exception: ControllerException, call: ApplicationCall) {
        mapping.redirectUnauthorizedToUrl?.takeIf { exception.code == HttpStatusCode.Unauthorized }?.let { url ->
            call.respondRedirect(url.replace("{path}", call.request.path()))
            return
        }
        call.response.status(exception.code)
        call.respondTemplate(
            mapping.errorTemplate,
            mapOf(
                "route" to route,
                "code" to exception.code.value,
                "error" to exception.key
            )
        )
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
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
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
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
            }
        }
    }

    open fun createTemplatePostCreateRoute(root: Route) {
        mapping.createTemplate ?: return
        root.post("$fullRoute/create") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    createPayloadClass, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                create(call, payload)
                call.respondRedirect("../$route")
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
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
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
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
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
            }
        }
    }

    open fun createTemplatePostIdUpdateRoute(root: Route) {
        mapping.updateTemplate ?: return
        root.post("$fullRoute/{$id}/update") {
            try {
                val payload = ModelAnnotations.constructPayloadFromStringLists(
                    updatePayloadClass, call.receiveParameters().toMap()
                ) ?: throw ControllerException(HttpStatusCode.BadRequest, "error_body_invalid")
                update(call, payload)
                call.respondRedirect("../../$route")
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
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
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
            }
        }
    }

    open fun createTemplatePostIdDeleteRoute(root: Route) {
        mapping.deleteTemplate ?: return
        root.post("$fullRoute/{$id}/delete") {
            try {
                delete(call)
                call.respondRedirect("../../$route")
            } catch (exception: ControllerException) {
                handleExceptionTemplate(exception, call)
            }
        }
    }

}
