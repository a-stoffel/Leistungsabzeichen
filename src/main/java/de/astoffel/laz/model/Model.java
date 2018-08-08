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

import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.Objects;

/**
 *
 * @author astoffel
 */
public final class Model implements AutoCloseable {

	private final TransferModel transferModel;
	private final AssessmentSet assessments;
	private final CategorySet categories;
	private final ExamSet exams;
	private final GradeSet grades;
	private final InstrumentSet instruments;
	private final JurySet juries;
	private final MetaSet metas;
	private final ParticipantSet participants;
	private final ParticipationSet participations;

	public Model(TransferModel transferModel) {
		this.transferModel = transferModel;
		this.assessments = new AssessmentSet(this, transferModel);
		this.categories = new CategorySet(transferModel);
		this.exams = new ExamSet(transferModel);
		this.grades = new GradeSet(transferModel);
		this.instruments = new InstrumentSet(transferModel);
		this.juries = new JurySet(transferModel);
		this.metas = new MetaSet(transferModel);
		this.participants = new ParticipantSet(transferModel);
		this.participations = new ParticipationSet(this, transferModel);
	}

	@Override
	public void close() {
		try {
			transferModel.close();
		} catch (TransferException ex) {
			throw new ModelException(ex);
		}
	}

	public void clear() {
		assessments.clear();
		categories.clear();
		exams.clear();
		grades.clear();
		instruments.clear();
		juries.clear();
		metas.clear();
		participants.clear();
		participations.clear();
	}

	AssessmentSet assessments() {
		return assessments;
	}

	public TransferModel transferModel() {
		return transferModel;
	}

	public <E extends AbstractEntity<?>> AbstractEntitySet<E, ?> getEntitySet(
			Class<E> type) {
		AbstractEntitySet<?, ?> result = null;
		if (Objects.equals(type, Category.class)) {
			result = categories;
		} else if (Objects.equals(type, Exam.class)) {
			result = exams;
		} else if (Objects.equals(type, Grade.class)) {
			result = grades;
		} else if (Objects.equals(type, Instrument.class)) {
			result = instruments;
		} else if (Objects.equals(type, Jury.class)) {
			result = juries;
		} else if (Objects.equals(type, Meta.class)) {
			result = metas;
		} else if (Objects.equals(type, Participant.class)) {
			result = participants;
		} else if (Objects.equals(type, Participation.class)) {
			result = participations;
		} else {
			throw new ModelException(String.format(
					"Unknown entity type %s",
					type.getSimpleName()));
		}
		@SuppressWarnings("unchecked")
		var casted = (AbstractEntitySet<E, ?>) result;
		return casted;
	}

	public CategorySet categories() {
		return categories;
	}

	public ExamSet exams() {
		return exams;
	}

	public GradeSet grades() {
		return grades;
	}

	public InstrumentSet instruments() {
		return instruments;
	}

	public JurySet juries() {
		return juries;
	}

	public MetaSet metas() {
		return metas;
	}

	public ParticipantSet participants() {
		return participants;
	}

	public ParticipationSet participations() {
		return participations;
	}

}
