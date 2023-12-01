package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.usecases.base.IPairSuspendUseCase

interface ICreateCodeRegisterUseCase<RegisterPayload> : IPairSuspendUseCase<ApplicationCall, RegisterPayload, String?>
