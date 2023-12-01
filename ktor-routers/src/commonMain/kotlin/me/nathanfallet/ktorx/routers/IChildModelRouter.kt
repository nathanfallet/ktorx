package me.nathanfallet.ktorx.routers

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.usecases.models.IChildModel
import kotlin.reflect.KClass

interface IChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId> :
    IRouter {

    val modelClass: KClass<Model>
    val createPayloadClass: KClass<CreatePayload>
    val updatePayloadClass: KClass<UpdatePayload>

    val controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>
    val parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?

    val route: String
    val id: String
    val prefix: String

    suspend fun get(call: ApplicationCall): Model

}
