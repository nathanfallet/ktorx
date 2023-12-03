package me.nathanfallet.ktorx.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.usecases.base.IUseCase
import me.nathanfallet.usecases.users.ISessionPayload

interface IGetSessionForCallUseCase : IUseCase<ApplicationCall, ISessionPayload?>
