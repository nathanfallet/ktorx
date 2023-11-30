package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.ktorx.models.auth.ISessionPayload
import me.nathanfallet.usecases.base.IUseCase
import me.nathanfallet.usecases.users.IUser

interface ICreateSessionForUserUseCase : IUseCase<IUser, ISessionPayload>
