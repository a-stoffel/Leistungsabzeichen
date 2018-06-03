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
public class MetaTest extends AbstractPersistenceTest {

	public MetaTest() {
	}

	/**
	 * Test of find method, of class Meta.
	 */
	@Test
	public void testFind() {
		System.out.println("find");
		model.atomic(session -> {
			  var result = Meta.find(session);
			assertNotNull(result);
			assertFalse(result.getLocation().isEmpty());
			assertFalse(result.getEventDate().isEmpty());
		});
	}

	/**
	 * Test of deleteAll method, of class Meta.
	 */
	@Test
	public void testDeleteAll() {
		System.out.println("deleteAll");
		model.atomic(session -> {
			Meta.deleteAll(session);
		});
		model.atomic(session -> {
			  var result = Meta.find(session);
			assertTrue(result.getLocation().isEmpty());
			assertTrue(result.getEventDate().isEmpty());
		});
	}

}
