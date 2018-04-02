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

import de.astoffel.laz.model.DataModel;
import de.astoffel.laz.model.Participant;
import java.util.List;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author astoffel
 */
final class LiveParticipant extends LiveEntity {

	private final ObjectProperty<String> name;
	private final List<PropertySheetItem> propertySheetItems;

	public LiveParticipant(DataModel model, Participant participant) {
		this.name = new LiveObjectProperty<>(
				model, participant, Participant::getName, Participant::setName);
		this.propertySheetItems = List.of(
				new PropertySheetItem(String.class, "Name", "", "", this.name)
		);
	}

	@Override
	public List<PropertySheetItem> propertySheetItems() {
		return propertySheetItems;
	}

	@Override
	public ObjectProperty<String> nameProperty() {
		return name;
	}

}
