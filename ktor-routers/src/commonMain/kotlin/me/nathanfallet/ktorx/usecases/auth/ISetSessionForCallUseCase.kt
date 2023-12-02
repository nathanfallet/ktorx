package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.usecases.base.IPairUseCase
import me.nathanfallet.usecases.users.ISessionPayload

interface ISetSessionForCallUseCase : IPairUseCase<ApplicationCall, ISessionPayload, Unit>
