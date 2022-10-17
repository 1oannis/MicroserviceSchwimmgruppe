package com.acme.schwimmgruppe.graphql

import am.ik.yavi.core.ConstraintViolation
import graphql.GraphQLError
import org.springframework.graphql.execution.ErrorType.BAD_REQUEST

/**
 * Exception, falls die Werte für die neu anzulegende Schwimmgruppe nicht valide sind
 * @property violations Die verletzten Constraints
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
class ConstraintViolationsException(val violations: Collection<ConstraintViolation>) : SchwimmgruppeGraphQlException()

/**
 * Fehlerklasse für GraphQL, falls eine [ConstraintViolationsException] geworfen wurde. Die Abbildung
 * erfolgt in [ExceptionResolverAdapter]
 * @property violation Das verletzte Constraint
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
class ConstraintViolationError(private val violation: ConstraintViolation) : GraphQLError {
    /**
     * `ErrorType` auf `BAD_REQUEST` setzen.
     * @return `BAD_REQUEST`
     */
    override fun getErrorType() = BAD_REQUEST

    /**
     * Message innerhalb von _Errors_ beim Response für einen GraphQL-Request.
     * @return _Message Key_ zum verletzten Constraint
     */
    override fun getMessage() = "${violation.messageKey()}: ${violation.message()}"

    /**
     * Pfadangabe von der Wurzel bis zum fehlerhaften Datenfeld
     * @return Liste der Datenfelder von der Wurzel bis zum Fehler
     */
    override fun getPath() = listOf("input") + violation.name().split('.')

    /**
     * Keine Angabe von Zeilen- und Spaltennummer der GraphQL-Mutation, falls Constraints verletzt sind.
     * @return null
     */
    override fun getLocations() = null
}
