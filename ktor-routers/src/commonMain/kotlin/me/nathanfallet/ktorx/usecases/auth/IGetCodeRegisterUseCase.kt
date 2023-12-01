package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.usecases.base.IPairSuspendUseCase

interface IGetCodeRegisterUseCase<RegisterPayload> : IPairSuspendUseCase<ApplicationCall, String, RegisterPayload?>
