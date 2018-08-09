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
import de.astoffel.laz.model.transfer.TransferExam;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.beans.property.Property;

/**
 *
 * @author astoffel
 */
public final class Exam extends AbstractEntity<TransferExam> implements NamedEntity, Comparable<Exam> {

	private final Property<Integer> sort;
	private final Property<String> name;
	private final Property<String> displayName;
	private final Property<String> displayShortName;
	private final Map<Category, String> descriptions;

	Exam(TransferModel transferModel, TransferExam transfer) {
		super(transferModel, TransferEntityType.EXAM, transfer);
		this.sort = createProperty(TransferExam::getSort, TransferExam::setSort);
		this.name = createProperty(TransferExam::getName, TransferExam::setName);
		this.displayName = createProperty(TransferExam::getDisplayName,
				TransferExam::setDisplayName);
		this.displayShortName = createProperty(TransferExam::getDisplayShortName,
				TransferExam::setDisplayShortName);
		this.descriptions = new WeakHashMap<>();
	}

	@Override
	public int compareTo(Exam other) {
		return name.getValue().compareTo(other.name.getValue());
	}

	public Property<Integer> sortProperty() {
		return sort;
	}

	public Integer getSort() {
		return sort.getValue();
	}

	@Override
	@PropertyDescriptor(name = "Name")
	public Property<String> nameProperty() {
		return name;
	}

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

	@PropertyDescriptor(name = "Display Short Name")
	public Property<String> displayShortNameProperty() {
		return displayShortName;
	}

	public String getDisplayShortName() {
		return displayShortName.getValue();
	}

	public String descriptionOf(Category category) {
		var result = descriptions.get(category);
		if (result == null) {
			result = transfer().descriptionOf(category.transfer());
			descriptions.put(category, result);
		}
		return result;
	}

}
