package me.nathanfallet.ktorx.routers.templates

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.models.IChildModel

open class LocalizedTemplateChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    modelTypeInfo: TypeInfo,
    createPayloadTypeInfo: TypeInfo,
    updatePayloadTypeInfo: TypeInfo,
    listTypeInfo: TypeInfo,
    controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    parentRouter: IChildModelRouter<ParentModel, ParentId, *, *, *, *>?,
    mapping: TemplateMapping,
    respondTemplate: suspend ApplicationCall.(String, Map<String, Any>) -> Unit,
    private val getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    route: String? = null,
    id: String? = null,
    prefix: String? = null,
) : TemplateChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>(
    modelTypeInfo,
    createPayloadTypeInfo,
    updatePayloadTypeInfo,
    listTypeInfo,
    controller,
    parentRouter,
    mapping,
    ILocalizedTemplateRouter.wrapRespondTemplate(respondTemplate, getLocaleForCallUseCase),
    route,
    id,
    prefix
), ILocalizedTemplateRouter {

    final override fun createRoutes(root: Route, openAPI: OpenAPI?) = localizeRoutes(root, openAPI)

    override fun isUnauthorizedRedirectPath(call: ApplicationCall): Boolean =
        isUnauthorizedRedirectPath(call, mapping, getLocaleForCallUseCase)

    override fun createLocalizedRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
    }

}
