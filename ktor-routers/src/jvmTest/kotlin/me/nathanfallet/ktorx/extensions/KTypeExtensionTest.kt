package me.nathanfallet.ktorx.extensions

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class KTypeExtensionTest {

    @Test
    fun returnsActualType() {
        assertEquals(typeOf<String>(), typeOf<String>().underlyingType)
    }

    @Test
    fun returnsTypeFromList() {
        assertEquals(typeOf<String>(), typeOf<List<String>>().underlyingType)
    }

}
