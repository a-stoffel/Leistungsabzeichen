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

import de.astoffel.laz.test.TestModelGenerator;
import java.util.List;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author astoffel
 */
public abstract class AbstractPersistenceTest {

	protected List<TransferCategory> categories;
	protected List<TransferExam> exams;
	protected List<TransferGrade> grades;
	protected List<TransferInstrument> instruments;
	protected List<TransferJury> juries;
	protected List<TransferParticipant> participants;
	protected List<TransferParticipation> participantions;
	protected TransferMeta meta;
	protected TestTransferModel model;

	@Before
	public void setUp() throws TransferException {
		model = new TestTransferModel();
		model.execute(this::init);
	}

	@After
	public void tearDown() throws TransferException {
		clear();
		if (model != null) {
			model.close();
			model = null;
		}
	}

	private void init(TransferSession session) throws TransferException {
		var generator = new TestModelGenerator(session);
		this.categories = generator.categories;
		this.exams = generator.exams;
		this.grades = generator.grades;
		this.instruments = generator.instruments;
		this.juries = generator.juries;
		this.participants = generator.participants;
		this.participantions = generator.participantions;
		this.meta = generator.meta;
	}

	private void clear() {
		this.categories = null;
		this.exams = null;
		this.grades = null;
		this.instruments = null;
		this.juries = null;
		this.meta = null;
		this.participants = null;
		this.participantions = null;
	}
}
