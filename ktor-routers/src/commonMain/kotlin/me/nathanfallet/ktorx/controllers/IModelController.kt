package me.nathanfallet.ktorx.controllers

import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel

interface IModelController<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload, UpdatePayload> :
    IChildModelController<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>
