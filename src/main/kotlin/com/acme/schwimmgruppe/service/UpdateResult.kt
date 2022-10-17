package com.acme.schwimmgruppe.service

import am.ik.yavi.core.ConstraintViolation
import com.acme.schwimmgruppe.entity.Schwimmgruppe

/**
 * Resultat-Typ für [SchwimmgruppeWriteService.update]
 */
sealed interface UpdateResult {
    /**
     * Resultat-Typ, wenn eine Schwimmgruppe erfolgreich aktualisiert wurde.
     * @property schwimmgruppe Die aktualisierte Schwimmgruppe
     */
    data class Updated(val schwimmgruppe: Schwimmgruppe) : UpdateResult

    /**
     * Resultat-Typ, wenn eine Schwimmgruppe wegen Constraint-Verletzungen nicht aktualisiert wurde.
     * @property violations Die verletzten Constraints
     */
    data class ConstraintViolations(val violations: Collection<ConstraintViolation>) : UpdateResult

    /**
     * Resultat-Typ, wenn eine nicht-vorhandene Schwimmgruppe aktualisiert werden sollte.
     */
    object NotFound : UpdateResult
}
