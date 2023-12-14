package me.nathanfallet.ktorx.repositories.api

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.serialization.json.Json
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.repositories.IChildModelSuspendRepository

open class APIChildModelRemoteRepository<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentId>(
    val modelTypeInfo: TypeInfo,
    val createPayloadTypeInfo: TypeInfo,
    val updatePayloadTypeInfo: TypeInfo,
    val parentRepository: IChildModelSuspendRepository<*, ParentId, *, *, *>?,
    json: Json? = null,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : IChildModelSuspendRepository<Model, Id, CreatePayload, UpdatePayload, ParentId> {

    val route = route ?: (modelTypeInfo.type.simpleName!!.lowercase() + "s")
    val id = id ?: (modelTypeInfo.type.simpleName!!.lowercase() + "Id")
    val prefix = prefix ?: ""

    val listTypeInfo = typeInfo<List<Model>>()

    val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(json ?: Json)
        }
    }

}
