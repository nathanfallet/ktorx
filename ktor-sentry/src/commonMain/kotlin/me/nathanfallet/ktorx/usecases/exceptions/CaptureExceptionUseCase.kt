package me.nathanfallet.ktorx.usecases.exceptions

import io.sentry.Sentry
import me.nathanfallet.usecases.exceptions.ICaptureExceptionUseCase

class CaptureExceptionUseCase : ICaptureExceptionUseCase {

    override fun invoke(input: Throwable) {
        Sentry.captureException(input)
    }

}
