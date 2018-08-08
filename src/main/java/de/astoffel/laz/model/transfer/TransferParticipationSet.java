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

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author astoffel
 */
public final class TransferParticipationSet extends EntitySet<TransferParticipation> {

	public TransferParticipationSet(TransferSession unit) {
		super(unit);
	}

	public TransferParticipation create(TransferParticipant participant,
			TransferCategory category,
			TransferInstrument instrument, TransferJury jury,
			Set<TransferExam> exams) {
		return new TransferParticipation(participant, category, instrument, jury,
				exams);
	}

	@Override
	public Optional<TransferParticipation> find(long id) throws TransferException {
		return unit.compute(session -> Optional.ofNullable(session
				.get(TransferParticipation.class, id)));
	}

	public Optional<TransferParticipation> find(TransferParticipant participant,
			TransferCategory category, TransferInstrument instrument)
			throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findParticipation",
						TransferParticipation.class)
				.setParameter("participant", participant)
				.setParameter("category", category)
				.setParameter("instrument", instrument)
				.uniqueResultOptional());
	}

	@Override
	public List<TransferParticipation> findAll() throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findAllParticipations",
						TransferParticipation.class)
				.list());
	}

	public List<TransferParticipation> findAllByParticipant(
			TransferParticipant participant)
			throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findAllParticipationsByParticipant",
						TransferParticipation.class)
				.setParameter("participant", participant)
				.list());
	}

	@Override
	public void deleteAll() throws TransferException {
		unit.execute(session -> session
				.createNamedQuery("deleteAllParticipations")
				.executeUpdate());
	}

}
