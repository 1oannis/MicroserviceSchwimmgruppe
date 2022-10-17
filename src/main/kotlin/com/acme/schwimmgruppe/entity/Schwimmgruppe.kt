package com.acme.schwimmgruppe.entity

import com.acme.schwimmgruppe.service.SchwimmgruppeValidator.Companion.TIMEZONE_BERLIN
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REMOVE
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.NamedQuery
import javax.persistence.OneToOne
import javax.persistence.Table

/**
 * Unveränderliche Daten einer Schwimmgruppe.
 *
 * ![UML-Klassendiagramm](../../../images/Schwimmgruppe.svg)
 * @property id Id einer Schwimmgruppe als UUID
 * @property name Gültiger Name einer Schwimmgruppe
 * @property aktiv Trainingsstatus einer Schwimmgruppe
 * @property trainingstermin1 Der erste Trainingstermin für die Schwimmgruppe
 * @property trainingstermin2 Der zweite Trainingstermin für die Schwimmgruppe
 * @property ligaklasse Die Ligaklasse in welcher die Schwimmgruppe antritt
 * @property schwimmhalle Die Schwimmhalle in der das Training stattfindet
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Entity
@Table(name = "schwimmgruppe")
@NamedQuery(
    name = Schwimmgruppe.ALL,
    query = "SELECT s FROM Schwimmgruppe s",
)
@NamedQuery(
    name = Schwimmgruppe.BY_NAME,
    query = """
        SELECT s
        FROM Schwimmgruppe s
        WHERE s.name LIKE :${Schwimmgruppe.PARAM_NAME}
    """,
)
@NamedQuery(
    name = Schwimmgruppe.BY_LIGAKLASSE,
    query = """
        SELECT s
        FROM Schwimmgruppe s
        WHERE s.ligaklasse= :${Schwimmgruppe.PARAM_LIGAKLASSE}
    """,
)
@NamedQuery(
    name = Schwimmgruppe.SCHWIMMHALLE_PREFIX,
    query = """
        SELECT DISTINCT s.bezeichnung
        FROM Schwimmhalle s
        WHERE s.bezeichnung LIKE :${Schwimmgruppe.PARAM_SCHWIMMHALLE}
    """,
)
@Suppress("DataClassShouldBeImmutable")
data class Schwimmgruppe(
    var name: String,

    var aktiv: Boolean,

    var trainingstermin1: LocalDateTime?,

    var trainingstermin2: LocalDateTime?,

    @Convert(converter = LigaklasseTypeConverter::class)
    var ligaklasse: LigaklasseType? = LigaklasseType.KEINE_LIGA,

    @OneToOne(cascade = [PERSIST, REMOVE])
    @JoinColumn(updatable = false)
    var schwimmhalle: Schwimmhalle,

    @CreationTimestamp
    @Suppress("UnusedPrivateMember")
    private val erzeugt: LocalDateTime = LocalDateTime.now(ZoneId.of(TIMEZONE_BERLIN)),

    @UpdateTimestamp
    @Suppress("UnusedPrivateMember")
    private val aktualisiert: LocalDateTime = LocalDateTime.now(ZoneId.of(TIMEZONE_BERLIN)),

    @Id
    @GeneratedValue
    var id: SchwimmgruppeId? = null,
) {

    /**
     * Vergleich mit einem anderen Objekt oder null.
     * @param other Das zu vergleichende Objekt oder null
     * @return True, falls das zu vergleichende Objekt die gleiche Id hat.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schwimmgruppe
        return id != null && id == other.id
    }

    /**
     * Hashwert aufgrund der ID.
     * @return Der Hashwert.
     */
    override fun hashCode() = id?.hashCode() ?: this::class.hashCode()

    /**
     * Ausgabe einer Schwimmgruppe
     * @return Die Schwimmgruppe
     */
    override fun toString(): String = "id=$id, name=$name, aktiv=$aktiv, trainingstermin1=$trainingstermin1, " +
        "trainingstermin2=$trainingstermin2, ligaklasse=$ligaklasse, $schwimmhalle"

    /**
     * Properties überschreiben, z.B. bei PUT-Requests von der REST-Schnittstelle
     * @param neu Ein transientes Schwimmgruppe-Objekt mit den neuen Werten für die Properties
     */
    @Suppress("DataClassContainsFunctions")
    fun set(neu: Schwimmgruppe) {
        name = neu.name
        aktiv = neu.aktiv
        trainingstermin1 = neu.trainingstermin1
        trainingstermin2 = neu.trainingstermin2
        ligaklasse = neu.ligaklasse
        schwimmhalle = neu.schwimmhalle
    }

    /**
     * Konstanten für Named Queries
     */
    companion object {
        private const val PREFIX = "Schwimmgruppe."

        /**
         * Name für die Named Query, mit der alle Schwimmgruppen gesucht werden
         */
        const val ALL = "${PREFIX}all"

        /**
         * Name für die Named Query, mit der Schwimmgruppen anhand des Gruppennamens gesucht werden
         */
        const val BY_NAME = "${PREFIX}byName"

        /**
         * Parameter für den Gruppennamen
         */
        const val PARAM_NAME = "name"

        /**
         * Name für die Named Query, mit der Schwimmgruppen anhand der Ligaklasse gesucht werden
         */
        const val BY_LIGAKLASSE = "${PREFIX}byLigaklasse"

        /**
         * Parameter für die Ligaklasse
         */
        const val PARAM_LIGAKLASSE = "ligaklasse"

        /**
         * Name für die Named Query, mit der ermittelt wird, ob es zum Präfix eine Schwimmhalle gibt
         */
        const val SCHWIMMHALLE_PREFIX = "${PREFIX}schwimmhallePrefix"

        /**
         * Parameter für die Schwimmhalle
         */
        const val PARAM_SCHWIMMHALLE = "bezeichnung"
    }
}

/**
 * Datentyp für die IDs von Schwimmgruppen-Objekten
 */
typealias SchwimmgruppeId = UUID

/**
 * Datentyp für sonstige IDs in DB-Tabellen
 */
typealias DbId = UUID
