package me.nathanfallet.ktorx.directives

import freemarker.core.Environment
import freemarker.template.*
import me.nathanfallet.usecases.localization.ITranslateUseCase
import java.util.*

class TDirective(
    private val translateUseCase: ITranslateUseCase
) : TemplateDirectiveModel {

    override fun execute(
        env: Environment?,
        params: MutableMap<Any?, Any?>?,
        loopVars: Array<out TemplateModel>?,
        body: TemplateDirectiveBody?
    ) {
        val locale = params?.get("locale") as? TemplateScalarModel
            ?: env?.getVariable("locale") as? TemplateScalarModel
            ?: throw TemplateModelException("Missing locale parameter")
        val key = params?.get("key") as? TemplateScalarModel
            ?: throw TemplateModelException("Missing key parameter")
        val args = (params["args"] as? TemplateSequenceModel)?.let {
            (0..<it.size()).map { i -> it[i].toString() }
        } ?: emptyList()

        env?.out?.write(
            translateUseCase(
                Locale.forLanguageTag(locale.asString),
                key.asString,
                args
            )
        )
    }

}
