package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class TestCodePayload(
    val code: String
)
