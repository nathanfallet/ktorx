package me.nathanfallet.ktorx.routers.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.routers.templates.ILocalizedTemplateRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import kotlin.reflect.KClass

open class LocalizedAuthWithCodeTemplateRouter<LoginPayload : Any, RegisterPayload : Any, RegisterCodePayload : Any>(
    loginPayloadClass: KClass<LoginPayload>,
    registerPayloadClass: KClass<RegisterPayload>,
    registerCodePayloadClass: KClass<RegisterCodePayload>,
    authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = "auth",
    prefix: String? = null
) : AuthWithCodeTemplateRouter<LoginPayload, RegisterPayload, RegisterCodePayload>(
    loginPayloadClass,
    registerPayloadClass,
    registerCodePayloadClass,
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
