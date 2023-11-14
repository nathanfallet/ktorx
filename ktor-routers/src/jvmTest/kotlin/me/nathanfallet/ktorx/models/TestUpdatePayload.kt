package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable

@Serializable
data class TestUpdatePayload(
    val string: String,
)
