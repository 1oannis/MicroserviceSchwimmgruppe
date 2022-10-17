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

import kotlin.reflect.KProperty1

/**
 * Extension Function für "type-safe property access" ausgehend von der Entity-Klasse E als Root.
 * V ist die "Value-Klasse" der Property.
 * @param prop Property der Entity-Klasse E
 */
inline fun <reified E, V> Root<E>.get(prop: KProperty1<E, V>): Path<V> = get(prop.name)

/**
 * Extension Function für einen "type-safe join" ausgehend von der Entity-Klasse E als Root.
 * V ist die "Value-Klasse" der Property.
 * @param prop Property der Entity-Klasse E
 */
@Suppress("unused")
inline fun <reified E, V> Root<E>.join(prop: KProperty1<E, V>): Join<E, E> = join(prop.name)
