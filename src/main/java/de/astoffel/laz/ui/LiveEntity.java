/*
 * Copyright (C) 2018 astoffel
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.astoffel.laz.ui;

import java.util.List;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author astoffel
 */
abstract class LiveEntity<E extends Comparable<E>> implements Comparable<LiveEntity<E>> {

	private final E entity;

	protected LiveEntity(E entity) {
		this.entity = entity;
	}

	@Override
	public int compareTo(LiveEntity<E> o) {
		return this.entity.compareTo(o.entity);
	}
	
	abstract ObjectProperty<String> nameProperty();

	abstract List<PropertySheetItem> propertySheetItems();
}
