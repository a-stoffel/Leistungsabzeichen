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
package de.astoffel.laz.model.xml.v1;

import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.transfer.TransferExam;
import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferParticipant;
import de.astoffel.laz.model.transfer.TransferParticipation;
import de.astoffel.laz.model.transfer.TransferSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author astoffel
 */
final class XmlParticipation {

	@XmlAttribute(name = "category", required = true)
	private String category;
	@XmlAttribute(name = "instrument", required = true)
	private String instrument;
	@XmlAttribute(name = "jury", required = true)
	private String jury;
	@XmlElement(name = "assessment")
	private final List<XmlAssessment> assessments = new ArrayList<>();

	private XmlParticipation() {
	}

	public XmlParticipation(TransferParticipation participation) {
		this.category = participation.getCategory().getName();
		this.instrument = participation.getInstrument().getName();
		this.jury = participation.getJury().getName();
		for (var a : participation.getAssessments().entrySet()) {
			assessments.add(new XmlAssessment(a.getKey(), a.getValue()));
		}
	}

	void create(TransferSession session, TransferParticipant participant) throws TransferException {
		var exams = new HashSet<TransferExam>();
		for (var a : assessments) {
			exams.add(session.exams().findByName(a.getExam()).get());
		}
		var participation = new TransferParticipation(participant,
				session.categories().findByName(category).get(),
				session.instruments().findByName(instrument).get(),
				session.juries().findByName(jury).get(),
				exams);
		for (var a : assessments) {
			if (a.getGrade() == null) {
				continue;
			}
			var exam = session.exams().findByName(a.getExam()).get();
			var grade = session.grades().findByName(a.getGrade()).get();
			participation.assessmentOf(exam).setGrade(grade);
		}
		session.participations().persist(participation);
	}

	String getCategory() {
		return category;
	}

	String getInstrument() {
		return instrument;
	}

	String getJury() {
		return jury;
	}

	List<XmlAssessment> getAssessments() {
		return assessments;
	}

}
