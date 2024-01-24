package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class AuthorizePath(
    val path: String = "",
)
