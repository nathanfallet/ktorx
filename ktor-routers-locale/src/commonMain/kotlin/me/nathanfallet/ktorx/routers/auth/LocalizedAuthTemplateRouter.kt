package me.nathanfallet.ktorx.routers.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.routers.templates.ILocalizedTemplateRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import kotlin.reflect.KClass

open class LocalizedAuthTemplateRouter<LoginPayload : Any, RegisterPayload : Any>(
    loginPayloadClass: KClass<LoginPayload>,
    registerPayloadClass: KClass<RegisterPayload>,
    authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    controller: IAuthController<LoginPayload, RegisterPayload>,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = "auth",
    prefix: String? = null
) : AuthTemplateRouter<LoginPayload, RegisterPayload>(
    loginPayloadClass,
    registerPayloadClass,
    authMapping,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    controller,
    route,
    prefix
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route) = localizeRoutes(root)

    override fun createLocalizedRoutes(root: Route) {
        super.createRoutes(root)
    }

}
