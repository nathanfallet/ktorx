package me.nathanfallet.ktor.routers.models

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.IModel

@Serializable
data class TestModel(
    override val id: Long,
    val string: String,
) : IModel<Long, TestCreatePayload, TestUpdatePayload>