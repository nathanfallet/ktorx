package me.nathanfallet.ktorx.models.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ParentModel(
    val id: String,
)
