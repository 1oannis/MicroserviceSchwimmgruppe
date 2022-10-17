package com.acme.schwimmgruppe.rest

import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import com.acme.schwimmgruppe.rest.SchwimmgruppeGetController.Companion.API_PATH
import com.acme.schwimmgruppe.service.FindByIdResult
import com.acme.schwimmgruppe.service.SchwimmgruppeReadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Eine `@RestController`-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
 * Funktionen der Klasse abgebildet werden
 *
 * ![UML-Klassendiagramm](../../../images/SchwimmgruppeGetController.svg)
 * @property service Injiziertes Objekt von [SchwimmgruppeReadService]
 * @constructor Einen SchwimmgruppeController mit einem injizierten [SchwimmgruppeReadService] erzeugen.
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@RestController
@RequestMapping(API_PATH)
@Tag(name = "Schwimmgruppe API")
@Suppress("RegExpUnexpectedAnchor")
class SchwimmgruppeGetController(private val service: SchwimmgruppeReadService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Suche anhand der Schwimmgruppen-ID als Pfad-Parameter
     * @param id ID der zu suchenden Schwimmgruppe
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @return Eine Response mit dem Statuscode 200 und der gefundenen Schwimmgruppe mit oder Statuscode 404.
     */
    @GetMapping(path = ["/{id:$ID_PATTERN}"], produces = [HAL_JSON_VALUE])
    @Operation(summary = "Suche mit der Schwimmgruppe-ID", tags = ["Suchen"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Schwimmgruppe gefunden"),
        ApiResponse(responseCode = "404", description = "Schwimmgruppe nicht gefunden"),
    )
    // https://localhost:8080/swagger-ui.html
    suspend fun findById(
        @PathVariable id: SchwimmgruppeId,
        request: ServerHttpRequest,
    ): ResponseEntity<SchwimmgruppeModel> {
        logger.trace("findById: id={}", id)
        // Anwendungskern
        val schwimmgruppe = when (val result = service.findById(id)) {
            is FindByIdResult.Found -> result.schwimmgruppe
            is FindByIdResult.NotFound -> return notFound().build()
        }
        logger.trace("findById: {}", schwimmgruppe)

        // HATEOAS
        val model = SchwimmgruppeModel(schwimmgruppe)
        val baseUri = getBaseUri(request.headers, request.uri, id)
        val idUri = "$baseUri/${schwimmgruppe.id}"

        val selfLink = Link.of(idUri)
        val listLink = Link.of(baseUri, LinkRelation.of("list"))
        val addLink = Link.of(baseUri, LinkRelation.of("add"))
        val updateLink = Link.of(idUri, LinkRelation.of("update"))
        model.add(selfLink, listLink, addLink, updateLink)

        return ok(model)
    }

    /**
     * Gebe alle Schwimmgruppen aus
     * @param queryParams Query-Parameter als Map.
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @return Eine Response mit dem Statuscode 200 und gefundenen Schwimmgruppen oder mit Statuscode 404
     */
    @GetMapping(produces = [HAL_JSON_VALUE])
    @Operation(summary = "Suche mit Kriterium", tags = ["Suchen"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Liste mit allen Schwimmgruppen"),
        ApiResponse(responseCode = "404", description = "Keine Schwimmgruppen gefunden"),
    )
    suspend fun find(
        @RequestParam queryParams: MultiValueMap<String, String>,
        request: ServerHttpRequest,
    ): ResponseEntity<CollectionModel<SchwimmgruppeModel>> {
        logger.debug("find: queryParams={}", queryParams)

        val baseUri = getBaseUri(request.headers, request.uri)
        val models = service.find(queryParams)
            .map { schwimmgruppe ->
                val model = SchwimmgruppeModel(schwimmgruppe)
                val selfLink = Link.of("$baseUri/${schwimmgruppe.id}")
                model.add(selfLink)
            }
        logger.debug("find: {}", models)

        if (models.isEmpty()) {
            return notFound().build()
        }
        logger.trace("find: {}", models)

        return ok(CollectionModel.of(models))
    }

    /**
     * Abfrage, welche Schwimmhallen es zu einem Präfix gibt.
     * @param prefix Schwimmhalle-Präfix als Pfadvariable.
     * @return Die passenden Schwimmhallen oder Statuscode 404, falls es keine gibt.
     */
    @GetMapping(path = ["$SCHWIMMHALLE_PATH/{prefix}"])
    suspend fun findSchwimmhallenByPrefix(@PathVariable prefix: String): ResponseEntity<String> {
        logger.debug("findSchwimmhallenByPrefix: prefix={}", prefix)
        val schwimmhallen = service.findSchwimmhalleByPrefix(prefix)
        logger.debug("findSchwimmhallenByPrefix: schwimmhallen={}", schwimmhallen)

        if (schwimmhallen.isEmpty()) {
            return notFound().build()
        }
        return ok(schwimmhallen.toString())
    }

    /**
     * Konstante für den REST-Controller
     */
    companion object {
        /**
         * Basis-Pfad der REST-Schnittstelle. const: "compile time constant"
         */
        const val API_PATH = "/api"

        private const val HEX_PATTERN = "[\\dA-Fa-f]"

        /**
         * Muster für eine UUID. `$HEX_PATTERN{8}-($HEX_PATTERN{4}-){3}$HEX_PATTERN{12}` enthält eine _capturing group_
         * und ist nicht zulässig.
         */
        const val ID_PATTERN = "$HEX_PATTERN{8}-$HEX_PATTERN{4}-$HEX_PATTERN{4}-$HEX_PATTERN{4}-$HEX_PATTERN{12}"

        /**
         * Pfad, um Ligaklasse abzufragen
         */
        private const val SCHWIMMHALLE_PATH = "/schwimmhalle"
    }
}
