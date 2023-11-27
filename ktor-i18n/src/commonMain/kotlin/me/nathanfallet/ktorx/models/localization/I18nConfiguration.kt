package me.nathanfallet.ktorx.models.localization

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
 */
class I18nConfiguration {

    lateinit var supportedLocales: List<Locale>
    var defaultLocale: Locale? = null
    var translateUseCase: ITranslateUseCase = TranslateUseCase()
    var getLocaleForCallUseCase: IGetLocaleForCallUseCase = GetLocaleForCallUseCase()
    var useOfCookie: Boolean = false
    var useOfUri: Boolean = false
    var cookieName: String = "locale"

}
