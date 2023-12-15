package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.annotations.ModelProperty
import me.nathanfallet.usecases.models.annotations.Schema

@Serializable
data class TestModel(
    @ModelProperty("id") @Schema("ID", "123")
    override val id: Long,
    @ModelProperty("string") @Schema("String", "abc")
    val string: String,
) : IModel<Long, TestCreatePayload, TestUpdatePayload>
