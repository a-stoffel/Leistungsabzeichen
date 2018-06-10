/*
 * Copyright (C) 2018 astoffel
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
package de.astoffel.laz.model.xml;

import de.astoffel.laz.model.DataSession;
import de.astoffel.laz.model.xml.v1.XmlModelV1;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author astoffel
 */
public interface XmlModel {

	public static XmlModel create(DataSession session) {
		return new XmlModelV1(session);
	}

	public static XmlModel read(InputStream in) throws IOException {
		return read(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	public static XmlModel read(Reader reader) throws IOException {
		try {
			  var context = JAXBContext.newInstance(XmlModelV1.class);
			  var unmarshaller = context.createUnmarshaller();
			try (InputStream v1SchemaStream = XmlModel.class.getResourceAsStream(XmlModelV1.SCHEMA_LOCATION)) {
				  var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				  var schema = schemaFactory.newSchema(new StreamSource(v1SchemaStream));
				unmarshaller.setSchema(schema);
			}
			return (XmlModel) unmarshaller.unmarshal(reader);
		} catch (JAXBException | SAXException ex) {
			throw new IOException("Reading model failed", ex);
		}
	}

	public default void write(OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
		write(writer);
		writer.flush();
	}

	public void write(Writer writer) throws IOException;

	public void populate(DataSession session);
}
