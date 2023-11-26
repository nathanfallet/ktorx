package me.nathanfallet.ktorx.routers.localization

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.nathanfallet.ktorx.controllers.base.IChildModelController
import me.nathanfallet.ktorx.models.templates.TemplateMapping
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.templates.TemplateChildModelRouter
import me.nathanfallet.ktorx.usecases.localization.GetLocaleForCallUseCase
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
    getLocaleForCallUseCase: IGetLocaleForCallUseCase = GetLocaleForCallUseCase(),
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : TemplateChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>(
    modelClass,
    createPayloadClass,
    updatePayloadClass,
    controller,
    parentRouter,
    mapping,
    { template, model ->
        respondTemplate(template, model + mapOf("locale" to getLocaleForCallUseCase(this)))
    },
    route,
    id,
    prefix
) {

    override fun createRoutes(root: Route) {
        createLocalizedRoutes(root)
        root.route("/{locale}") {
            createLocalizedRoutes(this)
        }
    }

    open fun createLocalizedRoutes(root: Route) {
        super.createRoutes(root)
    }

}
