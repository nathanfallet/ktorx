package me.nathanfallet.ktor.routers.routers.base

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.reflect.*
import me.nathanfallet.ktor.routers.controllers.base.IChildModelController
import me.nathanfallet.ktor.routers.routers.IRouter
import me.nathanfallet.usecases.models.IChildModel
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.annotations.ModelKey
import me.nathanfallet.usecases.models.annotations.ModelProperty
import me.nathanfallet.usecases.models.annotations.PayloadKey
import me.nathanfallet.usecases.models.annotations.PayloadProperty
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

abstract class AbstractChildModelRouter<Model : IChildModel<Id, CreatePayload, UpdatePayload, ParentId>, Id, CreatePayload : Any, UpdatePayload : Any, ParentModel : IChildModel<ParentId, *, *, *>, ParentId>(
    val modelClass: KClass<Model>,
    val createPayloadClass: KClass<CreatePayload>,
    val updatePayloadClass: KClass<UpdatePayload>,
    val controller: IChildModelController<Model, Id, CreatePayload, UpdatePayload, ParentModel, ParentId>,
    val parentRouter: AbstractChildModelRouter<ParentModel, *, *, *, *, *>?,
    route: String? = null,
    id: String? = null,
    prefix: String? = null
) : IRouter {

    val route = route ?: (modelClass.simpleName!!.lowercase() + "s")
    val id = id ?: (modelClass.simpleName!!.lowercase() + "Id")
    val prefix = prefix ?: ""

    val fullRoute = this.prefix + (parentRouter?.let { "/${it.route}/{${it.id}}/" } ?: "/") + this.route

    val modelTypeInfo = TypeInfo(
        modelClass, modelClass.java,
        modelClass.starProjectedType
    )
    val createPayloadTypeInfo = TypeInfo(
        createPayloadClass, createPayloadClass.java,
        createPayloadClass.starProjectedType
    )
    val updatePayloadTypeInfo = TypeInfo(
        updatePayloadClass, updatePayloadClass.java,
        updatePayloadClass.starProjectedType
    )
    val listTypeInfo = TypeInfo(
        List::class, List::class.java,
        List::class.createType(
            listOf(KTypeProjection(KVariance.INVARIANT, modelClass.starProjectedType))
        )
    )

    val modelKeys = getAnnotatedMembersSorted<ModelProperty>(modelClass).map { (member, annotation) ->
        ModelKey(member.name, annotation.type, annotation.style)
    }
    val updatePayloadKeys = getAnnotatedMembersSorted<ModelProperty>(modelClass).mapNotNull { (member, a) ->
        val annotation = a.takeIf { it.visibleOnUpdate } ?: return@mapNotNull null
        PayloadKey(member.name, annotation.type, annotation.style, false)
    } + getAnnotatedMembersSorted<PayloadProperty>(updatePayloadClass).map { (member, annotation) ->
        PayloadKey(member.name, annotation.type, annotation.style, true)
    }
    val createPayloadKeys = getAnnotatedMembersSorted<PayloadProperty>(createPayloadClass).map { (member, annotation) ->
        PayloadKey(member.name, annotation.type, annotation.style, true)
    }

    private inline fun <reified O : Annotation> getAnnotatedMembersSorted(kClass: KClass<*>): List<Pair<KCallable<*>, O>> {
        return kClass.members.mapNotNull { member ->
            val annotation = member.annotations.firstNotNullOfOrNull {
                it as? O
            } ?: return@mapNotNull null
            Pair(member, annotation)
        }.sortedBy { (member, _) ->
            kClass.constructors.firstOrNull {
                it.parameters.any { parameter ->
                    parameter.name == member.name
                }
            }?.parameters?.indexOfFirst { it.name == member.name }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun constructId(id: String): Id {
        val idType = modelClass.members.first { it.name == "id" }.returnType
        return when (idType) {
            typeOf<Int>() -> id.toInt() as Id
            typeOf<Long>() -> id.toLong() as Id
            typeOf<String>() -> id as Id
            else -> throw IllegalArgumentException("Unsupported id type: $idType")
        }
    }

    fun <O : Any> constructPayload(type: KClass<O>, parameters: Parameters): O? {
        val constructor = type.constructors.firstOrNull {
            it.parameters.all { parameter ->
                parameter.name in parameters.names()
                        || parameter.isOptional
                        || parameter.type.isSubtypeOf(typeOf<Boolean>())
            }
        } ?: return null
        val params = constructor.parameters.associateWith {
            it.name?.let { name ->
                when (it.type) {
                    typeOf<Boolean>() -> parameters[name] != null
                    typeOf<Int>() -> parameters[name]?.toInt()
                    typeOf<Long>() -> parameters[name]?.toLong()
                    typeOf<Float>() -> parameters[name]?.toFloat()
                    typeOf<Double>() -> parameters[name]?.toDouble()
                    else -> parameters[name]
                }
            }
        }
        return constructor.callBy(params)
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getAll(call: ApplicationCall): List<Model> {
        return controller.getAll(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel
        )
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun get(call: ApplicationCall): Model {
        return controller.get(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            constructId(call.parameters[id]!!)
        )
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun create(call: ApplicationCall, payload: CreatePayload): Model {
        return controller.create(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            payload
        )
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun update(call: ApplicationCall, payload: UpdatePayload): Model {
        return controller.update(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            constructId(call.parameters[id]!!),
            payload
        )
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun delete(call: ApplicationCall) {
        return controller.delete(
            call,
            parentRouter?.get(call) ?: UnitModel as ParentModel,
            constructId(call.parameters[id]!!)
        )
    }

}
