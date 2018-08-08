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

import de.astoffel.laz.model.transfer.TransferCategory;
import de.astoffel.laz.model.transfer.TransferExam;
import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferGrade;
import de.astoffel.laz.model.transfer.TransferInstrument;
import de.astoffel.laz.model.transfer.TransferJury;
import de.astoffel.laz.model.transfer.TransferMeta;
import de.astoffel.laz.model.transfer.TransferParticipant;
import de.astoffel.laz.model.transfer.TransferParticipation;
import de.astoffel.laz.model.transfer.TransferSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author astoffel
 */
public final class TestModelGenerator {

	public final List<TransferCategory> categories = new ArrayList<>();
	public final List<TransferExam> exams = new ArrayList<>();
	public final List<TransferGrade> grades = new ArrayList<>();
	public final List<TransferInstrument> instruments = new ArrayList<>();
	public final List<TransferJury> juries = new ArrayList<>();
	public final List<TransferParticipant> participants = new ArrayList<>();
	public final List<TransferParticipation> participantions = new ArrayList<>();
	public final TransferMeta meta;

	public TestModelGenerator(TransferSession session) throws TransferException {
		var rand = new Random();
		for (var c = 0; c < 3; ++c) {
			var category = new TransferCategory("Category " + c,
					"Name of Category " + c);
			session.categories().persist(category);
			categories.add(category);
		}
		for (var e = 0; e < 3; ++e) {
			var descriptions = new HashMap<TransferCategory, String>();
			for (var c : categories) {
				descriptions.put(c, "Description of Exam " + e + " in " + c
						.getName());
			}
			var exam = new TransferExam(e, "Exam " + e, "Name of Exam " + e, "E" + e,
					descriptions);
			session.exams().persist(exam);
			exams.add(exam);
		}
		for (int g = 0; g < 3; ++g) {
			var grade = new TransferGrade("Grade " + g, "Name of Grade " + g);
			session.grades().persist(grade);
			grades.add(grade);
		}
		for (int i = 0; i < 3; ++i) {
			var instrument = new TransferInstrument("Instrument " + i,
					"Name of Instrument " + i);
			session.instruments().persist(instrument);
			instruments.add(instrument);
		}
		for (int j = 0; j < 3; ++j) {
			var jury = new TransferJury("Jury " + j);
			session.juries().persist(jury);
			juries.add(jury);
		}
		for (int p = 0; p < 3; ++p) {
			var participant = new TransferParticipant("Jury " + p);
			session.participants().persist(participant);
			participants.add(participant);
		}
		for (var p : participants) {
			for (var c : categories) {
				for (var i : instruments) {
					var j = juries.get(rand.nextInt(juries.size()));
					var participation = new TransferParticipation(p, c, i, j,
							new HashSet<>(exams));
					session.participations().persist(participation);
					participantions.add(participation);
					var first = true;
					for (var a : participation.getAssessments().values()) {
						if (first) {
							first = false;
							continue;
						}
						a.setGrade(grades.get(rand.nextInt(grades.size())));
					}
				}
			}
		}
		meta = new TransferMeta("location", "date");
		session.meta().persist(meta);
	}

}
