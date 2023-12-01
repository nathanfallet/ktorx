package me.nathanfallet.ktorx.controllers

import io.ktor.server.application.*
import me.nathanfallet.usecases.models.IChildModel

interface IChildModelController<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload, UpdatePayload, ParentModel : IChildModel<ParentId, *, *, *>, ParentId> :
    IController {

    suspend fun list(call: ApplicationCall, parent: ParentModel): List<Model>
    suspend fun get(call: ApplicationCall, parent: ParentModel, id: Id): Model
    suspend fun create(call: ApplicationCall, parent: ParentModel, payload: CreatePayload): Model
    suspend fun update(call: ApplicationCall, parent: ParentModel, id: Id, payload: UpdatePayload): Model
    suspend fun delete(call: ApplicationCall, parent: ParentModel, id: Id)

}
