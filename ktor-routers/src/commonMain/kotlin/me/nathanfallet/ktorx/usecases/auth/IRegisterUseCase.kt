package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.IRegisterPayload
import me.nathanfallet.usecases.base.IPairSuspendUseCase
import me.nathanfallet.usecases.users.IUser

interface IRegisterUseCase : IPairSuspendUseCase<ApplicationCall, IRegisterPayload, IUser?>
