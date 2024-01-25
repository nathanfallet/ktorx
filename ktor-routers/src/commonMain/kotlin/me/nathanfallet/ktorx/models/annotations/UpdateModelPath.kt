package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class UpdateModelPath(
    val path: String = "",
)
