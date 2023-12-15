package me.nathanfallet.ktorx.repositories.api

import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId
import me.nathanfallet.usecases.models.repositories.remote.IModelRemoteRepository

abstract class AbstractAPIModelRemoteRepository<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    modelTypeInfo: TypeInfo,
    createPayloadTypeInfo: TypeInfo,
    updatePayloadTypeInfo: TypeInfo,
    client: IAPIClient,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : AbstractAPIChildModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload, Unit>(
    modelTypeInfo,
    createPayloadTypeInfo,
    updatePayloadTypeInfo,
    client,
    null,
    route,
    id,
    prefix,
), IModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload> {

    override suspend fun get(id: Id, parentId: RecursiveId<*, Unit, *>, context: IContext?): Model? {
        return super<AbstractAPIChildModelRemoteRepository>.get(id, parentId, context)
    }

    override suspend fun get(id: Id, context: IContext?): Model? {
        return get(id, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun create(payload: CreatePayload, parentId: RecursiveId<*, Unit, *>, context: IContext?): Model? {
        return super<AbstractAPIChildModelRemoteRepository>.create(payload, parentId, context)
    }

    override suspend fun create(payload: CreatePayload, context: IContext?): Model? {
        return create(payload, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun delete(id: Id, parentId: RecursiveId<*, Unit, *>, context: IContext?): Boolean {
        return super<AbstractAPIChildModelRemoteRepository>.delete(id, parentId, context)
    }

    override suspend fun delete(id: Id, context: IContext?): Boolean {
        return delete(id, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun update(
        id: Id,
        payload: UpdatePayload,
        parentId: RecursiveId<*, Unit, *>,
        context: IContext?,
    ): Boolean {
        return super<AbstractAPIChildModelRemoteRepository>.update(id, payload, parentId, context)
    }

    override suspend fun update(id: Id, payload: UpdatePayload, context: IContext?): Boolean {
        return update(id, payload, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

}
