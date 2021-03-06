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
import de.astoffel.laz.model.transfer.TransferGrade;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author astoffel
 */
public final class GradeSet extends AbstractEntitySet<Grade, TransferGrade> {

	public GradeSet(TransferModel transferModel) {
		super(transferModel, TransferEntityType.GRADE);
	}

	@Override
	TransferGrade createTransfer() {
		return new TransferGrade("", "");
	}

	@Override
	Grade createEntity(TransferGrade transfer) {
		return new Grade(transferModel(), transfer);
	}

	public Optional<Grade> find(long id) {
		return doFind(session -> session.grades().find(id));
	}

	@Override
	public List<Grade> findAll() {
		return doFindAll(session -> session.grades().findAll());
	}

	public Optional<Grade> findByName(String name) {
		return doFind(session -> session.grades().findByName(name));
	}

}
