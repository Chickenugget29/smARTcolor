package org.headroyce.smartcolor.smartcolor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class EditorGraphic extends BorderPane {
    private ImageView imageView;
    private Image img;
    private Button uploadBtn;
    private Logic logic;

    private Button grayscaleBtn;

    public EditorGraphic(){
        this.setCenter(imgLayout());
        logic = new Logic();
    }

    /**
     * Creates the layout of the screen
     * @return the layout
     */
    private VBox imgLayout(){
        imageView = new ImageView();
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.setPreserveRatio(true);
        uploadBtn = new Button("Choose Image");
        uploadBtn.setOnAction(new UploadHandler());
        grayscaleBtn = new Button("Grayscale");
        grayscaleBtn.setOnAction(new GrayscaleHandler());

        VBox rtn = new VBox();
        rtn.getChildren().addAll(uploadBtn, grayscaleBtn, imageView);
        return rtn;
    }

    /**
     * Handler for the upload button
     */
    private class UploadHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter, only shows jpg and png
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);

            //Shows selected image
            if (file != null) {
                img = new Image(file.toURI().toString());
                imageView.setImage(img);
            }
        }
    }

    /**
     * Handler for the grayscale button
     */
    private class GrayscaleHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            imageView.setImage( logic.toGrayScale(img) );
        }
    }
}