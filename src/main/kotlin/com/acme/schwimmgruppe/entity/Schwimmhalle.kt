package com.acme.schwimmgruppe.entity

import net.minidev.json.annotate.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

/**
 * Schwimmhallendaten für die Anwendungslogik und zum Abspeichern in der DB.
 * @property id Generierte UUID
 * @property bezeichnung Bezeichnung der Schwimmhalle als unveränderliches Pflichtfeld
 * @property plz Die 5-Stellige Postleitzahl als unveränderliches Pflichtfeld
 * @property ort Der Ort als unveränderliches Pflichtfeld
 * @constructor Erzeugt ein Objekt mit Bezeichnung, Postleitzahl und Ort
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Entity
@Table(name = "schwimmhalle")
data class Schwimmhalle(
    @Id
    @GeneratedValue
    @JsonIgnore
    val id: DbId? = null,

    val bezeichnung: String = "",

    val plz: String = "",

    val ort: String = "",
)
