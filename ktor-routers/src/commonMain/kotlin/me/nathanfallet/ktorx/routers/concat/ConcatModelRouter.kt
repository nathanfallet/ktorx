package me.nathanfallet.ktorx.routers.concat

import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.routers.IModelRouter
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel
import kotlin.reflect.KClass

open class ConcatModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    routers: List<IModelRouter<Model, Id, CreatePayload, UpdatePayload>>,
    controllerClass: KClass<out IModelController<Model, Id, CreatePayload, UpdatePayload>>,
) : ConcatChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>(routers, null, controllerClass),
    IModelRouter<Model, Id, CreatePayload, UpdatePayload>
