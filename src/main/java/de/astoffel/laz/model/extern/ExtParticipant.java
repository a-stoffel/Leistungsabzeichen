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

import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.Participation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author andreas
 */
public final class ExtParticipant {

	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlElement(name = "participation")
	private final List<ExtParticipation> participations = new ArrayList<>();

	private ExtParticipant() {
	}

	public ExtParticipant(Participant participant, List<Participation> participations) {
		this.name = participant.getName();
		for (Participation p : participations) {
			this.participations.add(new ExtParticipation(p));
		}
	}

	public String getName() {
		return name;
	}

	public List<ExtParticipation> getParticipations() {
		return Collections.unmodifiableList(participations);
	}

}
