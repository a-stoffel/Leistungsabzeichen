/*
 * Copyright (C) 2018 Andreas Stoffel
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author astoffel
 */
final class SelectableEntity<T> {

	private final T entity;
	private final BooleanProperty selected;

	public SelectableEntity(T entity, boolean selected) {
		this.entity = entity;
		this.selected = new SimpleBooleanProperty(selected);
	}

	public T getEntity() {
		return entity;
	}

	public BooleanProperty selectedProperty() {
		return selected;
	}

}
