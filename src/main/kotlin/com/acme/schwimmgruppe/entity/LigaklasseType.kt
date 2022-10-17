package com.acme.schwimmgruppe.entity

import com.fasterxml.jackson.annotation.JsonValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.AttributeConverter

/**
 * Enum Klasse für Ligaklassen
 * @property value Interner Wert
 * @author Ioannis Theodosiadis
 */
enum class LigaklasseType(val value: String) {

    /**
     *_Keine Liga_ mit dem internen Wert `KEINE` für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    KEINE_LIGA("KEINE"),

    /**
     * _Stadtliga_ mit dem internen Wert `SL` für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    STADTLIGA("SL"),

    /**
     * _Landesliga_ mit dem internen Wert `LL` für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    LANDESLIGA("LL"),

    /**
     * _Zweite Bundesliga_ mit dem internen Wert `2.BL` für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    ZWEITE_BUNDESLIGA("2.BL"),

    /**
     * _Erste Bundesliga_ mit dem internen Wert `1.BL` für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    ERSTE_BUNDESLIGA("1.BL");

    /**
     * Einen enum-Wert als String mit dem internen Wert ausgeben.
     * @return Interner Wert
     */
    @JsonValue
    override fun toString() = value

    /**
     * Companion Object, um aus einem String einen Enum-Wert von LigaklasseType zu bauen
     */
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LigaklasseType::class.java)

        /**
         * Konvertierung eines Strings in einen Enum-Wert
         * @param value Der String, zu dem ein passender Enum-Wert ermittelt werden soll
         * @return Passender Enum-Wert
         */
        fun fromValue(value: String?) = try {
            enumValues<LigaklasseType>().single { ligaklasse -> ligaklasse.value == value }
        } catch (e: NoSuchElementException) {
            logger.warn("Ungueltiger Wert '{}' fuer LigaklasseType: {}", value, e.message)
            null
        }
    }
}

/**
 * Konvertierungsklasse, um die Enum-Werte abgekürzt abzuspeichern
 */
class LigaklasseTypeConverter : AttributeConverter<LigaklasseType?, String> {
    /**
     * Konvertierungsfunktion, um einen Enum-Wert in einen abgekürzten String für die DB zu transformieren
     * @param ligaklasse Der Enum-Wert
     * @return Der abgekürzte String
     */
    override fun convertToDatabaseColumn(ligaklasse: LigaklasseType?) = ligaklasse?.value

    /**
     * Konvertierungsfunktion, um einen abgekürzten String aus einer DB-Spalte in einen Enum-Wert zu transformieren.
     * @param value Der abgekürzte String
     * @return Der Enum-Wert
     */
    override fun convertToEntityAttribute(value: String?) = LigaklasseType.fromValue(value)
}
