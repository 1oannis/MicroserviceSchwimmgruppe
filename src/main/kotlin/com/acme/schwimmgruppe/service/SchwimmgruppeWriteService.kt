package com.acme.schwimmgruppe.service

import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.withTimeout
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory
import org.hibernate.reactive.mutiny.find
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Suppress("TooManyFunctions")
/**
 * Anwendungslogik für das Schreiben von Schwimmgruppen.
 * @constructor Einen SchwimmgruppeService mit einem injizierten `ValidatorFactory` erzeugen.
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Service
class SchwimmgruppeWriteService(
    private val factory: SessionFactory,
    @Lazy private val validator: SchwimmgruppeValidator,
    @Lazy private val readService: SchwimmgruppeReadService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Eine neue Schwimmgruppe anlegen.
     * @param schwimmgruppe Das Objekt der neuen anzulegenden Schwimmgruppe.
     * @return Ein Resultat-Objekt mit entweder der neu angelegten Schwimmgruppe oder mit dem Fehlermeldungsobjekt
     */
    suspend fun create(schwimmgruppe: Schwimmgruppe): CreateResult {
        logger.debug("create: {}", schwimmgruppe)
        val violations = validator.validate(schwimmgruppe)
        if (violations.isNotEmpty()) {
            logger.debug("create: violations={}", violations)
            return CreateResult.ConstraintViolations(violations)
        }
        logger.trace("create: Keine \"Constraint Violations\"")

        withTimeout(TIMEOUT_LONG) {
            factory.withTransaction { session, _ ->
                session.persist(schwimmgruppe)
            }.awaitSuspending()
        }
        logger.debug("create: {}", schwimmgruppe)
        return CreateResult.Created(schwimmgruppe)
    }

    /**
     * Eine vorhandene Schwimmgruppe aktualisieren. Merge/PUT
     * @param schwimmgruppe Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID der zu aktualisierenden Schwimmgruppe
     * @return Ein Resultatobjekt mit entweder der aktualisierten Schwimmgruppe oder mit dem Fehlermeldungsobjekt
     */
    @Suppress("ReturnCount")
    suspend fun update(schwimmgruppe: Schwimmgruppe, id: SchwimmgruppeId): UpdateResult {
        logger.debug("update: {}", schwimmgruppe)
        logger.debug("update: id={}", id)
        val violations = validator.validate(schwimmgruppe)
        if (violations.isNotEmpty()) {
            logger.debug("update: violations={}", violations)
            return UpdateResult.ConstraintViolations(violations)
        }
        logger.trace("update: Keine \"Constraint Violations\"")

        val schwimmgruppeDb = readService.findByIdWrite(id) ?: return UpdateResult.NotFound
        logger.trace("update: schwimmgruppeDb={}", schwimmgruppeDb)

        return update(schwimmgruppe, schwimmgruppeDb)
    }

    private suspend fun update(schwimmgruppe: Schwimmgruppe, schwimmgruppeDb: Schwimmgruppe): UpdateResult {
        schwimmgruppeDb.set(schwimmgruppe)

        logger.trace("update: vor session.merge() = {}", schwimmgruppeDb)
        val result = withTimeout(TIMEOUT_LONG) {
            factory.withTransaction { session, _ ->
                logger.trace("session vor detach: {}", session)
                session.detach(schwimmgruppeDb)
                logger.trace("session nach detach: {}", session)
                session.merge(schwimmgruppeDb)
            }.awaitSuspending()
        }
        logger.trace("update: nach session.merge(): {}", result)

        return UpdateResult.Updated(result)
    }

    /**
     * Eine vorhandene Schwimmgruppe in der DB löschen.
     * @param id Die ID der zu löschenden Schwimmgruppe.
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @Suppress("UseIfInsteadOfWhen")
    suspend fun deleteById(id: SchwimmgruppeId) {
        withTimeout(TIMEOUT_SHORT) {
            logger.debug("deleteById: id={}", id)
            factory.withTransaction { session, _ ->
                session.find<Schwimmgruppe>(id)
                    .chain { schwimmgruppe ->
                        when (schwimmgruppe) {
                            null -> {
                                logger.trace("deleteById: kein Kunde gefunden")
                                Uni.createFrom().nullItem()
                            }
                            else -> {
                                logger.trace("deleteById: {}", schwimmgruppe)
                                session.remove(schwimmgruppe)
                            }
                        }
                    }
            }.awaitSuspending()
        }
    }

    private companion object {
        /**
         * Konstante für ein kurzes Timeout
         */
        const val TIMEOUT_SHORT = 50000L

        /**
         * Konstante für ein langes Timeout
         */
        const val TIMEOUT_LONG = 20000L
    }
}
