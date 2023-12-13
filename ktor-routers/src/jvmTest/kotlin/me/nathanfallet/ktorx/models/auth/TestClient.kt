package me.nathanfallet.ktorx.models.auth

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.auth.IClient
import me.nathanfallet.usecases.models.IModel

@Serializable
data class TestClient(
    override val id: String,
) : IModel<String, Unit, Unit>, IClient {

    override val clientId: String = id
    override val clientSecret: String = ""
    override val redirectUri: String = "app://redirect?code={code}"

}
