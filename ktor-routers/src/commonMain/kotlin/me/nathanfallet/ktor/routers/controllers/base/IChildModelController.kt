package me.nathanfallet.ktor.routers.controllers.base

import io.ktor.server.application.*
import me.nathanfallet.ktor.routers.controllers.IController
import me.nathanfallet.usecases.models.IChildModel

interface IChildModelController<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload, UpdatePayload, ParentModel : IChildModel<ParentId, *, *, *>, ParentId> :
    IController {

    suspend fun getAll(call: ApplicationCall, parent: ParentModel): List<Model>
    suspend fun get(call: ApplicationCall, parent: ParentModel, id: Id): Model
    suspend fun create(call: ApplicationCall, parent: ParentModel, payload: CreatePayload): Model
    suspend fun update(call: ApplicationCall, parent: ParentModel, id: Id, payload: UpdatePayload): Model
    suspend fun delete(call: ApplicationCall, parent: ParentModel, id: Id)

}