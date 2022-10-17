package com.acme.schwimmgruppe.config.dev

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform
import org.springframework.boot.cloud.CloudPlatform.KUBERNETES
import org.springframework.context.annotation.Bean

/**
 * Protokoll-Ausgabe, wenn Kubernetes erkannt wird
 *
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
interface K8s {
    /**
     * Protokoll-Ausgabe, wenn Kubernetes erkannt wird
     */
    @Bean
    @ConditionalOnCloudPlatform(KUBERNETES)
    fun detectK8s() = CommandLineRunner {
        LoggerFactory.getLogger(K8s::class.java).debug("Plattform \"Kubernetes\"")
    }
}
