package com.acme.schwimmgruppe.rest

/**
 * Allgemeiner Response-Body für Schreiboperationen.
 */
sealed interface GenericBody {
    /**
     * Body, wenn es mehrere Werte, wie z.B. Constraint-Verletzungen, gibt.
     * @property values Die verletzten Constraints
     */
    data class Values(val values: Map<String, String>) : GenericBody
}
