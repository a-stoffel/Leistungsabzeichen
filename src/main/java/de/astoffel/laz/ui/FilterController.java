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
import de.astoffel.laz.model.Participation;
import java.util.List;
import java.util.Set;
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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author andreas
 */
public final class FilterController {

	private static final Predicate<ObservableParticipation> PREDICATE_NONE = p -> true;

	@Inject
	private ApplicationState application;

	private ChangeListener projectListener;
	private ChangeListener selectionListener;

	private final ListProperty<SelectableEntity<Category>> categories = new SimpleListProperty(
			FXCollections.observableArrayList());
	private final ListProperty<SelectableEntity<Jury>> juries = new SimpleListProperty(
			FXCollections.observableArrayList());

	private final ObjectProperty<Predicate<ObservableParticipation>> predicate
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
		application.projectProperty().addListener(new WeakChangeListener<>(projectListener));
		projectListener.changed(application.projectProperty(), null, application.projectProperty().get());

		categoryFilter.setCellFactory(CheckBoxListCell.forListView(SelectableEntity::selectedProperty,
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

		juryFilter.setCellFactory(CheckBoxListCell.forListView(SelectableEntity::selectedProperty,
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
			Session session = project.getModel().getSession();
			Transaction transaction = session.beginTransaction();
			try {
				categories.setAll(loadCategories(session));
				for (SelectableEntity<Category> c : categories) {
					c.selectedProperty().addListener(selectionListener);
				}
				juries.setAll(loadJuries(session));
				for (SelectableEntity<Jury> j : juries) {
					j.selectedProperty().addListener(selectionListener);
				}
				updatePredicate();
				categoryFilter.setDisable(false);
				juryFilter.setDisable(false);
				transaction.commit();
			} catch (Throwable th) {
				transaction.rollback();
				throw th;
			}
		}
	}

	private static List<SelectableEntity<Category>> loadCategories(Session session) {
		Query<Category> query = session.getNamedQuery("findAllCategories");
		return query.list().stream()
				.map(c -> new SelectableEntity<>(c, true))
				.collect(Collectors.toList());
	}

	private static List<SelectableEntity<Jury>> loadJuries(Session session) {
		Query<Jury> query = session.getNamedQuery("findAllJuries");
		return query.list().stream()
				.map(j -> new SelectableEntity<>(j, true))
				.collect(Collectors.toList());
	}

	private void updatePredicate() {
		if (categories.isEmpty() && juries.isEmpty()) {
			predicate.set(PREDICATE_NONE);
			return;
		}
		Set<Long> selectedCategories = categories.stream()
				.filter(c -> c.selectedProperty().get())
				.map(c -> c.getEntity().getId())
				.collect(Collectors.toSet());
		Set<Long> selectedJuries = juries.stream()
				.filter(j -> j.selectedProperty().get())
				.map(j -> j.getEntity().getId())
				.collect(Collectors.toSet());
		predicate.set(p -> {
			Participation participation = p.getParticipation();
			return selectedCategories.contains(participation.getCategory().getId())
					&& selectedJuries.contains(participation.getJury().getId());
		});
	}

	ObservableValue<Predicate<ObservableParticipation>> predicateProperty() {
		return predicate;
	}
}
