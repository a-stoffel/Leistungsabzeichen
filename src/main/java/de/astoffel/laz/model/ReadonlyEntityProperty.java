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
import javafx.beans.property.ReadOnlyObjectPropertyBase;

/**
 *
 * @author astoffel
 */
final class ReadonlyEntityProperty<T extends TransferEntity, V> extends ReadOnlyObjectPropertyBase<V>
		implements ResetableEntityProperty {

	private final AbstractEntity<T> entity;
	private final Getter<T, V> getter;
	private V value;

	ReadonlyEntityProperty(AbstractEntity<T> entity, Getter<T, V> getter) {
		this.entity = entity;
		this.getter = getter;
		reset();
	}

	@Override
	public void reset() {
		value = getter.get(entity.transfer());
		fireValueChangedEvent();
	}

	@Override
	public V get() {
		return value;
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}

}
