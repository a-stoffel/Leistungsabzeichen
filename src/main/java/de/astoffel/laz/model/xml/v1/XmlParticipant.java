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
import de.astoffel.laz.model.transfer.TransferParticipant;
import de.astoffel.laz.model.transfer.TransferSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author astoffel
 */
final class XmlParticipant {

	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlElement(name = "participation")
	private final List<XmlParticipation> participations = new ArrayList<>();

	private XmlParticipant() {
	}

	public XmlParticipant(TransferParticipant participant, List<XmlParticipation> participations) {
		this.name = participant.getName();
		this.participations.addAll(participations);
	}

	void create(TransferSession session) throws TransferException {
		var participant = new TransferParticipant(name);
		session.participants().persist(participant);
		for (var p : participations) {
			p.create(session, participant);
		}
	}

	List<XmlParticipation> getParticipations() {
		return Collections.unmodifiableList(participations);
	}

}
