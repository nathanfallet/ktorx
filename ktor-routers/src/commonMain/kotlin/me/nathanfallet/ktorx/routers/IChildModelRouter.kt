package me.nathanfallet.ktorx.routers

import io.ktor.server.application.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.parameters.Parameter
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.usecases.models.IChildModel

interface IChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId> :
    IRouter {

    val modelTypeInfo: TypeInfo
    val createPayloadTypeInfo: TypeInfo
    val updatePayloadTypeInfo: TypeInfo
    val listTypeInfo: TypeInfo

    val controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>
    val parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?

    val route: String
    val id: String
    val prefix: String

    suspend fun get(call: ApplicationCall): Model

    fun getOpenAPIParameters(self: Boolean = true): List<Parameter>

}
