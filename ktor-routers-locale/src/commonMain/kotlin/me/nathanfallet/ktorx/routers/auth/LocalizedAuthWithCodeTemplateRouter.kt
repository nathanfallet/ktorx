package me.nathanfallet.ktorx.routers.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.routers.templates.ILocalizedTemplateRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase

open class LocalizedAuthWithCodeTemplateRouter<LoginPayload : Any, RegisterPayload : Any, RegisterCodePayload : Any>(
    loginPayloadTypeInfo: TypeInfo,
    registerPayloadTypeInfo: TypeInfo,
    registerCodePayloadTypeInfo: TypeInfo,
    authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = "auth",
    prefix: String? = null,
) : AuthWithCodeTemplateRouter<LoginPayload, RegisterPayload, RegisterCodePayload>(
    loginPayloadTypeInfo,
    registerPayloadTypeInfo,
    registerCodePayloadTypeInfo,
    authMapping,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    controller,
    route,
    prefix
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route, openAPI: OpenAPI?) = localizeRoutes(root, openAPI)

    override fun createLocalizedRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
    }

}
