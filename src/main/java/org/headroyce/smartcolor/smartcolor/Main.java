package org.headroyce.smartcolor.smartcolor;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {


        EditorGraphic eg = new EditorGraphic();
        Scene scene = new Scene(eg, 400, 400);
        stage.setTitle("smARTcolor");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                eg.handleKeyPress(keyEvent);
            }
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                eg.handleKeyRelease(keyEvent);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}