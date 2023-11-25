package me.nathanfallet.ktorx.models.localization

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import me.nathanfallet.i18n.usecases.localization.TranslateUseCase
import me.nathanfallet.ktorx.usecases.localization.GetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase
import java.util.*

/**
 * The configuration for the internationalization feature.
 *
 * [supportedLocales] represents the supported locales for the application, this list is used in the resolution
 * of the current locale. must be initialized and not empty.
 *
 * [defaultLocale]. Used as a fallback when the request locale is not supported. Defaults to the first
 * locale in [supportedLocales].
 *
 * [translateUseCase] instance of ITranslateUseCase used in message resolution. Defaults to
 * [TranslateUseCase].
 *
 * [useOfCookie] Whether to use cookie or not to resolve [Locale]
 * [cookieName] A sensible naming of the cookie
 *
 * [useOfRedirection] Whether to use redirection based on path language prefix
 *
 * [excludePredicates] The list of call predicates for redirect exclusion.
 * Any call matching any of the predicates will not be redirected by this feature.
 *
 * @see MessageResolver
 */
class I18nConfiguration {

    lateinit var supportedLocales: List<Locale>
    var defaultLocale: Locale? = null
    var translateUseCase: ITranslateUseCase = TranslateUseCase()
    var getLocaleForCallUseCase: IGetLocaleForCallUseCase = GetLocaleForCallUseCase()
    var useOfCookie: Boolean = false
    var cookieName: String = "locale"
    var useOfRedirection: Boolean = false
    val excludePredicates: MutableList<(ApplicationCall) -> Boolean> = mutableListOf()

    /**
     * Exclude calls with paths matching the [pathPrefixes] from being redirected with language prefix by this feature.
     */
    fun excludePrefixes(vararg pathPrefixes: String) {
        pathPrefixes.forEach { prefix ->
            exclude { call -> call.request.origin.uri.startsWith(prefix) }
        }
    }

    /**
     * Exclude calls matching the [predicate] from being redirected with language prefix by this feature.
     * @see io.ktor.server.plugins.httpsredirect for example of exclusions
     */
    fun exclude(predicate: (call: ApplicationCall) -> Boolean) {
        excludePredicates.add(predicate)
    }

}
