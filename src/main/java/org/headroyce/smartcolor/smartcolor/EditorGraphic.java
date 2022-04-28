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
    private ImageView img;
    private Button uploadBtn;

    public EditorGraphic(){
        this.setCenter(imgLayout());
    }

    private VBox imgLayout(){
        img = new ImageView();
        uploadBtn = new Button("Choose Image");
        uploadBtn.setOnAction(new UploadHandler());

        VBox rtn = new VBox();
        rtn.getChildren().addAll(uploadBtn, img);
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
                img.setImage( new Image(file.toURI().toString()) );
            }
        }
    }
}