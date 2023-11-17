package me.nathanfallet.ktorx.models.api

data class APIMapping(
    val listEnabled: Boolean = true,
    val getEnabled: Boolean = true,
    val createEnabled: Boolean = true,
    val updateEnabled: Boolean = true,
    val deleteEnabled: Boolean = true,
)
