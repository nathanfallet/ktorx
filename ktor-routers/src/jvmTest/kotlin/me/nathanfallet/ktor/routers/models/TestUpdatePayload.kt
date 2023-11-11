package me.nathanfallet.ktor.routers.models

import kotlinx.serialization.Serializable

@Serializable
data class TestUpdatePayload(
    val string: String,
)
