package me.nathanfallet.ktorx.routers.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.routers.templates.ILocalizedTemplateRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import kotlin.reflect.KClass

open class LocalizedAuthTemplateRouter<LoginPayload : Any, RegisterPayload : Any>(
    loginPayloadTypeInfo: TypeInfo,
    registerPayloadTypeInfo: TypeInfo,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
    errorTemplate: String? = null,
    redirectUnauthorizedToUrl: String? = null,
    redirectTemplate: String? = null,
    controller: IAuthController<LoginPayload, RegisterPayload>,
    controllerClass: KClass<out IAuthController<*, *>>,
    val getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = "auth",
    prefix: String? = null,
) : AuthTemplateRouter<LoginPayload, RegisterPayload>(
    loginPayloadTypeInfo,
    registerPayloadTypeInfo,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    errorTemplate,
    redirectUnauthorizedToUrl,
    redirectTemplate,
    controller,
    controllerClass,
    route,
    prefix
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route, openAPI: OpenAPI?) = localizeRoutes(root, openAPI)

    override fun isUnauthorizedRedirectPath(call: ApplicationCall): Boolean =
        isUnauthorizedRedirectPath(call, redirectUnauthorizedToUrl, getLocaleForCallUseCase)

    override fun createLocalizedRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
    }

}
