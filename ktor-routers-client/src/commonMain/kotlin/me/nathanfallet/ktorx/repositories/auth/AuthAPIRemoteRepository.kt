package me.nathanfallet.ktorx.repositories.auth

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.ktorx.repositories.api.APIUnitRemoteRepository
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId

open class AuthAPIRemoteRepository(
    client: IAPIClient,
    route: String? = "auth",
    prefix: String? = null,
) : APIUnitRemoteRepository(
    client,
    route,
    prefix
), IAuthAPIRemoteRepository {

    override suspend fun token(
        payload: AuthRequest,
    ): AuthToken? = client.request(
        HttpMethod.Post,
        "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/token"
    ) {
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.body()

}
