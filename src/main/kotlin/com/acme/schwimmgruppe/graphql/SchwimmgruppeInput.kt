package com.acme.schwimmgruppe.graphql

import com.acme.schwimmgruppe.entity.LigaklasseType
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.Schwimmhalle
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/**
 * Eine _Value_-Klasse für Eingabedaten passend zu SchwimmgruppeInput aus dem GraphQL-Schema
 * @property name Gültiger Name einer Schwimmgruppe
 * @property aktiv Trainingsstatus einer Schwimmgruppe
 * @property trainingstermin1 Der erste Trainingstermin für die Schwimmgruppe
 * @property trainingstermin2 Der zweite Trainingstermin für die Schwimmgruppe
 * @property ligaklasse Die Ligaklasse in welcher die Schwimmgruppe antritt
 * @property schwimmhalle Die Schwimmhalle in der das Training stattfindet
 * @constructor Ein SchwimmgruppeInput-Objekt mit den empfangenen Properties erzeugen.
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
data class SchwimmgruppeInput(
    val name: String,

    val aktiv: Boolean,

    val trainingstermin1: String?,

    val trainingstermin2: String?,

    val ligaklasse: LigaklasseType = LigaklasseType.KEINE_LIGA,

    val schwimmhalle: SchwimmhalleInput,
) {
    /**
     * Konvertierung in ein Objekt der Entity-Klasse [Schwimmgruppe]
     * @return Das konvertierte Schwimmgruppe-Objekt
     */
    fun toSchwimmgruppe(): Schwimmgruppe {
        val trainingstermin1 = try {
            LocalDateTime.parse(trainingstermin1)
        } catch (e: DateTimeParseException) {
            null
        }
        val trainingstermin2 = try {
            LocalDateTime.parse(trainingstermin2)
        } catch (e: DateTimeParseException) {
            null
        }
        val schwimmhalle = Schwimmhalle(
            bezeichnung = schwimmhalle.bezeichnung,
            plz = schwimmhalle.plz,
            ort = schwimmhalle.ort,
        )

        return Schwimmgruppe(
            id = null,
            name = name,
            aktiv = aktiv,
            trainingstermin1 = trainingstermin1,
            trainingstermin2 = trainingstermin2,
            ligaklasse = ligaklasse,
            schwimmhalle = schwimmhalle,
        )
    }
}

/**
 * Schwimmhallendaten.
 * @property bezeichnung Bezeichnung der Schwimmhalle als unveränderliches Pflichtfeld
 * @property plz Die 5-Stellige Postleitzahl als unveränderliches Pflichtfeld
 * @property ort Der Ort als unveränderliches Pflichtfeld
 * @constructor Erzeugt ein Objekt mit Bezeichnung, Postleitzahl und Ort
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
data class SchwimmhalleInput(val bezeichnung: String, val plz: String, val ort: String)
