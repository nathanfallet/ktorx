package me.nathanfallet.ktorx.routers.concat

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.models.routes.ControllerRoute
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.ktorx.routers.base.AbstractChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import kotlin.reflect.KClass

open class ConcatChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    vararg val routers: IChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
) : AbstractChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>(
    routers.first().modelTypeInfo,
    routers.first().createPayloadTypeInfo,
    routers.first().updatePayloadTypeInfo,
    routers.first().controller,
    routers.first().controllerClass,
    routers.first().parentRouter,
    routers.first().route,
    routers.first().id,
    routers.first().prefix
) {

    override fun createControllerRoute(root: Route, controllerRoute: ControllerRoute, openAPI: OpenAPI?) {
        routers.mapNotNull { it as? AbstractChildModelRouter<*, *, *, *, *, *> }.forEach {
            it.createControllerRoute(root, controllerRoute, openAPI)
        }
    }

    override suspend fun <Payload : Any> decodePayload(call: ApplicationCall, type: KClass<Payload>): Payload {
        throw UnsupportedOperationException("Cannot decode payload from concat router")
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
