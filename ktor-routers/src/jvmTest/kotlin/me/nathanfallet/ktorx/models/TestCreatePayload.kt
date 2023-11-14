package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable

@Serializable
data class TestCreatePayload(
    val string: String,
)
