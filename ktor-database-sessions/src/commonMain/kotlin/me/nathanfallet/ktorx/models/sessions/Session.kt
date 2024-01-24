package me.nathanfallet.ktorx.models.sessions

import me.nathanfallet.usecases.models.IModel

data class Session(
    override val id: String,
    val value: String,
) : IModel<String, Session, String>
