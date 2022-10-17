package com.acme.schwimmgruppe.service

import com.acme.schwimmgruppe.entity.LigaklasseType
import com.acme.schwimmgruppe.entity.Schwimmgruppe
import com.acme.schwimmgruppe.entity.SchwimmgruppeId
import com.acme.schwimmgruppe.entity.Schwimmhalle
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.smallrye.mutiny.Uni
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory
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
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random

@Tag("service")
@Tag("service_read")
@DisplayName("ReadService - Anwendungskern fuer Lesen testen")
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
class SchwimmgruppeReadServiceTest {
    private var sessionFactory = mockk<SessionFactory>()

    private val queryBuilder = QueryBuilder(sessionFactory)
    private val service = SchwimmgruppeReadService(sessionFactory, queryBuilder)

    @Test
    @Order(100)
    fun `Immer erfolgreich`() {
        true shouldBe true
    }

    @Suppress("ClassName")
    @Nested
    inner class `SUCHEN - anhand der ID` {
        @ParameterizedTest
        @ValueSource(strings = [ID_VORHANDEN])
        @Order(1000)
        fun `Suche mit vorhandener ID`(idStr: String) = runTest {
            // given
            val id = SchwimmgruppeId.fromString(idStr)
            val schwimmgruppeMock = createSchwimmgruppeMock(id)
            val schwimmgruppeUniMock = Uni.createFrom().item(schwimmgruppeMock)
            every {
                sessionFactory.withSession(any<JavaFunction<Mutiny.Session, Uni<Schwimmgruppe>>>())
            } returns schwimmgruppeUniMock
            // when
            val result = service.findById(id)
            // then
            result.shouldBeTypeOf<FindByIdResult.Found>()
            result.schwimmgruppe.id shouldBe id
        }
    }

    private fun createSchwimmgruppeMock(id: SchwimmgruppeId): Schwimmgruppe {
        @Suppress("MagicNumber")
        val plusDays1 = Random.nextLong(0, 2)
        val termin1 = LocalDateTime.now(ZoneId.of(SchwimmgruppeValidator.TIMEZONE_BERLIN)).plusDays(plusDays1)

        val plusDays2 = Random.nextLong(3, 6)
        val termin2 = LocalDateTime.now(ZoneId.of(SchwimmgruppeValidator.TIMEZONE_BERLIN)).plusDays(plusDays2)

        val schwimmhalle = Schwimmhalle(bezeichnung = "Sonnenbad", plz = "76185", ort = "Mühlburg")
        return Schwimmgruppe(
            id = id,
            name = NAME,
            aktiv = AKTIV,
            trainingstermin1 = termin1,
            trainingstermin2 = termin2,
            ligaklasse = LIGAKLASSE,
            schwimmhalle = schwimmhalle,
        )
    }

    private companion object {
        const val ID_VORHANDEN = "00000000-0000-0000-0000-000000000001"
        const val NAME = "T-Jugend"
        const val AKTIV = true
        val LIGAKLASSE = LigaklasseType.ERSTE_BUNDESLIGA
    }
}
