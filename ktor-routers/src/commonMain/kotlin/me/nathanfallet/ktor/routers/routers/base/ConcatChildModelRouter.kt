package me.nathanfallet.ktor.routers.routers.base

import io.ktor.server.routing.*
import me.nathanfallet.usecases.models.IChildModel

open class ConcatChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    val routers: List<AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>>,
    parentRouter: AbstractChildModelRouter<ParentModel, *, *, *, *, *>?
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

    override fun createRoutes(root: Route) {
        routers.forEach {
            it.createRoutes(root)
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
