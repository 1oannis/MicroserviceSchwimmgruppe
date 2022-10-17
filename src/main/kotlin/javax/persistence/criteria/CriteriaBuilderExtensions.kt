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
package javax.persistence.criteria

/**
 * Extension Function, damit man `CriteriaBuilder.create<E>()` _Kotlin-like_ aufrufen kann.
 * @return Erzeugtes Objekt der Java-Klasse `CriteriaQuery`
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> CriteriaBuilder.createQuery(): CriteriaQuery<E> = createQuery(E::class.java)
