package com.acme.schwimmgruppe.service

import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.SchwimmgruppeId

/**
 * Resultat-Typ für [SchwimmgruppeReadService.findById].
 */
sealed interface FindByIdResult {
    /**
     * Resultat-Typ, wenn die Schwimmgruppe gefunden wurde.
     * @property schwimmgruppe Die gefundene Schwimmgruppe
     */
    data class Found(val schwimmgruppe: Schwimmgruppe) : FindByIdResult

    /**
     * Resultat-Typ, wenn keine Schwimmgruppe gefunden wurde.
     * @property id Die Id der nicht existierenden Schwimmgruppe
     */
    data class NotFound(val id: SchwimmgruppeId) : FindByIdResult
}
