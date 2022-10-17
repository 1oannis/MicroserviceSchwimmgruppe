package com.acme.schwimmgruppe.rest

import com.acme.schwimmgruppe.entity.LigaklasseType
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.Schwimmhalle
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.LocalDateTime

/**
 * Unveränderliche Daten einer Schwimmgruppe an der REST-API
 * @property name Gültiger Name einer Schwimmgruppe
 * @property aktiv Trainingsstatus einer Schwimmgruppe
 * @property trainingstermin1 Der erste Trainingstermin für die Schwimmgruppe
 * @property trainingstermin2 Der zweite Trainingstermin für die Schwimmgruppe
 * @property ligaklasse Die Ligaklasse in welcher die Schwimmgruppe antritt
 * @property schwimmhalle Die Schwimmhalle in der das Training stattfindet
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@JsonPropertyOrder("name", "aktiv", "trainingstermin1", "trainingstermin2", "ligaklasse", "schwimmhalle")
@Relation(collectionRelation = "schwimmgruppen", itemRelation = "schwimmgruppe")
data class SchwimmgruppeModel(
    val name: String,

    val aktiv: Boolean,

    val trainingstermin1: LocalDateTime?,

    val trainingstermin2: LocalDateTime?,

    val ligaklasse: LigaklasseType? = LigaklasseType.KEINE_LIGA,

    val schwimmhalle: Schwimmhalle,
) : RepresentationModel<SchwimmgruppeModel>() {
    constructor(schwimmgruppe: Schwimmgruppe) : this(
        schwimmgruppe.name,
        schwimmgruppe.aktiv,
        schwimmgruppe.trainingstermin1,
        schwimmgruppe.trainingstermin2,
        schwimmgruppe.ligaklasse,
        schwimmgruppe.schwimmhalle,
    )

    /**
     * Vergleich mit einem anderen Objekt oder null
     * @param other Das zu vergleichende Objekt oder null
     * @return True, falls das zu vergleichende Objekt gleichen _name_ und _ligaklasse_ hat,
     * sonst false
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SchwimmgruppeDTO
        return name == other.name && ligaklasse == other.ligaklasse
    }

    /**
     * Hashwert aufgrund von _name_ xor _ligaklasse_
     * @return Hashwert
     */
    override fun hashCode() = name.hashCode().xor(ligaklasse.hashCode())
}
