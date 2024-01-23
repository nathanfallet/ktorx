package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class RegisterCodePath(
    val path: String = "",
)
