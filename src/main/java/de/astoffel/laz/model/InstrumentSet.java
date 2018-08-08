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
import de.astoffel.laz.model.transfer.TransferInstrument;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.List;

/**
 *
 * @author astoffel
 */
public final class InstrumentSet extends AbstractEntitySet<Instrument, TransferInstrument> {

	InstrumentSet(TransferModel transferModel) {
		super(transferModel, TransferEntityType.INSTRUMENT);
	}

	@Override
	TransferInstrument createTransfer() {
		return new TransferInstrument("", "");
	}

	@Override
	Instrument createEntity(TransferInstrument transfer) {
		return new Instrument(transferModel(), transfer);
	}

	@Override
	public List<Instrument> findAll() {
		return doFindAll(session -> session.instruments().findAll());
	}

}
