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

import de.astoffel.laz.model.DataModel;
import de.astoffel.laz.model.Meta;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author astoffel
 */
final class LiveMeta {

	private final ObjectProperty<String> location;
	private final ObjectProperty<String> eventDate;

	public LiveMeta(DataModel model, Meta meta) {
		this.location = new LiveObjectProperty<>(
				model, meta, Meta::getLocation, Meta::setLocation);
		this.eventDate = new LiveObjectProperty<>(
				model, meta, Meta::getEventDate, Meta::setEventDate);
	}

	public ObjectProperty<String> locationProperty() {
		return location;
	}

	public ObjectProperty<String> eventDateProperty() {
		return eventDate;
	}

}
