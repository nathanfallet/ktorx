package me.nathanfallet.ktorx.routers.base

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.ktorx.routers.IChildModelRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.annotations.ModelAnnotations
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.typeOf

data class ControllerRoute(
    val type: RouteType,
    val path: String,
    val method: HttpMethod?,
    val function: KFunction<*>,
) {

    @Suppress("UNCHECKED_CAST")
    suspend operator fun <Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId> invoke(
        call: ApplicationCall,
        router: IChildModelRouter<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
        parameters: Map<String, Any?> = mapOf(),
    ): Any? {
        try {
            return function.callBy(function.parameters.associateWith { parameter ->
                if (parameter.kind == KParameter.Kind.INSTANCE) return@associateWith router.controller
                if (parameter.type == typeOf<ApplicationCall>()) return@associateWith call
                parameter.annotations.firstNotNullOfOrNull { it as? me.nathanfallet.ktorx.models.annotations.Id }
                    ?.let {
                        return@associateWith ModelAnnotations.constructIdFromString(
                            router.modelTypeInfo.type as KClass<Model>,
                            call.parameters[router.id]!!
                        )
                    }
                parameter.annotations.firstNotNullOfOrNull { it as? me.nathanfallet.ktorx.models.annotations.ParentModel }
                    ?.let {
                        var target: IChildModelRouter<*, *, *, *, *, *> = router
                        do {
                            target =
                                target.parentRouter ?: throw IllegalArgumentException("Illegal parent model: ${it.id}")
                        } while (target.id != it.id)
                        return@associateWith target.get(call)
                    }
                parameters[parameter.name]
                    ?: throw IllegalArgumentException("Unknown parameter type: ${parameter.type}")
            })
        } catch (e: Exception) {
            throw if (e is InvocationTargetException) e.targetException else e
        }
    }

}
