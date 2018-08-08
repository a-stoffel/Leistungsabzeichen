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

import de.astoffel.laz.model.Model;
import de.astoffel.laz.model.transfer.TransferException;
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

	public static void importDatabase(Model model, Path path) throws IOException, TransferException {
		try ( Reader reader = new InputStreamReader(
				Files.newInputStream(path), StandardCharsets.UTF_8)) {
			importDatabase(model, reader);
			model.clear();
		}
	}

	public static void importDatabase(Model model, Reader reader)
			throws IOException, TransferException {
		var importedModel = XmlModel.read(reader);
		model.transferModel().execute(session -> {
			importedModel.populate(session);
		});
	}

}
