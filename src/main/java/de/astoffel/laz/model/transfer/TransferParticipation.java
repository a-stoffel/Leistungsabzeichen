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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author astoffel
 */
@Entity(name = "Participation")
@Access(AccessType.FIELD)
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"participant_id", "category_id", "instrument_id"
	})}
)
@NamedQueries({
	@NamedQuery(
			name = "findParticipation",
			query = "from Participation p where p.participant = :participant and p.category = :category and p.instrument = :instrument"
	),
	@NamedQuery(
			name = "findAllParticipationsByParticipant",
			query = "from Participation p where p.participant = :participant"
	),
	@NamedQuery(
			name = "findAllParticipations",
			query = "from Participation p"
	),
	@NamedQuery(
			name = "deleteAllParticipations",
			query = "delete from Participation p"
	)
})
public class TransferParticipation implements TransferEntity, Serializable {

	private static final long serialVersionUID = 0L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	@ManyToOne
	@JoinColumn(nullable = false)
	private TransferParticipant participant;

	@ManyToOne
	@JoinColumn(nullable = false)
	private TransferCategory category;

	@ManyToOne
	@JoinColumn(nullable = false)
	private TransferInstrument instrument;

	@ManyToOne
	@JoinColumn(nullable = false)
	private TransferJury jury;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "participation_id", nullable = false)
	@MapKeyJoinColumn(name = "exam_id", nullable = false)
	private Map<TransferExam, TransferAssessment> assessments;

	protected TransferParticipation() {
	}

	public TransferParticipation(TransferParticipant participant,
			TransferCategory category,
			TransferInstrument instrument, TransferJury jury,
			Set<TransferExam> exams) {
		this.participant = participant;
		this.category = category;
		this.instrument = instrument;
		this.jury = jury;
		this.assessments = new HashMap<>();
		for (TransferExam exam : exams) {
			this.assessments.put(exam, new TransferAssessment());
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	public TransferParticipant getParticipant() {
		return participant;
	}

	public TransferCategory getCategory() {
		return category;
	}

	public TransferInstrument getInstrument() {
		return instrument;
	}

	public TransferJury getJury() {
		return jury;
	}

	public Map<TransferExam, TransferAssessment> getAssessments() {
		return Collections.unmodifiableMap(assessments);
	}

	public TransferAssessment getAssessment(TransferExam exam) {
		return assessments.get(exam);
	}

}
