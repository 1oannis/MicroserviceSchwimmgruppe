package com.acme.schwimmgruppe.graphql

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

/**
 * Eine _Value_-Klasse für Eingabedaten passend zu Suchkriterien aus dem GraphQL-Schema
 * @property name Name der Gruppe
 * @property aktiv Aktivitätsstatus
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
data class Suchkriterien(val name: String?, val aktiv: String?) {
    /**
     * Konvertierung in eine Map
     * @return Das konvertierte Map-Objekt
     */
    fun toMultiValueMap(): MultiValueMap<String, String> {
        val map = LinkedMultiValueMap<String, String>()

        if (name != null) {
            map["name"] = name
        }
        if (aktiv != null) {
            map["aktiv"] = aktiv
        }

        return map
    }
}
