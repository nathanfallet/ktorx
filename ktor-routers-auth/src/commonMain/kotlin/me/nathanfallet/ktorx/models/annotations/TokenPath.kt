package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class TokenPath(
    val path: String = "",
)
