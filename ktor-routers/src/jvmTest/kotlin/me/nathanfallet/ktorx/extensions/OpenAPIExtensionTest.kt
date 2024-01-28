package me.nathanfallet.ktorx.extensions

import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.models.TestModel
import me.nathanfallet.ktorx.models.TestRecursiveModel
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenAPIExtensionTest {

    @Test
    fun testOpenAPISchemaList() {
        val openAPI = OpenAPI()
        val actual = openAPI.schema(typeOf<List<String>>())
        assertEquals(String::class.qualifiedName, actual.items.type)
    }

    @Test
    fun testOpenAPISchemaMap() {
        val openAPI = OpenAPI()
        val actual = openAPI.schema(typeOf<Map<String, String>>())
        assertEquals("object", actual.type)
        assertEquals(null, actual.properties)
    }

    @Test
    fun testOpenAPISchemaTestModel() {
        val openAPI = OpenAPI()
        val actual = openAPI.schema(typeOf<TestModel>())
        assertEquals(TestModel::class.qualifiedName, actual.type)
        val schema = openAPI.components.schemas["me.nathanfallet.ktorx.models.TestModel"]
        assertEquals("object", schema?.type)
        assertEquals(2, schema?.properties?.size)
        assertEquals("ID", schema?.properties?.get("id")?.description)
        assertEquals("123", schema?.properties?.get("id")?.example)
        assertEquals("String", schema?.properties?.get("string")?.description)
        assertEquals("abc", schema?.properties?.get("string")?.example)
        assertEquals(listOf("id", "string"), schema?.required)
    }

    @Test
    fun testOpenAPIRecursiveModel() {
        val openAPI = OpenAPI()
        val actual = openAPI.schema(typeOf<TestRecursiveModel>())
        assertEquals(TestRecursiveModel::class.qualifiedName, actual.type)
        val schema = openAPI.components.schemas["me.nathanfallet.ktorx.models.TestRecursiveModel"]
        assertEquals("object", schema?.type)
        assertEquals(2, schema?.properties?.size)
        assertEquals("Name", schema?.properties?.get("name")?.description)
        assertEquals("abc", schema?.properties?.get("name")?.example)
        assertEquals("Children", schema?.properties?.get("children")?.description)
        assertEquals("[]", schema?.properties?.get("children")?.example)
        assertEquals(
            "#/components/schemas/me.nathanfallet.ktorx.models.TestRecursiveModel",
            schema?.properties?.get("children")?.items?.`$ref`
        )
    }

}
