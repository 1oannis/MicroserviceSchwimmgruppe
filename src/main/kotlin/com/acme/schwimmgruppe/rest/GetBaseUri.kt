package com.acme.schwimmgruppe.rest

import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import com.acme.schwimmgruppe.rest.SchwimmgruppeGetController.Companion.API_PATH
import org.springframework.http.HttpHeaders
import java.net.URI

/**
 * Basis-URI ermitteln, d.h. ohne angehängten Pfad-Parameter für die ID und ohne Query-Parameter
 * @param headers Header-Daten des Request-Objekts
 * @param uri URI zum eingegangenen Request
 * @param id Eine Schwimmgruppe-ID oder null als Defaultwert
 * @return Die Basis-URI als String
 */
@Suppress("UastIncorrectHttpHeaderInspection", "ReturnCount")
fun getBaseUri(headers: HttpHeaders, uri: URI, id: SchwimmgruppeId? = null): String {
    val envoyOriginalPath = headers.getFirst("x-envoy-original-path")
    if (envoyOriginalPath != null) {
        // Forwarding durch Envoy von K8s
        // host: "localhost"
        // x-forwarded-proto: "http"
        // x-envoy-decorator-operation: "schwimmgruppe.acme.svc.cluster.local:8080/schwimmgruppen/*",
        // x-envoy-original-path: "/schwimmgruppen/api/00000000-0000-0000-0000-000000000001"
        val host = headers.getFirst("Host")
        val forwardedProto = headers.getFirst("x-forwarded-proto")
        val basePath = envoyOriginalPath.substringBefore('?').removeSuffix("/")
        return "$forwardedProto://$host$basePath"
    }

    val forwardedHost = headers.getFirst("x-forwarded-host")
    if (forwardedHost != null) {
        // Forwarding durch Spring Cloud Gateway
        // x-forwarded-proto: "https"
        // x-forwarded-host: "localhost:8443"
        // x-forwarded-prefix: "/schwimmgruppen"
        val forwardedProto = headers.getFirst("x-forwarded-proto")
        val forwardedPrefix = headers.getFirst("x-forwarded-prefix")
        return "$forwardedProto://$forwardedHost$forwardedPrefix$API_PATH"
    }

    // KEIN Forwarding von einem API-Gateway
    val baseUri = uri.toString().substringBefore('?').removeSuffix("/")
    if (id == null) {
        return baseUri
    }
    return baseUri.removeSuffix("/$id")
}
