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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author astoffel
 */
public class JuryTest extends AbstractPersistenceTest {

	public JuryTest() {
	}

	/**
	 * Test of findByName method, of class Jury.
	 */
	@Test
	public void testFindByName() {
		System.out.println("findByName");
		model.atomic(session -> {
			  var query = juries.get(0);
			  var name = query.getName();
			  var result = Jury.findByName(session, name);
			assertTrue(result.isPresent());
			assertEquals(query.getId(), result.get().getId());
		});
	}

	/**
	 * Test of findAll method, of class Jury.
	 */
	@Test
	public void testFindAll() {
		System.out.println("findAll");
		model.atomic(session -> {
			  var result = Jury.findAll(session);
			assertEquals(juries.size(), result.size());
		});
	}

	/**
	 * Test of deleteAll method, of class Jury.
	 */
	@Test
	public void testDeleteAll() {
		System.out.println("deleteAll");
		model.atomic(session -> {
			Jury.deleteAll(session);
		});
		model.atomic(session -> {
			assertTrue(session.juries().findAll().isEmpty());
		});
	}

}
