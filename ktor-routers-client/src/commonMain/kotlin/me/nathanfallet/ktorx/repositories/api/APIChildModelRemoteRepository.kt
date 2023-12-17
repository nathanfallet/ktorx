package me.nathanfallet.ktorx.repositories.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.id.RecursiveId

open class APIChildModelRemoteRepository<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentId>(
    final override val modelTypeInfo: TypeInfo,
    final override val createPayloadTypeInfo: TypeInfo,
    final override val updatePayloadTypeInfo: TypeInfo,
    final override val listTypeInfo: TypeInfo,
    val client: IAPIClient,
    val parentRepository: IAPIChildModelRemoteRepository<*, ParentId, *, *, *>?,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : IAPIChildModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload, ParentId> {

    override val route = route ?: (modelTypeInfo.type.simpleName!!.lowercase() + "s")
    override val id = id ?: (modelTypeInfo.type.simpleName!!.lowercase() + "Id")
    override val prefix = prefix ?: "/api"

    open fun constructFullRoute(parentId: RecursiveId<*, ParentId, *>): String {
        return this.prefix + (parentRepository?.let {
            val parentRoute = it.route.takeIf(String::isNotEmpty)?.let { r -> "/$r" } ?: ""
            val parentIdString = it.id.takeIf(String::isNotEmpty)?.let { "/${parentId.id}" } ?: ""
            parentRoute + parentIdString
        } ?: "") + "/" + this.route
    }

    override suspend fun list(parentId: RecursiveId<*, ParentId, *>, context: IContext?): List<Model> {
        return client.request(HttpMethod.Get, constructFullRoute(parentId)).body(listTypeInfo)
    }

    override suspend fun list(
        limit: Long,
        offset: Long,
        parentId: RecursiveId<*, ParentId, *>,
        context: IContext?,
    ): List<Model> {
        return client.request(HttpMethod.Get, constructFullRoute(parentId)) {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body(listTypeInfo)
    }

    override suspend fun get(id: Id, parentId: RecursiveId<*, ParentId, *>, context: IContext?): Model? {
        return client.request(HttpMethod.Get, "${constructFullRoute(parentId)}/$id").body(modelTypeInfo)
    }

    override suspend fun create(
        payload: CreatePayload,
        parentId: RecursiveId<*, ParentId, *>,
        context: IContext?,
    ): Model? {
        return client.request(HttpMethod.Post, constructFullRoute(parentId)) {
            contentType(ContentType.Application.Json)
            setBody(payload, createPayloadTypeInfo)
        }.body(modelTypeInfo)
    }

    override suspend fun update(
        id: Id,
        payload: UpdatePayload,
        parentId: RecursiveId<*, ParentId, *>,
        context: IContext?,
    ): Model? {
        return client.request(HttpMethod.Put, "${constructFullRoute(parentId)}/$id") {
            contentType(ContentType.Application.Json)
            setBody(payload, updatePayloadTypeInfo)
        }.body(modelTypeInfo)
    }

    override suspend fun delete(id: Id, parentId: RecursiveId<*, ParentId, *>, context: IContext?): Boolean {
        client.request(HttpMethod.Delete, "${constructFullRoute(parentId)}/$id")
        return true
    }

}
