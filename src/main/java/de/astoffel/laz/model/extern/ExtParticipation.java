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

import de.astoffel.laz.model.Assessment;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Participation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author andreas
 */
public final class ExtParticipation {

	@XmlAttribute(name = "category", required = true)
	private String category;
	@XmlAttribute(name = "instrument", required = true)
	private String instrument;
	@XmlAttribute(name = "jury", required = true)
	private String jury;
	@XmlElement(name = "assessment")
	private final List<ExtAssessment> assessments = new ArrayList<>();

	private ExtParticipation() {
	}

	public ExtParticipation(Participation participation) {
		this.category = participation.getCategory().getName();
		this.instrument = participation.getInstrument().getName();
		this.jury = participation.getJury().getName();
		for (Map.Entry<Exam, Assessment> a : participation.getAssessments().entrySet()) {
			assessments.add(new ExtAssessment(a.getKey(), a.getValue()));
		}
	}

	public String getCategory() {
		return category;
	}

	public String getInstrument() {
		return instrument;
	}

	public String getJury() {
		return jury;
	}

	public List<ExtAssessment> getAssessments() {
		return Collections.unmodifiableList(assessments);
	}

}
