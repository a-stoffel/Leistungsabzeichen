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

import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author astoffel
 */
public final class DataSession {

	private final Session session;

	public DataSession(Session session) {
		this.session = session;
	}

	public <T> Query<T> getNamedQuery(String queryName) {
		@SuppressWarnings("unchecked")
		Query<T> result = (Query<T>) session.getNamedQuery(queryName);
		return result;
	}

	public void persist(Object object) {
		session.persist(object);
	}

	public void update(Object object) {
		session.update(object);
	}

	public void refresh(Object object) {
		session.refresh(object);
	}
}
