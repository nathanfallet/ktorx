package me.nathanfallet.ktorx.routers.admin

import io.ktor.server.application.*
import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.controllers.base.UnitController
import me.nathanfallet.ktorx.routers.IUnitRouter
import me.nathanfallet.usecases.models.UnitModel
import kotlin.reflect.KClass

open class AdminUnitRouter(
    controller: IUnitController = UnitController,
    controllerClass: KClass<out IUnitController> = UnitController::class,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    errorTemplate: String? = null,
    redirectUnauthorizedToUrl: String? = null,
    route: String? = null,
    prefix: String? = null,
) : AdminModelRouter<UnitModel, Unit, Unit, Unit>(
    typeInfo<UnitModel>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    controllerClass,
    respondTemplate,
    errorTemplate,
    redirectUnauthorizedToUrl,
    route = route ?: "",
    prefix = prefix
), IUnitRouter
