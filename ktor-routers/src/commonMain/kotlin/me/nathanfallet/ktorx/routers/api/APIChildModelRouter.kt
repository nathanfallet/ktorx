package me.nathanfallet.ktorx.routers.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.extensions.*
import me.nathanfallet.ktorx.models.annotations.APIMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.models.routes.ControllerRoute
import me.nathanfallet.ktorx.models.routes.RouteType
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.annotations.validators.PropertyValidatorException
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
open class APIChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    modelTypeInfo: TypeInfo,
    createPayloadTypeInfo: TypeInfo,
    updatePayloadTypeInfo: TypeInfo,
    listTypeInfo: TypeInfo,
    controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?,
    controllerClass: KClass<out IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>>,
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
    prefix ?: "/api"
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        createSchema(openAPI)
        super.createRoutes(root, openAPI)
    }

    open fun createSchema(openAPI: OpenAPI?) {
        openAPI?.schema(modelTypeInfo.type)
        openAPI?.schema(createPayloadTypeInfo.type)
        openAPI?.schema(updatePayloadTypeInfo.type)
    }

    open suspend fun handleExceptionAPI(exception: Exception, call: ApplicationCall) {
        when (exception) {
            is ControllerException -> {
                call.response.status(exception.code)
                call.respond(mapOf("error" to exception.key))
            }

            is PropertyValidatorException -> {
                handleExceptionAPI(
                    ControllerException(
                        HttpStatusCode.BadRequest, "${route}_${exception.key}_${exception.reason}"
                    ), call
                )
            }

            is ContentTransformationException -> {
                handleExceptionAPI(
                    ControllerException(
                        HttpStatusCode.BadRequest, "error_body_invalid"
                    ), call
                )
            }

            else -> throw exception
        }
    }

    override suspend fun <Payload : Any> decodePayload(call: ApplicationCall, type: KClass<Payload>): Payload {
        return call.receive(type)
    }

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        controllerRoute.function.annotations.firstNotNullOfOrNull { it as? APIMapping } ?: return

        val path = ("/" + (controllerRoute.path ?: when (controllerRoute.type) {
            RouteType.get, RouteType.update, RouteType.delete -> "{$id}"
            else -> ""
        }).removePrefix("/")).removeSuffix("/")
        val method = controllerRoute.method ?: when (controllerRoute.type) {
            RouteType.create -> HttpMethod.Post
            RouteType.update -> HttpMethod.Put
            RouteType.delete -> HttpMethod.Delete
            else -> HttpMethod.Get
        }

        root.route(fullRoute + path, method) {
            handle {
                try {
                    invokeControllerRoute(call, controllerRoute)
                        ?.takeIf { it != Unit }
                        ?.let {
                            if (controllerRoute.type == RouteType.create) {
                                call.response.status(HttpStatusCode.Created)
                            }
                            call.respond(it)
                        }
                        ?: run {
                            call.respond(HttpStatusCode.NoContent)
                        }
                } catch (exception: Exception) {
                    handleExceptionAPI(exception, call)
                }
            }
        }

        val type = controllerRoute.function.returnType.classifier as KClass<*>
        val description = when (controllerRoute.type) { // TODO: overriding
            RouteType.list -> "Get all ${modelTypeInfo.type.simpleName}"
            RouteType.get -> "Get a ${modelTypeInfo.type.simpleName} by id"
            RouteType.create -> "Create a ${modelTypeInfo.type.simpleName}"
            RouteType.update -> "Update a ${modelTypeInfo.type.simpleName} by id"
            RouteType.delete -> "Delete a ${modelTypeInfo.type.simpleName} by id"
            else -> null
        }

        openAPI?.route(method, fullRoute + path) {
            operationId("${controllerRoute.type.value}${modelTypeInfo.type.simpleName}") // TODO: overriding
            addTagsItem(modelTypeInfo.type.simpleName)
            parameters(getOpenAPIParameters(path.contains("{$id}")))
            description?.let { description(it) }

            when (controllerRoute.type) {
                RouteType.list -> {
                    response("200") {
                        description("List of ${modelTypeInfo.type.simpleName}")
                        mediaType("application/json") {
                            arraySchema(modelTypeInfo.type)
                        }
                    }
                }

                RouteType.get -> {
                    response("200") {
                        description("A ${modelTypeInfo.type.simpleName}")
                        mediaType("application/json") {
                            schema(modelTypeInfo.type)
                        }
                    }
                }

                RouteType.create -> {
                    if (createPayloadTypeInfo.type != Unit::class) {
                        requestBody {
                            mediaType("application/json") {
                                schema(createPayloadTypeInfo.type)
                            }
                        }
                    }
                    response("201") {
                        description("A ${modelTypeInfo.type.simpleName}")
                        mediaType("application/json") {
                            schema(modelTypeInfo.type)
                        }
                    }
                    response("400") {
                        description("Invalid body")
                        mediaType("application/json") {
                            errorSchema("error_body_invalid")
                        }
                    }
                }

                RouteType.update -> {
                    if (updatePayloadTypeInfo.type != Unit::class) {
                        requestBody {
                            mediaType("application/json") {
                                schema(updatePayloadTypeInfo.type)
                            }
                        }
                    }
                    response("200") {
                        description("A ${modelTypeInfo.type.simpleName}")
                        mediaType("application/json") {
                            schema(modelTypeInfo.type)
                        }
                    }
                    response("400") {
                        description("Invalid body")
                        mediaType("application/json") {
                            errorSchema("error_body_invalid")
                        }
                    }
                }

                RouteType.delete -> response("204")
            }
        }
    }

}
