package me.nathanfallet.ktorx.models.exceptions

data class ControllerRedirect(
    val url: String,
    val permanent: Boolean = false,
) : Exception()
