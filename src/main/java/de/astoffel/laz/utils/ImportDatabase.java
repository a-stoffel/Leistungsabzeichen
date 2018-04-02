/*
 * Copyright (C) 2018 Andreas Stoffel
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
package de.astoffel.laz.utils;

import de.astoffel.laz.model.Assessment;
import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.DataModel;
import de.astoffel.laz.model.DataSession;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Meta;
import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.Participation;
import de.astoffel.laz.model.extern.ExtAssessment;
import de.astoffel.laz.model.extern.ExtCategory;
import de.astoffel.laz.model.extern.ExtData;
import de.astoffel.laz.model.extern.ExtExam;
import de.astoffel.laz.model.extern.ExtGrade;
import de.astoffel.laz.model.extern.ExtInstrument;
import de.astoffel.laz.model.extern.ExtJury;
import de.astoffel.laz.model.extern.ExtParticipant;
import de.astoffel.laz.model.extern.ExtParticipation;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXB;

/**
 *
 * @author astoffel
 */
public abstract class ImportDatabase {

	public static void importDatabase(DataModel model, Path path) throws IOException {
		try (Reader reader = new InputStreamReader(
				Files.newInputStream(path), StandardCharsets.UTF_8)) {
			importDatabase(model, reader);
		}
	}

	public static void importDatabase(DataModel model, Reader reader) throws IOException {
		model.atomic(session -> {
			clearDatabase(session);
			ExtData data = JAXB.unmarshal(reader, ExtData.class);
			importMeta(session, data);
			importGrades(session, data.getGrades());
			importInstruments(session, data.getInstruments());
			importCategories(session, data.getCategories());
			importExams(session, data.getExams());
			importJuries(session, data.getJuries());
			importParticipants(session, data.getParticipants());
		});
	}

	private static void clearDatabase(DataSession session) {
		session.getNamedQuery("deleteParticipations").executeUpdate();
		session.participants().deleteAll();
		session.juries().deleteAll();
		session.exams().deleteAll();
		session.categories().deleteAll();
		session.instruments().deleteAll();
		session.grades().deleteAll();
		session.getNamedQuery("deleteMetas").executeUpdate();
	}

	private static void importMeta(DataSession session, ExtData data) {
		session.persist(new Meta(data.getLocation(), data.getWhen()));
	}

	private static void importGrades(DataSession session, List<ExtGrade> grades) {
		for (ExtGrade g : grades) {
			session.persist(new Grade(g.getName(), g.getDisplayName()));
		}
	}

	private static void importInstruments(DataSession session, List<ExtInstrument> instruments) {
		for (ExtInstrument i : instruments) {
			session.persist(new Instrument(i.getName(), i.getDisplayName()));
		}
	}

	private static void importCategories(DataSession session, List<ExtCategory> categories) {
		for (ExtCategory c : categories) {
			session.persist(new Category(c.getName(), c.getDisplayName()));
		}
	}

	private static void importExams(DataSession session, List<ExtExam> exams) {
		for (ExtExam e : exams) {
			Map<Category, String> descriptions = new HashMap<>();
			for (ExtExam.ExtDescription d : e.getDescriptions()) {
				descriptions.put(
						session.categories().findByName(d.getCategory()),
						d.getDescription());
			}
			session.persist(new Exam(e.getSort(), e.getName(),
					e.getDisplayName(), e.getDisplayShortName(), descriptions));
		}
	}

	private static void importJuries(DataSession session, List<ExtJury> juries) {
		for (ExtJury j : juries) {
			session.persist(new Jury(j.getName()));
		}
	}

	private static void importParticipants(DataSession session, List<ExtParticipant> participants) {
		List<Exam> exams = session.exams().findAll();
		for (ExtParticipant p : participants) {
			Participant participant = new Participant(p.getName());
			session.persist(participant);
			for (ExtParticipation pp : p.getParticipations()) {
				Participation participation = new Participation(participant,
						session.categories().findByName(pp.getCategory()),
						session.instruments().findByName(pp.getInstrument()),
						session.juries().findByName(pp.getJury()),
						exams);
				for (ExtAssessment a : pp.getAssessments()) {
					Assessment assessment = participation.getAssessment(
							session.exams().findByName(a.getExam()));
					assessment.setGrade(session.grades().findByName(a.getGrade()));
				}
				session.persist(participation);
			}
		}
	}

}
