package me.nathanfallet.ktorx.routers.base

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.annotations.*
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

@Suppress("UNCHECKED_CAST")
abstract class AbstractChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    final override val modelTypeInfo: TypeInfo,
    final override val createPayloadTypeInfo: TypeInfo,
    final override val updatePayloadTypeInfo: TypeInfo,
    final override val listTypeInfo: TypeInfo,
    override val controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    final override val parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?,
    controllerClass: KClass<out IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>>,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : IChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId> {

    // Parameters linked to routing

    final override val route = route ?: (modelTypeInfo.type.simpleName!!.lowercase() + "s")
    final override val id = id ?: (modelTypeInfo.type.simpleName!!.lowercase() + "Id")
    final override val prefix = prefix ?: ""

    final override val routeIncludingParent = (parentRouter?.let {
        val parentRoute = it
            .routeIncludingParent
            .trim('/')
            .takeIf(String::isNotEmpty)
            ?.let { r -> "/$r" } ?: ""
        val parentId = it.id.takeIf(String::isNotEmpty)?.let { i -> "/{$i}" } ?: ""
        parentRoute + parentId
    } ?: "") + "/" + this.route

    val fullRoute = this.prefix + routeIncludingParent

    // Keys for model

    val modelKeys = ModelAnnotations.modelKeys(modelTypeInfo.type as KClass<Model>)
    val createPayloadKeys = ModelAnnotations.createPayloadKeys(
        modelTypeInfo.type as KClass<Model>,
        createPayloadTypeInfo.type as KClass<CreatePayload>
    )
    val updatePayloadKeys = ModelAnnotations.updatePayloadKeys(
        modelTypeInfo.type as KClass<Model>,
        updatePayloadTypeInfo.type as KClass<UpdatePayload>
    )

    // Route calculation

    val controllerRoutes = controllerClass.memberFunctions.mapNotNull {
        val typeAnnotation = it.annotations.mapNotNull { annotation ->
            if (annotation.annotationClass.simpleName?.endsWith("Path") == true) Triple(
                RouteType(annotation.annotationClass.simpleName!!.removeSuffix("Path").lowercase()),
                annotation.annotationClass.members.firstOrNull { parameter -> parameter.name == "path" }
                    ?.call(annotation) as? String ?: "",
                annotation.annotationClass.members.firstOrNull { parameter -> parameter.name == "method" }
                    ?.call(annotation) as? String
            ) else null
        }.singleOrNull() ?: return@mapNotNull null
        ControllerRoute(
            typeAnnotation.first,
            typeAnnotation.second,
            typeAnnotation.third?.let { method -> HttpMethod.parse(method.uppercase()) },
            it
        )
    }

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        controllerRoutes.forEach { createControllerRoute(root, it, openAPI) }
    }

    abstract fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?)

    // Default operations

    override suspend fun get(call: ApplicationCall): Model {
        return controllerRoutes.singleOrNull { it.type == RouteType.get }?.invoke(call, this) as Model
    }

    override fun getOpenAPIParameters(self: Boolean): List<Parameter> {
        return parentRouter?.getOpenAPIParameters().orEmpty() + if (self) listOf(
            Parameter()
                .name(id)
                .schema(Schema<Id>().type(modelTypeInfo.type.memberProperties.first { it.name == "id" }.returnType.toString()))
                .`in`("path")
                .description("Id of the ${modelTypeInfo.type.simpleName}")
                .required(true)
        ) else emptyList()
    }

}
