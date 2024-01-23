package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.usecases.auth.ISessionPayload
import me.nathanfallet.usecases.base.IUseCase

interface IGetSessionForCallUseCase : IUseCase<ApplicationCall, ISessionPayload?>
