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

import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.DataModel;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.Participation;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author andreas
 */
final class ObservableParticipation {

	private final Participation participation;
	private final ObjectProperty<String> participant;
	private final ObjectProperty<Category> category;
	private final ObjectProperty<Instrument> instrument;
	private final ObjectProperty<Jury> jury;
	private final Map<Exam, ObservableAssessment> assessments;

	public ObservableParticipation(DataModel model, Participation participation) {
		this.participation = participation;
		this.participant = new UpdateObjectProperty<>(
				model, participation.getParticipant(),
				Participant::getName,
				Participant::setName
		);
		this.category = new ReadOnlyObjectWrapper(participation.getCategory());
		this.instrument = new ReadOnlyObjectWrapper(participation.getInstrument());
		this.jury = new ReadOnlyObjectWrapper(participation.getJury());
		this.assessments = participation.getAssessments().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> new ObservableAssessment(model, e.getValue())));
	}

	public Participation getParticipation() {
		return participation;
	}

	public ObjectProperty<String> getParticipant() {
		return participant;
	}

	public ObservableValue<Category> getCategory() {
		return category;
	}

	public ObservableValue<Instrument> getInstrument() {
		return instrument;
	}

	public ObservableValue<Jury> getJury() {
		return jury;
	}

	public ObservableAssessment getAssessment(Exam exam) {
		return assessments.get(exam);
	}

	public boolean hasParticipated() {
		return assessments.values().stream()
				.filter(a -> a.participatedProperty().get() != null)
				.anyMatch(a -> a.participatedProperty().get());
	}
}
