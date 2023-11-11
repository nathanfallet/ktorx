package me.nathanfallet.ktor.routers.routers.base

import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel

open class ConcatModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    routers: List<AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>>
) : ConcatChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>(routers, null)
