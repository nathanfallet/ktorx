package me.nathanfallet.ktorx.routers.concat

import me.nathanfallet.ktorx.routers.IModelRouter
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel

open class ConcatModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    routers: List<IModelRouter<Model, Id, CreatePayload, UpdatePayload>>,
) : ConcatChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>(routers, null),
    IModelRouter<Model, Id, CreatePayload, UpdatePayload>
