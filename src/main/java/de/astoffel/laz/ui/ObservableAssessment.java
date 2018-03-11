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
package de.astoffel.laz.ui;

import de.astoffel.laz.model.Assessment;
import de.astoffel.laz.model.DataModel;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author andreas
 */
final class ObservableAssessment {

	private final Assessment assessment;
	private final ObjectProperty<Grade> grade;
	private final ObjectProperty<Boolean> participated;

	public ObservableAssessment(DataModel model, Assessment assessment) {
		this.assessment = assessment;
		this.grade = new UpdateObjectProperty<>(
				model, assessment, Assessment::getGrade, Assessment::setGrade);
		this.participated = new UpdateObjectProperty<>(
				model, assessment, Assessment::getParticipated, Assessment::setParticipated);
		
		this.grade.addListener((source, oldValue, newValue) -> {
			if (newValue != null) {
				this.participated.set(true);
			}
		});
	}

	public ObjectProperty<Grade> gradeProperty() {
		return grade;
	}

	public ObjectProperty<Boolean> participatedProperty() {
		return participated;
	}

}
