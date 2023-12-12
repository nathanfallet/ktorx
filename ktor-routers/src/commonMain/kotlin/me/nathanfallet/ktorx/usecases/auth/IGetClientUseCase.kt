package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.usecases.auth.IClient
import me.nathanfallet.usecases.base.ISuspendUseCase

interface IGetClientUseCase : ISuspendUseCase<String, IClient?>
