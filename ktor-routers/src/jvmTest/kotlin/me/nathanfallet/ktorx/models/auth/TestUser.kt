package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.users.IUser

@Serializable
data class TestUser(
    val id: String,
) : IUser
