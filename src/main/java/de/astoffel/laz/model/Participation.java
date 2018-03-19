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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author andreas
 */
@Entity
@Access(AccessType.FIELD)
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"participant_id", "category_id", "instrument_id"
	})}
)
@NamedQueries({
	@NamedQuery(
			name = "findParticipation",
			query = "from Participation p where p.participant.id = :participant and p.category.name = :category and p.instrument.name = :instrument"
	),
	@NamedQuery(
			name = "findParticipations",
			query = "from Participation p where p.participant.id = :participant"
	),
	@NamedQuery(
			name = "findAllParticipations",
			query = "from Participation p"
	),
	@NamedQuery(
			name = "deleteParticipations",
			query = "delete from Participation p"
	)
})
public class Participation implements Serializable {

	private static final long serialVersionUID = 0L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	@ManyToOne
	@JoinColumn(nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Participant participant;

	@ManyToOne
	@JoinColumn(nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Category category;

	@ManyToOne
	@JoinColumn(nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Instrument instrument;

	@ManyToOne
	@JoinColumn(nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Jury jury;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Map<Exam, Assessment> assessments;

	protected Participation() {
	}

	public Participation(Participant participant, Category category,
			Instrument instrument, Jury jury, List<Exam> exams) {
		this.participant = participant;
		this.category = category;
		this.instrument = instrument;
		this.jury = jury;
		this.assessments = new HashMap<>();
		for (Exam exam : exams) {
			this.assessments.put(exam, new Assessment());
		}
	}

	public Participant getParticipant() {
		return participant;
	}

	public Category getCategory() {
		return category;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public Jury getJury() {
		return jury;
	}

	public Map<Exam, Assessment> getAssessments() {
		return Collections.unmodifiableMap(assessments);
	}

	public Assessment getAssessment(Exam exam) {
		return assessments.get(exam);
	}

}
