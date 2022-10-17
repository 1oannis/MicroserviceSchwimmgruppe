package com.acme.schwimmgruppe.config.dev

import com.acme.schwimmgruppe.config.ProfilesBanner.DEV
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Konfigurationsklasse für die Anwendung bzw. den Microservice, falls das Profile _dev_ aktiviert ist.
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
@Configuration(proxyBeanMethods = false)
@Profile(DEV)
class DevConfig : LogRequestHeaders, K8s
