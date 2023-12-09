package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.models.IChildModel
import kotlin.reflect.KClass

open class LocalizedTemplateChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    modelClass: KClass<Model>,
    createPayloadClass: KClass<CreatePayload>,
    updatePayloadClass: KClass<UpdatePayload>,
    controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    parentRouter: IChildModelRouter<ParentModel, ParentId, *, *, *, *>?,
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : TemplateChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>(
    modelClass,
    createPayloadClass,
    updatePayloadClass,
    controller,
    parentRouter,
    mapping,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    route,
    id,
    prefix
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route, openAPI: OpenAPI?) = localizeRoutes(root, openAPI)

    override fun createLocalizedRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
    }

}
