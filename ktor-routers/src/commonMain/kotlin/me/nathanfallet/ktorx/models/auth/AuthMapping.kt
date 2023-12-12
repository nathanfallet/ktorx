package me.nathanfallet.ktorx.models.auth

data class AuthMapping(
    val errorTemplate: String? = null,
    val loginTemplate: String? = null,
    val registerTemplate: String? = null,
    val authorizeTemplate: String? = null,
    val redirectTemplate: String? = null,
    val redirectUnauthorizedToUrl: String? = null,
)
