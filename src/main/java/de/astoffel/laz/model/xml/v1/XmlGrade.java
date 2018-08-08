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

import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferGrade;
import de.astoffel.laz.model.transfer.TransferSession;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author astoffel
 */
final class XmlGrade {

	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlValue
	private String displayName;

	private XmlGrade() {
	}

	public XmlGrade(TransferGrade grade) {
		this.name = grade.getName();
		this.displayName = grade.getDisplayName();
	}

	void create(TransferSession session) throws TransferException {
		session.grades().persist(new TransferGrade(name, displayName));
	}

	String getName() {
		return name;
	}

}
