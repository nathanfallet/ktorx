package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase

open class LocalizedTemplateUnitRouter(
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : TemplateUnitRouter(
    mapping,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route) = localizeRoutes(root)

    override fun createLocalizedRoutes(root: Route) {
        super.createRoutes(root)
    }

}
