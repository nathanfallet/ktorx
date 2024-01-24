package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.controllers.base.UnitController
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import kotlin.reflect.KClass

open class LocalizedTemplateUnitRouter(
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    errorTemplate: String? = null,
    redirectUnauthorizedToUrl: String? = null,
    controller: IUnitController = UnitController,
    controllerClass: KClass<out IUnitController> = UnitController::class,
    val getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : TemplateUnitRouter(
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    errorTemplate,
    redirectUnauthorizedToUrl,
    controller,
    controllerClass,
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route, openAPI: OpenAPI?) = localizeRoutes(root, openAPI)

    override fun isUnauthorizedRedirectPath(call: ApplicationCall): Boolean =
        isUnauthorizedRedirectPath(call, redirectUnauthorizedToUrl, getLocaleForCallUseCase)

    override fun createLocalizedRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
    }

}
