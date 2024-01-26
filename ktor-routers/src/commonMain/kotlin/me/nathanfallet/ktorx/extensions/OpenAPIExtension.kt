package me.nathanfallet.ktorx.extensions

import io.ktor.http.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf

fun OpenAPI.info(build: Info.() -> Unit): OpenAPI = info(
    (info ?: Info()).apply(build)
)

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun OpenAPI.schema(type: KType): Schema<Any> {
    if (type.isSubtypeOf(typeOf<List<*>>())) return Schema<List<*>>().type("array").items(
        schema(type.arguments.firstOrNull()?.type ?: typeOf<Any>())
    )
    if (type.isSubtypeOf(typeOf<Map<*, *>>())) return Schema<Map<*, *>>().type("object")
    if (components?.schemas?.containsKey(type.toString()) != true) {
        val klass = type.classifier as KClass<*>
        val properties = klass.serializer().descriptor.elementNames.associateWith { name ->
            klass.memberProperties.first { it.name == name }
        }
        schema(
            type.toString(),
            Schema<Any>()
                .type("object")
                .properties(properties.mapValues {
                    (if (it.value.returnType.underlyingType == type) Schema<Any>().`$ref`("#/components/schemas/$type")
                    else schema(it.value.returnType)).apply {
                        it.value.annotations.firstNotNullOfOrNull { annotation ->
                            annotation as? me.nathanfallet.usecases.models.annotations.Schema
                        }?.let { annotation ->
                            description = annotation.name
                            example = annotation.example
                        }
                    }
                })
                .required(properties.filter {
                    it.value.returnType.isMarkedNullable.not()
                }.keys.toList())
        )
    }
    return Schema<Any>().`$ref`("#/components/schemas/$type")
}

fun OpenAPI.path(path: String, build: PathItem.() -> Unit): OpenAPI = path(
    path, (paths?.get(path) ?: PathItem()).apply(build)
)

fun OpenAPI.route(method: HttpMethod, path: String, build: Operation.() -> Unit) = path(path) {
    javaClass.methods.firstOrNull { it.name == method.value.lowercase() }?.invoke(this, Operation().apply(build))
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

fun ApiResponse.description(type: KType): ApiResponse = description(
    if (type.isSubtypeOf(typeOf<List<*>>())) "List of ${type.arguments.firstOrNull()?.type ?: typeOf<Any>()}"
    else "A $type"
)

fun MediaType.schema(type: KType): MediaType = schema(
    if (type.isSubtypeOf(typeOf<List<*>>())) Schema<List<*>>().type("array").items(
        Schema<Any>().`$ref`("#/components/schemas/${type.arguments.firstOrNull()?.type ?: typeOf<Any>()}")
    ) else Schema<Any>().`$ref`("#/components/schemas/$type")
)

fun MediaType.errorSchema(key: String): MediaType = schema(
    Schema<Map<String, String>>().type("object").properties(
        mapOf(
            "error" to Schema<String>().type("string").example(key)
        )
    )
)
