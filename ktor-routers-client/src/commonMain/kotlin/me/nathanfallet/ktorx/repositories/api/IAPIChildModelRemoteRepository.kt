package me.nathanfallet.ktorx.repositories.api

import io.ktor.util.reflect.*
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.repositories.remote.IChildModelRemoteRepository

interface IAPIChildModelRemoteRepository<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentId> :
    IChildModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload, ParentId> {

    val modelTypeInfo: TypeInfo
    val createPayloadTypeInfo: TypeInfo
    val updatePayloadTypeInfo: TypeInfo
    val listTypeInfo: TypeInfo

    val route: String
    val id: String
    val prefix: String

}
