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
package de.astoffel.laz.utils;

import de.astoffel.laz.Project;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Meta;
import de.astoffel.laz.model.Model;
import de.astoffel.laz.model.Participation;
import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.query.Query;

/**
 *
 * @author astoffel
 */
public abstract class CertificateGenerator {

	private static final Logger LOG = Logger
			.getLogger(CertificateGenerator.class.getName());

	public static void generate(Project project, Path templateFile, List<Participation> participations, Model model) {
		try {
			Path texDirectory = project.getPrefix().resolve("latex");
			Path certiciateTemplateFile = texDirectory.resolve("template.vtl");
			Path latexFile = texDirectory.resolve("Urkunden.tex");
			Path pdfFile = texDirectory.resolve("Urkunden.pdf");
			cleanTexDirectory(texDirectory);
			Files.createDirectories(texDirectory);
			copyResources(templateFile, texDirectory);
			if (!Files.exists(certiciateTemplateFile)) {
				throw new IOException("Template file not found");
			}
			generateLatex(certiciateTemplateFile, latexFile,
					model.metas().get(), participations, model.exams().findAll());
			createPdf(latexFile);
			openResult(pdfFile);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Creating certificates failed", ex);
		}
	}

	private static void cleanTexDirectory(Path texDirectory) throws IOException {
		if (!Files.exists(texDirectory)) {
			return;
		}
		Files.walkFileTree(texDirectory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

		});
	}

	private static void copyResources(Path templateFile, Path texDirectory) throws IOException {
		try ( ZipInputStream stream = new ZipInputStream(Files
				.newInputStream(templateFile))) {
			ZipEntry entry;
			while ((entry = stream.getNextEntry()) != null) {
				Path target = texDirectory.resolve(entry.getName());
				Files.copy(stream, target);
			}
		}
	}

	private static void generateLatex(Path templateFile, Path latexFile,
			Meta meta, List<Participation> participations, List<Exam> exams) throws IOException {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(VelocityEngine.RESOURCE_LOADER, "template");
		engine
				.setProperty("template.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		engine.setProperty("template.resource.loader.path", templateFile
				.getParent().toString());
		engine.init();
		Template template = engine.getTemplate(templateFile.getFileName()
				.toString(), "UTF-8");
		VelocityContext context = new VelocityContext();
		context.put("meta", meta);
		context.put("participations", participations);
		context.put("exams", exams);
		try ( Writer writer = new OutputStreamWriter(Files
				.newOutputStream(latexFile), StandardCharsets.UTF_8)) {
			template.merge(context, writer);
		}
	}

	private static void createPdf(Path latexFile) throws IOException {
		Process process = new ProcessBuilder("latexmk", "-pdf", "-quiet", "-f", latexFile
				.getFileName().toString())
				.directory(latexFile.getParent().toFile())
				.inheritIO()
				.start();
		try {
			process.waitFor();
		} catch (InterruptedException ex) {
			throw new IOException("Latex interrupted");
		}
	}

	private static void openResult(Path pdfFile) throws IOException {
		if (!Desktop.isDesktopSupported()) {
			return;
		}
		Desktop.getDesktop().open(pdfFile.toFile());
	}
}
