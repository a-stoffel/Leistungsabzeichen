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

import de.astoffel.laz.model.DataModel;
import de.astoffel.laz.model.Meta;
import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.Participation;
import de.astoffel.laz.model.extern.ExtData;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
public abstract class ExportDatabase {

	public static void exportDatabase(DataModel model, Path path) throws IOException {
		try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
			exportDatabase(model, writer);
		}
	}

	public static void exportDatabase(DataModel model, Writer writer) throws IOException {
		Session session = model.getSession();
		Transaction transaction = session.beginTransaction();
		try {
			ExtData data = exportData(session);
			JAXB.marshal(data, writer);
			transaction.commit();
		} catch (Throwable th) {
			transaction.rollback();
			throw th;
		}
	}

	private static ExtData exportData(Session session) {
		Query<Meta> query = session.getNamedQuery("findMeta");
		Meta meta = query.uniqueResult();
		return new ExtData(meta,
				session.getNamedQuery("findAllGrades").list(),
				session.getNamedQuery("findAllInstruments").list(),
				session.getNamedQuery("findAllJuries").list(),
				session.getNamedQuery("findAllCategories").list(),
				session.getNamedQuery("findAllExams").list(),
				exportParticipants(session)
		);
	}

	private static Map<Participant, List<Participation>> exportParticipants(Session session) {
		Map<Participant, List<Participation>> result = new HashMap<>();
		Query<Participant> participants = session.getNamedQuery("findAllParticipants");
		Query<Participation> participation = session.getNamedQuery("findParticipations");
		for (Participant p : participants.list()) {
			participation.setParameter("participant", p.getId());
			result.put(p, participation.list());
		}
		return result;
	}
}
