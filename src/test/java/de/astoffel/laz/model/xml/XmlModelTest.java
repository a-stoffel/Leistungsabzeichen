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

import de.astoffel.laz.model.TestDataModel;
import de.astoffel.laz.test.TestModelGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author astoffel
 */
public class XmlModelTest {

	private TestDataModel model;
	private TestModelGenerator generator;

	public XmlModelTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		model = new TestDataModel();
		model.atomic(session -> generator = new TestModelGenerator(session));
	}

	@After
	public void tearDown() {
		this.generator = null;
		if (model != null) {
			model.close();
			model = null;
		}
	}

	/**
	 * Test of create method, of class XmlModel.
	 */
	@Test
	public void testCreate() {
		System.out.println("create");
		model.atomic(session -> {
			  var result = XmlModel.create(session);
			assertNotNull(result);
		});
	}

	/**
	 * Test of read method, of class XmlModel.
	 */
	@Test
	public void testRead_InputStream() throws Exception {
		System.out.println("read");
		model.atomicThrows(session -> {
			  var xmlModel = XmlModel.create(session);
			  var stream = new ByteArrayOutputStream();
			try (stream) {
				xmlModel.write(stream);
			}
			  var xmlBytes = stream.toByteArray();
			try (  var in = new ByteArrayInputStream(xmlBytes)) {
				  var model = XmlModel.read(in);
				assertNotNull(model);
			}
		});
	}

	/**
	 * Test of read method, of class XmlModel.
	 */
	@Test
	public void testRead_Reader() throws Exception {
		System.out.println("read");
		model.atomicThrows(session -> {
			  var xmlModel = XmlModel.create(session);
			  var writer = new StringWriter();
			try (writer) {
				xmlModel.write(writer);
			}
			  var xmlString = writer.toString();
			try (  var reader = new StringReader(xmlString)) {
				  var model = XmlModel.read(reader);
				assertNotNull(model);
			}
		});
	}

	/**
	 * Test of write method, of class XmlModel.
	 */
	@Test
	public void testWrite_OutputStream() throws Exception {
		System.out.println("write");
		model.atomicThrows(session -> {
			  var xmlModel = XmlModel.create(session);
			  var stream = new ByteArrayOutputStream();
			try (stream) {
				xmlModel.write(stream);
			}
			  var xmlBytes = stream.toByteArray();
			assertTrue(xmlBytes.length > 0);
		});
	}

	/**
	 * Test of write method, of class XmlModel.
	 */
	@Test
	public void testWrite_Writer() throws Exception {
		System.out.println("write");
		model.atomicThrows(session -> {
			  var xmlModel = XmlModel.create(session);
			  var writer = new StringWriter();
			try (writer) {
				xmlModel.write(writer);
			}
			  var xmlString = writer.toString();
			assertFalse(xmlString.isEmpty());
		});
	}

	/**
	 * Test of importInto method, of class XmlModel.
	 */
	@Test
	public void testImportInto() {
		System.out.println("importInto");
		  var xmlModel = model.atomicComputeThrows(XmlModel::create);
		model.close();
		model = new TestDataModel();
		model.atomic(session -> {
			xmlModel.populate(session);
		});
	}

}
