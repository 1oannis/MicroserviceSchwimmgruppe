package com.acme.schwimmgruppe.graphql

import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component

/**
 * Abbildung von Exceptions auf `GraphQLError`
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Component
class ExceptionResolverAdapter : DataFetcherExceptionResolverAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Abbildung der Exceptions aus [SchwimmgruppeQueryController] und [SchwimmgruppeMutationController]
     * @param ex Exception aus dem `Controller`
     * @param env Environment-Object
     */
    override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment) =
        when (ex) {
            is NotFoundException -> NotFoundError(ex.id, ex.suchkriterien)
            else -> super.resolveToSingleError(ex, env)
        }

    /**
     * Abbildung der Exceptions aus [SchwimmgruppeQueryController] und [SchwimmgruppeMutationController] auf
     * `GraphQLError`
     * @param ex Exception aus dem `Controller`
     * @param env Environment-Objekt
     */
    override fun resolveToMultipleErrors(ex: Throwable, env: DataFetchingEnvironment) =
        @Suppress("UseIfInsteadOfWhen")
        when (ex) {
            is ConstraintViolationsException ->
                ex.violations
                    .map { violation -> ConstraintViolationError(violation) }
                    .onEach { error -> logger.debug("error.message={}", error.message) }
            else -> super.resolveToMultipleErrors(ex, env)
        }
}
