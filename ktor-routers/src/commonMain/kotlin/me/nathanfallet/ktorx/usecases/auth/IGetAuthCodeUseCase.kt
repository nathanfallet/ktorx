package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.usecases.base.ISuspendUseCase

interface IGetAuthCodeUseCase : ISuspendUseCase<String, ClientForUser?>
