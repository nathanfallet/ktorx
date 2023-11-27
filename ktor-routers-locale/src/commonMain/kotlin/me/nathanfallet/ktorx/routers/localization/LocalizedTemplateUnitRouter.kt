package me.nathanfallet.ktorx.routers.localization

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.base.UnitModelController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.usecases.models.UnitModel

open class LocalizedTemplateUnitRouter(
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
) : LocalizedTemplateModelRouter<UnitModel, Unit, Unit, Unit>(
    UnitModel::class,
    Unit::class,
    Unit::class,
    UnitModelController,
    mapping,
    respondTemplate
)
