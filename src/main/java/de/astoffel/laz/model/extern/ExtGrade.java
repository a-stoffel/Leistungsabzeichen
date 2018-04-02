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
package de.astoffel.laz.model.extern;

import de.astoffel.laz.model.Grade;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author astoffel
 */
public final class ExtGrade {

	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlValue
	private String displayName;

	private ExtGrade() {
	}

	public ExtGrade(Grade grade) {
		this.name = grade.getName();
		this.displayName = grade.getDisplayName();
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

}
