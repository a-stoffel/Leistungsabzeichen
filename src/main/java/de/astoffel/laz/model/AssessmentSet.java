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

import de.astoffel.laz.model.transfer.TransferAssessment;
import de.astoffel.laz.model.transfer.TransferEntityType;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.List;

/**
 *
 * @author astoffel
 */
final class AssessmentSet extends AbstractEntitySet<Assessment, TransferAssessment> {

	private final Model model;

	public AssessmentSet(Model model, TransferModel transferModel) {
		super(transferModel, TransferEntityType.ASSESSMENT);
		this.model = model;
	}

	@Override
	TransferAssessment createTransfer() {
		throw new ModelException("Invalid operation creating assessment");
	}

	@Override
	Assessment createEntity(TransferAssessment transfer) {
		return new Assessment(model, transferModel(), transfer);
	}

	@Override
	public List<Assessment> findAll() {
		throw new ModelException("Invalid operation finding assessment");
	}

}
