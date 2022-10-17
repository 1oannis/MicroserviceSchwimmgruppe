package com.acme.schwimmgruppe.config

import org.hibernate.dialect.MySQL8Dialect
import org.hibernate.dialect.Oracle12cDialect
import org.hibernate.dialect.PostgreSQL10Dialect

/**
 * Enum-Typ mit den unterstützten DB-Systemen
 * @property sqlDialect Der SQL-Dialekt für Hibernate
 */
enum class DbSystemType(val sqlDialect: String) {
    /**
     * DB-System PostgreSQL in application.yml
     */
    POSTGRES(PostgreSQL10Dialect::class.java.name),

    /**
     * DB-System MySQL in application.yml
     */
    MYSQL(MySQL8Dialect::class.java.name),

    /**
     * DB-System Oracle in application.yml
     */
    ORACLE(Oracle12cDialect::class.java.name);
}
