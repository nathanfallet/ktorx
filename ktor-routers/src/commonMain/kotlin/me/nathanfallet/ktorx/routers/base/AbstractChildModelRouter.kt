package me.nathanfallet.ktorx.routers.base

import io.ktor.server.application.*
import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.controllers.base.IChildModelController
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.full.starProjectedType

abstract class AbstractChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    final override val modelClass: KClass<Model>,
    final override val createPayloadClass: KClass<CreatePayload>,
    final override val updatePayloadClass: KClass<UpdatePayload>,
    final override val controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    final override val parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?,
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : IChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId> {

    final override val route = route ?: (modelClass.simpleName!!.lowercase() + "s")
    final override val id = id ?: (modelClass.simpleName!!.lowercase() + "Id")
    final override val prefix = prefix ?: ""

    val fullRoute = this.prefix + (parentRouter?.let {
        val parentRoute = it.route.takeIf(String::isNotEmpty)?.let { r -> "/$r" } ?: ""
        val parentId = it.id.takeIf(String::isNotEmpty)?.let { i -> "/{$i}" } ?: ""
        parentRoute + parentId
    } ?: "") + "/" + this.route

    val modelTypeInfo = TypeInfo(
        modelClass, modelClass.java,
        modelClass.starProjectedType
    )
    val createPayloadTypeInfo = TypeInfo(
        createPayloadClass, createPayloadClass.java,
        createPayloadClass.starProjectedType
    )
    val updatePayloadTypeInfo = TypeInfo(
        updatePayloadClass, updatePayloadClass.java,
        updatePayloadClass.starProjectedType
    )
    val listTypeInfo = TypeInfo(
        List::class, List::class.java,
        List::class.createType(
            listOf(KTypeProjection(KVariance.INVARIANT, modelClass.starProjectedType))
        )
    )

    val modelKeys = ModelAnnotations.modelKeys(modelClass)
    val updatePayloadKeys = ModelAnnotations.updatePayloadKeys(modelClass, updatePayloadClass)
    val createPayloadKeys = ModelAnnotations.createPayloadKeys(modelClass, createPayloadClass)

    @Suppress("UNCHECKED_CAST")
    override suspend fun get(call: ApplicationCall): Model {
        return controller.get(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            ModelAnnotations.constructIdFromString(modelClass, call.parameters[id]!!)
        )
    }

    @Suppress("UNCHECKED_CAST")
    open suspend fun getAll(call: ApplicationCall): List<Model> {
        return controller.getAll(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel
        )
    }

    @Suppress("UNCHECKED_CAST")
    open suspend fun create(call: ApplicationCall, payload: CreatePayload): Model {
        return controller.create(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            payload
        )
    }

    @Suppress("UNCHECKED_CAST")
    open suspend fun update(call: ApplicationCall, payload: UpdatePayload): Model {
        return controller.update(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            ModelAnnotations.constructIdFromString(modelClass, call.parameters[id]!!),
            payload
        )
    }

    @Suppress("UNCHECKED_CAST")
    open suspend fun delete(call: ApplicationCall) {
        return controller.delete(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            ModelAnnotations.constructIdFromString(modelClass, call.parameters[id]!!)
        )
    }

}
