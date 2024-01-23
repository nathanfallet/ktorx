package me.nathanfallet.ktorx.routers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.ktorx.controllers.auth.IAuthController
import me.nathanfallet.ktorx.extensions.*
import me.nathanfallet.ktorx.routers.api.APIUnitRouter
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken
import kotlin.reflect.KClass

open class AuthAPIRouter(
    override val controller: IAuthController<*, *>,
    controllerClass: KClass<out IAuthController<*, *>>,
    route: String? = "auth",
    prefix: String? = null,
) : APIUnitRouter(
    controller,
    controllerClass,
    route,
    prefix
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        createSchema(openAPI)
        createAPIPostTokenRoute(root, openAPI)
    }

    override fun createSchema(openAPI: OpenAPI?) {
        openAPI?.schema(AuthRequest::class)
        openAPI?.schema(AuthToken::class)
    }

    open fun createAPIPostTokenRoute(root: Route, openAPI: OpenAPI?) {
        root.post("$fullRoute/token") {
            try {
                val request = call.receive<AuthRequest>()
                val response = controller.token(call, request)
                call.response.status(HttpStatusCode.Created)
                call.respond(response)
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.post("$fullRoute/token") {
            operationId("createToken")
            addTagsItem("Auth")
            description("Create a token")
            requestBody {
                mediaType("application/json") {
                    schema(AuthRequest::class)
                }
            }
            response("201") {
                description("A token")
                mediaType("application/json") {
                    schema(AuthToken::class)
                }
            }
            response("400") {
                description("Invalid body")
                mediaType("application/json") {
                    errorSchema("error_body_invalid")
                }
            }
        }
    }

}
