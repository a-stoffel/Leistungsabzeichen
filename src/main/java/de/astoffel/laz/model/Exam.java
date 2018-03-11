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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author andreas
 */
@Entity
@Access(AccessType.FIELD)
@NamedQueries({
	@NamedQuery(
			name = "findExam",
			query = "from Exam e where e.name = :exam"
	),
	@NamedQuery(
			name = "findAllExams",
			query = "from Exam e order by e.sort asc"
	),
	@NamedQuery(
			name = "deleteExams",
			query = "delete from Exam e"
	)
})
public class Exam implements Serializable {

	@Id
	@GeneratedValue
	private Long id;
	@Version
	private Long version;

	@Basic(optional = false)
	@Column(nullable = false)
	private Integer sort;

	@Basic(optional = false)
	@Column(nullable = false)
	private String name;

	@Basic(optional = false)
	@Column(nullable = false)
	private String displayName;

	@Basic(optional = false)
	@Column(nullable = false)
	private String displayShortName;

	@ElementCollection
	@JoinColumn(nullable = false)
	@Column(nullable = false, length = 4096)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Map<Category, String> descriptions;

	protected Exam() {
	}

	public Exam(Integer sort, String name, String displayName,
			String displayShortName, Map<Category, String> description) {
		this.sort = sort;
		this.name = name;
		this.displayName = displayName;
		this.displayShortName = displayShortName;
		this.descriptions = new HashMap<>(description);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + Objects.hashCode(this.id);
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
		final Exam other = (Exam) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}

	public Integer getSort() {
		return sort;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDisplayShortName() {
		return displayShortName;
	}

	public Map<Category, String> getDescriptions() {
		return Collections.unmodifiableMap(descriptions);
	}

	public String getDescription(Category category) {
		return descriptions.get(category);
	}

}
