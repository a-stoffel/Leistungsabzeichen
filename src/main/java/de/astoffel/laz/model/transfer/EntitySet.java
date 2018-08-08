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
package de.astoffel.laz.model.transfer;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author astoffel
 */
public abstract class EntitySet<E extends TransferEntity> {

	protected final TransferSession unit;

	protected EntitySet(TransferSession unit) {
		this.unit = unit;
	}

	public abstract Optional<E> find(long id) throws TransferException;

	public abstract List<E> findAll() throws TransferException;

	public abstract void deleteAll() throws TransferException;

	public void delete(E entity) throws TransferException {
		unit.execute(session -> session.delete(entity));
	}

	public void persist(E entity) throws TransferException {
		unit.execute(session -> session.persist(entity));
	}

	public void update(E entity) throws TransferException {
		unit.execute(session -> session.update(entity));
	}

	public void refresh(E entity) throws TransferException {
		unit.execute(session -> session.refresh(entity));
	}

}
