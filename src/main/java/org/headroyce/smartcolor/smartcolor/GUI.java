package org.headroyce.smartcolor.smartcolor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GUI {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}