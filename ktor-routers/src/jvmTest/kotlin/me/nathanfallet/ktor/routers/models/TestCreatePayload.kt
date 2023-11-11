package me.nathanfallet.ktor.routers.models

import kotlinx.serialization.Serializable

@Serializable
data class TestCreatePayload(
    val string: String,
)
