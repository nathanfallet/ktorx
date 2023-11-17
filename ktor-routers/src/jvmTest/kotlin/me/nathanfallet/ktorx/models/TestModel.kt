package me.nathanfallet.ktorx.models

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.annotations.ModelProperty

@Serializable
data class TestModel(
    @ModelProperty("id")
    override val id: Long,
    @ModelProperty("string")
    val string: String,
) : IModel<Long, TestCreatePayload, TestUpdatePayload>
