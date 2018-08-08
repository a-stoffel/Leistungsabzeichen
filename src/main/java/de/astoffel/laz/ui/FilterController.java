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
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Model;
import de.astoffel.laz.model.Participation;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import javax.inject.Inject;

/**
 *
 * @author astoffel
 */
public final class FilterController {

	private static final Predicate<Participation> PREDICATE_NONE = p -> true;

	@Inject
	private ApplicationState application;

	private ChangeListener<Project> projectListener;
	private ChangeListener<Boolean> selectionListener;

	private final ListProperty<SelectableEntity<Category>> categories = new SimpleListProperty<>(
			FXCollections.observableArrayList());
	private final ListProperty<SelectableEntity<Jury>> juries = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	private final ObjectProperty<Predicate<Participation>> predicate
			= new SimpleObjectProperty<>(PREDICATE_NONE);

	@FXML
	private ListView<SelectableEntity<Category>> categoryFilter;
	@FXML
	private ListView<SelectableEntity<Jury>> juryFilter;

	@FXML
	public void initialize() {
		selectionListener = (source, oldValue, newValue) -> {
			updatePredicate();
		};
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

		categoryFilter.setCellFactory(CheckBoxListCell
				.forListView(SelectableEntity::selectedProperty,
						new StringConverter<SelectableEntity<Category>>() {
					@Override
					public String toString(SelectableEntity<Category> object) {
						return object.getEntity().getName();
					}

					@Override
					public SelectableEntity<Category> fromString(String string) {
						throw new UnsupportedOperationException();
					}
				}));
		categoryFilter.itemsProperty().bind(categories);

		juryFilter.setCellFactory(CheckBoxListCell
				.forListView(SelectableEntity::selectedProperty,
						new StringConverter<SelectableEntity<Jury>>() {
					@Override
					public String toString(SelectableEntity<Jury> object) {
						return object.getEntity().getName();
					}

					@Override
					public SelectableEntity<Jury> fromString(String string) {
						throw new UnsupportedOperationException();
					}
				}));
		juryFilter.itemsProperty().bind(juries);
	}

	private void reloadData() {
		Project project = application.projectProperty().get();
		if (project == null) {
			for (SelectableEntity<Category> c : categories) {
				c.selectedProperty().removeListener(selectionListener);
			}
			categories.clear();
			for (SelectableEntity<Jury> j : juries) {
				j.selectedProperty().removeListener(selectionListener);
			}
			juries.clear();
			updatePredicate();
			categoryFilter.setDisable(true);
			juryFilter.setDisable(true);
		} else {
			categories.setAll(loadCategories(project.getModel()));
			for (SelectableEntity<Category> c : categories) {
				c.selectedProperty().addListener(selectionListener);
			}
			juries.setAll(loadJuries(project.getModel()));
			for (SelectableEntity<Jury> j : juries) {
				j.selectedProperty().addListener(selectionListener);
			}
			updatePredicate();
			categoryFilter.setDisable(false);
			juryFilter.setDisable(false);
		}
	}

	private static List<SelectableEntity<Category>> loadCategories(Model model) {
		return model.categories().findAll().stream()
				.map(c -> new SelectableEntity<>(c, true))
				.collect(Collectors.toList());
	}

	private static List<SelectableEntity<Jury>> loadJuries(Model model) {
		return model.juries().findAll().stream()
				.map(j -> new SelectableEntity<>(j, true))
				.collect(Collectors.toList());
	}

	private void updatePredicate() {
		if (categories.isEmpty() && juries.isEmpty()) {
			predicate.set(PREDICATE_NONE);
			return;
		}
		var selectedCategories = categories.stream()
				.filter(c -> c.selectedProperty().get())
				.map(c -> c.getEntity())
				.collect(Collectors.toSet());
		var selectedJuries = juries.stream()
				.filter(j -> j.selectedProperty().get())
				.map(j -> j.getEntity())
				.collect(Collectors.toSet());
		predicate.set(p -> {
			return selectedCategories.contains(p.getCategory())
					&& selectedJuries.contains(p.getJury());
		});
	}

	ObservableValue<Predicate<Participation>> predicateProperty() {
		return predicate;
	}
}
