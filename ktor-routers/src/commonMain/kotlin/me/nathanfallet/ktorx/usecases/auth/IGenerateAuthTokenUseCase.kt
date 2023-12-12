package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.usecases.auth.AuthToken
import me.nathanfallet.usecases.base.ISuspendUseCase

interface IGenerateAuthTokenUseCase : ISuspendUseCase<ClientForUser, AuthToken>
