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
import de.astoffel.laz.model.transfer.TransferExam;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author astoffel
 */
public final class ExamSet extends AbstractEntitySet<Exam, TransferExam> {

	public ExamSet(TransferModel transferModel) {
		super(transferModel, TransferEntityType.EXAM);
	}

	@Override
	TransferExam createTransfer() {
		return new TransferExam(0, "", "", "", Collections.emptyMap());
	}

	@Override
	Exam createEntity(TransferExam transfer) {
		return new Exam(transferModel(), transfer);
	}

	@Override
	public List<Exam> findAll() {
		return doFindAll(session -> session.exams().findAll());
	}

}
