package me.nathanfallet.ktorx.models.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
annotation class DocumentedType(
    val type: KClass<*>,
)
