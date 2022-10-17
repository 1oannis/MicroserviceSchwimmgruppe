package com.acme.schwimmgruppe.graphql

import com.acme.schwimmgruppe.entity.SchwimmgruppeId

/**
 * Entity-Klasse für das Resultat, wenn an der GraphQL-Schnittstelle einer neuen Schwimmgruppe angelegt wurde
 * @property id ID der neu angelegten Schwimmgruppe
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
data class CreatePayload(val id: SchwimmgruppeId?)
