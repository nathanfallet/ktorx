package me.nathanfallet.ktorx.models.auth

interface IClient {

    val clientId: String
    val clientSecret: String
    val redirectUri: String

}
