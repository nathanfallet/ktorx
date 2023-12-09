package me.nathanfallet.ktorx.routers.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.extensions.*
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import me.nathanfallet.usecases.models.annotations.validators.PropertyValidatorException
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
    prefix: String? = null,
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

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        createSchema(openAPI)
        createAPIGetRoute(root, openAPI)
        createAPIGetIdRoute(root, openAPI)
        createAPIPostRoute(root, openAPI)
        createAPIPutIdRoute(root, openAPI)
        createAPIDeleteIdRoute(root, openAPI)
    }

    open fun createSchema(openAPI: OpenAPI?) {
        openAPI?.schema(modelClass)
        openAPI?.schema(createPayloadClass)
        openAPI?.schema(updatePayloadClass)
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

    open fun createAPIGetRoute(root: Route, openAPI: OpenAPI?) {
        if (!mapping.listEnabled) return
        root.get(fullRoute) {
            try {
                call.respond(getAll(call), listTypeInfo)
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.get(fullRoute) {
            operationId("list${modelClass.simpleName}")
            addTagsItem(modelClass.simpleName)
            description("Get all ${modelClass.simpleName}")
            parameters(getOpenAPIParameters(false))
            response("200") {
                description("List of ${modelClass.simpleName}")
                mediaType("application/json") {
                    arraySchema(modelClass)
                }
            }
        }
    }

    open fun createAPIGetIdRoute(root: Route, openAPI: OpenAPI?) {
        if (!mapping.getEnabled) return
        root.get("$fullRoute/{$id}") {
            try {
                call.respond(get(call), modelTypeInfo)
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.get("$fullRoute/{$id}") {
            operationId("get${modelClass.simpleName}ById")
            addTagsItem(modelClass.simpleName)
            description("Get a ${modelClass.simpleName} by id")
            parameters(getOpenAPIParameters())
            response("200") {
                description("A ${modelClass.simpleName}")
                mediaType("application/json") {
                    schema(modelClass)
                }
            }
        }
    }

    open fun createAPIPostRoute(root: Route, openAPI: OpenAPI?) {
        if (!mapping.createEnabled) return
        root.post(fullRoute) {
            try {
                val payload: CreatePayload = call.receive(createPayloadTypeInfo)
                ModelAnnotations.validatePayload(payload, createPayloadClass)
                val response = create(call, payload)
                call.response.status(HttpStatusCode.Created)
                call.respond(response, modelTypeInfo)
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.post(fullRoute) {
            operationId("create${modelClass.simpleName}")
            addTagsItem(modelClass.simpleName)
            description("Create a ${modelClass.simpleName}")
            parameters(getOpenAPIParameters(false))
            requestBody {
                mediaType("application/json") {
                    schema(createPayloadClass)
                }
            }
            response("201") {
                description("A ${modelClass.simpleName}")
                mediaType("application/json") {
                    schema(modelClass)
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

    open fun createAPIPutIdRoute(root: Route, openAPI: OpenAPI?) {
        if (!mapping.updateEnabled) return
        root.put("$fullRoute/{$id}") {
            try {
                val payload: UpdatePayload = call.receive(updatePayloadTypeInfo)
                ModelAnnotations.validatePayload(payload, updatePayloadClass)
                call.respond(
                    update(call, payload),
                    modelTypeInfo
                )
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.put("$fullRoute/{$id}") {
            operationId("update${modelClass.simpleName}ById")
            addTagsItem(modelClass.simpleName)
            description("Update a ${modelClass.simpleName} by id")
            parameters(getOpenAPIParameters())
            requestBody {
                mediaType("application/json") {
                    schema(updatePayloadClass)
                }
            }
            response("200") {
                description("A ${modelClass.simpleName}")
                mediaType("application/json") {
                    schema(modelClass)
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

    open fun createAPIDeleteIdRoute(root: Route, openAPI: OpenAPI?) {
        if (!mapping.deleteEnabled) return
        root.delete("$fullRoute/{$id}") {
            try {
                delete(call)
                call.respond(HttpStatusCode.NoContent)
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.delete("$fullRoute/{$id}") {
            operationId("delete${modelClass.simpleName}ById")
            addTagsItem(modelClass.simpleName)
            description("Delete a ${modelClass.simpleName} by id")
            parameters(getOpenAPIParameters())
            response("204")
        }
    }

}
