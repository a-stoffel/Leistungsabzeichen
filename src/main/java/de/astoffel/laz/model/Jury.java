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
import de.astoffel.laz.model.transfer.TransferJury;
import de.astoffel.laz.model.transfer.TransferModel;
import javafx.beans.property.Property;

/**
 *
 * @author astoffel
 */
public final class Jury extends AbstractEntity<TransferJury> implements NamedEntity, Comparable<Jury> {

	private final Property<String> name;

	Jury(TransferModel transferModel, TransferJury transfer) {
		super(transferModel, TransferEntityType.JURY, transfer);
		this.name = createProperty(TransferJury::getName, TransferJury::setName);
	}

	@Override
	public int compareTo(Jury other) {
		return name.getValue().compareTo(other.name.getValue());
	}

	@Override
	@PropertyDescriptor(name = "Name")
	public Property<String> nameProperty() {
		return name;
	}

	public String getName() {
		return name.getValue();
	}

}
