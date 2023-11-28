package me.nathanfallet.ktorx.controllers.base

import io.ktor.server.application.*
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel

interface IModelController<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload, UpdatePayload> :
    IChildModelController<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit> {

    suspend fun list(call: ApplicationCall): List<Model>
    suspend fun get(call: ApplicationCall, id: Id): Model
    suspend fun create(call: ApplicationCall, payload: CreatePayload): Model
    suspend fun update(call: ApplicationCall, id: Id, payload: UpdatePayload): Model
    suspend fun delete(call: ApplicationCall, id: Id)

    override suspend fun list(call: ApplicationCall, parent: UnitModel): List<Model> {
        return list(call)
    }

    override suspend fun get(call: ApplicationCall, parent: UnitModel, id: Id): Model {
        return get(call, id)
    }

    override suspend fun create(call: ApplicationCall, parent: UnitModel, payload: CreatePayload): Model {
        return create(call, payload)
    }

    override suspend fun update(call: ApplicationCall, parent: UnitModel, id: Id, payload: UpdatePayload): Model {
        return update(call, id, payload)
    }

    override suspend fun delete(call: ApplicationCall, parent: UnitModel, id: Id) {
        return delete(call, id)
    }

}
