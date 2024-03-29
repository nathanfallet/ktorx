package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.plugins.I18n
import me.nathanfallet.ktorx.plugins.LocalizedRouteInterceptor
import me.nathanfallet.ktorx.plugins.LocalizedRouteSelector
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase

interface ILocalizedTemplateRouter {

    companion object {

        fun wrapRespondTemplate(
            respondTemplate: suspend ApplicationCall.(String, Map<String, Any?>) -> Unit,
            getLocaleForCallUseCase: IGetLocaleForCallUseCase,
        ): suspend ApplicationCall.(String, Map<String, Any?>) -> Unit {
            return { template, model ->
                respondTemplate(template, model + mapOf("locale" to getLocaleForCallUseCase(this)))
            }
        }

    }

    fun localizeRoutes(root: Route, openAPI: OpenAPI? = null) {
        val localizedRoutes = root.createChild(LocalizedRouteSelector())
        localizedRoutes.install(LocalizedRouteInterceptor)

        createLocalizedRoutes(localizedRoutes, openAPI)
        localizedRoutes.plugin(I18n).supportedLocales.forEach {
            localizedRoutes.route("/$it") {
                createLocalizedRoutes(this)
            }
        }
    }

    fun isUnauthorizedRedirectPath(
        call: ApplicationCall,
        redirectUnauthorizedToUrl: String?,
        getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    ): Boolean {
        return redirectUnauthorizedToUrl?.let {
            "/${getLocaleForCallUseCase(call)}$it"
        }?.startsWith(call.request.path()) == true
    }

    fun createLocalizedRoutes(root: Route, openAPI: OpenAPI? = null)

}
