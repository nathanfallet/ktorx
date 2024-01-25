package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class DeleteModelPath(
    val path: String = "",
)
