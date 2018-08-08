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
package de.astoffel.laz.model.transfer;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author astoffel
 */
public final class TransferSession {

	Object metas() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FunctionalInterface
	interface Work {

		void execute(Session session) throws HibernateException, TransferException;
	}

	@FunctionalInterface
	interface WorkWithResult<R> {

		R execute(Session session) throws HibernateException, TransferException;
	}

	private final Session session;
	private TransferAssessmentSet assessments;
	private TransferCategorySet categories;
	private TransferExamSet exams;
	private TransferGradeSet grades;
	private TransferInstrumentSet instruments;
	private TransferJurySet juries;
	private TransferMetaSet metas;
	private TransferParticipantSet participants;
	private TransferParticipationSet participations;

	public TransferSession(Session session) {
		this.session = session;
	}

	void execute(Work work) throws TransferException {
		compute(session -> {
			work.execute(session);
			return null;
		});
	}

	<R> R compute(WorkWithResult<R> work) throws TransferException {
		try {
			return work.execute(session);
		} catch (HibernateException ex) {
			throw new TransferException(ex);
		}
	}

	TransferAssessmentSet assessments() {
		if (assessments == null) {
			assessments = new TransferAssessmentSet(this);
		}
		return assessments;
	}

	public TransferCategorySet categories() {
		if (categories == null) {
			categories = new TransferCategorySet(this);
		}
		return categories;
	}

	public TransferExamSet exams() {
		if (exams == null) {
			exams = new TransferExamSet(this);
		}
		return exams;
	}

	public TransferGradeSet grades() {
		if (grades == null) {
			grades = new TransferGradeSet(this);
		}
		return grades;
	}

	public TransferInstrumentSet instruments() {
		if (instruments == null) {
			instruments = new TransferInstrumentSet(this);
		}
		return instruments;
	}

	public TransferJurySet juries() {
		if (juries == null) {
			juries = new TransferJurySet(this);
		}
		return juries;
	}

	public TransferMetaSet meta() {
		if (metas == null) {
			metas = new TransferMetaSet(this);
		}
		return metas;
	}

	public TransferParticipantSet participants() {
		if (participants == null) {
			participants = new TransferParticipantSet(this);
		}
		return participants;
	}

	public TransferParticipationSet participations() {
		if (participations == null) {
			participations = new TransferParticipationSet(this);
		}
		return participations;
	}

}
