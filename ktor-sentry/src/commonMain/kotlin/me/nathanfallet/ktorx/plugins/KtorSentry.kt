package me.nathanfallet.ktorx.plugins

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.sentry.*
import io.sentry.protocol.Request
import io.sentry.protocol.Response
import me.nathanfallet.ktorx.hooks.SentryContextHook

val sentryTransactionKey = AttributeKey<ITransaction>("SentryTransaction")

val KtorSentry = createApplicationPlugin("KtorSentry") {
    on(MonitoringEvent(Routing.RoutingCallStarted)) { call ->
        val transaction = Sentry.startTransaction(
            /* name = */ "${call.request.httpMethod.value} ${call.request.path()}",
            /* operation = */ "call",
            /* customSamplingContext = */ CustomSamplingContext().apply {
                this["path"] = call.request.path().lowercase()
            },
            /* bindToScope = */ true
        )
        Sentry.configureScope { scope ->
            scope.addBreadcrumb(Breadcrumb.http(call.request.uri, call.request.httpMethod.value))
            scope.request = Request().apply {
                method = call.request.httpMethod.value
                url = call.request.path()
                queryString = call.request.queryString()
                headers = call.request.headers.toMap()
                    .mapValues { (_, v) -> v.firstOrNull() }
            }
            scope.setTag("url", call.request.uri)
            scope.setTag("host", call.request.host())
        }
        call.attributes.put(sentryTransactionKey, transaction)
        transaction.startChild("setup", "Call setup")
    }

    on(MonitoringEvent(Routing.RoutingCallStarted)) { call ->
        call.sentryTransactionOrNull()?.let { t ->
            t.latestActiveSpan?.finish(SpanStatus.OK)
            t.setTag("route", call.route.parent.toString())
            t.startChild("processing", "Request processing")
        }
    }

    on(ResponseBodyReadyForSend) { call, _ ->
        call.sentryTransactionOrNull()?.let { t ->
            t.latestActiveSpan?.finish(SpanStatus.OK)
            t.startChild("sending", "Sending response")
        }
    }

    on(CallFailed) { call, cause ->
        Sentry.captureException(cause)
        call.sentryTransactionOrNull()?.apply {
            throwable = cause
        }
    }

    on(ResponseSent) { call ->
        call.sentryTransactionOrNull()?.let { t ->
            t.latestActiveSpan?.finish(SpanStatus.OK)
            Sentry.addBreadcrumb(
                Breadcrumb.http(call.request.uri, call.request.httpMethod.value, call.response.status()?.value)
            )
            t.contexts.setResponse(
                Response().apply {
                    headers = call.response.headers.allValues().toMap().mapValues { (_, v) -> v.firstOrNull() }
                    statusCode = call.response.status()?.value
                },
            )
            t.finish(SpanStatus.fromHttpStatusCode(call.response.status()?.value, SpanStatus.OK))
        }
    }

    on(SentryContextHook()) { block ->
        block()
    }
}

fun ApplicationCall.sentryTransactionOrNull() =
    if (attributes.contains(sentryTransactionKey)) attributes[sentryTransactionKey] else null
