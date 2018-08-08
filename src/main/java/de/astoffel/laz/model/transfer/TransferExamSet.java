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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author astoffel
 */
public final class TransferExamSet extends EntitySet<TransferExam> {

	public TransferExamSet(TransferSession unit) {
		super(unit);
	}

	public TransferExam create(String name, String displayName) {
		return new TransferExam(0, name, displayName, "", Collections.emptyMap());
	}

	@Override
	public Optional<TransferExam> find(long id) throws TransferException {
		return unit.compute(session -> Optional.ofNullable(session
				.get(TransferExam.class, id)));
	}

	public Optional<TransferExam> findByName(String name) throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findExamByName", TransferExam.class)
				.setParameter("name", name)
				.uniqueResultOptional());
	}

	@Override
	public List<TransferExam> findAll() throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findAllExams", TransferExam.class)
				.list());
	}

	@Override
	public void deleteAll() throws TransferException {
		unit.execute(session -> session.createNamedQuery("deleteAllExams")
				.executeUpdate());
	}

}
