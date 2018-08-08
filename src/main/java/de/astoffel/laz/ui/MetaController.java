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
import de.astoffel.laz.Project;
import de.astoffel.laz.model.Meta;
import de.astoffel.laz.model.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javax.inject.Inject;

/**
 *
 * @author astoffel
 */
public class MetaController {

	@Inject
	private ApplicationState application;

	private ChangeListener<Project> projectListener;

	private Meta meta;

	@FXML
	private TextField locationTextField;
	@FXML
	private TextField eventDateTextField;

	@FXML
	public void initialize() {
		application.addListener((source, message) -> {
			switch (message) {
				case RELOAD:
					reloadData();
					break;
			}
		});
		projectListener = (source, oldValue, newValue) -> {
			reloadData();
		};
		application.projectProperty()
				.addListener(new WeakChangeListener<>(projectListener));
		projectListener.changed(application.projectProperty(), null, application
				.projectProperty().get());
	}

	private void reloadData() {
		if (meta != null) {
			locationTextField.textProperty().unbindBidirectional(meta
					.locationProperty());
			eventDateTextField.textProperty().unbindBidirectional(meta
					.eventDateProperty());
			meta = null;
		}
		Project project = application.projectProperty().get();
		if (project == null) {
			locationTextField.setDisable(true);
			eventDateTextField.setDisable(true);
		} else {
			Model model = project.getModel();
			meta = model.metas().get();
			locationTextField.textProperty().bindBidirectional(meta
					.locationProperty());
			eventDateTextField.textProperty().bindBidirectional(meta
					.eventDateProperty());
			locationTextField.setDisable(false);
			eventDateTextField.setDisable(false);
		}
	}
}
