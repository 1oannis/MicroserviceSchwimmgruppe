package com.acme.schwimmgruppe.service

import com.acme.schwimmgruppe.entity.LigaklasseType
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.Schwimmhalle
import org.hibernate.reactive.mutiny.Mutiny
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.criteria.createQuery
import javax.persistence.criteria.from
import javax.persistence.criteria.get

/**
 * Singleton-Klasse, um _Criteria Queries_ für _Hibernate_ zu bauen.
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Suppress("TooManyFunctions")
@Service
class QueryBuilder(private val factory: Mutiny.SessionFactory) {
    private val logger: Logger = LoggerFactory.getLogger(QueryBuilder::class.java)

    /**
     * Aus einer `MultiValueMap` von _Spring_ wird eine Criteria Query für Hibernate gebaut, um flexibel nach
     * Schwimmgruppen suchen zu können.
     * @param queryParams Die Query-Parameter in einer `MultiValueMap`.
     * @return [QueryBuilderResult] abhängig von den Query-Parametern.
     */
    @Suppress("ReturnCount")
    fun build(queryParams: MultiValueMap<String, String>): QueryBuilderResult {
        logger.debug("build: queryParams={}", queryParams)

        val criteriaBuilder = factory.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery<Schwimmgruppe>()
        val schwimmgruppeRoot = criteriaQuery.from(Schwimmgruppe::class)

        if (queryParams.isEmpty()) {
            // keine Suchkriterien
            return QueryBuilderResult.Success(criteriaQuery)
        }

        val predicates = queryParams.map { (paramName, paramValues) ->
            getPredicate(paramName, paramValues, criteriaBuilder, schwimmgruppeRoot)
        }.filterNotNull()

        if (predicates.isEmpty()) {
            return QueryBuilderResult.Failure
        }
        logger.debug("build: #predicates={}", predicates.size)

        @Suppress("SpreadOperator")
        val predicate = criteriaBuilder.and(*predicates.toTypedArray()) // variable Argumentleiste
        criteriaQuery.where(predicate)

        return QueryBuilderResult.Success(criteriaQuery)
    }

    private fun getPredicate(
        paramName: String,
        paramValues: Collection<String>?,
        criteriaBuilder: CriteriaBuilder,
        schwimmgruppeRoot: Root<Schwimmgruppe>,
    ): Predicate? {
        if (paramValues?.size != 1) {
            return null
        }

        logger.debug("getPredicate: propertyValues={}", paramValues)

        val value = paramValues.first()
        return when (paramName) {
            name -> getPredicateName(value, criteriaBuilder, schwimmgruppeRoot)
            aktiv -> getPredicateAktiv(value, criteriaBuilder, schwimmgruppeRoot)
            ligaklasse -> getPredicateLigaklasse(value, criteriaBuilder, schwimmgruppeRoot)
            schwimmhalle -> getPredicateSchwimmhalle(value, criteriaBuilder, schwimmgruppeRoot)
            plz -> getPredicatePlz(value, criteriaBuilder, schwimmgruppeRoot)
            ort -> getPredicateOrt(value, criteriaBuilder, schwimmgruppeRoot)
            else -> null
        }
    }

    // Name: Suche nach Teilstrings
    private fun getPredicateName(
        name: String,
        criteriaBuilder: CriteriaBuilder,
        schwimmgruppeRoot: Root<Schwimmgruppe>,
    ) = criteriaBuilder.like(schwimmgruppeRoot.get(Schwimmgruppe::name), "%$name%")

    private fun getPredicateAktiv(
        aktiv: String,
        criteriaBuilder: CriteriaBuilder,
        schwimmgruppeRoot: Root<Schwimmgruppe>,
    ) = criteriaBuilder.equal(schwimmgruppeRoot.get(Schwimmgruppe::aktiv), "%$aktiv%")

    private fun getPredicateLigaklasse(
        ligaklasseValue: String,
        criteriaBuilder: CriteriaBuilder,
        schwimmgruppeRoot: Root<Schwimmgruppe>,
    ) = criteriaBuilder.equal(
        schwimmgruppeRoot.get(Schwimmgruppe::ligaklasse),
        LigaklasseType.fromValue(ligaklasseValue),
    )

    // Schwimmhalle Bezeichnung: Suche mit Praefix
    private fun getPredicateSchwimmhalle(
        bezeichnung: String,
        criteriaBuilder: CriteriaBuilder,
        schwimmgruppeRoot: Root<Schwimmgruppe>,
    ) = criteriaBuilder.like(
        schwimmgruppeRoot
            .get(Schwimmgruppe::schwimmhalle)
            .get(Schwimmhalle::bezeichnung),
        "$bezeichnung%",
    )

    // PLZ: Suche mit Praefix
    private fun getPredicatePlz(
        plz: String,
        criteriaBuilder: CriteriaBuilder,
        schwimmgruppeRoot: Root<Schwimmgruppe>,
    ) = criteriaBuilder.like(
        schwimmgruppeRoot
            .get(Schwimmgruppe::schwimmhalle)
            .get(Schwimmhalle::plz),
        "$plz%",
    )

    // Ort: Suche mit Praefix
    private fun getPredicateOrt(
        ort: String,
        criteriaBuilder: CriteriaBuilder,
        schwimmgruppeRoot: Root<Schwimmgruppe>,
    ) = criteriaBuilder.like(
        schwimmgruppeRoot
            .get(Schwimmgruppe::schwimmhalle)
            .get(Schwimmhalle::ort),
        "$ort%",
    )

    private companion object {
        private const val name = "name"
        private const val aktiv = "aktiv"
        private const val ligaklasse = "ligaklasse"
        private const val schwimmhalle = "schwimmhalle"
        private const val plz = "plz"
        private const val ort = "ort"
    }
}

/**
 * Resultat-Typ für [QueryBuilder.build]
 */
sealed interface QueryBuilderResult {
    /**
     * Resultat-Typ, wenn die Query-Parameter korrekt sind.
     * @property criteriaQuery Die CriteriaQuery
     */
    data class Success(val criteriaQuery: CriteriaQuery<Schwimmgruppe>) : QueryBuilderResult

    /**
     * Resultat-Typ, wenn mindestens 1 Query-Parameter falsch ist.
     */
    object Failure : QueryBuilderResult
}
