package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.auth.IClient

@Serializable
data class TestClient(
    override val clientId: String,
) : IClient {

    override val clientSecret: String = ""
    override val redirectUri: String = "app://redirect?code={code}"

}
