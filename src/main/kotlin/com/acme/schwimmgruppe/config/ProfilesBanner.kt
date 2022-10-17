package com.acme.schwimmgruppe.config

import org.springframework.boot.Banner
import org.springframework.boot.SpringBootVersion
import org.springframework.core.SpringVersion
import org.springframework.security.core.SpringSecurityCoreVersion
import java.net.InetAddress
import java.util.*

/**
 * Singleton-Klasse, um sinnvolle Konfigurationswerte für den Microservice vorzugeben.
 * @author [Ioannis Theodosiadis](mailto:thio1011@h-ka.de)
 */
object ProfilesBanner {
    /**
     * Konstante für das Spring-Profile "dev".
     */
    const val DEV = "dev"

    /**
     * Banner für den Start des Microservice in der Konsole.
     */
    val banner = Banner { _, _, out ->
        val jdkVersion = "${Runtime.version()} @ ${System.getProperty("java.version.date")}"
        val osVersion = System.getProperty("os.name")
        val localhost = InetAddress.getLocalHost()
        val serviceHost = System.getenv("SCHWIMMGRUPPE_SERVICE_HOST")
        val servicePort = System.getenv("SCHWIMMGRUPPE_SERVICE_PORT")
        val kubernetes = when (serviceHost) {
            null -> "N/A"
            else -> "SCHWIMMGRUPPE_SERVICE_HOST=$serviceHost, SCHWIMMGRUPPE_SERVICE_PORT=$servicePort"
        }
        val username = System.getProperty("user.name")

        // vgl. "Text Block" ab Java 15
        // https://en.wikipedia.org/wiki/ANSI_escape_code#8-bit
        out.println(
            """

            |   _____      _             _                                                    __      _____
            |  / ____|    | |           (_)                                                   \ \    / /__  \
            | | (___   ___| |____      ___ _ __ ___  _ __ ___   __ _ _ __ _   _ _ __  _ __   __\ \  / /   ) |
            |  \___ \ / __| '_ \ \ /\ / / | '_ ` _ \| '_ ` _ \ / _` | '__| | | | '_ \| '_ \ / _ \ \/ /   / /
            |  ____) | (__| | | \ V  V /| | | | | | | | | | | | (_| | |  | |_| | |_) | |_) |  __/\  /   / /__
            | |_____/ \___|_| |_|\_/\_/ |_|_| |_| |_|_| |_| |_|\__, |_|   \__,_| .__/| .__/ \___| \/   |_____|
            |                                                   __/ |          | |   | |
            |                                                  |___/           |_|   |_|
            |
            |(C) Ioannis Theodosiadis, Juergen Zimmermann, Hochschule Karlsruhe
            |Version              2.0
            |Spring Boot          ${SpringBootVersion.getVersion()}
            |Spring Security      ${SpringSecurityCoreVersion.getVersion()}
            |Spring Framework     ${SpringVersion.getVersion()}
            |Hibernate            ${org.hibernate.Version.getVersionString()}
            |Kotlin               ${KotlinVersion.CURRENT}
            |OpenJDK              $jdkVersion
            |Betriebssystem       $osVersion
            |Rechnername          ${localhost.hostName}
            |IP-Adresse           ${localhost.hostAddress}
            |Kubernetes           $kubernetes
            |Username             $username
            |JVM Locale           ${Locale.getDefault()}
            |OpenAPI              /swagger-ui.html /v3/api-docs
            |
            """
                .trimMargin("|"),
        )
    }
}
