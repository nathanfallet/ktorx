package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.IRegisterPayload
import me.nathanfallet.usecases.base.IPairSuspendUseCase

interface IGetCodeRegisterUseCase : IPairSuspendUseCase<ApplicationCall, String, IRegisterPayload?>
