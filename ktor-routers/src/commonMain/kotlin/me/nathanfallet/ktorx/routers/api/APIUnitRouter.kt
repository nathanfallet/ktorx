package me.nathanfallet.ktorx.routers.api

import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.controllers.base.UnitController
import me.nathanfallet.usecases.models.UnitModel

open class APIUnitRouter(
    controller: IUnitController = UnitController,
    route: String? = null,
    prefix: String? = null,
) : APIModelRouter<UnitModel, Unit, Unit, Unit>(
    UnitModel::class,
    Unit::class,
    Unit::class,
    controller,
    route = route,
    prefix = prefix
)
