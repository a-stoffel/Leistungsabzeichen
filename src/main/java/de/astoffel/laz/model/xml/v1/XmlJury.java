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
package de.astoffel.laz.model.xml.v1;

import de.astoffel.laz.model.DataSession;
import de.astoffel.laz.model.Jury;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author astoffel
 */
final class XmlJury {

	@XmlAttribute(name = "name", required = true)
	private String name;

	private XmlJury() {
	}

	public XmlJury(Jury jury) {
		this.name = jury.getName();
	}

	void create(DataSession session) {
		session.persist(new Jury(name));
	}

	String getName() {
		return name;
	}

}
