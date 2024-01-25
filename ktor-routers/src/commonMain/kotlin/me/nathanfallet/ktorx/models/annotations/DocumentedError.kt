package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class DocumentedError(
    val code: Int,
    val key: String,
    val description: String = "",
)
