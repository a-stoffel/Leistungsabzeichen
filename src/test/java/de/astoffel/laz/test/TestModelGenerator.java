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
package de.astoffel.laz.test;

import de.astoffel.laz.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author astoffel
 */
public final class TestModelGenerator {

	public final List<Category> categories = new ArrayList<>();
	public final List<Exam> exams = new ArrayList<>();
	public final List<Grade> grades = new ArrayList<>();
	public final List<Instrument> instruments = new ArrayList<>();
	public final List<Jury> juries = new ArrayList<>();
	public final List<Participant> participants = new ArrayList<>();
	public final List<Participation> participantions = new ArrayList<>();
	public final Meta meta;

	public TestModelGenerator(DataSession session) {
		  var rand = new Random();
		for (  var c = 0; c < 3; ++c) {
			  var category = new Category("Category " + c, "Name of Category " + c);
			session.persist(category);
			categories.add(category);
		}
		for (  var e = 0; e < 3; ++e) {
			  var descriptions = new HashMap<Category, String>();
			for (Category c : categories) {
				descriptions.put(c, "Description of Exam " + e + " in " + c.getName());
			}
			  var exam = new Exam(e, "Exam " + e, "Name of Exam " + e, "E" + e, descriptions);
			session.persist(exam);
			exams.add(exam);
		}
		for (int g = 0; g < 3; ++g) {
			  var grade = new Grade("Grade " + g, "Name of Grade " + g);
			session.persist(grade);
			grades.add(grade);
		}
		for (int i = 0; i < 3; ++i) {
			  var instrument = new Instrument("Instrument " + i, "Name of Instrument " + i);
			session.persist(instrument);
			instruments.add(instrument);
		}
		for (int j = 0; j < 3; ++j) {
			  var jury = new Jury("Jury " + j);
			session.persist(jury);
			juries.add(jury);
		}
		for (int p = 0; p < 3; ++p) {
			  var participant = new Participant("Jury " + p);
			session.persist(participant);
			participants.add(participant);
		}
		for (  var p : participants) {
			for (  var c : categories) {
				for (  var i : instruments) {
					  var j = juries.get(rand.nextInt(juries.size()));
					  var participation = new Participation(p, c, i, j, exams);
					session.persist(participation);
					participantions.add(participation);
					  var first = true;
					for (  var a : participation.getAssessments().values()) {
						if (first) {
							first = false;
							continue;
						}
						a.setGrade(grades.get(rand.nextInt(grades.size())));
					}
				}
			}
		}
		meta = new Meta("location", "date");
		session.persist(meta);
	}

}
