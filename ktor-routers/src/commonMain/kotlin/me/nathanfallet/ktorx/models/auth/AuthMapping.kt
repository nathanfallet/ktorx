package me.nathanfallet.ktorx.models.auth

data class AuthMapping(
    val loginTemplate: String? = null,
    val registerTemplate: String? = null,
    val authorizeTemplate: String? = null,
    val redirectTemplate: String? = null,
)
