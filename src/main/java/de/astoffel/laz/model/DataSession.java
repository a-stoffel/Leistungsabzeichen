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

import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author astoffel
 */
public final class DataSession {

	private final Session session;
	private final CategorySet categories;
	private final ExamSet exams;
	private final GradeSet grades;
	private final InstrumentSet instruments;
	private final JurySet juries;
	private final ParticipantSet participants;
	private final Map<Class<?>, EntitySet<?>> entitySets;

	public DataSession(Session session) {
		this.session = session;
		this.categories = new CategorySet(this);
		this.exams = new ExamSet(this);
		this.grades = new GradeSet(this);
		this.instruments = new InstrumentSet(this);
		this.juries = new JurySet(this);
		this.participants = new ParticipantSet(this);
		this.entitySets = Map.ofEntries(
				Map.entry(Category.class, this.categories),
				Map.entry(Exam.class, this.exams),
				Map.entry(Grade.class, this.grades),
				Map.entry(Instrument.class, this.instruments),
				Map.entry(Jury.class, this.juries),
				Map.entry(Participant.class, this.participants)
		);
	}

	public <T> EntitySet<T> findEntitySet(Class<T> type) {
		@SuppressWarnings("unchecked")
		EntitySet<T> result = (EntitySet<T>) entitySets.get(type);
		return result;
	}

	public CategorySet categories() {
		return categories;
	}

	public ExamSet exams() {
		return exams;
	}

	public GradeSet grades() {
		return grades;
	}

	public InstrumentSet instruments() {
		return instruments;
	}

	public JurySet juries() {
		return juries;
	}

	public ParticipantSet participants() {
		return participants;
	}

	public <T> Query<T> getNamedQuery(String queryName) {
		@SuppressWarnings("unchecked")
		Query<T> result = (Query<T>) session.getNamedQuery(queryName);
		return result;
	}

	public <T> T find(Class<T> type, long id) {
		return session.find(type, id);
	}

	public void persist(Object object) {
		session.persist(object);
	}

	public void update(Object object) {
		session.update(object);
	}

	public void refresh(Object object) {
		session.refresh(object);
	}
}
