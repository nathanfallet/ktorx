package me.nathanfallet.ktorx.plugins

import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import me.nathanfallet.ktorx.models.localization.I18nConfiguration
import me.nathanfallet.usecases.localization.ITranslateUseCase
import java.util.*

/**
 * Internationalization support feature for Ktor.
 *
 * @see Configuration for how to configure this feature
 */
class I18n private constructor(configuration: I18nConfiguration) : ITranslateUseCase by configuration.translateUseCase {

    val supportedLocales = configuration.supportedLocales
    val defaultLocale = configuration.defaultLocale ?: supportedLocales.first()
    val getLocaleForCallUseCase = configuration.getLocaleForCallUseCase
    val useOfCookie = configuration.useOfCookie
    val localeCookieName = configuration.cookieName
    val useOfRedirection = configuration.useOfRedirection
    val supportedPathPrefixes = "(${supportedLocales.joinToString("|", transform = { it.language })})".toRegex()
    val excludePredicates: List<(ApplicationCall) -> Boolean> = configuration.excludePredicates.toList()

    init {
        Locale.setDefault(defaultLocale)
    }

    private suspend fun intercept(ctx: PipelineContext<Unit, ApplicationCall>) {
        val call = ctx.context
        val language = getLocaleForCallUseCase(call).language

        val uri = call.request.origin.uri.trimStart('/').trimEnd('/').split('/')
        if (!uri.first().matches(supportedPathPrefixes) &&
            excludePredicates.none { predicate -> predicate(call) }
        ) {
            val toRedirect = mutableListOf<String>(
                language,
                *uri.toTypedArray()
            ).joinToString("/", prefix = "/").trimEnd('/')
            call.respondRedirect(toRedirect)
            ctx.finish()
        }
    }

    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, I18nConfiguration, I18n> {

        override val key = AttributeKey<I18n>("I18n")

        override fun install(pipeline: ApplicationCallPipeline, configure: I18nConfiguration.() -> Unit): I18n {
            val configuration = I18nConfiguration().apply { configure() }.apply {
                require(supportedLocales.isNotEmpty()) { "Supported locales must not be empty" }
                require(defaultLocale == null || defaultLocale!! in supportedLocales) {
                    "Default locale must be one of the supported locales"
                }
            }

            val plugin = I18n(configuration)

            if (configuration.useOfRedirection) {
                pipeline.intercept(Plugins) { plugin.intercept(this) }
            }

            return plugin
        }
    }
}

val Application.i18n
    get() = this.plugin(I18n)
