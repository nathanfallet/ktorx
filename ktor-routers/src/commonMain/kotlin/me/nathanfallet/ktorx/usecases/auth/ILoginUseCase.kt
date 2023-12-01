package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.usecases.base.ISuspendUseCase
import me.nathanfallet.usecases.users.IUser

interface ILoginUseCase<LoginPayload> : ISuspendUseCase<LoginPayload, IUser?>
