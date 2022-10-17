package com.acme.schwimmgruppe.graphql

import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.NOT_FOUND

/**
 * Exception, falls mit dem Anwendungskern keine Schwimmgruppe gefunden wird
 * @property id ID der nicht vorhandenen Schwimmgruppe
 * @property suchkriterien Suchkriterien, zu denen es keine Schwimmgruppe gibt
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
class NotFoundException(
    val id: SchwimmgruppeId? = null,
    val suchkriterien: Suchkriterien? = null,
) : SchwimmgruppeGraphQlException()

/**
 * Fehlerklasse für GraphQL, falls eine [NotFoundException] geworfen wurde. Die Abbildung erfolgt in
 * [ExceptionResolverAdapter]
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
class NotFoundError(
    private val id: SchwimmgruppeId? = null,
    private val suchkriterien: Suchkriterien? = null,
) : GraphQLError {
    /**
     * `ErrorType` auf `NOT_FOUND` setzen
     */
    override fun getErrorType() = NOT_FOUND

    /**
     * Message innerhalb von _Errors_ beim Response für einen GraphQL-Request.
     */
    override fun getMessage() = if (id == null) {
        "Keine Schwimmgruppe gefunden: $suchkriterien"
    } else {
        "Keine Schwimmgruppe mit der ID $id gefunden"
    }

    /**
     * Keine Angabe von Zeilen- und Spaltennummer der GraphQL-Query, falls keine Schwimmgruppe gefunden wurde.
     */
    override fun getLocations() = null
}
