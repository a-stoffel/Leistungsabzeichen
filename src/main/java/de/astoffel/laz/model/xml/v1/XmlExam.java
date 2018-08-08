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

import de.astoffel.laz.model.transfer.TransferCategory;
import de.astoffel.laz.model.transfer.TransferExam;
import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author astoffel
 */
final class XmlExam {

	@XmlAttribute(name = "sort", required = true)
	private int sort;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlElement(name = "display", required = true)
	private String displayName;
	@XmlElement(name = "short", required = true)
	private String displayShortName;
	@XmlElement(name = "description", required = true)
	private final List<XmlExamDescription> descriptions = new ArrayList<>();

	private XmlExam() {
	}

	public XmlExam(TransferExam exam) {
		this.sort = exam.getSort();
		this.name = exam.getName();
		this.displayName = exam.getDisplayName();
		this.displayShortName = exam.getDisplayShortName();
		for (var d : exam.getDescriptions().entrySet()) {
			this.descriptions.add(new XmlExamDescription(d.getKey().getName(), d
					.getValue()));
		}
	}

	void create(TransferSession session) throws TransferException {
		var description = new HashMap<TransferCategory, String>();
		for (var d : descriptions) {
			var category = session.categories().findByName(d.category).get();
			description.put(category, d.description);
		}
		session.exams()
				.persist(new TransferExam(sort, name, displayName, displayShortName, description));
	}

	String getName() {
		return name;
	}

	List<XmlExamDescription> getDescriptions() {
		return descriptions;
	}

	static final class XmlExamDescription {

		@XmlAttribute(name = "category", required = true)
		private String category;
		@XmlValue
		private String description;

		private XmlExamDescription() {
		}

		XmlExamDescription(String category, String description) {
			this.category = category;
			this.description = description;
		}

		String getCategory() {
			return category;
		}

	}
}
