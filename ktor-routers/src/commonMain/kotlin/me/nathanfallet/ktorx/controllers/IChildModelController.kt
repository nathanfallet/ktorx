package me.nathanfallet.ktorx.controllers

import me.nathanfallet.usecases.models.IChildModel

interface IChildModelController<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload, UpdatePayload, ParentModel : IChildModel<ParentId, *, *, *>, ParentId> :
    IController
