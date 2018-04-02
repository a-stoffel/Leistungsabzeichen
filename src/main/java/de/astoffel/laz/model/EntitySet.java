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
import java.util.function.Supplier;
import org.hibernate.query.Query;

/**
 *
 * @author astoffel
 */
public class EntitySet<T> {

	private final DataSession session;
	private final Class<T> type;
	private final Supplier<T> factory;
	private final String findByNameQuery;
	private final String findAllQuery;
	private final String deleteAllQuery;

	public EntitySet(DataSession session, Class<T> type, Supplier<T> factory,
			String findByNameQuery, String findAllQuery, String deleteAllQuery) {
		this.session = session;
		this.type = type;
		this.factory = factory;
		this.findByNameQuery = findByNameQuery;
		this.findAllQuery = findAllQuery;
		this.deleteAllQuery = deleteAllQuery;
	}

	public T create() {
		return factory.get();
	}

	public T find(long id) {
		return session.find(type, id);
	}

	public T findByName(String name) {
		Query<T> query = session.getNamedQuery(findByNameQuery);
		query.setParameter("name", name);
		return query.uniqueResult();
	}

	public List<T> findAll() {
		Query<T> query = session.getNamedQuery(findAllQuery);
		return query.list();
	}

	public void deleteAll() {
		session.getNamedQuery(deleteAllQuery).executeUpdate();
	}
}
