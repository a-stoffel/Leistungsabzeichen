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

import java.util.Objects;
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
	public void testFind() throws TransferException {
		System.out.println("find");
		model.execute(session -> {
			var query = participantions.get(0);
			var participant = query.getParticipant();
			var category = query.getCategory();
			var instrument = query.getInstrument();
			var result = session.participations().find(
					participant, category, instrument);
			assertTrue(result.isPresent());
			assertEquals(query.getId(), result.get().getId());
		});
	}

	/**
	 * Test of findAll method, of class Participation.
	 */
	@Test
	public void testFindAll() throws TransferException {
		System.out.println("findAll");
		model.execute(session -> {
			var result = session.participations().findAll();
			assertEquals(participantions.size(), result.size());
		});
	}

	/**
	 * Test of findAllOfParticipant method, of class Participation.
	 */
	@Test
	public void testFindAllOfParticipant() throws TransferException {
		System.out.println("findAllOfParticipant");
		model.execute(session -> {
			var participant = participants.get(0);
			var expResult = participantions.stream()
					.filter(p -> Objects.equals(p.getParticipant().getId(),
					participant.getId()))
					.collect(Collectors.toList());
			var result = session.participations().findAllByParticipant(participant);
			assertEquals(expResult.size(), result.size());
		});
	}

	/**
	 * Test of deleteAll method, of class Participation.
	 */
	@Test
	public void testDeleteAll() throws TransferException {
		System.out.println("deleteAll");
		model.execute(session -> {
			session.participations().deleteAll();
		});
		model.execute(session -> {
			assertTrue(session.participations().findAll().isEmpty());
		});
	}

}
