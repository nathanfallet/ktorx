package me.nathanfallet.ktorx.routers.concat

import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.usecases.models.IChildModel

open class ConcatChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    val routers: List<IChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>>,
    parentRouter: IChildModelRouter<ParentModel, *, *, *, *, *>?,
) : AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>(
    routers.first().modelClass,
    routers.first().createPayloadClass,
    routers.first().updatePayloadClass,
    routers.first().controller,
    parentRouter,
    routers.first().route,
    routers.first().id,
    routers.first().prefix
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        routers.forEach {
            it.createRoutes(root, openAPI)
        }
    }

    inline fun <reified T> routersOf(): List<T> {
        return routers.filterIsInstance<T>()
    }

    inline fun <reified T> routerOf(): T {
        return routersOf<T>().first()
    }

    inline fun <reified T> routerOfOrNull(): T? {
        return routersOf<T>().firstOrNull()
    }

}
