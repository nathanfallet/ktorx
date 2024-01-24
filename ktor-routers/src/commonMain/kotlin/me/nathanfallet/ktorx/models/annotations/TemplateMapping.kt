package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class TemplateMapping(
    val template: String,
)
