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
package de.astoffel.laz.ui;

import de.astoffel.laz.ApplicationState;
import de.astoffel.laz.FXMLLoaderProducer;
import de.astoffel.laz.Main;
import de.astoffel.laz.Project;
import de.astoffel.laz.model.Participation;
import de.astoffel.laz.utils.CertificateGenerator;
import de.astoffel.laz.utils.ExportDatabase;
import de.astoffel.laz.utils.ImportDatabase;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.inject.Inject;
import javax.swing.filechooser.FileSystemView;
import org.controlsfx.control.TaskProgressView;

/**
 *
 * @author astoffel
 */
public class MainController {

	private static final Logger LOG = Logger.getLogger(MainController.class.getName());

	private static final String PREF_LAST_DIR = "last.directory";

	@Inject
	private ApplicationState application;
	@Inject
	private FXMLLoaderProducer fxmlLoaderProducer;

	private Preferences preferences;

	@FXML
	private Parent view;

	@FXML
	private MenuItem openConfigurationMenuItem;
	@FXML
	private MenuItem importMenuItem;
	@FXML
	private MenuItem exportMenuItem;
	@FXML
	private MenuItem createCertificateMenuItem;

	@FXML
	private FilterController filterController;
	@FXML
	private ParticipationController participationController;

	@FXML
	public void initialize() {
		preferences = Preferences.userNodeForPackage(Main.class).node("settings");

		openConfigurationMenuItem.disableProperty().bind(application.projectProperty().isNull());
		exportMenuItem.disableProperty().bind(application.projectProperty().isNull());
		importMenuItem.disableProperty().bind(application.projectProperty().isNull());
		createCertificateMenuItem.disableProperty().bind(application.projectProperty().isNull());

		participationController.predicateProperty().bind(filterController.predicateProperty());
	}

	@FXML
	public void open() {
		DirectoryChooser chooser = new DirectoryChooser();
		File initialDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();
		String lastDirectory = preferences.get(PREF_LAST_DIR, "");
		if (!lastDirectory.isEmpty()) {
			initialDirectory = new File(lastDirectory);
		}
		chooser.setInitialDirectory(initialDirectory);
		chooser.setTitle("Verzeichnis auswählen");
		File selection = chooser.showDialog(view.getScene().getWindow());
		if (selection == null) {
			return;
		}
		preferences.put(PREF_LAST_DIR, selection.toString());
		application.open(selection.toPath());
	}

	@FXML
	public void openConfiguration() {
		try {
			FXMLLoader fxmlLoader = fxmlLoaderProducer.produceFXMLLoader();
			fxmlLoader.setLocation(ConfigurationDialogController.class.getResource("ConfigurationDialog.fxml"));
			Parent root = fxmlLoader.load();
			Stage dialog = new Stage();
			dialog.setScene(new Scene(root));
			dialog.initOwner(view.getScene().getWindow());
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.setTitle("Konfiguration");
			dialog.showAndWait();
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "opening configuration dialog failed", ex);
		}
	}

	@FXML
	public void importData() {
		Project project = application.projectProperty().get();
		if (project == null) {
			return;
		}
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(project.getPrefix().toFile());
		chooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Leistungsabzeichen Datei", "*.xml"),
				new FileChooser.ExtensionFilter("Alle Datei", "*.*")
		);
		File selection = chooser.showOpenDialog(view.getScene().getWindow());
		if (selection == null) {
			return;
		}
		try {
			ImportDatabase.importDatabase(project.getModel(), selection.toPath());
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "importing data failed", ex);
		}
		application.broadcast(ApplicationState.Message.RELOAD);
	}

	@FXML
	public void exportData() {
		Project project = application.projectProperty().get();
		if (project == null) {
			return;
		}
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(project.getPrefix().toFile());
		chooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Leistungsabzeichen Datei", "*.xml"),
				new FileChooser.ExtensionFilter("Alle Datei", "*.*")
		);
		File selection = chooser.showSaveDialog(view.getScene().getWindow());
		if (selection == null) {
			return;
		}
		try {
			ExportDatabase.exportDatabase(project.getModel(), selection.toPath());
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "exporting data failed", ex);
		}
		application.broadcast(ApplicationState.Message.RELOAD);
	}

	@FXML
	public void createCertificates() {
		if (application.generatingProperty().get()) {
			return;
		}
		Project project = application.projectProperty().get();
		if (project == null) {
			return;
		}
		Path templatePath = getCertificateTemplate(project);
		if (templatePath == null) {
			return;
		}
		List<Participation> participations = listCertificateParticipations();
		Task<Void> generatorTask = new Task<Void>() {
			{
				updateTitle("Urkunden erstellen...");
			}

			@Override
			protected Void call() throws Exception {
				CertificateGenerator.generate(project, templatePath, participations, project.getModel());
				return null;
			}
		};
		generateCertificates(generatorTask);
	}

	private List<Participation> listCertificateParticipations() {
		List<Participation> participations = participationController.participationsProperty().stream()
				.filter(p -> p.hasParticipated())
				.map(LiveParticipation::getParticipation)
				.sorted((a, b) -> {
					int comp = a.getJury().getName().compareTo(b.getJury().getName());
					if (comp != 0) {
						return 0;
					}
					comp = a.getCategory().getName().compareTo(b.getCategory().getName());
					if (comp != 0) {
						return 0;
					}
					return a.getParticipant().getName().compareTo(b.getParticipant().getName());
				})
				.collect(Collectors.toList());
		return participations;
	}

	private Path getCertificateTemplate(Project project) {
		Path templatePath = project.getPrefix().resolve("Template.zip");
		if (!Files.exists(templatePath)) {
			FileChooser chooser = new FileChooser();
			chooser.setInitialDirectory(project.getPrefix().toFile());
			chooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("Vorlagen Datei", "*.zip"),
					new FileChooser.ExtensionFilter("Alle Datei", "*.*")
			);
			File selection = chooser.showOpenDialog(view.getScene().getWindow());
			if (selection == null) {
				return null;
			}
			try {
				Files.copy(selection.toPath(), templatePath);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, "cannot copy template", ex);
				return null;
			}
		}
		return templatePath;
	}

	private void generateCertificates(Task<Void> generatorTask) {
		TaskProgressView<Task<Void>> progressView = new TaskProgressView<>();
		progressView.getTasks().add(generatorTask);
		Stage dialog = new Stage();
		generatorTask.stateProperty().addListener((source, oldValue, newValue) -> {
			switch (newValue) {
				case CANCELLED:
				case FAILED:
				case SUCCEEDED:
					dialog.close();
					break;
			}
		});
		dialog.setScene(new Scene(progressView));
		dialog.sizeToScene();
		dialog.initOwner(view.getScene().getWindow());
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.initModality(Modality.APPLICATION_MODAL);
		Thread thread = new Thread(generatorTask);
		thread.setDaemon(true);
		thread.start();
		switch (generatorTask.getState()) {
			case READY:
			case RUNNING:
			case SCHEDULED:
				dialog.showAndWait();
				break;
			default:
				// task already finished
				break;
		}
	}

	@FXML
	public void exit() {
		Platform.exit();
	}
}
