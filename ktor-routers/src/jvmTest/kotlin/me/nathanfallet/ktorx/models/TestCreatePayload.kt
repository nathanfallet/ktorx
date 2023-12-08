package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.PayloadProperty
import me.nathanfallet.usecases.models.annotations.validators.StringPropertyValidator

@Serializable
data class TestCreatePayload(
    @PayloadProperty("string") @StringPropertyValidator(regex = "[a-z]+")
    val string: String,
)
