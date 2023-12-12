package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val clientId: String,
    val clientSecret: String,
    val code: String,
)
