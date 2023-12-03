package me.nathanfallet.ktorx.usecases.users

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.usecases.users.IUser

class RequireUserForCallUseCase(
    private val getUserForCallUseCase: IGetUserForCallUseCase
) : IRequireUserForCallUseCase {

    override suspend fun invoke(input: ApplicationCall): IUser {
        return getUserForCallUseCase(input) ?: throw ControllerException(
            HttpStatusCode.Unauthorized, "auth_invalid_credentials"
        )
    }

}
