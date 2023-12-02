package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.usecases.base.IUseCase
import me.nathanfallet.usecases.users.ISessionPayload
import me.nathanfallet.usecases.users.IUser

interface ICreateSessionForUserUseCase : IUseCase<IUser, ISessionPayload>
