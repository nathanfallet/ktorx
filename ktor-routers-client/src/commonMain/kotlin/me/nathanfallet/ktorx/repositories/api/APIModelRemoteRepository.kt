package me.nathanfallet.ktorx.repositories.api

import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId

open class APIModelRemoteRepository<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    modelTypeInfo: TypeInfo,
    createPayloadTypeInfo: TypeInfo,
    updatePayloadTypeInfo: TypeInfo,
    listTypeInfo: TypeInfo,
    client: IAPIClient,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : APIChildModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload, Unit>(
    modelTypeInfo,
    createPayloadTypeInfo,
    updatePayloadTypeInfo,
    listTypeInfo,
    client,
    null,
    route,
    id,
    prefix,
), IAPIModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload> {

    override suspend fun list(parentId: RecursiveId<*, Unit, *>, context: IContext?): List<Model> {
        return super<APIChildModelRemoteRepository>.list(parentId, context)
    }

    override suspend fun list(context: IContext?): List<Model> {
        return list(RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun list(
        limit: Long,
        offset: Long,
        parentId: RecursiveId<*, Unit, *>,
        context: IContext?,
    ): List<Model> {
        return super<APIChildModelRemoteRepository>.list(limit, offset, parentId, context)
    }

    override suspend fun list(limit: Long, offset: Long, context: IContext?): List<Model> {
        return list(limit, offset, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun get(id: Id, parentId: RecursiveId<*, Unit, *>, context: IContext?): Model? {
        return super<APIChildModelRemoteRepository>.get(id, parentId, context)
    }

    override suspend fun get(id: Id, context: IContext?): Model? {
        return get(id, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun create(payload: CreatePayload, parentId: RecursiveId<*, Unit, *>, context: IContext?): Model? {
        return super<APIChildModelRemoteRepository>.create(payload, parentId, context)
    }

    override suspend fun create(payload: CreatePayload, context: IContext?): Model? {
        return create(payload, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun delete(id: Id, parentId: RecursiveId<*, Unit, *>, context: IContext?): Boolean {
        return super<APIChildModelRemoteRepository>.delete(id, parentId, context)
    }

    override suspend fun delete(id: Id, context: IContext?): Boolean {
        return delete(id, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

    override suspend fun update(
        id: Id,
        payload: UpdatePayload,
        parentId: RecursiveId<*, Unit, *>,
        context: IContext?,
    ): Model? {
        return super<APIChildModelRemoteRepository>.update(id, payload, parentId, context)
    }

    override suspend fun update(id: Id, payload: UpdatePayload, context: IContext?): Model? {
        return update(id, payload, RecursiveId<UnitModel, Unit, Unit>(Unit), context)
    }

}
