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
import de.astoffel.laz.model.AbstractEntity;
import de.astoffel.laz.model.Category;
import de.astoffel.laz.model.Exam;
import de.astoffel.laz.model.Grade;
import de.astoffel.laz.model.Instrument;
import de.astoffel.laz.model.Jury;
import de.astoffel.laz.model.Model;
import de.astoffel.laz.model.NamedEntity;
import de.astoffel.laz.model.Participant;
import de.astoffel.laz.model.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private static final Logger LOG = Logger.getLogger(ConfigurationDialogController.class.getName());

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
        application.projectProperty()
                .addListener(new WeakChangeListener<>(projectListener));
        initRoot();
        navigationTree.getSelectionModel().selectedItemProperty().addListener(
                (source, oldValue, newValue) -> {
                    if (newValue instanceof EntityNode) {
                        properties.getItems().setAll(((EntityNode<?>) newValue)
                                .propertySheetItems());
                    }
                });
        navigationTree
                .addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, (event) -> {
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
            TreeItem<String> item = navigationTree.getSelectionModel()
                    .getSelectedItem();
            if (item instanceof EntityNode) {
                ((EntityNode) item).delete();
            }
        }
    }

    @FXML
    public void createEntity() {
        TreeItem<String> item = navigationTree.getSelectionModel()
                .getSelectedItem();
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

        RootNode(Model model) {
            super("Konfiguration");
            getChildren().addAll(Arrays.asList(
                    new TypeNode<>(this, Instrument.class, "Instrumente", model),
                    new TypeNode<>(this, Jury.class, "Juries", model),
                    new TypeNode<>(this, Category.class, "Kategorien", model),
                    new TypeNode<>(this, Grade.class, "Noten", model),
                    new TypeNode<>(this, Exam.class, "Prüfungen", model),
                    new TypeNode<>(this, Participant.class, "Teilnehmer", model)
            ));
        }
    }

    private final class TypeNode<E extends AbstractEntity<?> & NamedEntity & Comparable<E>>
            extends TreeItem<String> {

        private final RootNode parent;
        private final Class<E> type;
        private final Model model;
        private final Optional<Function<E, Property<Integer>>> sortPropertyAccessor;

        TypeNode(RootNode parent, Class<E> type, String title, Model model) {
            this(parent, type, title, model, Optional.empty());
        }

        TypeNode(RootNode parent, Class<E> type, String title, Model model,
                Optional<Function<E, Property<Integer>>> sortPropertyAccessor) {
            super(title);
            this.parent = parent;
            this.type = type;
            this.model = model;
            this.sortPropertyAccessor = sortPropertyAccessor;
            initEntityNodes();
        }

        private void initEntityNodes() {
            var entities = model.getEntitySet(type).findAll();
            entities.sort(E::compareTo);
            for (E entity : entities) {
                getChildren().add(new EntityNode<>(this, model, entity));
            }
        }

        public boolean isEntitySortable() {
            return sortPropertyAccessor.isPresent();
        }

        public void createEntity() {
            var entity = model.getEntitySet(type).create();
            var node = new EntityNode<>(this, model, entity);
            getChildren().add(node);
            navigationTree.getSelectionModel().select(node);
        }

        public void updateSort() {
            Function<E, Property<Integer>> sorter = sortPropertyAccessor
                    .get();
            int order = 0;
            for (TreeItem<String> child : getChildren()) {
                @SuppressWarnings("unchecked")
                EntityNode<E> node = (EntityNode<E>) child;
                sorter.apply(node.entity).setValue(order);
                ++order;
            }
        }
    }

    private final class EntityNode<E extends AbstractEntity<?> & NamedEntity & Comparable<E>>
            extends TreeItem<String> {

        private final TypeNode<E> parent;
        private final Model model;
        private final E entity;
        private final List<PropertyItem<?>> propertyItems;

        EntityNode(TypeNode<E> parent, Model model, E entity) {
            super("");
            this.parent = parent;
            this.model = model;
            this.entity = entity;
            this.valueProperty().bind(this.entity.nameProperty());
            this.propertyItems = Collections.unmodifiableList(PropertyItem.createItems(entity));
        }

        public List<PropertyItem<?>> propertySheetItems() {
            return propertyItems;
        }

        public void delete() {
            model.getEntitySet(parent.type).delete(entity);
            parent.getChildren().remove(this);
        }
    }

    private static final class PropertyItem<T> implements PropertySheet.Item {

        public static List<PropertyItem<?>> createItems(AbstractEntity<?> entity) {
            var items = new ArrayList<PropertyItem<?>>();
            for (var m : entity.getClass().getMethods()) {
                if (Modifier.isStatic(m.getModifiers())
                        || !Modifier.isPublic(m.getModifiers())
                        || m.getParameterCount() != 0
                        || !Property.class.isAssignableFrom(m.getReturnType())) {
                    continue;
                }
                var descriptor = m.getAnnotation(PropertyDescriptor.class);
                if (descriptor == null) {
                    continue;
                }
                try {
                    var property = (Property<?>) m.invoke(entity);
                    items.add(new PropertyItem<>(descriptor, property));
                } catch (ReflectiveOperationException ex) {
                    LOG.log(Level.WARNING, "Creating property item failed", ex);
                }
            }
            return items;
        }

        private final PropertyDescriptor descriptor;
        private final Property<T> property;

        private PropertyItem(PropertyDescriptor descriptor, Property<T> property) {
            this.descriptor = descriptor;
            this.property = property;
        }

        @Override
        public Class<?> getType() {
            return descriptor.type();
        }

        @Override
        public String getCategory() {
            return descriptor.category();
        }

        @Override
        public String getName() {
            return descriptor.name();
        }

        @Override
        public String getDescription() {
            return descriptor.description();
        }

        @Override
        public T getValue() {
            return property.getValue();
        }

        @Override
        public void setValue(Object value) {
            @SuppressWarnings("unchecked")
            var tValue = (T) value;
            property.setValue(tValue);
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.of(property);
        }

        @Override
        public boolean isEditable() {
            return descriptor.editable();
        }

    }
}
