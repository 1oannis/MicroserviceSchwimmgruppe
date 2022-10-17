package com.acme.schwimmgruppe.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke // NOSONAR
import org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 * Security-Konfiguration.
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
// https://github.com/spring-projects/spring-security/tree/master/samples
interface SecurityConfig {
    /**
     * Bean-Definition, um den Zugriffsschutz an der REST-Schnittstelle zu konfigurieren.
     *
     * @param http Injiziertes Objekt von `ServerHttpSecurity` als Ausgangspunkt für die Konfiguration.
     * @return Objekt von `SecurityWebFilterChain`
     */
    @Bean
    fun securityWebFilterChainFn(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        authorizeExchange {
            authorize(anyExchange, permitAll)
        }
        httpBasic {}
        formLogin { disable() }
        csrf { disable() }
    }

    /**
     * Bean-Definition, um den Verschlüsselungsalgorithmus für Passwörter bereitzustellen. Es wird der
     * Default-Algorithmus von Spring Security verwendet: _bcrypt_.
     * @return Objekt für die Verschlüsselung von Passwörtern.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = createDelegatingPasswordEncoder()
}
