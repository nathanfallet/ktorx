package me.nathanfallet.ktorx.routers.concat

import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.usecases.models.UnitModel

open class ConcatUnitRouter(
    routers: List<IChildModelRouter<UnitModel, Unit, Unit, Unit, UnitModel, Unit>>,
) : ConcatModelRouter<UnitModel, Unit, Unit, Unit>(
    routers
)
