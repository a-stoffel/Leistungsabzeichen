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
package de.astoffel.laz;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author astoffel
 */
@ApplicationScoped
public class ApplicationState {

	public static enum Message {
		RELOAD
	}

	@FunctionalInterface
	public static interface Listener {

		public void on(ApplicationState application, Message message);
	}

	private final ObjectProperty<Project> project = new SimpleObjectProperty<>();
	private final BooleanProperty generating = new SimpleBooleanProperty(false);
	private final List<Listener> listeners = new CopyOnWriteArrayList<>();

	public ObjectProperty<Project> projectProperty() {
		return project;
	}

	public BooleanProperty generatingProperty() {
		return generating;
	}

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

	public void broadcast(Message message) {
		for (Listener l : listeners) {
			l.on(this, message);
		}
	}

	public void open(Path directory) {
		close();
		project.set(new Project(directory));
	}

	@PreDestroy
	public void close() {
		try (Project toClose = project.get()) {
			generating.set(false);
			this.project.set(null);
		}
	}

}
