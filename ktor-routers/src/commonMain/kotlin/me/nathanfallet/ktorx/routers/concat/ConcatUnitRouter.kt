package me.nathanfallet.ktorx.routers.concat

import me.nathanfallet.ktorx.routers.IUnitRouter
import me.nathanfallet.usecases.models.UnitModel

open class ConcatUnitRouter(
    vararg routers: IUnitRouter,
) : ConcatModelRouter<UnitModel, Unit, Unit, Unit>(*routers), IUnitRouter
