package com.acme.schwimmgruppe.graphql

import com.acme.schwimmgruppe.service.CreateResult.ConstraintViolations
import com.acme.schwimmgruppe.service.CreateResult.Created
import com.acme.schwimmgruppe.service.SchwimmgruppeWriteService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

/**
 * Eine _Controller_-Klasse für das Schreiben mit der GraphQL-Schnittstelle und den Typen aus dem GraphQL-Schema.
 * @property service Injiziertes Objekt von [SchwimmgruppeWriteService]
 * @constructor Einen SchwimmgruppeMutationController mit einem injizierten [SchwimmgruppeWriteService] erzeugen.
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Controller
@Suppress("unused")
class SchwimmgruppeMutationController(private val service: SchwimmgruppeWriteService) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Eine neue Schwimmgruppe anlegen
     * @param schwimmgruppeInput Die Eingabedaten für eine neue Schwimmgruppe
     * @return Die generierte ID für die neue Schwimmgruppe
     * @throws ConstraintViolationsException falls Constraints verletzt sind
     */
    @MutationMapping
    suspend fun create(@Argument("input") schwimmgruppeInput: SchwimmgruppeInput): CreatePayload {
        logger.debug("create: schwimmgruppeInput={}", schwimmgruppeInput)

        return when (val result = service.create(schwimmgruppeInput.toSchwimmgruppe())) {
            is Created -> CreatePayload(result.schwimmgruppe.id)
            is ConstraintViolations -> throw ConstraintViolationsException(result.violations)
        }
    }
}
