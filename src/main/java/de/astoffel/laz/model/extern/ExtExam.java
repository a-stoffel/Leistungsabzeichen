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

import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.Exam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author astoffel
 */
public final class ExtExam {

	@XmlAttribute(name = "sort", required = true)
	private int sort;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlElement(name = "display", required = true)
	private String displayName;
	@XmlElement(name = "short", required = true)
	private String displayShortName;
	@XmlElement(name = "description", required = true)
	private final List<ExtDescription> descriptions = new ArrayList<>();

	private ExtExam() {
	}

	public ExtExam(Exam exam) {
		this.sort = exam.getSort();
		this.name = exam.getName();
		this.displayName = exam.getDisplayName();
		this.displayShortName = exam.getDisplayShortName();
		for (Map.Entry<Category, String> d : exam.getDescriptions().entrySet()) {
			this.descriptions.add(new ExtDescription(d.getKey().getName(), d.getValue()));
		}
	}

	public int getSort() {
		return sort;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public String getDisplayShortName() {
		return displayShortName;
	}

	public List<ExtDescription> getDescriptions() {
		return Collections.unmodifiableList(descriptions);
	}

	public static final class ExtDescription {

		@XmlAttribute(name = "category", required = true)
		private String category;
		@XmlValue
		private String description;

		private ExtDescription() {
		}

		public ExtDescription(String category, String description) {
			this.category = category;
			this.description = description;
		}

		public String getCategory() {
			return category;
		}

		public String getDescription() {
			return description;
		}

	}
}
