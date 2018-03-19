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
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author andreas
 */
public final class ExtAssessment {

	@XmlAttribute(name = "exam", required = true)
	private String exam;
	@XmlAttribute(name = "grade")
	private String grade;

	private ExtAssessment() {
	}

	public ExtAssessment(Exam exam, Assessment assessment) {
		this.exam = exam.getName();
		if (assessment.getGrade() != null) {
			this.grade = assessment.getGrade().getName();
		}
	}

	public String getExam() {
		return exam;
	}

	public String getGrade() {
		return grade;
	}

}
