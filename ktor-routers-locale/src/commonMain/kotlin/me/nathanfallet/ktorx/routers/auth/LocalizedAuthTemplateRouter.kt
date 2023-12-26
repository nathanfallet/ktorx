package me.nathanfallet.ktorx.routers.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.routers.templates.ILocalizedTemplateRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase

open class LocalizedAuthTemplateRouter<LoginPayload : Any, RegisterPayload : Any>(
    loginPayloadTypeInfo: TypeInfo,
    registerPayloadTypeInfo: TypeInfo,
    authMapping: AuthMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    controller: IAuthController<LoginPayload, RegisterPayload>,
    val getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = "auth",
    prefix: String? = null,
) : AuthTemplateRouter<LoginPayload, RegisterPayload>(
    loginPayloadTypeInfo,
    registerPayloadTypeInfo,
    authMapping,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    controller,
    route,
    prefix
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route, openAPI: OpenAPI?) = localizeRoutes(root, openAPI)

    override fun isUnauthorizedRedirectPath(call: ApplicationCall): Boolean =
        isUnauthorizedRedirectPath(call, mapping, getLocaleForCallUseCase)

    override fun createLocalizedRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
    }

}
