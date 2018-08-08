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
public class MetaTest extends AbstractPersistenceTest {

	public MetaTest() {
	}

	/**
	 * Test of find method, of class Meta.
	 */
	@Test
	public void testFind() throws TransferException {
		System.out.println("find");
		model.execute(session -> {
			var result = session.meta().get();
			assertTrue(result.isPresent());
			assertFalse(result.get().getLocation().isEmpty());
			assertFalse(result.get().getEventDate().isEmpty());
		});
	}

	/**
	 * Test of deleteAll method, of class Meta.
	 */
	@Test
	public void testDeleteAll() throws TransferException {
		System.out.println("deleteAll");
		model.execute(session -> {
			session.meta().deleteAll();
		});
		model.execute(session -> {
			var result = session.meta().get();
			assertTrue(result.isPresent());
			assertTrue(result.get().getLocation().isEmpty());
			assertTrue(result.get().getEventDate().isEmpty());
		});
	}

}
