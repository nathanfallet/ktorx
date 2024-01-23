package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class TestLoginPayload(
    val email: String,
    val password: String
)
