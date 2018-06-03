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

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author astoffel
 */
public class EntitySet<E extends EntityObject> {

	private final DataSession session;
	private final Class<E> type;
	private final Supplier<E> factory;
	private final Function<DataSession, List<E>> findAll;

	public EntitySet(DataSession session, Class<E> type, Supplier<E> factory,
			Function<DataSession, List<E>> findAll) {
		this.session = session;
		this.type = type;
		this.factory = factory;
		this.findAll = findAll;
	}

	public E create() {
		return factory.get();
	}

	public E find(long id) {
		return session.find(type, id);
	}

	public List<E> findAll() {
		return findAll.apply(session);
	}

}
