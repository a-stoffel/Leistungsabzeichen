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
import org.hibernate.query.Query;

/**
 *
 * @author astoffel
 */
public abstract class ExportDatabase {

	public static void exportDatabase(DataModel model, Path path) throws IOException {
		try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
			exportDatabase(model, writer);
		}
	}

	public static void exportDatabase(DataModel model, Writer writer) throws IOException {
		model.atomic(session -> {
			ExtData data = exportData(session);
			JAXB.marshal(data, writer);
		});
	}

	private static ExtData exportData(DataSession session) {
		Query<Meta> query = session.getNamedQuery("findMeta");
		Meta meta = query.uniqueResult();
		return new ExtData(meta,
				session.<Grade>getNamedQuery("findAllGrades").list(),
				session.<Instrument>getNamedQuery("findAllInstruments").list(),
				session.<Jury>getNamedQuery("findAllJuries").list(),
				session.<Category>getNamedQuery("findAllCategories").list(),
				session.<Exam>getNamedQuery("findAllExams").list(),
				exportParticipants(session)
		);
	}

	private static Map<Participant, List<Participation>> exportParticipants(DataSession session) {
		Map<Participant, List<Participation>> result = new HashMap<>();
		Query<Participant> participants = session.getNamedQuery("findAllParticipants");
		for (Participant p : participants.list()) {
			result.put(p, Participation.findAllOfParticipant(session, p));
		}
		return result;
	}
}
