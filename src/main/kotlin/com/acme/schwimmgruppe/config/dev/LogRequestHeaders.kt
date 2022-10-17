package com.acme.schwimmgruppe.config.dev

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain

/**
 * WebFilter zur Protokollierung des Request-Headers.
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
interface LogRequestHeaders {
    /**
     * WebFilter zur Protokollierung des Request-Headers und zur Pufferung des Headers _Accept-Language_.
     */
    @Bean
    fun logRequestHeaders() = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        chain.filter(exchange).contextWrite { context ->
            logger.debug("headers: {}", exchange.request.headers)
            context
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(LogRequestHeaders::class.java)
    }
}
