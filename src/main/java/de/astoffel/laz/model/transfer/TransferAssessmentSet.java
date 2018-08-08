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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author astoffel
 */
final class TransferAssessmentSet extends EntitySet<TransferAssessment> {

	private static final Logger LOG = Logger.getLogger(TransferAssessmentSet.class
			.getName());

	public TransferAssessmentSet(TransferSession session) {
		super(session);
	}

	@Override
	public Optional<TransferAssessment> find(long id) throws TransferException {
		return unit.compute(session -> Optional.ofNullable(session
				.get(TransferAssessment.class, id)));
	}

	@Override
	public List<TransferAssessment> findAll() throws TransferException {
		LOG
				.log(Level.WARNING, "Trying to find all Assessments is not implemented");
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() throws TransferException {
		LOG
				.log(Level.WARNING, "Trying to delete all Assessments is not implemented");
		throw new UnsupportedOperationException();
	}

}
