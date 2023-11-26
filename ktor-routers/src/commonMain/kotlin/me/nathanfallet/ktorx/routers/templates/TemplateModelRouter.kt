package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import me.nathanfallet.ktorx.controllers.base.IModelController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.IModelRouter
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.UnitModel
import kotlin.reflect.KClass

open class TemplateModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IModelController<Model, Id, CreatePayload, UpdatePayload>,
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : TemplateChildModelRouter<Model, Id, CreatePayload, UpdatePayload, UnitModel, Unit>(
    modelClass,
    createPayloadClass,
    updatePayloadClass,
    controller,
    null,
    mapping,
    respondTemplate,
    route,
    id,
    prefix
), IModelRouter<Model, Id, CreatePayload, UpdatePayload>
