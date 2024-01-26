package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class AdminTemplateMapping(
    val template: String = "",
)
