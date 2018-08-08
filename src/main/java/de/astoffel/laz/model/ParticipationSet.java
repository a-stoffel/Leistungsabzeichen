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
import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferModel;
import de.astoffel.laz.model.transfer.TransferParticipation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author astoffel
 */
public final class ParticipationSet extends AbstractEntitySet<Participation, TransferParticipation> {

	private final Model model;

	ParticipationSet(Model model, TransferModel transferModel) {
		super(transferModel, TransferEntityType.PARTICIPATION);
		this.model = model;
	}

	@Override
	TransferParticipation createTransfer() {
		throw new ModelException("Invalid operation creating participation");
	}

	@Override
	Participation createEntity(TransferParticipation transfer) {
		return new Participation(model, transferModel(), transfer);
	}

	public Participation create(Participant participant, Category category,
			Instrument instrument, Jury jury, Set<Exam> exams) {
		try {
			var transfer = transferModel().compute(session -> {
				var t = new TransferParticipation(
						participant.transfer(),
						category.transfer(),
						instrument.transfer(),
						jury.transfer(),
						exams.stream()
								.map(Exam::transfer)
								.collect(Collectors.toSet())
				);
				session.participations().persist(t);
				return t;
			});
			return wrap(transfer);
		} catch (TransferException ex) {
			throw new ModelException("Failed creating entity participation", ex);
		}
	}

	@Override
	public List<Participation> findAll() {
		return doFindAll(session -> session.participations().findAll());
	}
}
