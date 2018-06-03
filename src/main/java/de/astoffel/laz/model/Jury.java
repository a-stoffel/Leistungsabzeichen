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
import java.util.List;
import java.util.Optional;
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
			name = "findJuryByName",
			query = "from Jury j where j.name = :name"
	),
	@NamedQuery(
			name = "findAllJuries",
			query = "from Jury j"
	),
	@NamedQuery(
			name = "deleteAllJuries",
			query = "delete from Jury j"
	)
})
public class Jury implements EntityObject, Serializable, Comparable<Jury> {

	public static Optional<Jury> findByName(DataSession session, String name) {
		return session.<Jury>getNamedQuery("findJuryByName")
				.setParameter("name", name)
				.uniqueResultOptional();
	}

	public static List<Jury> findAll(DataSession session) {
		return session.<Jury>getNamedQuery("findAllJuries")
				.list();
	}

	public static void deleteAll(DataSession session) {
		session.<Jury>getNamedQuery("deleteAllJuries").executeUpdate();
	}

	private static final long serialVersionUID = 0L;

	@Id
	@GeneratedValue
	private Long id;
	@Version
	private Long version;

	@Basic(optional = false)
	@Column(nullable = false)
	private String name;

	protected Jury() {
	}

	public Jury(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Jury o) {
		return (this.name == null ? "" : this.name).compareTo(
				o.name == null ? "" : o.name);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
