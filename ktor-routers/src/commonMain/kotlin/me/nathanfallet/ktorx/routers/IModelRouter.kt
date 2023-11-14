package me.nathanfallet.ktorx.routers

import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel

interface IModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any> :
    IChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>
