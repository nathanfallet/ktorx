package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.controllers.base.UnitController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.usecases.models.UnitModel

open class TemplateUnitRouter(
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    controller: IUnitController = UnitController,
    route: String? = null,
    prefix: String? = null,
) : TemplateModelRouter<UnitModel, Unit, Unit, Unit>(
    typeInfo<UnitModel>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<UnitModel>>(),
    controller,
    mapping,
    respondTemplate,
    route = route,
    prefix = prefix
)
