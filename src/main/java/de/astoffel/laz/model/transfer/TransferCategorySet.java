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
public final class TransferCategorySet extends EntitySet<TransferCategory> {

	public TransferCategorySet(TransferSession session) {
		super(session);
	}

	public TransferCategory create(String name, String description) {
		return new TransferCategory(name, description);
	}

	@Override
	public Optional<TransferCategory> find(long id) throws TransferException {
		return unit.compute(session -> Optional.ofNullable(session
				.get(TransferCategory.class, id)));
	}

	public Optional<TransferCategory> findByName(String name) throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findCategoryByName", TransferCategory.class)
				.setParameter("name", name)
				.uniqueResultOptional());
	}

	@Override
	public List<TransferCategory> findAll() throws TransferException {
		return unit.compute(session -> session
				.createNamedQuery("findAllCategories", TransferCategory.class)
				.list());
	}

	@Override
	public void deleteAll() throws TransferException {
		unit.execute(session -> session
				.createNamedQuery("deleteAllCategories")
				.executeUpdate());
	}

}
