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

import de.astoffel.laz.model.transfer.TransferEntityType;
import de.astoffel.laz.model.transfer.TransferModel;
import de.astoffel.laz.model.transfer.TransferParticipation;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author astoffel
 */
public final class Participation extends AbstractEntity<TransferParticipation> {

	private final ReadOnlyProperty<Participant> participant;
	private final ReadOnlyProperty<Category> category;
	private final ReadOnlyProperty<Instrument> instrument;
	private final ReadOnlyProperty<Jury> jury;
	private final Map<Exam, Assessment> assessments;
	private final Model model;

	Participation(Model model, TransferModel transferModel,
			TransferParticipation transfer) {
		super(transferModel, TransferEntityType.PARTICIPATION, transfer);
		this.model = model;
		this.participant = createReadonlyProperty(p -> model.participants()
				.wrap(p.getParticipant()));
		this.category = createReadonlyProperty(p -> model.categories()
				.wrap(p.getCategory()));
		this.instrument = createReadonlyProperty(p -> model.instruments()
				.wrap(p.getInstrument()));
		this.jury = createReadonlyProperty(p -> model.juries()
				.wrap(p.getJury()));
		this.assessments = new WeakHashMap<>();
	}

	public boolean hasParticipated() {
		for (var e : model.exams().findAll()) {
			if (assessmentOf(e).getGrade() != null) {
				return true;
			}
		}
		return false;
	}

	public ReadOnlyProperty<Participant> participantProperty() {
		return participant;
	}

	public Participant getParticipant() {
		return participant.getValue();
	}

	public ReadOnlyProperty<Category> categoryProperty() {
		return category;
	}

	public Category getCategory() {
		return category.getValue();
	}

	public ReadOnlyProperty<Instrument> instrumentProperty() {
		return instrument;
	}

	public ReadOnlyProperty<Jury> juryProperty() {
		return jury;
	}

	public Jury getJury() {
		return jury.getValue();
	}

	public Assessment assessmentOf(Exam exam) {
		var result = assessments.get(exam);
		if (result == null) {
			result = model.assessments().wrap(
					transfer().getAssessment(exam.transfer()));
			assessments.put(exam, result);
		}
		return result;
	}

}
