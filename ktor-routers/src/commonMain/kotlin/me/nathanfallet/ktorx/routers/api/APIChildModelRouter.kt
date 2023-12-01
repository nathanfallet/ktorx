package me.nathanfallet.ktorx.routers.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import kotlin.reflect.KClass

open class APIChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?,
    val mapping: APIMapping = APIMapping(),
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
    prefix ?: "/api"
) {

    override fun createRoutes(root: Route) {
        createAPIGetRoute(root)
        createAPIGetIdRoute(root)
        createAPIPostRoute(root)
        createAPIPutIdRoute(root)
        createAPIDeleteIdRoute(root)
    }

    open suspend fun handleExceptionAPI(exception: ControllerException, call: ApplicationCall) {
        call.response.status(exception.code)
        call.respond(mapOf("error" to exception.key))
    }

    open fun createAPIGetRoute(root: Route) {
        if (!mapping.listEnabled) return
        root.get(fullRoute) {
            try {
                call.respond(getAll(call), listTypeInfo)
            } catch (exception: ControllerException) {
                handleExceptionAPI(exception, call)
            }
        }
    }

    open fun createAPIGetIdRoute(root: Route) {
        if (!mapping.getEnabled) return
        root.get("$fullRoute/{$id}") {
            try {
                call.respond(get(call), modelTypeInfo)
            } catch (exception: ControllerException) {
                handleExceptionAPI(exception, call)
            }
        }
    }

    open fun createAPIPostRoute(root: Route) {
        if (!mapping.createEnabled) return
        root.post(fullRoute) {
            try {
                val response = create(call, call.receive(createPayloadTypeInfo))
                call.response.status(HttpStatusCode.Created)
                call.respond(response, modelTypeInfo)
            } catch (exception: ControllerException) {
                handleExceptionAPI(exception, call)
            } catch (exception: ContentTransformationException) {
                handleExceptionAPI(
                    ControllerException(
                        HttpStatusCode.BadRequest, "error_body_invalid"
                    ), call
                )
            }
        }
    }

    open fun createAPIPutIdRoute(root: Route) {
        if (!mapping.updateEnabled) return
        root.put("$fullRoute/{$id}") {
            try {
                call.respond(
                    update(call, call.receive(updatePayloadTypeInfo)),
                    modelTypeInfo
                )
            } catch (exception: ControllerException) {
                handleExceptionAPI(exception, call)
            } catch (exception: ContentTransformationException) {
                handleExceptionAPI(
                    ControllerException(
                        HttpStatusCode.BadRequest, "error_body_invalid"
                    ), call
                )
            }
        }
    }

    open fun createAPIDeleteIdRoute(root: Route) {
        if (!mapping.deleteEnabled) return
        root.delete("$fullRoute/{$id}") {
            try {
                delete(call)
                call.respond(HttpStatusCode.NoContent)
            } catch (exception: ControllerException) {
                handleExceptionAPI(exception, call)
            }
        }
    }

}
