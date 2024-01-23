package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class AuthorizeRedirectPath(
    val path: String = "",
)
