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
import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Model;
import de.astoffel.laz.model.Participation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javax.inject.Inject;

/**
 *
 * @author astoffel
 */
public class ParticipationController {

	@Inject
	private ApplicationState application;

	private final ListProperty<Participation> participations
			= new SimpleListProperty<>(FXCollections.observableArrayList());
	private final ListProperty<Grade> grades
			= new SimpleListProperty<>(FXCollections.observableArrayList());

	private final ObjectProperty<Predicate<Participation>> predicate
			= new SimpleObjectProperty<>();

	private final List<ExamColumns> exams = new ArrayList<>();

	private FilteredList<Participation> filteredParticipations;

	private ChangeListener<Project> projectListener;

	@FXML
	private TableView<Participation> table;

	@FXML
	private TableColumn<Participation, String> participantColumn;
	@FXML
	private TableColumn<Participation, Category> categoryColumn;
	@FXML
	private TableColumn<Participation, Instrument> instrumentColumn;
	@FXML
	private TableColumn<Participation, Jury> juryColumn;

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

		filteredParticipations = new FilteredList<>(participations);
		filteredParticipations.predicateProperty().bind(predicate);

		participantColumn.setCellValueFactory(param -> {
			return param.getValue().getParticipant().nameProperty();
		});
		participantColumn.setCellFactory(param -> {
			return new TextFieldTableCell<>(new DefaultStringConverter());
		});
		categoryColumn.setCellValueFactory(param -> {
			return param.getValue().categoryProperty();
		});
		categoryColumn.setCellFactory(param -> {
			return new TextFieldTableCell<>(new CategoryConverter());
		});
		instrumentColumn.setCellValueFactory(param -> {
			return param.getValue().instrumentProperty();
		});
		instrumentColumn.setCellFactory(param -> {
			return new TextFieldTableCell<>(new StringConverter<Instrument>() {
				@Override
				public String toString(Instrument object) {
					return object.getName();
				}

				@Override
				public Instrument fromString(String string) {
					throw new UnsupportedOperationException();
				}
			});
		});
		juryColumn.setCellValueFactory(param -> {
			return param.getValue().juryProperty();
		});
		juryColumn.setCellFactory(param -> {
			return new TextFieldTableCell<>(new JuryConverter());
		});

		filteredParticipations.sort((a, b) -> {
			return a.getParticipant().compareTo(b.getParticipant());
		});

		SortedList<Participation> sortedParticipations = new SortedList<>(filteredParticipations);
		sortedParticipations.comparatorProperty()
				.bind(table.comparatorProperty());
		table.setItems(sortedParticipations);
	}

	private void reloadData() {
		Project project = application.projectProperty().get();
		for (ExamColumns e : exams) {
			table.getColumns().remove(e.gradeColumn);
		}
		exams.clear();
		if (project == null) {
			participations.clear();
			grades.clear();
			table.setDisable(true);
		} else {
			Model model = project.getModel();
			participations.setAll(model.participations().findAll());
			grades.setAll(loadGrades(model));
			exams.addAll(loadExams(model));
			for (ExamColumns e : exams) {
				table.getColumns().add(e.gradeColumn);
			}
			table.setDisable(false);
		}
	}

	private static List<Grade> loadGrades(Model model) {
		List<Grade> result = new ArrayList<>();
		result.add(null);
		result.addAll(model.grades().findAll());
		return result;
	}

	private List<ExamColumns> loadExams(Model model) {
		return model.exams().findAll().stream()
				.map(ExamColumns::new)
				.collect(Collectors.toList());
	}

	ObjectProperty<Predicate<Participation>> predicateProperty() {
		return predicate;
	}

	ObservableList<Participation> participationsProperty() {
		return filteredParticipations;
	}

	private final class ExamColumns {

		private final TableColumn<Participation, Grade> gradeColumn;

		public ExamColumns(Exam exam) {
			this.gradeColumn = new TableColumn<>(exam.getName() + " - Note");
			gradeColumn.setCellValueFactory(param -> {
				return param.getValue().assessmentOf(exam).gradeProperty();
			});
			gradeColumn.setCellFactory(param -> {
				ComboBoxTableCell<Participation, Grade> cell = new ComboBoxTableCell<>(new StringConverter<Grade>() {
					@Override
					public String toString(Grade object) {
						return object == null ? "" : object.getName();
					}

					@Override
					public Grade fromString(String string) {
						throw new UnsupportedOperationException();
					}
				}, grades);
				return cell;
			});
		}

	}
}
