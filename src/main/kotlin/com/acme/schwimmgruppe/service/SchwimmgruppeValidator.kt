package com.acme.schwimmgruppe.service

import am.ik.yavi.builder.validator
import am.ik.yavi.core.ViolationMessage
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.time.ZoneId

/**
 * Validierung von Objekten der Klasse [Schwimmgruppe]
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Service
class SchwimmgruppeValidator(schwimmhalleValidator: SchwimmhalleValidator) {
    private val validator = validator {
        Schwimmgruppe::name {
            notEmpty().message(
                ViolationMessage.of(
                    "schwimmgruppe.name.notEmpty",
                    "Groupname is required",
                ),
            )
                .lessThanOrEqual(MAX_LENGTH).message(
                    ViolationMessage.of(
                        "schwimmgruppe.name.lessThanOrEqual",
                        "The Group name exceeds the max length (40 characters)",
                    ),
                )
                .pattern(NAME_PATTERN).message(
                    ViolationMessage.of(
                        "schwimmgruppe.name.pattern",
                        "The Groupname must start with a capital letter (A-F) " +
                            "followed by -Jugend",
                    ),
                )
        }

        Schwimmgruppe::schwimmhalle.nest(schwimmhalleValidator.validator)

        Schwimmgruppe::trainingstermin1 {
            after { now(ZoneId.of(TIMEZONE_BERLIN)) }.message(
                ViolationMessage.of(
                    "schwimmgruppe.trainingstermin1.after",
                    "The training date 1 must be in the future.",
                ),
            )
        }

        Schwimmgruppe::trainingstermin2 {
            after { now(ZoneId.of(TIMEZONE_BERLIN)) }.message(
                ViolationMessage.of(
                    "schwimmgruppe.trainingstermin2.after",
                    "The training date 2 must be in the future.",
                ),
            )
        }
    }

    /**
     * Validierung eines Entity-Objekts der Klasse [Schwimmgruppe]
     * @param schwimmgruppe Das zu validierende Schwimmgruppe-Objekt
     * @return Eine Liste mit den Verletzungen der Constraints oder eine leere Liste
     */
    fun validate(schwimmgruppe: Schwimmgruppe) = validator.validate(schwimmgruppe)

    /**
     * Konstanten für Validierung
     */
    companion object {
        /**
         * Maximale Länge eines Strings
         */
        const val MAX_LENGTH = 40

        /**
         * Muster für gültigen Namen
         */
        const val NAME_PATTERN = "[A-Z][-][Jugend]{6,6}"

        /**
         * Mitteleuropäische Zeitzone
         */
        const val TIMEZONE_BERLIN = "Europe/Berlin"
    }
}
