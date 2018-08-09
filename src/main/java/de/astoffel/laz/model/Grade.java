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
import de.astoffel.laz.model.transfer.TransferGrade;
import de.astoffel.laz.model.transfer.TransferModel;
import javafx.beans.property.Property;

/**
 *
 * @author astoffel
 */
public final class Grade extends AbstractEntity<TransferGrade> implements NamedEntity, Comparable<Grade> {

	private final Property<String> name;
	private final Property<String> displayName;

	Grade(TransferModel transferModel, TransferGrade transfer) {
		super(transferModel, TransferEntityType.GRADE, transfer);
		this.name = createProperty(TransferGrade::getName,
				TransferGrade::setName);
		this.displayName = createProperty(TransferGrade::getDisplayName,
				TransferGrade::setDisplayName);
	}

	@Override
	public int compareTo(Grade other) {
		return name.getValue().compareTo(other.name.getValue());
	}

	@Override
	@PropertyDescriptor(name = "Name")
	public Property<String> nameProperty() {
		return name;
	}

	@PropertyDescriptor(name = "Name")
	public String getName() {
		return name.getValue();
	}

	@PropertyDescriptor(name = "Display Name")
	public Property<String> displayNameProperty() {
		return displayName;
	}

	public String getDisplayName() {
		return displayName.getValue();
	}

}
