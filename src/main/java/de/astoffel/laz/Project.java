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
package de.astoffel.laz;

import de.astoffel.laz.model.Model;
import de.astoffel.laz.model.transfer.DefaultTransferModel;
import java.nio.file.Path;

/**
 *
 * @author astoffel
 */
public final class Project implements AutoCloseable {

	private final Path prefix;
	private final Model model;

	public Project(Path prefix) {
		this.prefix = prefix;
		this.model = new Model(new DefaultTransferModel(prefix));
	}

	public Path getPrefix() {
		return prefix;
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void close() {
		model.close();
	}

}
