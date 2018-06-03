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

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author astoffel
 */
public class ExamTest extends AbstractPersistenceTest {

	public ExamTest() {
	}

	/**
	 * Test of findByName method, of class Exam.
	 */
	@Test
	public void testFindByName() {
		System.out.println("findByName");
		model.atomic(session -> {
			  var query = exams.get(0);
			  var name = query.getName();
			  var result = Exam.findByName(session, name);
			assertTrue(result.isPresent());
			assertEquals(query.getId(), result.get().getId());
		});
	}

	/**
	 * Test of findAll method, of class Exam.
	 */
	@Test
	public void testFindAll() {
		System.out.println("findAll");
		model.atomic(session -> {
			  var result = Exam.findAll(session);
			assertEquals(exams.size(), result.size());
		});
	}

	/**
	 * Test of deleteAll method, of class Exam.
	 */
	@Test
	public void testDeleteAll() {
		System.out.println("deleteAll");
		model.atomic(session -> {
			Exam.deleteAll(session);
		});
		model.atomic(session -> {
			assertTrue(session.exams().findAll().isEmpty());
		});
	}

}
