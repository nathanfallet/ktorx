package me.nathanfallet.ktorx.routers.concat

import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.routers.IUnitRouter
import me.nathanfallet.usecases.models.UnitModel
import kotlin.reflect.KClass

open class ConcatUnitRouter(
    routers: List<IUnitRouter>,
    controllerClass: KClass<out IUnitController>,
) : ConcatModelRouter<UnitModel, Unit, Unit, Unit>(routers, controllerClass)
