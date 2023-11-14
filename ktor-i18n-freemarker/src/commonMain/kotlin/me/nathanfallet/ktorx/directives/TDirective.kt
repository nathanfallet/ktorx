package me.nathanfallet.ktorx.directives

import com.github.aymanizz.ktori18n.MessageResolver
import com.github.aymanizz.ktori18n.R
import freemarker.core.Environment
import freemarker.template.*
import java.util.*

class TDirective(
    private val i18n: MessageResolver
) : TemplateDirectiveModel {

    override fun execute(
        env: Environment?,
        params: MutableMap<Any?, Any?>?,
        loopVars: Array<out TemplateModel>?,
        body: TemplateDirectiveBody?
    ) {
        val locale = env?.getVariable("locale") as? TemplateScalarModel
            ?: params?.get("locale") as? TemplateScalarModel
            ?: throw TemplateModelException("Missing locale parameter")
        val key = params?.get("key") as? TemplateScalarModel
            ?: throw TemplateModelException("Missing key parameter")
        val args = (params["args"] as? TemplateSequenceModel)?.let {
            (0..<it.size()).map { i -> it[i].toString() }
        } ?: emptyList()

        env?.out?.write(
            i18n.t(
                Locale.forLanguageTag(locale.asString),
                R(key.asString),
                *args.toTypedArray()
            )
        )
    }

}
