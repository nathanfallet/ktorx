package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.models.IModel
import kotlin.reflect.KClass

open class LocalizedTemplateModelRouter<Model : IModel<Id, CreatePayload, UpdatePayload>, Id, CreatePayload : Any, UpdatePayload : Any>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IModelController<Model, Id, CreatePayload, UpdatePayload>,
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : TemplateModelRouter<Model, Id, CreatePayload, UpdatePayload>(
    modelClass,
    createPayloadClass,
    updatePayloadClass,
    controller,
    mapping,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    route,
    id,
    prefix
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route) = localizeRoutes(root)

    override fun createLocalizedRoutes(root: Route) {
        super.createRoutes(root)
    }

}
