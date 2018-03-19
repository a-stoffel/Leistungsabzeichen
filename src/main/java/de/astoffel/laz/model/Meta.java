/*
 * Copyright (C) 2018 Andreas Stoffel
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

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author andreas
 */
@Entity
@Access(AccessType.FIELD)
@NamedQueries({
	@NamedQuery(
			name = "findMeta",
			query = "from Meta m"
	)
	,
	@NamedQuery(
			name = "deleteMetas",
			query = "delete from Meta m"
	)
})
public class Meta implements Serializable {
	
	public static Meta getInstance(Session session) {
		Query<Meta> query = session.getNamedQuery("findMeta");
		Meta result = query.uniqueResult();
		if (result == null) {
			result = new Meta("", "");
			session.persist(result);
		}
		return result;
	}

	private static final long serialVersionUID = 0L;

	@Id
	private long id;
	@Version
	private Long version;

	@Basic(optional = false)
	@Column(nullable = false)
	private String location;

	@Basic(optional = false)
	@Column(nullable = false)
	private String eventDate;

	protected Meta() {
		this.id = 0;
	}

	public Meta(String location, String eventDate) {
		this.id = 0;
		this.location = location;
		this.eventDate = eventDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String when) {
		this.eventDate = when;
	}

}
