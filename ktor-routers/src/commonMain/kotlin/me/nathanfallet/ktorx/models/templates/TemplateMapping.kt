package me.nathanfallet.ktorx.models.templates

data class TemplateMapping(
    val errorTemplate: String? = null,
    val listTemplate: String? = null,
    val getTemplate: String? = null,
    val createTemplate: String? = null,
    val updateTemplate: String? = null,
    val deleteTemplate: String? = null,
    val redirectUnauthorizedToUrl: String? = null
)
