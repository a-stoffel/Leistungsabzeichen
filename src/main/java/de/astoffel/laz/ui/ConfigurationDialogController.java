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
package de.astoffel.laz.ui;

import de.astoffel.laz.ApplicationState;
import de.astoffel.laz.Project;
import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.DataModel;
import de.astoffel.laz.model.EntitySet;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Participant;
import java.util.Arrays;
import java.util.function.Function;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javax.inject.Inject;
import org.controlsfx.control.PropertySheet;

/**
 *
 * @author astoffel
 */
public class ConfigurationDialogController {

	private ChangeListener<Project> projectListener;

	@Inject
	private ApplicationState application;

	@FXML
	private TreeView<String> navigationTree;
	@FXML
	private PropertySheet properties;

	@FXML
	public void initialize() {
		projectListener = (source, oldValue, newValue) -> {
			initRoot();
		};
		application.projectProperty().addListener(new WeakChangeListener<>(projectListener));
		initRoot();
		navigationTree.getSelectionModel().selectedItemProperty().addListener(
				(source, oldValue, newValue) -> {
					if (newValue instanceof EntityNode) {
						LiveEntity liveEntity = ((EntityNode<Object>) newValue).liveEntity;
						if (liveEntity != null) {
							properties.getItems().setAll(liveEntity.propertySheetItems());
						}
					}
				});
	}

	private void initRoot() {
		Project project = application.projectProperty().get();
		if (project == null) {
			navigationTree.setRoot(null);
		} else {
			navigationTree.setRoot(new RootNode(project.getModel()));
		}
	}

	private static final class RootNode extends TreeItem<String> {

		RootNode(DataModel model) {
			super("Konfiguration");
			getChildren().addAll(Arrays.asList(
					new TypeNode<>(Instrument.class, "Instrumente", model,
							instrument -> new LiveInstrument(model, instrument)),
					new TypeNode<>(Jury.class, "Juries", model,
							jury -> new LiveJury(model, jury)),
					new TypeNode<>(Category.class, "Kategorien", model,
							category -> new LiveCategory(model, category)),
					new TypeNode<>(Grade.class, "Noten", model,
							grade -> new LiveGrade(model, grade)),
					new TypeNode<>(Exam.class, "Prüfungen", model,
							exam -> new LiveExam(model, exam)),
					new TypeNode<>(Participant.class, "Teilnehmer", model,
							participant -> new LiveParticipant(model, participant))
			));
		}
	}

	private static final class TypeNode<T> extends TreeItem<String> {

		private final Class<T> type;

		TypeNode(Class<T> type, String title, DataModel model,
				Function<T, LiveEntity> liveEntityFactory) {
			super(title);
			this.type = type;
			initEntityNodes(model, liveEntityFactory);
		}

		private void initEntityNodes(
				DataModel model, Function<T, LiveEntity> liveEntityFactory) {
			model.atomic(session -> {
				EntitySet<T> entities = session.findEntitySet(type);
				for (T entity : entities.findAll()) {
					getChildren().add(new EntityNode<>(
							entity, liveEntityFactory.apply(entity)));
				}
			});
		}

	}

	private static final class EntityNode<T> extends TreeItem<String> {

		private final T entity;
		private final LiveEntity liveEntity;

		EntityNode(T entity, LiveEntity liveEntity) {
			super("");
			this.entity = entity;
			this.liveEntity = liveEntity;
			if (this.liveEntity != null) {
				this.valueProperty().bind(this.liveEntity.nameProperty());
			}
		}
	}
}
