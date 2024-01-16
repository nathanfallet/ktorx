package me.nathanfallet.ktorx.routers.base

import io.ktor.server.application.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@Suppress("UNCHECKED_CAST")
abstract class AbstractChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    final override val modelTypeInfo: TypeInfo,
    final override val createPayloadTypeInfo: TypeInfo,
    final override val updatePayloadTypeInfo: TypeInfo,
    final override val listTypeInfo: TypeInfo,
    override val controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    final override val parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : IChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId> {

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

    val modelKeys = ModelAnnotations.modelKeys(modelTypeInfo.type as KClass<Model>)
    val createPayloadKeys = ModelAnnotations.createPayloadKeys(
        modelTypeInfo.type as KClass<Model>,
        createPayloadTypeInfo.type as KClass<CreatePayload>
    )
    val updatePayloadKeys = ModelAnnotations.updatePayloadKeys(
        modelTypeInfo.type as KClass<Model>,
        updatePayloadTypeInfo.type as KClass<UpdatePayload>
    )

    override suspend fun get(call: ApplicationCall): Model {
        return controller.get(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            ModelAnnotations.constructIdFromString(modelTypeInfo.type as KClass<Model>, call.parameters[id]!!)
        )
    }

    open suspend fun getAll(call: ApplicationCall): List<Model> {
        return controller.list(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel
        )
    }

    open suspend fun create(call: ApplicationCall, payload: CreatePayload): Model {
        return controller.create(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            payload
        )
    }

    open suspend fun update(call: ApplicationCall, payload: UpdatePayload): Model {
        return controller.update(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            ModelAnnotations.constructIdFromString(modelTypeInfo.type as KClass<Model>, call.parameters[id]!!),
            payload
        )
    }

    open suspend fun delete(call: ApplicationCall) {
        return controller.delete(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            ModelAnnotations.constructIdFromString(modelTypeInfo.type as KClass<Model>, call.parameters[id]!!)
        )
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
