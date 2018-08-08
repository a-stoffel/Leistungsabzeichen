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

import de.astoffel.laz.model.transfer.TransferEntity;
import de.astoffel.laz.model.transfer.TransferEntityType;
import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author astoffel
 */
public abstract class AbstractEntity<T extends TransferEntity> {

	private final TransferModel transferModel;
	private final TransferEntityType<T> transferType;
	private final T transfer;
	private final List<ResetableEntityProperty> properties;

	AbstractEntity(TransferModel transferModel,
			TransferEntityType<T> transferType, T transfer) {
		this.transferModel = transferModel;
		this.transferType = transferType;
		this.transfer = transfer;
		this.properties = new ArrayList<>();
	}

	final <V> Property<V> createProperty(Getter<T, V> getter,
			Setter<T, V> setter) {
		var result = new EntityProperty<>(this, getter, setter);
		properties.add(result);
		return result;
	}

	final <V> ReadOnlyProperty<V> createReadonlyProperty(Getter<T, V> getter) {
		var result = new ReadonlyEntityProperty<>(this, getter);
		properties.add(result);
		return result;
	}

	final TransferModel transferModel() {
		return transferModel;
	}

	final TransferEntityType<T> transferType() {
		return transferType;
	}

	final T transfer() {
		return transfer;
	}

	final void reset() {
		try {
			transferModel.execute(session -> {
				transferType.entitySet(session).refresh(transfer);
			});
			for (var p : properties) {
				p.reset();
			}
		} catch (TransferException ex) {
			throw new ModelException(
					String.format(
							"Refreshing object %s.%s failed",
							transferType.name(), transfer.getId()),
					ex);
		}
	}

}
