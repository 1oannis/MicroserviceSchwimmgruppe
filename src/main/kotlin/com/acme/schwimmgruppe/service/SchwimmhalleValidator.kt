package com.acme.schwimmgruppe.service

import am.ik.yavi.builder.validator
import am.ik.yavi.core.ViolationMessage
import com.acme.schwimmgruppe.entity.Schwimmhalle
import org.springframework.stereotype.Service

/**
 * Validierung von Objekten der Klasse [Schwimmhalle]
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Service
@Suppress("UseDataClass")
class SchwimmhalleValidator {
    /**
     * Ein Validierungsobjekt für die Validierung von Schwimmhallen-Objekten
     */
    val validator = validator {

        Schwimmhalle::bezeichnung {
            notEmpty().message(
                ViolationMessage.of(
                    "schwimmhalle.bezeichnung.notEmpty",
                    "Training facility is required",
                ),
            )
                .lessThanOrEqual(SchwimmgruppeValidator.MAX_LENGTH).message(
                    ViolationMessage.of(
                        "schwimmhalle.bezeichnung.lessThanOrEqual",
                        "The name of the training facility exceeds the max length (40 characters)",
                    ),
                )
        }

        @Suppress("MagicNumber")
        Schwimmhalle::plz {
            notEmpty().message(
                ViolationMessage.of(
                    "schwimmhalle.plz.notEmpty",
                    "ZIP code is required.",
                ),
            )
                .fixedSize(5).message(
                    ViolationMessage.of(
                        "schwimmhalle.plz.fixedSize",
                        "ZIP code does not consist of 5 characters.",
                    ),
                )
                .pattern(PLZ_PATTERN).message(
                    ViolationMessage.of(
                        "schwimmhalle.plz.pattern",
                        "ZIP code does not consist of 5 digits.",
                    ),
                )
        }

        Schwimmhalle::ort {
            notEmpty().message(
                ViolationMessage.of(
                    "schwimmhalle.ort.notEmpty",
                    "A city is required.",
                ),
            )
                .lessThanOrEqual(SchwimmgruppeValidator.MAX_LENGTH).message(
                    ViolationMessage.of(
                        "schwimmhalle.ort.lessThanOrEqual",
                        "A city can be a maximum of 40 characters long.",
                    ),
                )
        }
    }

    /**
     * Konstante für die Validierung einer Adresse.
     */
    companion object {
        /**
         * Konstante für den regulären Ausdruck einer Postleitzahl als 5-stellige Zahl mit führender Null.
         */
        const val PLZ_PATTERN = "\\d{5}"
    }
}
