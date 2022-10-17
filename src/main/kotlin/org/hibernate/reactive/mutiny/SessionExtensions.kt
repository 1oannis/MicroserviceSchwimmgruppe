// ktlint-disable filename
/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.hibernate.reactive.mutiny

import io.smallrye.mutiny.Uni

// Fuer Hibernate gibt es keinen Kotlin-Code und deshalb in dessen build.gradle keine dependencies fuer Kotlin
// https://hibernate.zulipchat.com/#narrow/stream/132094-hibernate-orm-dev

/**
 * Extension Function, damit man `Session.find<E>(id)` _Kotlin-like_ aufrufen kann.
 * @return Uni mit dem gefundenen Object oder leeres Uni
 */
// reified: die Typinformation ist zur Laufzeit vollstaendig vorhanden (vs. "type erasure" in Java)
inline fun <reified E : Any> Mutiny.Session.find(id: Any): Uni<E> = find(E::class.java, id)
