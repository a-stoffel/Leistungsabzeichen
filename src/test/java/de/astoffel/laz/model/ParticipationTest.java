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
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author astoffel
 */
public class ParticipationTest extends AbstractPersistenceTest {

	public ParticipationTest() {
	}

	/**
	 * Test of find method, of class Participation.
	 */
	@Test
	public void testFind() {
		System.out.println("find");
		model.atomic(session -> {
			  var query = participantions.get(0);
			  var participant = query.getParticipant();
			  var category = query.getCategory();
			  var instrument = query.getInstrument();
			  var result = Participation.find(session, participant, category, instrument);
			assertTrue(result.isPresent());
			assertEquals(query.getId(), result.get().getId());
		});
	}

	/**
	 * Test of findAll method, of class Participation.
	 */
	@Test
	public void testFindAll() {
		System.out.println("findAll");
		model.atomic(session -> {
			  var result = Participation.findAll(session);
			assertEquals(participantions.size(), result.size());
		});
	}

	/**
	 * Test of findAllOfParticipant method, of class Participation.
	 */
	@Test
	public void testFindAllOfParticipant() {
		System.out.println("findAllOfParticipant");
		model.atomic(session -> {
			  var participant = participants.get(0);
			  var expResult = participantions.stream()
					.filter(p -> Objects.equals(p.getParticipant().getId(), participant.getId()))
					.collect(Collectors.toList());
			  var result = Participation.findAllOfParticipant(session, participant);
			assertEquals(expResult.size(), result.size());
		});
	}

	/**
	 * Test of deleteAll method, of class Participation.
	 */
	@Test
	public void testDeleteAll() {
		System.out.println("deleteAll");
		model.atomic(session -> {
			Participation.deleteAll(session);
		});
		model.atomic(session -> {
			assertTrue(Participation.findAll(session).isEmpty());
		});
	}

}
