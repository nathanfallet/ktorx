package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.ktorx.models.auth.ILoginPayload
import me.nathanfallet.usecases.base.ISuspendUseCase
import me.nathanfallet.usecases.users.IUser

interface ILoginUseCase : ISuspendUseCase<ILoginPayload, IUser?>
