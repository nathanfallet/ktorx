package me.nathanfallet.ktorx.usecases.localization

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.*
import me.nathanfallet.ktorx.plugins.i18n
import me.nathanfallet.usecases.localization.Locale

class GetLocaleForCallUseCase : IGetLocaleForCallUseCase {

    private val localeKey = AttributeKey<Locale>("ktor-i18n-locale")

    override fun invoke(input: ApplicationCall): Locale {
        return input.attributes.computeIfAbsent(localeKey) locale@{
            val i18n = input.application.i18n

            fun writeCookie(locale: Locale) {
                input.response.cookies.append(Cookie(i18n.localeCookieName, locale.language, maxAge = 60 * 60))
            }

            fun readCookie(): String? {
                return input.request.cookies[i18n.localeCookieName]
            }

            if (i18n.useOfRedirection) {
                val uri = input.request.origin.uri.trimStart('/').trimEnd('/').split('/')
                val languagePrefix = uri.first()
                if (languagePrefix.matches(i18n.supportedPathPrefixes)) {
                    val locale = java.util.Locale.forLanguageTag(languagePrefix)
                    if (i18n.useOfCookie && languagePrefix != readCookie()) {
                        writeCookie(locale)
                    }
                    return@locale locale
                }
            }

            if (i18n.useOfCookie) {
                val cookieLocale = readCookie()
                if (cookieLocale != null) {
                    return@locale java.util.Locale.forLanguageTag(cookieLocale)
                }
            }

            val acceptLocales = input.request.acceptLanguage()
            val ranges = if (acceptLocales != null) java.util.Locale.LanguageRange.parse(acceptLocales) else listOf()
            val locale = java.util.Locale.filter(ranges, i18n.supportedLocales).firstOrNull()
                ?: java.util.Locale.lookup(ranges, i18n.supportedLocales)
                ?: i18n.defaultLocale

            if (i18n.useOfCookie) {
                writeCookie(locale)
            }

            return@locale locale
        }
    }

}
