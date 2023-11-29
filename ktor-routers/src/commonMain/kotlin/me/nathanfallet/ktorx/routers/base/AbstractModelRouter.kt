package me.nathanfallet.ktorx.routers.base

import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.routers.IModelRouter
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel
import kotlin.reflect.KClass

abstract class AbstractModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IModelController<Model, Id, CreatePayload, UpdatePayload>,
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>(
    modelClass,
    createPayloadClass,
    updatePayloadClass,
    controller,
    null,
    route,
    id,
    prefix
), IModelRouter<Model, Id, CreatePayload, UpdatePayload>
