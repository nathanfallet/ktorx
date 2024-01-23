package me.nathanfallet.ktorx.models.templates

import kotlinx.serialization.Serializable
import me.nathanfallet.ktorx.models.TestUser

@Serializable
data class TemplateResponseData<Model, Keys>(
    val route: String,
    val keys: List<Keys>? = null,
    val item: Model? = null,
    val itemString: String? = null,
    val itemMap: Map<String, String>? = null,
    val itemModel: TestUser? = null,
    val items: List<Model>? = null,
    val code: Int? = null,
    val error: String? = null,
)
