package me.nathanfallet.ktor.routers.routers.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nathanfallet.ktor.routers.controllers.base.IChildModelController
import me.nathanfallet.ktor.routers.models.exceptions.ControllerException
import me.nathanfallet.ktor.routers.routers.base.AbstractChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import kotlin.reflect.KClass

open class APIChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    parentRouter: APIChildModelRouter<ParentModel, *, *, *, *, *>?,
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

    private suspend fun handleExceptionAPI(exception: ControllerException, call: ApplicationCall) {
        call.response.status(exception.code)
        call.respond(
            mapOf(
                "error" to /*translateUseCase(call.locale, */exception.key//)
            )
        )
    }

    fun createAPIGetRoute(root: Route) {
        root.get(fullRoute) {
            try {
                call.respond(getAll(call), listTypeInfo)
            } catch (exception: ControllerException) {
                handleExceptionAPI(exception, call)
            }
        }
    }

    fun createAPIGetIdRoute(root: Route) {
        root.get("$fullRoute/{$id}") {
            try {
                call.respond(get(call), modelTypeInfo)
            } catch (exception: ControllerException) {
                handleExceptionAPI(exception, call)
            }
        }
    }

    fun createAPIPostRoute(root: Route) {
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

    fun createAPIPutIdRoute(root: Route) {
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

    fun createAPIDeleteIdRoute(root: Route) {
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
