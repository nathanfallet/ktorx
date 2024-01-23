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
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.ktorx.routers.base.ControllerRoute
import me.nathanfallet.ktorx.routers.base.RouteType
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
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

    open suspend fun <Payload : Any> decodeAndValidatePayload(call: ApplicationCall, typeInfo: TypeInfo): Payload {
        if (typeInfo.type == Unit::class) return Unit as Payload
        val payload: Payload = call.receive(typeInfo)
        ModelAnnotations.validatePayload(payload, typeInfo.type as KClass<Payload>)
        return payload
    }

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        controllerRoute.function.annotations
            .firstNotNullOfOrNull { it as? APIMapping } ?: return
        when (controllerRoute.type) {
            RouteType.list -> {
                root.get(fullRoute) {
                    try {
                        call.respond(controllerRoute(call, this@APIChildModelRouter), listTypeInfo)
                    } catch (exception: Exception) {
                        handleExceptionAPI(exception, call)
                    }
                }
                openAPI?.get(fullRoute) {
                    operationId("list${modelTypeInfo.type.simpleName}")
                    addTagsItem(modelTypeInfo.type.simpleName)
                    description("Get all ${modelTypeInfo.type.simpleName}")
                    parameters(getOpenAPIParameters(false))
                    response("200") {
                        description("List of ${modelTypeInfo.type.simpleName}")
                        mediaType("application/json") {
                            arraySchema(modelTypeInfo.type)
                        }
                    }
                }
            }

            RouteType.get -> {
                root.get("$fullRoute/{$id}") {
                    try {
                        call.respond(controllerRoute(call, this@APIChildModelRouter), modelTypeInfo)
                    } catch (exception: Exception) {
                        handleExceptionAPI(exception, call)
                    }
                }
                openAPI?.get("$fullRoute/{$id}") {
                    operationId("get${modelTypeInfo.type.simpleName}ById")
                    addTagsItem(modelTypeInfo.type.simpleName)
                    description("Get a ${modelTypeInfo.type.simpleName} by id")
                    parameters(getOpenAPIParameters())
                    response("200") {
                        description("A ${modelTypeInfo.type.simpleName}")
                        mediaType("application/json") {
                            schema(modelTypeInfo.type)
                        }
                    }
                }
            }

            RouteType.create -> {
                root.post(fullRoute) {
                    try {
                        val payload = decodeAndValidatePayload<CreatePayload>(call, createPayloadTypeInfo)
                        val response = controllerRoute(call, this@APIChildModelRouter, mapOf("payload" to payload))
                        call.response.status(HttpStatusCode.Created)
                        call.respond(response, modelTypeInfo)
                    } catch (exception: Exception) {
                        handleExceptionAPI(exception, call)
                    }
                }
                openAPI?.post(fullRoute) {
                    operationId("create${modelTypeInfo.type.simpleName}")
                    addTagsItem(modelTypeInfo.type.simpleName)
                    description("Create a ${modelTypeInfo.type.simpleName}")
                    parameters(getOpenAPIParameters(false))
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
            }

            RouteType.update -> {
                root.put("$fullRoute/{$id}") {
                    try {
                        val payload = decodeAndValidatePayload<UpdatePayload>(call, updatePayloadTypeInfo)
                        call.respond(
                            controllerRoute(call, this@APIChildModelRouter, mapOf("payload" to payload)),
                            modelTypeInfo
                        )
                    } catch (exception: Exception) {
                        handleExceptionAPI(exception, call)
                    }
                }
                openAPI?.put("$fullRoute/{$id}") {
                    operationId("update${modelTypeInfo.type.simpleName}ById")
                    addTagsItem(modelTypeInfo.type.simpleName)
                    description("Update a ${modelTypeInfo.type.simpleName} by id")
                    parameters(getOpenAPIParameters())
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
            }

            RouteType.delete -> {
                root.delete("$fullRoute/{$id}") {
                    try {
                        controllerRoute(call, this@APIChildModelRouter)
                        call.respond(HttpStatusCode.NoContent)
                    } catch (exception: Exception) {
                        handleExceptionAPI(exception, call)
                    }
                }
                openAPI?.delete("$fullRoute/{$id}") {
                    operationId("delete${modelTypeInfo.type.simpleName}ById")
                    addTagsItem(modelTypeInfo.type.simpleName)
                    description("Delete a ${modelTypeInfo.type.simpleName} by id")
                    parameters(getOpenAPIParameters())
                    response("204")
                }
            }

            else -> root.route(
                "$fullRoute/${controllerRoute.path}",
                controllerRoute.method ?: HttpMethod.Get
            ) {
                handle {
                    try {
                        controllerRoute(call, this@APIChildModelRouter)
                            ?.takeIf { it != Unit }
                            ?.let {
                                call.respond(it)
                            }
                    } catch (exception: Exception) {
                        handleExceptionAPI(exception, call)
                    }
                }
            }
        }
    }

}
