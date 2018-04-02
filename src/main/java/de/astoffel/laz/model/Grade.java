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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author astoffel
 */
@Entity
@Access(AccessType.FIELD)
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"name"
	})}
)
@NamedQueries({
	@NamedQuery(
			name = "findGradeByName",
			query = "from Grade g where g.name = :name"
	),
	@NamedQuery(
			name = "findAllGrades",
			query = "from Grade g"
	),
	@NamedQuery(
			name = "deleteGrades",
			query = "delete from Grade g"
	)
})
public class Grade implements Serializable, Comparable<Grade> {

	private static final long serialVersionUID = 0L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	@Basic(optional = false)
	@Column(nullable = false)
	private String name;

	@Basic(optional = false)
	@Column(nullable = false)
	private String displayName;

	protected Grade() {
	}

	public Grade(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	@Override
	public int compareTo(Grade o) {
		return (this.name == null ? "" : this.name).compareTo(
				o.name == null ? "" : o.name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
