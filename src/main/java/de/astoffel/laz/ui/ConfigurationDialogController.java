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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javax.inject.Inject;
import org.controlsfx.control.PropertySheet;

/**
 *
 * @author astoffel
 */
public class ConfigurationDialogController {

	private static final DataFormat DRAG_TYPE_INDEX = new DataFormat("laz.entity.type.index");
	private static final DataFormat DRAG_ENTITY_INDEX = new DataFormat("laz.entity.index");

	private ChangeListener<Project> projectListener;

	@Inject
	private ApplicationState application;

	@FXML
	private Parent view;

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
						LiveEntity<?> liveEntity = ((EntityNode<?, ?>) newValue).liveEntity;
						if (liveEntity != null) {
							properties.getItems().setAll(liveEntity.propertySheetItems());
						}
					}
				});
		navigationTree.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, (event) -> {
			if (!(navigationTree.getSelectionModel().getSelectedItem() instanceof TypeNode)) {
				event.consume();
			}
		});
	}

	@FXML
	public void close() {
		Stage stage = (Stage) view.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void navigationTreeOnKeyPress(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			TreeItem<String> item = navigationTree.getSelectionModel().getSelectedItem();
			if (item instanceof EntityNode) {
				((EntityNode) item).delete();
			}
		}
	}

	@FXML
	public void createEntity() {
		TreeItem<String> item = navigationTree.getSelectionModel().getSelectedItem();
		if (item instanceof TypeNode) {
			((TypeNode) item).createEntity();
		}
	}

	private void initRoot() {
		Project project = application.projectProperty().get();
		if (project == null) {
			navigationTree.setRoot(null);
		} else {
			navigationTree.setRoot(new RootNode(project.getModel()));
		}
	}

	private final class RootNode extends TreeItem<String> {

		RootNode(DataModel model) {
			super("Konfiguration");
			getChildren().addAll(Arrays.asList(
					new TypeNode<>(this, Instrument.class, "Instrumente", model,
							LiveInstrument::new),
					new TypeNode<>(this, Jury.class, "Juries", model,
							LiveJury::new),
					new TypeNode<>(this, Category.class, "Kategorien", model,
							LiveCategory::new),
					new TypeNode<>(this, Grade.class, "Noten", model,
							LiveGrade::new),
					new TypeNode<>(this, Exam.class, "Prüfungen", model,
							LiveExam::new),
					new TypeNode<>(this, Participant.class, "Teilnehmer", model,
							LiveParticipant::new)
			));
		}
	}

	private final class TypeNode<T extends Comparable<T>, L extends LiveEntity<T>> extends TreeItem<String> {

		private final RootNode parent;
		private final Class<T> type;
		private final DataModel model;
		private final BiFunction<DataModel, T, L> liveFactory;
		private final Optional<Function<L, ObjectProperty<Integer>>> sortPropertyAccessor;

		TypeNode(RootNode parent, Class<T> type, String title, DataModel model,
				BiFunction<DataModel, T, L> liveFactory) {
			this(parent, type, title, model, liveFactory, Optional.empty());
		}

		TypeNode(RootNode parent, Class<T> type, String title, DataModel model,
				BiFunction<DataModel, T, L> liveFactory,
				Optional<Function<L, ObjectProperty<Integer>>> sortPropertyAccessor) {
			super(title);
			this.parent = parent;
			this.type = type;
			this.model = model;
			this.liveFactory = liveFactory;
			this.sortPropertyAccessor = sortPropertyAccessor;
			initEntityNodes();
		}

		private void initEntityNodes() {
			List<T> entities = model.atomicCompute(session -> {
				List<T> result = new ArrayList<>();
				EntitySet<T> entitySet = session.findEntitySet(type);
				for (T entity : entitySet.findAll()) {
					result.add(entity);
				}
				return result;
			});
			entities.sort(T::compareTo);
			for (T entity : entities) {
				getChildren().add(new EntityNode<>(this, model, entity, liveFactory));
			}
		}

		public boolean isEntitySortable() {
			return sortPropertyAccessor.isPresent();
		}

		public void createEntity() {
			T entity = model.atomicCompute(session -> {
				T result = session.findEntitySet(type).create();
				session.persist(result);
				return result;
			});
			EntityNode<T, L> node = new EntityNode<>(this, model, entity, liveFactory);
			getChildren().add(node);
			navigationTree.getSelectionModel().select(node);
		}

		public void updateSort() {
			Function<L, ObjectProperty<Integer>> sorter = sortPropertyAccessor.get();
			int order = 0;
			for (TreeItem<String> child : getChildren()) {
				@SuppressWarnings("unchecked")
				EntityNode<T, L> node = (EntityNode<T, L>) child;
				sorter.apply(node.liveEntity).set(order);
				++order;
			}
		}
	}

	private final class EntityNode<T extends Comparable<T>, L extends LiveEntity<T>>
			extends TreeItem<String> {

		private final TypeNode<T, L> parent;
		private final DataModel model;
		private final T entity;
		private final L liveEntity;

		EntityNode(TypeNode<T, L> parent, DataModel model, T entity,
				BiFunction<DataModel, T, L> liveFactory) {
			super("");
			this.parent = parent;
			this.model = model;
			this.entity = entity;
			this.liveEntity = liveFactory.apply(model, entity);
			if (this.liveEntity != null) {
				this.valueProperty().bind(this.liveEntity.nameProperty());
			}
		}

		public void delete() {
			model.atomic((session) -> {
				session.delete(entity);
			});
			parent.getChildren().remove(this);
		}
	}
}
