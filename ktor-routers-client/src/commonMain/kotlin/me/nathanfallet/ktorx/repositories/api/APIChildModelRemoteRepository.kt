package me.nathanfallet.ktorx.repositories.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.id.RecursiveId
import me.nathanfallet.usecases.pagination.IPaginationOptions
import me.nathanfallet.usecases.pagination.Pagination

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

    override fun constructRouteIncludingParent(parentId: Any?): String =
        (parentRepository?.let {
            if (parentId !is RecursiveId<*, *, *>) return@let null
            val parentRoute = it
                .constructRouteIncludingParent(parentId.parentId)
                .trim('/')
                .takeIf(String::isNotEmpty)
                ?.let { r -> "/$r" } ?: ""
            val parentIdString = it.id.takeIf(String::isNotEmpty)?.let { "/${parentId.id}" } ?: ""
            parentRoute + parentIdString
        } ?: "") + "/" + this.route

    open fun constructFullRoute(parentId: RecursiveId<*, ParentId, *>): String =
        this.prefix + constructRouteIncludingParent(parentId)

    override suspend fun list(parentId: RecursiveId<*, ParentId, *>, context: IContext?): List<Model> =
        client.request(HttpMethod.Get, constructFullRoute(parentId)).body(listTypeInfo)

    override suspend fun list(
        pagination: Pagination,
        parentId: RecursiveId<*, ParentId, *>,
        context: IContext?,
    ): List<Model> = client.request(HttpMethod.Get, constructFullRoute(parentId)) {
        parameter("limit", pagination.limit)
        parameter("offset", pagination.offset)
        pagination.options?.let { encodePaginationOptions(it, this) }
    }.body(listTypeInfo)

    open fun encodePaginationOptions(
        options: IPaginationOptions,
        builder: HttpRequestBuilder,
    ) {
        // Do nothing by default, override to implement custom behavior
    }

    override suspend fun get(id: Id, parentId: RecursiveId<*, ParentId, *>, context: IContext?): Model? =
        client.request(HttpMethod.Get, "${constructFullRoute(parentId)}/$id").body(modelTypeInfo)

    override suspend fun create(
        payload: CreatePayload,
        parentId: RecursiveId<*, ParentId, *>,
        context: IContext?,
    ): Model? = client.request(HttpMethod.Post, constructFullRoute(parentId)) {
        contentType(ContentType.Application.Json)
        setBody(payload, createPayloadTypeInfo)
    }.body(modelTypeInfo)

    override suspend fun update(
        id: Id,
        payload: UpdatePayload,
        parentId: RecursiveId<*, ParentId, *>,
        context: IContext?,
    ): Model? = client.request(HttpMethod.Put, "${constructFullRoute(parentId)}/$id") {
        contentType(ContentType.Application.Json)
        setBody(payload, updatePayloadTypeInfo)
    }.body(modelTypeInfo)

    override suspend fun delete(id: Id, parentId: RecursiveId<*, ParentId, *>, context: IContext?): Boolean =
        client.request(HttpMethod.Delete, "${constructFullRoute(parentId)}/$id").let { true }

}
