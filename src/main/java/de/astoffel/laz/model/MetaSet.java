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
import de.astoffel.laz.model.transfer.TransferMeta;
import de.astoffel.laz.model.transfer.TransferModel;
import java.util.List;

/**
 *
 * @author astoffel
 */
public final class MetaSet extends AbstractEntitySet<Meta, TransferMeta> {

	MetaSet(TransferModel transferModel) {
		super(transferModel, TransferEntityType.META);
	}

	@Override
	TransferMeta createTransfer() {
		return new TransferMeta("", "");
	}

	@Override
	Meta createEntity(TransferMeta transfer) {
		return new Meta(transferModel(), transfer);
	}

	@Override
	public List<Meta> findAll() {
		return doFindAll(session -> session.meta().findAll());
	}

	public Meta get() {
		return doFind(session -> session.meta().get()).get();
	}
}
