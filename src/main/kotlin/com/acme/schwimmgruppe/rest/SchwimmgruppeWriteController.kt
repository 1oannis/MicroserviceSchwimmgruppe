package com.acme.schwimmgruppe.rest

import am.ik.yavi.core.ConstraintViolation
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import com.acme.schwimmgruppe.rest.SchwimmgruppeGetController.Companion.API_PATH
import com.acme.schwimmgruppe.rest.SchwimmgruppeGetController.Companion.ID_PATTERN
import com.acme.schwimmgruppe.service.CreateResult
import com.acme.schwimmgruppe.service.SchwimmgruppeWriteService
import com.acme.schwimmgruppe.service.UpdateResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.unprocessableEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Eine `@RestController`-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
 * Funktionen der Klasse abgebildet werden
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 *
 * @constructor Einen SchwimmgruppeController mit einem injizierten [SchwimmgruppeWriteService] erzeugen.
 *
 * @property service Injiziertes Objekt von [SchwimmgruppeWriteService]
 */
@RestController
@RequestMapping(API_PATH)
@Tag(name = "Schwimmgruppe API")
@Suppress("TooManyFunctions", "LargeClass", "RegExpUnexpectedAnchor")
class SchwimmgruppeWriteController(private val service: SchwimmgruppeWriteService) {
    private val logger = LoggerFactory.getLogger(SchwimmgruppeWriteController::class.java)

    // https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-ann-methods
    /**
     * Einen neuen Schwimmgruppe-Datensatz anlegen.
     * @param schwimmgruppeDTO Das Schwimmgruppenobjekt aus dem eingegangenen Request-Body.
     * @param request Das Request-Objekt, um `Location` im Response-Header zu erstellen.
     * @return Response mit Statuscode 201 einschließlich Location-Header oder Statuscode 422,
     *      falls Constraints verletzt sind oder der Name bereits existiert oder Statuscode 400,
     *      falls syntaktische Fehler im Request-Body
     *      vorliegen.
     */
    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Eine neue Schwimmgruppe anlegen", tags = ["Anlegen"])
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Schwimmgruppe neu angelegt"),
        ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body"),
        ApiResponse(responseCode = "422", description = "Ungültige Werte oder name vorhanden"),
    )
    suspend fun create(
        @RequestBody schwimmgruppeDTO: SchwimmgruppeDTO,
        request: ServerHttpRequest,
    ): ResponseEntity<GenericBody> {
        logger.debug("create: {}", schwimmgruppeDTO)
        logger.trace("getId: ={}", request.id)
        logger.trace("getPath: ={}", request.path)
        return when (val result = service.create(schwimmgruppeDTO.toSchwimmgruppe())) {
            is CreateResult.Created -> handleCreatedSuccess(result.schwimmgruppe, request)
            is CreateResult.ConstraintViolations -> handleConstraintViolations(result.violations)
        }
    }

    private fun handleCreatedSuccess(
        neueSchwimmgruppe: Schwimmgruppe,
        request: ServerHttpRequest,
    ): ResponseEntity<GenericBody> {
        logger.trace("handleCreated: {}", neueSchwimmgruppe)
        logger.trace("header: {}", request.headers)
        logger.trace("uri: {}", request.uri)
        val baseUri = getBaseUri(request.headers, request.uri)
        val location = URI("$baseUri/${neueSchwimmgruppe.id}")
        logger.trace("handleCreateSuccess: location={}", location)
        return created(location).build()
    }

    private fun handleConstraintViolations(violations: Collection<ConstraintViolation>): ResponseEntity<GenericBody> {
        if (violations.isEmpty()) {
            return unprocessableEntity().build()
        }

        val schwimmgruppeViolations = violations.associate { violation ->
            violation.messageKey() to violation.message()
        }
        logger.trace("mapConstraintViolations: {}", schwimmgruppeViolations)

        return unprocessableEntity().body(GenericBody.Values(schwimmgruppeViolations))
    }

    /**
     * Einen vorhandenen Schwimmgruppe-Datensatz überschreiben.
     * @param id ID der zu aktualisierenden Schwimmgruppe.
     * @param schwimmgruppeDTO Das Schwimmgruppeobjekt aus dem eingegangenen Request-Body.
     * @return Response mit Statuscode 204 oder Statuscode 422, falls Constraints verletzt sind oder
     *      der JSON-Datensatz syntaktisch nicht korrekt ist oder falls der Name bereits existiert oder
     *      Statuscode 400, falls syntaktische Fehler im Request-Body vorliegen.
     */
    @PutMapping(path = ["/{id:$ID_PATTERN}"], consumes = [APPLICATION_JSON_VALUE])
    @Operation(summary = "Eine Schwimmgruppe mit neuen Werten aktualisieren", tags = ["Aktualisieren"])
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Aktualisiert"),
        ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body"),
        ApiResponse(responseCode = "404", description = "Schwimmgruppe nicht vorhanden"),
        ApiResponse(responseCode = "422", description = "Ungültige Werte oder Name vorhanden"),
    )
    suspend fun update(
        @PathVariable id: SchwimmgruppeId,
        @RequestBody schwimmgruppeDTO: SchwimmgruppeDTO,
    ): ResponseEntity<GenericBody> {
        logger.debug("update: id={}, {}", id, schwimmgruppeDTO)

        val result = service.update(schwimmgruppeDTO.toSchwimmgruppe(), id)
        return handleUpdateResult(result)
    }

    private fun handleUpdateResult(result: UpdateResult): ResponseEntity<GenericBody> =
        when (result) {
            is UpdateResult.Updated -> noContent().build() // 204
            is UpdateResult.NotFound -> notFound().build() // 404
            is UpdateResult.ConstraintViolations -> handleConstraintViolations(result.violations)
        }

    /**
     * Eine vorhandene Schwimmgruppe anhand ihrer ID löschen.
     * @param id ID der zu löschenden Schwimmgruppe.
     * @return Response mit Statuscode 204.
     */
    @DeleteMapping(path = ["/{id:$ID_PATTERN}"])
    @Operation(summary = "Eine Schwimmgruppe anhand der ID loeschen", tags = ["Loeschen"])
    @ApiResponses(ApiResponse(responseCode = "204", description = "Gelöscht"))
    suspend fun deleteById(@PathVariable id: SchwimmgruppeId): ResponseEntity<Unit> {
        logger.debug("deleteById: id={}", id)
        service.deleteById(id)
        return noContent().build()
    }
}
