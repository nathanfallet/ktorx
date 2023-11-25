package me.nathanfallet.ktorx.usecases.localization

import io.ktor.server.application.*
import me.nathanfallet.usecases.base.IUseCase
import me.nathanfallet.usecases.localization.Locale

interface IGetLocaleForCallUseCase : IUseCase<ApplicationCall, Locale>
