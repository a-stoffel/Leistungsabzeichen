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
import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferOptimisticLockingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectPropertyBase;

/**
 *
 * @author astoffel
 */
final class EntityProperty<T extends TransferEntity, V> extends ObjectPropertyBase<V>
		implements ResetableEntityProperty {

	private static final Logger LOG = Logger.getLogger(EntityProperty.class
			.getName());

	private final AbstractEntity<T> entity;
	private final Getter<T, V> getter;
	private final Setter<T, V> setter;

	EntityProperty(AbstractEntity<T> entity, Getter<T, V> getter, Setter<T, V> setter) {
		this.entity = entity;
		this.getter = getter;
		this.setter = setter;
		reset();
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public void reset() {
		super.set(getter.get(entity.transfer()));
	}

	@Override
	public void set(V newValue) {
		try {
			entity.transferModel().execute(session -> {
				setter.set(entity.transfer(), newValue);
				entity.transferType().entitySet(session)
						.update(entity.transfer());
			});
			super.set(newValue);
		} catch (TransferOptimisticLockingException ex) {
			LOG.log(Level.FINE,
					String.format(
							"Optimistic lock exception while updating entity %s.%s",
							entity.transferType().name(),
							entity.transfer().getId()),
					ex);
			entity.reset();
		} catch (TransferException ex) {
			throw new ModelException(
					String.format(
							"Updating entity %s.%s failed",
							entity.transferType().name(),
							entity.transfer().getId()),
					ex);
		}
	}

}
