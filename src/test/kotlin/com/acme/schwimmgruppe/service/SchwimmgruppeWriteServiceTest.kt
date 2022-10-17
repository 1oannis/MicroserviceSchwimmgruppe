package com.acme.schwimmgruppe.service

import com.acme.schwimmgruppe.entity.LigaklasseType
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.Schwimmhalle
import com.acme.schwimmgruppe.service.CreateResult.Created
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.smallrye.mutiny.Uni
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.JRE
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.get
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.function.BiFunction
import kotlin.random.Random

@Tag("service")
@Tag("service_write")
@DisplayName("WriteService - Anwendungskern fuer Schreiben testen")
@ExtendWith(MockKExtension::class)
@EnabledForJreRange(min = JRE.JAVA_17, max = JRE.JAVA_19)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ExperimentalCoroutinesApi
@Suppress(
    "ReactorUnusedPublisher",
    "ReactiveStreamsUnusedPublisher",
    "UndocumentedPublicClass",
    "UndocumentedPublicFunction",
)
class SchwimmgruppeWriteServiceTest {
    private var sessionFactory = mockk<Mutiny.SessionFactory>()
    private var validator = SchwimmgruppeValidator(schwimmhalleValidator = SchwimmhalleValidator())
    private val queryBuilder = QueryBuilder(sessionFactory)

    private val readService = SchwimmgruppeReadService(sessionFactory, queryBuilder)
    private val writeService = SchwimmgruppeWriteService(sessionFactory, validator, readService)

    @Test
    @Order(100)
    fun `Immer erfolgreich`() {
        true shouldBe true
    }

    @Suppress("ClassName")
    @Nested
    inner class `ERZEUGEN - einer Schwimmgruppe` {
        @ParameterizedTest
        @CsvSource("$NAME, $BEZEICHNUNG, $PLZ, $ORT")
        @Order(1000)
        fun `Neue Schwimmgruppe abspeichern`(args: ArgumentsAccessor) = runTest {
            // given
            val name = args.get<String>(0)
            val bezeichnung = args.get<String>(1)
            val plz = args.get<String>(2)
            val ort = args.get<String>(3)
            // session.persist()
            every {
                sessionFactory.withTransaction(any<BiFunction<Mutiny.Session, Mutiny.Transaction, Uni<Void>>>())
            } returns Uni.createFrom().nullItem()
            val schwimmgruppeMock = createSchwimmgruppeMock(name, bezeichnung, plz, ort)
            // when
            val result = writeService.create(schwimmgruppeMock)
            // then
            result.shouldBeInstanceOf<Created>()
            // Destructing
            val (schwimmgruppe) = result
            assertSoftly(schwimmgruppe) {
                this.name shouldBe name
                this.schwimmhalle.bezeichnung shouldBe bezeichnung
                this.schwimmhalle.plz shouldBe plz
                this.schwimmhalle.ort shouldBe ort
            }
        }
    }

    private fun createSchwimmgruppeMock(name: String, bezeichnung: String, plz: String, ort: String): Schwimmgruppe {
        @Suppress("MagicNumber")
        val plusDays1 = Random.nextLong(1, 3)
        val termin1 = LocalDateTime.now(ZoneId.of(SchwimmgruppeValidator.TIMEZONE_BERLIN)).plusDays(plusDays1)

        val plusDays2 = Random.nextLong(4, 6)
        val termin2 = LocalDateTime.now(ZoneId.of(SchwimmgruppeValidator.TIMEZONE_BERLIN)).plusDays(plusDays2)

        val schwimmhalle = Schwimmhalle(bezeichnung = bezeichnung, plz = plz, ort = ort)
        return Schwimmgruppe(
            id = null,
            name = name,
            aktiv = AKTIV,
            trainingstermin1 = termin1,
            trainingstermin2 = termin2,
            ligaklasse = LIGAKLASSE,
            schwimmhalle = schwimmhalle,
        )
    }

    private companion object {
        const val NAME = "T-Jugend"
        const val BEZEICHNUNG = "Freibad"
        const val PLZ = "72793"
        const val ORT = "Pfullingen"
        const val AKTIV = true
        val LIGAKLASSE = LigaklasseType.ERSTE_BUNDESLIGA
    }
}
