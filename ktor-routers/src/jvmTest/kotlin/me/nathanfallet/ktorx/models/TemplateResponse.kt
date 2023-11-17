package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable

@Serializable
data class TemplateResponse<Model, Keys>(
    val template: String,
    val data: TemplateResponseData<Model, Keys>
)
