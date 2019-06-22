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
package de.astoffel.laz.model;

import de.astoffel.laz.model.transfer.TransferEntityType;
import de.astoffel.laz.model.transfer.TransferMeta;
import de.astoffel.laz.model.transfer.TransferModel;
import javafx.beans.property.Property;

/**
 *
 * @author astoffel
 */
public final class Meta extends AbstractEntity<TransferMeta> {

	private final Property<String> location;
	private final Property<String> when;

	Meta(TransferModel transferModel, TransferMeta transfer) {
		super(transferModel, TransferEntityType.META, transfer);
		this.location = createProperty(TransferMeta::getLocation,
				TransferMeta::setLocation);
		this.when = createProperty(TransferMeta::getEventDate,
				TransferMeta::setEventDate);
	}

	@PropertyDescriptor(name = "Location")
	public Property<String> locationProperty() {
		return location;
	}

	public String getLocation() {
		return location.getValue();
	}

	@PropertyDescriptor(name = "Event Date")
	public Property<String> whenProperty() {
		return when;
	}

	public String getWhen() {
		return when.getValue();
	}

}
