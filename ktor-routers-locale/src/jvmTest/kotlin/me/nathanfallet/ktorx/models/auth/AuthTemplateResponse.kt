package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTemplateResponse(
    val template: String,
    val locale: String,
    val error: String? = null,
    val code: TestCodePayload? = null,
)
