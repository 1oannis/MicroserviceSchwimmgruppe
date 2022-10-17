package com.acme.schwimmgruppe.config

import com.acme.schwimmgruppe.config.DbSystemType.MYSQL
import com.acme.schwimmgruppe.config.DbSystemType.ORACLE
import com.acme.schwimmgruppe.config.DbSystemType.POSTGRES
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.bind.DefaultValue

/**
 * Eingelesene Properties mit dem Praefix `db`
 *
 * @property system Das DB-System: `POSTGRES`, `MYSQL`, `ORACLE`
 * @property user Der Username für den DB-User
 * @property password Das Passwort für den DB-User
 * @property dbHost Rechnername des DB-Servers
 * @property dbname Der DB-Name
 * @property url URL für den DB-Zugriff
 * @property sqlDialect SQL-Dialekt für Hibernate
 */
@ConfigurationProperties(prefix = "app.db")
@ConstructorBinding
data class DbProps(
    @DefaultValue("POSTGRES")
    val system: DbSystemType,

    @DefaultValue("schwimmgruppe")
    val user: String,

    val password: String,

    private val dbHost: String = if (osName.startsWith("Windows")) {
        "localhost"
    } else {
        system.name.lowercase()
    },

    @DefaultValue("schwimmgruppe")
    private val dbname: String,

    // https://jdbc.postgresql.org/documentation/head/connect.html
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html
    // Default-Port
    //      PostgreSQL: 5432
    //      MySQL: 3306
    //      Oracle: 1521
    val url: String =
        when (system) {
            POSTGRES -> "postgresql://$dbHost/$dbname"
            MYSQL -> "mysql://$dbHost/$dbname"
            // io.vertx:vertx-oracle-client nutzt ojdbc11
            ORACLE -> "oracle:thin:@$dbHost/XEPDB1"
        },

    val sqlDialect: String = system.sqlDialect,
) {
    private companion object {
        val osName = System.getProperty("os.name") ?: error("Die Property 'os.name' existiert nicht")
    }
}
