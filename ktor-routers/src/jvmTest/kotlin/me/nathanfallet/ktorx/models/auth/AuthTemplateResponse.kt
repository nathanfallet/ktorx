package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTemplateResponse(
    val template: String,
    val error: String? = null,
    val code: TestCodePayload? = null,
    val client: String? = null,
    val user: String? = null,
)
