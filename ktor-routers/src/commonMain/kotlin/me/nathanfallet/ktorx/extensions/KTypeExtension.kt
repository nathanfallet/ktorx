package me.nathanfallet.ktorx.extensions

import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

val KType.underlyingType: KType?
    get() = if (isSubtypeOf(typeOf<List<*>>())) arguments.firstOrNull()?.type else this
