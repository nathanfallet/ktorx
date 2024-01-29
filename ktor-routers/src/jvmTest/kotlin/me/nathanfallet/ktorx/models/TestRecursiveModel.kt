package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.Schema

@Serializable
data class TestRecursiveModel(
    @Schema("Name", "abc")
    val name: String,
    @Schema("Children", "[]")
    val children: List<TestRecursiveModel>? = null,
)
