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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author astoffel
 */
public class CategoryTest extends AbstractPersistenceTest {

	public CategoryTest() {
	}

	/**
	 * Test of findByName method, of class Category.
	 */
	@Test
	public void testFindByName() throws TransferException {
		System.out.println("findByName");
		model.execute(session -> {
			var query = categories.get(0);
			var name = query.getName();
			var result = session.categories().findByName(name);
			assertTrue(result.isPresent());
			assertEquals(query.getId(), result.get().getId());
		});
	}

	/**
	 * Test of findAll method, of class Category.
	 */
	@Test
	public void testFindAll() throws TransferException {
		System.out.println("findAll");
		model.execute(session -> {
			var result = session.categories().findAll();
			assertEquals(categories.size(), result.size());
		});
	}

	/**
	 * Test of deleteAll method, of class Category.
	 */
	@Test
	public void testDeleteAll() throws TransferException {
		System.out.println("deleteAll");
		model.execute(session -> {
			session.categories().deleteAll();
		});
		model.execute(session -> {
			assertTrue(session.categories().findAll().isEmpty());
		});
	}
}
