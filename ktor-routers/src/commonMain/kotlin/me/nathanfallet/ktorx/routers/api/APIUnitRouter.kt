package me.nathanfallet.ktorx.routers.api

import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.controllers.base.UnitController
import me.nathanfallet.usecases.models.UnitModel

open class APIUnitRouter(
    controller: IUnitController = UnitController,
    route: String? = null,
    prefix: String? = null,
) : APIModelRouter<UnitModel, Unit, Unit, Unit>(
    typeInfo<UnitModel>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    route = route,
    prefix = prefix
)
