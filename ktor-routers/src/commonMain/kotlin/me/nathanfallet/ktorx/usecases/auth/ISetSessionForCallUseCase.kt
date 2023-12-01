package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.auth.ISessionPayload
import me.nathanfallet.usecases.base.IPairUseCase

interface ISetSessionForCallUseCase : IPairUseCase<ApplicationCall, ISessionPayload, Unit>
