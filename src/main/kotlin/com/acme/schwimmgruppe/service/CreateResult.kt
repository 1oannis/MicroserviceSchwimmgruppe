package com.acme.schwimmgruppe.service

import am.ik.yavi.core.ConstraintViolation
import com.acme.schwimmgruppe.entity.Schwimmgruppe

/**
 * Resultat-Typ für [SchwimmgruppeWriteService.create]
 */
sealed interface CreateResult {
    /**
     * Resultat-Typ, wenn eine neue Schwimmgruppe erfolgreich angelegt wurde.
     * @property schwimmgruppe Die neu angelegte Schwimmgruppe
     */
    data class Created(val schwimmgruppe: Schwimmgruppe) : CreateResult

    /**
     * Resultat-Typ, wenn eine Schwimmgruppe wegen Constraint-Verletzungen nicht angelegt wurde.
     * @property violations Die verletzten Constraints
     */
    data class ConstraintViolations(val violations: Collection<ConstraintViolation>) : CreateResult
}
