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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author astoffel
 */
@Entity
@Access(AccessType.FIELD)
@NamedQueries({
	@NamedQuery(
			name = "findExamByName",
			query = "from Exam e where e.name = :name"
	),
	@NamedQuery(
			name = "findAllExams",
			query = "from Exam e order by e.sort asc"
	),
	@NamedQuery(
			name = "deleteAllExams",
			query = "delete from Exam e"
	)
})
public class Exam implements EntityObject, Serializable, Comparable<Exam> {

	public static Optional<Exam> findByName(DataSession session, String name) {
		return session.<Exam>getNamedQuery("findExamByName")
				.setParameter("name", name)
				.uniqueResultOptional();
	}

	public static List<Exam> findAll(DataSession session) {
		return session.<Exam>getNamedQuery("findAllExams")
				.list();
	}

	public static void deleteAll(DataSession session) {
		session.<Exam>getNamedQuery("deleteAllExams")
				.executeUpdate();
	}

	private static final long serialVersionUID = 0L;

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
	@JoinTable(name = "Exam_Description")
	@JoinColumn(name = "exam_id", nullable = false)
	@MapKeyJoinColumn(name = "category_id", nullable = false)
	@Column(name = "description", nullable = false, length = 4096)
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
		return Objects.equals(this.id, other.id);
	}

	@Override
	public int compareTo(Exam o) {
		return Integer.compare(
				this.sort == null ? 0 : this.sort,
				o.sort == null ? 0 : o.sort);
	}

	public Long getId() {
		return id;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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

	public String getDisplayShortName() {
		return displayShortName;
	}

	public void setDisplayShortName(String displayShortName) {
		this.displayShortName = displayShortName;
	}

	public Map<Category, String> getDescriptions() {
		return Collections.unmodifiableMap(descriptions);
	}

	public String getDescription(Category category) {
		return descriptions.get(category);
	}

}
