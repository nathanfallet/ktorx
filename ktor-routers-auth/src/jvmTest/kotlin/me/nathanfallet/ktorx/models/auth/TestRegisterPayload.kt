package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class TestRegisterPayload(
    val email: String,
    val password: String,
)
