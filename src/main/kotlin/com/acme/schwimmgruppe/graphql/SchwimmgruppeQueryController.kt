package com.acme.schwimmgruppe.graphql

import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import com.acme.schwimmgruppe.service.FindByIdResult
import com.acme.schwimmgruppe.service.SchwimmgruppeReadService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

/**
 * _Controller_-Klasse für das Lesen mit der GraphQL-Schnittstelle und den Typen aus dem GraphQL-Schema
 *
 * @property service Injiziertes Objekt von [SchwimmgruppeReadService]
 * @constructor Einen SchwimmgruppeQueryController mit einem injizierten [SchwimmgruppeReadService] erzeugen.
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Controller
@Suppress("unused")
class SchwimmgruppeQueryController(private val service: SchwimmgruppeReadService) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Suche anhand der Schwimmgruppe-ID als Pfad Parameter
     * @param id ID der zu suchenden Schwimmgruppe
     * @return Die gefundene Schwimmgruppe
     * @throws NotFoundException falls keine Schwimmgruppe gefunden wurde
     */
    @QueryMapping
    suspend fun schwimmgruppe(@Argument id: SchwimmgruppeId): Schwimmgruppe {
        logger.debug("findId: id={}", id)

        return when (val result = service.findById(id)) {
            is FindByIdResult.Found -> result.schwimmgruppe
            is FindByIdResult.NotFound -> throw NotFoundException(id)
        }
    }

    /**
     * Suche mit Suchkriterien
     * @param suchkriterien Suchkriterien und ihre Werte, z.B. name und B-Jugend
     * @return Die gefundenen Schwimmgruppen als Liste
     * @throws NotFoundException falls keine Schwimmgruppen gefunden wurden
     */
    @QueryMapping
    suspend fun schwimmgruppen(@Argument("input") suchkriterien: Suchkriterien): Collection<Schwimmgruppe> {
        logger.debug("find: suchkriterien={}", suchkriterien)

        // Leere Liste, falls nichts gefunden
        val schwimmgruppen = service.find(suchkriterien.toMultiValueMap())
        logger.debug("find: {}", schwimmgruppen)

        if (schwimmgruppen.isEmpty()) {
            throw NotFoundException(suchkriterien = suchkriterien)
        }

        return schwimmgruppen
    }
}
