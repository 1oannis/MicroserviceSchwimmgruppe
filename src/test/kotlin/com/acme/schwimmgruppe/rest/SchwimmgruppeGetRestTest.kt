package com.acme.schwimmgruppe.rest

import com.acme.schwimmgruppe.config.ProfilesBanner.DEV
import com.acme.schwimmgruppe.service.SchwimmgruppeValidator
import com.jayway.jsonpath.JsonPath
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
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
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.mediatype.hal.HalLinkDiscoverer
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitExchange

@Tag("rest")
@Tag("rest_get")
@DisplayName("REST-Schnittstelle fuer Schwimmgruppen testen")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JRE.JAVA_17, max = JRE.JAVA_19)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ExperimentalCoroutinesApi
@Suppress(
    "ClassName",
    "UndocumentedPublicClass",
    "UndocumentedPublicProperty",
    "UndocumentedPublicFunction",
    "StringLiteralDuplication",
)
class SchwimmgruppeGetRestTest(@LocalServerPort private val port: Int, ctx: ApplicationContext) {
    private val baseUrl = "http://localhost:$port/api"
    private val client = WebClient
        .builder()
        .baseUrl(baseUrl)
        .build()

    init {
        ctx.getBean<SchwimmgruppeGetController>() shouldNotBe null
    }

    @Test
    @Order(100)
    fun `Immer erfolgreich`() {
        true shouldBe true
    }

    @Nested
    inner class `SUCHEN - anhand der ID suchen` {
        @ParameterizedTest
        @ValueSource(strings = [NAME])
        @Order(1000)
        fun `Suche mit vorhandenem Name`(name: String) = runTest {
            // when
            val schwimmgruppenModel = client.get()
                .uri { builder ->
                    builder
                        .queryParam(NAME_PARAM, name)
                        .build()
                }
                .retrieve()
                .awaitBody<SchwimmgruppenModel>()
            // then
            assertSoftly {
                val (schwimmgruppen) = schwimmgruppenModel._embedded
                schwimmgruppen shouldNot beEmpty()
                schwimmgruppen.forEach { schwimmgruppe ->
                    schwimmgruppe.content?.name shouldBe name
                }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["00000000-0000-0000-0000-000000000001"])
        @Order(2000)
        fun `Suche mit vorhandener ID`(id: String) = runTest {
            // given
            val namePath = "$.name"

            // when
            val response = client.get()
                .uri("/{id}", id)
                .accept(MediaTypes.HAL_JSON)
                .awaitExchange { response -> response.awaitEntity<String>() }

            // then
            assertSoftly(response) {
                statusCode shouldBe HttpStatus.OK

                body shouldNotBe null

                body shouldContainJsonKey namePath
                val nachname: String = JsonPath.read(body, namePath)
                nachname shouldMatch Regex(SchwimmgruppeValidator.NAME_PATTERN)

                val selfLink = HalLinkDiscoverer().findLinkWithRel("self", body ?: "").get().href
                selfLink shouldBe "$baseUrl/$id"
            }
        }
    }

    private companion object {
        const val NAME_PARAM = "name"
        const val NAME = "A-Jugend"
    }
}
