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

import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.DataSession;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Meta;
import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.Participation;
import de.astoffel.laz.model.xml.XmlModel;
import de.astoffel.laz.model.xml.XmlModelException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author astoffel
 */
@XmlRootElement(name = "leistungsabzeichen")
public final class XmlModelV1 implements XmlModel {

	public static final String SCHEMA_LOCATION = "/de/astoffel/laz/model/xml/v1/schema.xsd";

	private static Schema loadSchema() throws SAXException {
		return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
				.newSchema(new StreamSource(XmlModelV1.class.getResourceAsStream(SCHEMA_LOCATION)));
	}

	@XmlAttribute(name = "location", required = true)
	private String location;

	@XmlAttribute(name = "when", required = true)
	private String when;

	@XmlElementWrapper(name = "grades", required = true, nillable = false)
	@XmlElement(name = "grade")
	private final List<XmlGrade> grades = new ArrayList<>();

	@XmlElementWrapper(name = "instruments", required = true, nillable = false)
	@XmlElement(name = "instrument")
	private final List<XmlInstrument> instruments = new ArrayList<>();

	@XmlElementWrapper(name = "juries", required = true, nillable = false)
	@XmlElement(name = "jury")
	private final List<XmlJury> juries = new ArrayList<>();

	@XmlElementWrapper(name = "categories", required = true, nillable = false)
	@XmlElement(name = "category")
	private final List<XmlCategory> categories = new ArrayList<>();

	@XmlElementWrapper(name = "exams", required = true, nillable = false)
	@XmlElement(name = "exam")
	private final List<XmlExam> exams = new ArrayList<>();

	@XmlElementWrapper(name = "participants", required = true, nillable = false)
	@XmlElement(name = "participant")
	private final List<XmlParticipant> participants = new ArrayList<>();

	private XmlModelV1() {
	}

	public XmlModelV1(DataSession session) {
		  var meta = Meta.find(session);
		this.location = meta.getLocation();
		this.when = meta.getEventDate();
		for (  var g : Grade.findAll(session)) {
			this.grades.add(new XmlGrade(g));
		}
		for (  var i : Instrument.findAll(session)) {
			this.instruments.add(new XmlInstrument(i));
		}
		for (  var j : Jury.findAll(session)) {
			this.juries.add(new XmlJury(j));
		}
		for (  var c : Category.findAll(session)) {
			this.categories.add(new XmlCategory(c));
		}
		for (  var e : Exam.findAll(session)) {
			this.exams.add(new XmlExam(e));
		}
		for (  var p : Participant.findAll(session)) {
			this.participants.add(new XmlParticipant(p,
					Participation.findAllOfParticipant(session, p).stream()
							.map(XmlParticipation::new)
							.collect(Collectors.toList())));
		}
		verifyIntegrity();
	}

	private void verifyIntegrity() {
		  var gradeNames = grades.stream()
				.map(XmlGrade::getName)
				.collect(Collectors.toSet());
		  var instrumentNames = instruments.stream()
				.map(XmlInstrument::getName)
				.collect(Collectors.toSet());
		  var juryNames = juries.stream()
				.map(XmlJury::getName)
				.collect(Collectors.toSet());
		  var categoryNames = categories.stream()
				.map(XmlCategory::getName)
				.collect(Collectors.toSet());
		  var examNames = exams.stream()
				.map(XmlExam::getName)
				.collect(Collectors.toSet());
		for (  var e : this.exams) {
			for (  var d : e.getDescriptions()) {
				if (!categoryNames.contains(d.getCategory())) {
					throw new XmlModelException("Unknown category " + d.getCategory());
				}
			}
		}
		for (  var p : this.participants) {
			for (  var it : p.getParticipations()) {
				if (!instrumentNames.contains(it.getInstrument())) {
					throw new XmlModelException("Unknown instrument " + it.getInstrument());
				}
				if (!juryNames.contains(it.getJury())) {
					throw new XmlModelException("Unknown jury " + it.getJury());
				}
				if (!categoryNames.contains(it.getCategory())) {
					throw new XmlModelException("Unknown category " + it.getCategory());
				}
				for (  var a : it.getAssessments()) {
					if (a.getGrade() != null && !gradeNames.contains(a.getGrade())) {
						throw new XmlModelException("Unknown grade " + a.getGrade());
					}
					if (!examNames.contains(a.getExam())) {
						throw new XmlModelException("Unknown exam " + a.getExam());
					}
				}
			}
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		try {
			  var context =JAXBContext.newInstance(XmlModelV1.class);
			  var marshaller = context.createMarshaller();
			marshaller.setSchema(loadSchema());
			marshaller.marshal(this, writer);
		} catch (JAXBException | SAXException ex) {
			throw new IOException("Writing model failed", ex);
		}
	}

	@Override
	public void populate(DataSession session) {
		verifyIntegrity();
		clearDatabase(session);
		  var meta = Meta.find(session);
		meta.setLocation(location);
		meta.setEventDate(when);
		session.persist(meta);
		for (  var g : grades) {
			g.create(session);
		}
		for (  var i : instruments) {
			i.create(session);
		}
		for (  var c : categories) {
			c.create(session);
		}
		for (  var e : exams) {
			e.create(session);
		}
		for (  var j : juries) {
			j.create(session);
		}
		for (  var p : participants) {
			p.create(session);
		}
	}

	private void clearDatabase(DataSession session) {
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
