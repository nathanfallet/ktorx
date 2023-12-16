package me.nathanfallet.ktorx.repositories.api

import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.repositories.remote.IModelRemoteRepository

interface IAPIModelRemoteRepository<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any> :
    IAPIChildModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload, Unit>,
    IModelRemoteRepository<Model, Id, CreatePayload, UpdatePayload>
