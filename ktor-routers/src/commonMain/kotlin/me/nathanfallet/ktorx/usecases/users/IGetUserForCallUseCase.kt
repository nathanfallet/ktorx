package me.nathanfallet.ktorx.usecases.users

import io.ktor.server.application.*
import me.nathanfallet.usecases.base.ISuspendUseCase
import me.nathanfallet.usecases.users.IUser

interface IGetUserForCallUseCase : ISuspendUseCase<ApplicationCall, IUser?>
