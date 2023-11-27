package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.PayloadProperty

@Serializable
data class TestCreatePayload(
    @PayloadProperty("string")
    val string: String,
)
