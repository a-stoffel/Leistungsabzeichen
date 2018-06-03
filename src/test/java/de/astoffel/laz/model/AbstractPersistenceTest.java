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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author astoffel
 */
public abstract class AbstractPersistenceTest {

	protected List<Category> categories;
	protected List<Exam> exams;
	protected List<Grade> grades;
	protected List<Instrument> instruments;
	protected List<Jury> juries;
	protected Meta meta;
	protected List<Participant> participants;
	protected List<Participation> participantions;
	protected TestDataModel model;

	@Before
	public void setUp() {
		model = new TestDataModel();
		model.atomic(this::init);
	}

	@After
	public void tearDown() {
		clear();
		if (model != null) {
			model.close();
			model = null;
		}
	}

	private void init(DataSession session) {
		Random rand = new Random();
		categories = new ArrayList<>();
		for (int c = 0; c < 3; ++c) {
			Category category = new Category("Category " + c, "Name of Category " + c);
			session.persist(category);
			categories.add(category);
		}
		exams = new ArrayList<>();
		for (int e = 0; e < 3; ++e) {
			Map<Category, String> descriptions = new HashMap<>();
			for (Category c : categories) {
				descriptions.put(c, "Description of Exam " + e + " in " + c.getName());
			}
			Exam exam = new Exam(e, "Exam " + e, "Name of Exam " + e, "E" + e, descriptions);
			session.persist(exam);
			exams.add(exam);
		}
		grades = new ArrayList<>();
		for (int g = 0; g < 3; ++g) {
			Grade grade = new Grade("Grade " + g, "Name of Grade " + g);
			session.persist(grade);
			grades.add(grade);
		}
		instruments = new ArrayList<>();
		for (int i = 0; i < 3; ++i) {
			Instrument instrument = new Instrument("Instrument " + i, "Name of Instrument " + i);
			session.persist(instrument);
			instruments.add(instrument);
		}
		juries = new ArrayList<>();
		for (int j = 0; j < 3; ++j) {
			Jury jury = new Jury("Jury " + j);
			session.persist(jury);
			juries.add(jury);
		}
		meta = new Meta("location", "date");
		session.persist(meta);
		participants = new ArrayList<>();
		for (int p = 0; p < 3; ++p) {
			Participant participant = new Participant("Jury " + p);
			session.persist(participant);
			participants.add(participant);
		}
		participantions = new ArrayList<>();
		for (Participant p : participants) {
			for (Category c : categories) {
				for (Instrument i : instruments) {
					Jury j = juries.get(rand.nextInt(juries.size()));
					Participation participation = new Participation(p, c, i, j, exams);
					session.persist(participation);
					participantions.add(participation);
				}
			}
		}
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
