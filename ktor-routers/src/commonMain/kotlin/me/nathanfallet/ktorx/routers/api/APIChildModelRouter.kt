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
import me.nathanfallet.ktorx.models.annotations.Payload
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

            is PropertyValidatorException -> handleExceptionAPI(
                ControllerException(HttpStatusCode.BadRequest, "${route}_${exception.key}_${exception.reason}"), call
            )

            is ContentTransformationException -> handleExceptionAPI(
                ControllerException(HttpStatusCode.BadRequest, "error_body_invalid"), call
            )

            else -> throw exception
        }
    }

    override suspend fun <Payload : Any> decodePayload(call: ApplicationCall, type: KClass<Payload>): Payload {
        return call.receive(type)
    }

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        val apiMapping = controllerRoute.function.annotations.firstNotNullOfOrNull { it as? APIMapping } ?: return

        // Calculate route (path and method)
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

        // Route handling
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

        // API docs
        openAPI?.route(method, fullRoute + path) {
            val type = controllerRoute.function.returnType

            // General metadata
            operationId(
                apiMapping.operationId.takeIf { it.isNotEmpty() }
                    ?: "${controllerRoute.type.value}${modelTypeInfo.type.simpleName}"
            )
            addTagsItem(modelTypeInfo.type.simpleName)
            parameters(getOpenAPIParameters(path.contains("{$id}")))
            (apiMapping.description.takeIf { it.isNotEmpty() } ?: when (controllerRoute.type) {
                RouteType.list -> "Get all ${modelTypeInfo.type.simpleName}"
                RouteType.get -> "Get a ${modelTypeInfo.type.simpleName} by id"
                RouteType.create -> "Create a ${modelTypeInfo.type.simpleName}"
                RouteType.update -> "Update a ${modelTypeInfo.type.simpleName} by id"
                RouteType.delete -> "Delete a ${modelTypeInfo.type.simpleName} by id"
                else -> null
            })?.let { description(it) }

            // Body and response linked to payload
            controllerRoute.function.parameters.singleOrNull {
                it.annotations.any { annotation -> annotation is Payload }
            }?.let {
                requestBody {
                    mediaType("application/json") {
                        schema(it.type)
                    }
                }
                response("400") {
                    description("Invalid body")
                    mediaType("application/json") {
                        errorSchema("error_body_invalid")
                    }
                }
            }

            // Default response (direct return type of the function)
            response(
                if (type == Unit::class) "204"
                else if (controllerRoute.type == RouteType.create) "201"
                else "200"
            ) {
                if (type != Unit::class) {
                    description(type)
                    mediaType("application/json") {
                        schema(type)
                    }
                }
            }
        }
    }

}
