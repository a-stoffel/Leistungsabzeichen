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

import de.astoffel.laz.ui.MainController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 *
 * @author andreas
 */
public class Main extends Application {

	private Weld weld;
	private WeldContainer container;
	private ApplicationState application;

	@Override
	public void init() throws Exception {
		weld = new Weld();
		container = weld.initialize();
	}

	@Override
	public void stop() throws Exception {
		weld.shutdown();
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = container.select(FXMLLoader.class).get();
		loader.setLocation(MainController.class.getResource("Main.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setTitle("Leistungsabzeichen");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
