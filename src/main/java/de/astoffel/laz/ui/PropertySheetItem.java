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

import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;

/**
 *
 * @author astoffel
 */
class PropertySheetItem<T> implements PropertySheet.Item {

	private final Class<T> type;
	private final String name;
	private final String description;
	private final String category;
	private final ObjectProperty<T> property;

	public PropertySheetItem(Class<T> type, String name, String description, String category,
			ObjectProperty<T> property) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.category = category;
		this.property = property;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public T getValue() {
		return property.get();
	}

	@Override
	public void setValue(Object value) {
		property.set(type.cast(value));
	}

	@Override
	public Optional<ObservableValue<? extends Object>> getObservableValue() {
		return Optional.of(property);
	}

}
