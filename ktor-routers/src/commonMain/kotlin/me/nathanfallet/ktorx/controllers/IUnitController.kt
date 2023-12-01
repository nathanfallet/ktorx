package me.nathanfallet.ktorx.controllers

import io.ktor.server.application.*
import me.nathanfallet.usecases.models.UnitModel

interface IUnitController : IModelController<UnitModel, Unit, Unit, Unit> {

    override suspend fun list(call: ApplicationCall): List<UnitModel> {
        return listOf(UnitModel)
    }

    override suspend fun delete(call: ApplicationCall, id: Unit) {
        // Do nothing
    }

    override suspend fun update(call: ApplicationCall, id: Unit, payload: Unit): UnitModel {
        return UnitModel
    }

    override suspend fun create(call: ApplicationCall, payload: Unit): UnitModel {
        return UnitModel
    }

    override suspend fun get(call: ApplicationCall, id: Unit): UnitModel {
        return UnitModel
    }

}
