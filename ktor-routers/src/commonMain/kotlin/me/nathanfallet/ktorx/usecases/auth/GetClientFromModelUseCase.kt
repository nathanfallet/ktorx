package me.nathanfallet.ktorx.usecases.auth

import me.nathanfallet.usecases.auth.IClient
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.get.IGetModelUseCase

open class GetClientFromModelUseCase<Model : IModel<String, *, *>>(
    private val getModelUseCase: IGetModelUseCase<Model, String>,
) : IGetClientUseCase {

    override suspend fun invoke(input: String): IClient? {
        return getModelUseCase(input) as? IClient
    }

}
