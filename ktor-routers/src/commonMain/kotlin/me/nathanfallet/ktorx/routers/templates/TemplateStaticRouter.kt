package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.base.UnitModelController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.usecases.models.UnitModel

open class TemplateStaticRouter(
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
) : TemplateModelRouter<UnitModel, Unit, Unit, Unit>(
    UnitModel::class,
    Unit::class,
    Unit::class,
    UnitModelController,
    mapping,
    respondTemplate
)
