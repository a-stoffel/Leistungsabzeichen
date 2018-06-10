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
import de.astoffel.laz.model.xml.XmlModel;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
		model.atomicThrows(session -> {
			clearDatabase(session);
			XmlModel.read(reader).populate(session);
		});
	}

	private static void clearDatabase(DataSession session) {
		Participation.deleteAll(session);
		Participant.deleteAll(session);
		Jury.deleteAll(session);
		Exam.deleteAll(session);
		Category.deleteAll(session);
		Instrument.deleteAll(session);
		Grade.deleteAll(session);
		Meta.deleteAll(session);
	}

}
