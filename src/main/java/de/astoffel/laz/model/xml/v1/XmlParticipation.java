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

import de.astoffel.laz.model.Assessment;
import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.DataSession;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.Participation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	public XmlParticipation(Participation participation) {
		this.category = participation.getCategory().getName();
		this.instrument = participation.getInstrument().getName();
		this.jury = participation.getJury().getName();
		for (Map.Entry<Exam, Assessment> a : participation.getAssessments().entrySet()) {
			assessments.add(new XmlAssessment(a.getKey(), a.getValue()));
		}
	}

	void create(DataSession session, Participant participant) {
		  var exams = new ArrayList<Exam>();
		for (  var a : assessments) {
			exams.add(Exam.findByName(session, a.getExam()).get());
		}
		  var participation = new Participation(participant,
				Category.findByName(session, category).get(),
				Instrument.findByName(session, instrument).get(),
				Jury.findByName(session, jury).get(),
				exams);
		for (  var a : assessments) {
			if (a.getGrade() == null) {
				continue;
			}
			  var exam = Exam.findByName(session, a.getExam()).get();
			  var grade = Grade.findByName(session, a.getGrade()).get();
			participation.getAssessment(exam).setGrade(grade);
		}
		session.persist(participation);
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
