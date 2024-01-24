package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class RegisterPath(
    val path: String = "",
)
