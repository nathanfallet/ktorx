package me.nathanfallet.ktorx.routers.api

import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.controllers.base.UnitController
import me.nathanfallet.ktorx.routers.IUnitRouter
import me.nathanfallet.usecases.models.UnitModel
import kotlin.reflect.KClass

open class APIUnitRouter(
    controller: IUnitController = UnitController,
    controllerClass: KClass<out IUnitController>,
    route: String? = null,
    prefix: String? = null,
) : APIModelRouter<UnitModel, Unit, Unit, Unit>(
    typeInfo<UnitModel>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    controllerClass,
    route = route,
    prefix = prefix
), IUnitRouter
