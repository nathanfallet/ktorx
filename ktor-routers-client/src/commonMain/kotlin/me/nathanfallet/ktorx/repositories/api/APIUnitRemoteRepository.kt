package me.nathanfallet.ktorx.repositories.api

import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.usecases.models.UnitModel

open class APIUnitRemoteRepository(
    client: IAPIClient,
    route: String? = null,
    prefix: String? = null,
) : APIModelRemoteRepository<UnitModel, Unit, Unit, Unit>(
    typeInfo<UnitModel>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<UnitModel>>(),
    client,
    route = route,
    prefix = prefix
)
