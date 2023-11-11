package me.nathanfallet.ktor.routers.routers.api

import me.nathanfallet.ktor.routers.controllers.base.IModelController
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel
import kotlin.reflect.KClass

open class APIModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IModelController<Model, Id, CreatePayload, UpdatePayload>,
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : APIChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>(
    modelClass,
    createPayloadClass,
    updatePayloadClass,
    controller,
    null,
    route,
    id,
    prefix
)
