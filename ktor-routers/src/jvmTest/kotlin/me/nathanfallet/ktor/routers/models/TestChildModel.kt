package me.nathanfallet.ktor.routers.models

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.IChildModel

@Serializable
data class TestChildModel(
    override val id: Long,
    override val parentId: Long,
    val string: String
) : IChildModel<Long, TestCreatePayload, TestUpdatePayload, Long>
