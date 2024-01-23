package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class RegisterCodeRedirectPath(
    val path: String = "",
)
