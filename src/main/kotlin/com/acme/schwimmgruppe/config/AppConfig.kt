package com.acme.schwimmgruppe.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL
import org.springframework.hateoas.support.WebStack.WEBFLUX

/**
 * Konfigurationsklasse für die Anwendung bzw. den Microservice.
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
// @EnableReactiveMethodSecurity fuer @PreAuthorize und @Secured
// TODO @AutoConfiguration ab Spring Boot 3
@Configuration(proxyBeanMethods = false)
@EnableHypermediaSupport(type = [HAL], stacks = [WEBFLUX])
// @EnableReactiveMethodSecurity
@EnableConfigurationProperties(DbProps::class)
class AppConfig : HibernateReactiveConfig, SecurityConfig
