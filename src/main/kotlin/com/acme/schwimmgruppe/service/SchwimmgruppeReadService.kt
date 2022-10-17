package com.acme.schwimmgruppe.service

import com.acme.schwimmgruppe.entity.LigaklasseType
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.withTimeout
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory
import org.hibernate.reactive.mutiny.find
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap

/**
 * Anwendungslogik für Schwimmgruppen
 *
 * ![UML-Klassendiagramm](../../../images/SchwimmgruppeReadService.svg)
 * @constructor Einen SchwimmgruppenService erzeugen
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Service
class SchwimmgruppeReadService(
    private val factory: SessionFactory,
    @Lazy private val queryBuilder: QueryBuilder,
) {
    private val logger = LoggerFactory.getLogger(SchwimmgruppeReadService::class.java)

    /**
     * Eine Schwimmgruppe anhand ihrer ID suchen.
     * @param id Die ID der gesuchen Schwimmgruppe
     * @return [FindByIdResult.Found], im Fehlerfall [FindByIdResult.NotFound]
     */
    suspend fun findById(id: SchwimmgruppeId): FindByIdResult {
        logger.debug("findById: id={}", id)

        val schwimmgruppe = factory.withSession { session ->
            session.find<Schwimmgruppe>(id)
        }.awaitSuspending()

        return if (schwimmgruppe == null) {
            FindByIdResult.NotFound(id)
        } else {
            FindByIdResult.Found(schwimmgruppe)
        }
    }

    /**
     * Eine Schwimmgruppe anhand ihrer ID suchen -
     * `public` für [SchwimmgruppeWriteService]
     * @param id Die ID der gesuchten Schwimmgruppe
     * @return Die gefundene Schwimmgruppe
     */
    @Suppress("unused")
    suspend fun findByIdWrite(id: SchwimmgruppeId): Schwimmgruppe? {
        val schwimmgruppe = withTimeout(TIMEOUT_SHORT) {
            factory.withSession { session ->
                session.find<Schwimmgruppe>(id) // Pures lesen, KEINE Transaktion
            }.awaitSuspending()
        }
        logger.debug("findByIdWrite: {}", schwimmgruppe)
        return schwimmgruppe
    }

    /**
     * Schwimmgruppen anhand von Suchkriterien als List suchen wie sie später auch von der DB kommen.
     * @param suchkriterien Die Suchkriterien
     * @return Die gefundenen Schwimmgruppen oder eine leere Liste
     */
    @Suppress("ReturnCount")
    suspend fun find(suchkriterien: MultiValueMap<String, String>): Collection<Schwimmgruppe> {
        logger.debug("find: suchkriterien={}", suchkriterien)

        if (suchkriterien.isEmpty()) {
            return findAll()
        }

        if (suchkriterien.size == 1) {
            val name = suchkriterien["name"]
            if (name?.size == 1) {
                return findByName(name[0])
            }

            val ligaklasse = suchkriterien["ligaklasse"]
            if (ligaklasse?.size == 1) {
                return findByLigaklasse(LigaklasseType.fromValue(ligaklasse[0]))
            }
        }

        val criteriaQuery = when (val builderResult = queryBuilder.build(suchkriterien)) {
            is QueryBuilderResult.Success -> builderResult.criteriaQuery
            is QueryBuilderResult.Failure -> return emptyList()
        }

        val schwimmgruppen = withTimeout(TIMEOUT_LONG) {
            factory.withSession { session ->
                session.createQuery(criteriaQuery).resultList
            }.awaitSuspending()
        }
        logger.debug("find: {}", schwimmgruppen)

        return schwimmgruppen
    }

    /**
     * Alle Schwimmgruppen als Liste ermitteln, wie sie später auch von der DB kommen
     * @return Alle Schwimmgruppen
     */
    private suspend fun findAll(): Collection<Schwimmgruppe> = withTimeout(TIMEOUT_LONG) {
        factory.withSession { session ->
            session.createNamedQuery<Schwimmgruppe>(Schwimmgruppe.ALL)
                .resultList
        }.awaitSuspending()
    }

    /**
     * Schwimmgruppe anhand ihres Namens suchen
     * @param name Der Name der gesuchten Schwimmgruppen
     * @return Die gefundenen Schwimmgruppen oder eine leere Liste
     */
    private suspend fun findByName(name: String): Collection<Schwimmgruppe> = withTimeout(TIMEOUT_SHORT) {
        factory.withSession { session ->
            session.createNamedQuery<Schwimmgruppe>(Schwimmgruppe.BY_NAME)
                .setParameter(Schwimmgruppe.PARAM_NAME, "%$name%")
                .resultList
        }.awaitSuspending()
    }

    /**
     * Schwimmgruppe anhand ihrer Schwimmhalle suchen
     * @param ligaklasse Die Schwimmhalle der gesuchten Schwimmgruppen
     * @return Die gefundenen Schwimmgruppen oder eine leere Liste
     */
    private suspend fun findByLigaklasse(
        ligaklasse: LigaklasseType?,
    ): Collection<Schwimmgruppe> = withTimeout(TIMEOUT_SHORT) {
        logger.debug("findByLigaklasse: ligaklasse={}", ligaklasse)
        factory.withSession { session ->
            session.createNamedQuery<Schwimmgruppe>(Schwimmgruppe.BY_LIGAKLASSE)
                .setParameter(Schwimmgruppe.PARAM_LIGAKLASSE, ligaklasse)
                .resultList
        }.awaitSuspending()
    }

    /**
     * Abfrage, welche Ligaklassen es zu einem Präfix gibt.
     * @param prefix Ligaklasse-Präfix als Pfavariable
     * @return Die passenden Ligaklassen oder Statuscode 404, falls es keine gibt
     */
    suspend fun findSchwimmhalleByPrefix(prefix: String): Collection<String> = withTimeout(TIMEOUT_SHORT) {
        factory.withSession { session ->
            session.createNamedQuery<String>(Schwimmgruppe.SCHWIMMHALLE_PREFIX)
                .setParameter(Schwimmgruppe.PARAM_SCHWIMMHALLE, "$prefix%")
                .resultList
                .map { list ->
                    list.map { str -> str }
                }
        }.awaitSuspending()
    }

    private companion object {
        /**
         * Konstante für ein kurzes Timeout
         */
        const val TIMEOUT_SHORT = 100000L

        /**
         * Konstante für ein langes Timeout
         */
        const val TIMEOUT_LONG = 20000L
    }
}
