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
public class InstrumentTest extends AbstractPersistenceTest {

	public InstrumentTest() {
	}

	/**
	 * Test of findByName method, of class Instrument.
	 */
	@Test
	public void testFindByName() throws TransferException {
		System.out.println("findByName");
		model.execute(session -> {
			var query = instruments.get(0);
			var name = query.getName();
			var result = session.instruments().findByName(name);
			assertTrue(result.isPresent());
			assertEquals(query.getId(), result.get().getId());
		});
	}

	/**
	 * Test of findAll method, of class Instrument.
	 */
	@Test
	public void testFindAll() throws TransferException {
		System.out.println("findAll");
		model.execute(session -> {
			var result = session.instruments().findAll();
			assertEquals(instruments.size(), result.size());
		});
	}

	/**
	 * Test of deleteAll method, of class Instrument.
	 */
	@Test
	public void testDeleteAll() throws TransferException {
		System.out.println("deleteAll");
		model.execute(session -> {
			session.instruments().deleteAll();
		});
		model.execute(session -> {
			assertTrue(session.instruments().findAll().isEmpty());
		});
	}

}
