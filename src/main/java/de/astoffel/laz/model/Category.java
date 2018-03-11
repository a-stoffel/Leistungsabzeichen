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
import java.util.Objects;
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
 * @author andreas
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
			name = "findCategory",
			query = "from Category c where c.name = :category"
	),
	 @NamedQuery(
			name = "findAllCategories",
			query = "from Category c"
	),
	 @NamedQuery(
			name = "deleteCategories",
			query = "delete from Category c"
	)
})
public class Category implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private long version;

	@Basic(optional = false)
	@Column(nullable = false)
	private String name;

	@Basic(optional = false)
	@Column(nullable = false)
	private String displayName;

	protected Category() {
	}

	public Category(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Category other = (Category) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

}
