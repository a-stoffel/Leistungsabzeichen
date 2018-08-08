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

/**
 *
 * @author astoffel
 */
public final class TransferGradeSet extends EntitySet<TransferGrade> {

	public TransferGradeSet(TransferSession unit) {
		super(unit);
	}

	public TransferGrade create(String name, String displayName) {
		return new TransferGrade(name, displayName);
	}

	@Override
	public Optional<TransferGrade> find(long id) throws TransferException {
		return unit.compute(session -> Optional.ofNullable(session
				.get(TransferGrade.class, id)));
	}

	public Optional<TransferGrade> findByName(String name) throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findGradeByName", TransferGrade.class)
				.setParameter("name", name)
				.uniqueResultOptional());
	}

	@Override
	public List<TransferGrade> findAll() throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findAllGrades", TransferGrade.class)
				.list());
	}

	@Override
	public void deleteAll() throws TransferException {
		unit.execute(session -> session.createNamedQuery("deleteAllGrades")
				.executeUpdate());
	}

}
