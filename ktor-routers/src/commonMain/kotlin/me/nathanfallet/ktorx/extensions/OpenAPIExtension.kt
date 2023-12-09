package me.nathanfallet.ktorx.extensions

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

fun <Model : Any> OpenAPI.schema(modelClass: KClass<Model>) {
    schema(
        modelClass.qualifiedName,
        Schema<Model>()
            .type("object")
            .properties(modelClass.memberProperties.associate { property ->
                property.name to Schema<Any>()
                    .type(property.returnType.toString())
            })
    )
}

fun OpenAPI.path(path: String, build: PathItem.() -> Unit): OpenAPI = path(
    path, (paths?.get(path) ?: PathItem()).apply(build)
)

fun OpenAPI.get(path: String, build: Operation.() -> Unit) = path(path) {
    get(Operation().apply(build))
}

fun OpenAPI.post(path: String, build: Operation.() -> Unit) = path(path) {
    post(Operation().apply(build))
}

fun OpenAPI.put(path: String, build: Operation.() -> Unit) = path(path) {
    put(Operation().apply(build))
}

fun OpenAPI.patch(path: String, build: Operation.() -> Unit) = path(path) {
    patch(Operation().apply(build))
}

fun OpenAPI.delete(path: String, build: Operation.() -> Unit) = path(path) {
    delete(Operation().apply(build))
}

fun Operation.requestBody(build: RequestBody.() -> Unit): Operation = requestBody(
    (requestBody ?: RequestBody()).apply(build)
)

fun RequestBody.mediaType(name: String, build: MediaType.() -> Unit): RequestBody = content(
    (content ?: Content()).addMediaType(name, MediaType().apply(build))
)

fun Operation.response(name: String, build: ApiResponse.() -> Unit = {}): Operation = responses(
    (responses ?: ApiResponses()).addApiResponse(name, ApiResponse().apply(build))
)

fun ApiResponse.mediaType(name: String, build: MediaType.() -> Unit): ApiResponse = content(
    (content ?: Content()).addMediaType(name, MediaType().apply(build))
)

fun <Model : Any> MediaType.schema(modelClass: KClass<Model>): MediaType = schema(
    Schema<Model>().`$ref`("#/components/schemas/${modelClass.qualifiedName}")
)

fun <Model : Any> MediaType.arraySchema(modelClass: KClass<Model>): MediaType = schema(
    Schema<List<Model>>().type("array")
        .items(Schema<Model>().`$ref`("#/components/schemas/${modelClass.qualifiedName}"))
)
