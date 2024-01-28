package me.nathanfallet.ktorx.extensions

import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

val KType.isList: Boolean
    get() = isSubtypeOf(typeOf<List<*>>()) || isSubtypeOf(typeOf<Array<*>>())

val KType.underlyingType: KType?
    get() = if (isList) arguments.firstOrNull()?.type else this
