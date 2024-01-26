package me.nathanfallet.ktorx.models.admin

@Target(AnnotationTarget.FUNCTION)
annotation class AdminTemplateMapping(
    val template: String = "",
)
