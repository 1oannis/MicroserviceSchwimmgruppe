package com.acme.schwimmgruppe.rest

import com.acme.schwimmgruppe.config.ProfilesBanner.DEV
import com.acme.schwimmgruppe.entity.LigaklasseType
import com.acme.schwimmgruppe.entity.Schwimmhalle
import com.acme.schwimmgruppe.rest.SchwimmgruppeGetController.Companion.ID_PATTERN
import com.acme.schwimmgruppe.service.SchwimmgruppeValidator
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.JRE
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.get
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.LocalDateTime
import java.time.ZoneId

@Tag("rest")
@Tag("rest_write")
@DisplayName("REST-Schnittstelle fuer Schreiben testen")
// Alternative zu @ContextConfiguration von Spring
// Default: webEnvironment = MOCK, d.h.
//          Mocking mit ReactiveWebApplicationContext anstatt z.B. Netty oder Tomcat
@SpringBootTest(webEnvironment = RANDOM_PORT)
// @SpringBootTest(webEnvironment = DEFINED_PORT, ...)
// ggf.: @DirtiesContext, falls z.B. ein Spring Bean modifiziert wurde
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JRE.JAVA_17, max = JRE.JAVA_19)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ExperimentalCoroutinesApi
@Suppress(
    "UndocumentedPublicClass",
    "UndocumentedPublicFunction",
    "StringLiteralDuplication",
)
class SchwimmgruppeWriteRestTest(@LocalServerPort private val port: Int, ctx: ApplicationContext) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val baseUrl = "http://localhost:$port/api"
    private val client = WebClient
        .builder()
        .baseUrl(baseUrl)
        .build()

    init {
        ctx.getBean<SchwimmgruppeWriteController>() shouldNotBe null
    }

    @Test
    @Order(100)
    fun `Immer erfolgreich`() {
        true shouldBe true
    }

    @Suppress("ClassName")
    @Nested
    inner class `ERZEUGEN - einer Schwimmgruppe` {
        @ParameterizedTest
        @CsvSource("$NEU_NAME, $NEU_SCHWIMMHALLE_BEZ, $NEU_PLZ, $NEU_ORT")
        @Order(1000)
        fun `Neue Schwimmgruppe abspeichern`(args: ArgumentsAccessor) = runTest {
            // given
            val neueSchwimmgruppe = SchwimmgruppeDTO(
                name = args.get<String>(0),
                aktiv = true,
                trainingstermin1 = LocalDateTime.now(ZoneId.of(SchwimmgruppeValidator.TIMEZONE_BERLIN)).plusDays(2),
                trainingstermin2 = LocalDateTime.now(ZoneId.of(SchwimmgruppeValidator.TIMEZONE_BERLIN)).plusDays(4),
                ligaklasse = LigaklasseType.LANDESLIGA,
                schwimmhalle = Schwimmhalle(null, "Hallenbad", "01099", "Dresden"),
            )
            // when
            val response = client.post()
                .contentType(APPLICATION_JSON)
                .bodyValue(neueSchwimmgruppe)
                .awaitExchange { response -> response.awaitBodilessEntity() }
            logger.debug("response:= {}", response)
            // then
            var id = ""
            assertSoftly(response) {
                statusCode shouldBe CREATED

                val location = headers.location
                logger.debug("location:= {}", location)
                id = location.toString().substringAfterLast('/')
                logger.debug("id:= {}", id)
                id shouldMatch ID_PATTERN
            }
            // Ist die neue Schwimmgruppe auch wirklich abgespeichert?
            val schwimmgruppeModel = client.get()
                .uri("/{id}", id)
                .retrieve()
                .awaitBody<EntityModel<SchwimmgruppeDTO>>()
            logger.debug("schwimmgruppeModel:= {}", schwimmgruppeModel)
            logger.debug("neueSchwimmgruppe:= {}", neueSchwimmgruppe)
            schwimmgruppeModel.content?.name shouldBe neueSchwimmgruppe.name
        }
    }

    @Suppress("ClassName")
    @Nested
    inner class `LOESCHEN - einer Schwimmgruppe` {
        @ParameterizedTest
        @ValueSource(strings = ["00000000-0000-0000-0000-000000000020"])
        @Order(1700)
        fun `Loeschen einer vorhandenen Schwimmgruppe mit der ID`(id: String) = runTest {
            // when
            val statusCode = client.delete()
                .uri("/{id}", id)
                .awaitExchange { response -> response.statusCode() }

            // then
            statusCode shouldBe HttpStatus.NO_CONTENT
        }
    }

    private companion object {
        private const val NEU_NAME = "B-Jugend"
        private const val NEU_SCHWIMMHALLE_BEZ = "Hallenbad"
        private const val NEU_PLZ = "01099"
        private const val NEU_ORT = "Dresden"
    }
}
