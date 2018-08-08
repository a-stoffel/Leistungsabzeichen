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
package de.astoffel.laz.model.transfer;

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

/**
 *
 * @author astoffel
 */
@Entity(name = "Meta")
@Access(AccessType.FIELD)
@NamedQueries({
	@NamedQuery(
			name = "findAllMeta",
			query = "from Meta m"
	),
	@NamedQuery(
			name = "deleteAllMetas",
			query = "delete from Meta m"
	)
})
public class TransferMeta implements TransferEntity, Serializable {

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

	protected TransferMeta() {
		this.id = 0;
	}

	public TransferMeta(String location, String eventDate) {
		this.id = 0;
		this.location = location;
		this.eventDate = eventDate;
	}

	@Override
	public Long getId() {
		return id;
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
