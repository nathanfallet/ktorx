package me.nathanfallet.ktorx.routers.base

import io.ktor.util.reflect.*
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.routers.IModelRouter
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel

abstract class AbstractModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    modelTypeInfo: TypeInfo,
    createPayloadTypeInfo: TypeInfo,
    updatePayloadTypeInfo: TypeInfo,
    listTypeInfo: TypeInfo,
    controller: IModelController<Model, Id, CreatePayload, UpdatePayload>,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>(
    modelTypeInfo,
    createPayloadTypeInfo,
    updatePayloadTypeInfo,
    listTypeInfo,
    controller,
    null,
    route,
    id,
    prefix
), IModelRouter<Model, Id, CreatePayload, UpdatePayload>
