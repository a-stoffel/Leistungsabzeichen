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
package de.astoffel.laz.model.extern;

import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Meta;
import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.Participation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author astoffel
 */
@XmlRootElement(name = "leistungsabzeichen")
public final class ExtData {

	@XmlAttribute(name = "location", required = true)
	private String location;

	@XmlAttribute(name = "when", required = true)
	private String when;

	@XmlElementWrapper(name = "grades", required = true, nillable = false)
	@XmlElement(name = "grade")
	private final List<ExtGrade> grades = new ArrayList<>();

	@XmlElementWrapper(name = "instruments", required = true, nillable = false)
	@XmlElement(name = "instrument")
	private final List<ExtInstrument> instruments = new ArrayList<>();

	@XmlElementWrapper(name = "juries", required = true, nillable = false)
	@XmlElement(name = "jury")
	private final List<ExtJury> juries = new ArrayList<>();

	@XmlElementWrapper(name = "categories", required = true, nillable = false)
	@XmlElement(name = "category")
	private final List<ExtCategory> categories = new ArrayList<>();

	@XmlElementWrapper(name = "exams", required = true, nillable = false)
	@XmlElement(name = "exam")
	private final List<ExtExam> exams = new ArrayList<>();

	@XmlElementWrapper(name = "participants", required = true, nillable = false)
	@XmlElement(name = "participant")
	private final List<ExtParticipant> participants = new ArrayList<>();

	private ExtData() {
	}

	public ExtData(Meta meta, List<Grade> grades, List<Instrument> instruments,
			List<Jury> juries, List<Category> categories,
			List<Exam> exams, Map<Participant, List<Participation>> participants) {
		this.location = meta.getLocation();
		this.when = meta.getEventDate();
		for (Grade g : grades) {
			this.grades.add(new ExtGrade(g));
		}
		for (Instrument i : instruments) {
			this.instruments.add(new ExtInstrument(i));
		}
		for (Jury j : juries) {
			this.juries.add(new ExtJury(j));
		}
		for (Category c : categories) {
			this.categories.add(new ExtCategory(c));
		}
		for (Exam e : exams) {
			this.exams.add(new ExtExam(e));
		}
		for (Map.Entry<Participant, List<Participation>> p : participants.entrySet()) {
			this.participants.add(new ExtParticipant(p.getKey(), p.getValue()));
		}
	}

	public String getLocation() {
		return location;
	}

	public String getWhen() {
		return when;
	}

	public List<ExtGrade> getGrades() {
		return Collections.unmodifiableList(grades);
	}

	public List<ExtInstrument> getInstruments() {
		return Collections.unmodifiableList(instruments);
	}

	public List<ExtJury> getJuries() {
		return Collections.unmodifiableList(juries);
	}

	public List<ExtCategory> getCategories() {
		return Collections.unmodifiableList(categories);
	}

	public List<ExtExam> getExams() {
		return Collections.unmodifiableList(exams);
	}

	public List<ExtParticipant> getParticipants() {
		return Collections.unmodifiableList(participants);
	}

}
