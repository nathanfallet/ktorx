package me.nathanfallet.ktorx.models.auth

import me.nathanfallet.usecases.users.IUser

data class ClientForUser(
    val client: IClient,
    val user: IUser,
)
