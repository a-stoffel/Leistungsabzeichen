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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author andreas
 */
public abstract class ImportDatabase {

	public static void importDatabase(DataModel model, Path path) throws IOException {
		try (Reader reader = new InputStreamReader(
				Files.newInputStream(path), StandardCharsets.UTF_8)) {
			importDatabase(model, reader);
		}
	}

	public static void importDatabase(DataModel model, Reader reader) throws IOException {
		Session session = model.getSession();
		Transaction transaction = session.beginTransaction();
		try {
			clearDatabase(session);
			ExtData data = JAXB.unmarshal(reader, ExtData.class);
			importMeta(session, data);
			importGrades(session, data.getGrades());
			importInstruments(session, data.getInstruments());
			importCategories(session, data.getCategories());
			importExams(session, data.getExams());
			importJuries(session, data.getJuries());
			importParticipants(session, data.getParticipants());
			transaction.commit();
		} catch (Throwable th) {
			transaction.rollback();
			throw th;
		}
	}

	private static void clearDatabase(Session session) {
		session.getNamedQuery("deleteParticipations").executeUpdate();
		session.getNamedQuery("deleteParticipants").executeUpdate();
		session.getNamedQuery("deleteJuries").executeUpdate();
		session.getNamedQuery("deleteExams").executeUpdate();
		session.getNamedQuery("deleteCategories").executeUpdate();
		session.getNamedQuery("deleteInstruments").executeUpdate();
		session.getNamedQuery("deleteGrades").executeUpdate();
		session.getNamedQuery("deleteMetas").executeUpdate();
	}

	private static void importMeta(Session session, ExtData data) {
		session.persist(new Meta(data.getLocation(), data.getWhen()));
	}

	private static void importGrades(Session session, List<ExtGrade> grades) {
		for (ExtGrade g : grades) {
			session.persist(new Grade(g.getName(), g.getDisplayName()));
		}
	}

	private static void importInstruments(Session session, List<ExtInstrument> instruments) {
		for (ExtInstrument i : instruments) {
			session.persist(new Instrument(i.getName(), i.getDisplayName()));
		}
	}

	private static void importCategories(Session session, List<ExtCategory> categories) {
		for (ExtCategory c : categories) {
			session.persist(new Category(c.getName(), c.getDisplayName()));
		}
	}

	private static void importExams(Session session, List<ExtExam> exams) {
		Query<Category> category = session.getNamedQuery("findCategory");
		for (ExtExam e : exams) {
			Map<Category, String> descriptions = new HashMap<>();
			for (ExtExam.ExtDescription d : e.getDescriptions()) {
				category.setParameter("category", d.getCategory());
				descriptions.put(category.uniqueResult(), d.getDescription());
			}
			session.persist(new Exam(e.getSort(), e.getName(),
					e.getDisplayName(), e.getDisplayShortName(), descriptions));
		}
	}

	private static void importJuries(Session session, List<ExtJury> juries) {
		for (ExtJury j : juries) {
			session.persist(new Jury(j.getName()));
		}
	}

	private static void importParticipants(Session session, List<ExtParticipant> participants) {
		Query<Category> category = session.getNamedQuery("findCategory");
		Query<Instrument> instrument = session.getNamedQuery("findInstrument");
		Query<Jury> jury = session.getNamedQuery("findJury");
		List<Exam> exams = session.getNamedQuery("findAllExams").list();
		Query<Exam> exam = session.getNamedQuery("findExam");
		Query<Grade> grade = session.getNamedQuery("findGrade");
		for (ExtParticipant p : participants) {
			Participant participant = new Participant(p.getName());
			session.persist(participant);
			for (ExtParticipation pp : p.getParticipations()) {
				category.setParameter("category", pp.getCategory());
				instrument.setParameter("instrument", pp.getInstrument());
				jury.setParameter("jury", pp.getJury());
				Participation participation = new Participation(participant,
						category.uniqueResult(),
						instrument.uniqueResult(),
						jury.uniqueResult(), exams);
				for (ExtAssessment a : pp.getAssessments()) {
					exam.setParameter("exam", a.getExam());
					grade.setParameter("grade", a.getGrade());
					Assessment assessment = participation.getAssessment(exam.uniqueResult());
					assessment.setGrade(grade.uniqueResult());
				}
				session.persist(participation);
			}
		}
	}

}
