package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class APIMapping(
    val operationId: String = "",
    val description: String = "",
)
