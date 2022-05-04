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
import javax.imageio.ImageIO;


public class EditorGraphic extends BorderPane {
    private ImageView imageView;
    private Image img;
    private Image originalImg;
    private Button uploadBtn;
    private Button resetBtn;
    private Button saveBtn;
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
        imageView.fitHeightProperty().bind(this.heightProperty());
        imageView.setPreserveRatio(true);
        uploadBtn = new Button("Choose Image");
        uploadBtn.setOnAction(new UploadHandler());
        saveBtn = new Button("Save Image");
        saveBtn.setOnAction(new SaveHandler());
        resetBtn = new Button("Reset Image");
        resetBtn.setOnAction(new ResetHandler());
        grayscaleBtn = new Button("Grayscale");
        grayscaleBtn.setOnAction(new GrayscaleHandler());

        VBox rtn = new VBox();
        rtn.getChildren().addAll(uploadBtn, saveBtn, resetBtn, grayscaleBtn, imageView);
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
                originalImg = img;
                imageView.setImage(img);
            }
        }
    }

    /**
     * Handler for the grayscale button
     */
    private class GrayscaleHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            //can be removed if we don't want img to change
            img = logic.toGrayScale(img);
            imageView.setImage( img );
        }
    }

    /**
     * Handler for the reset button
     */
    private class ResetHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            img = originalImg;
            imageView.setImage( img );
        }
    }

    /**
     * Handler for the save image button
     */
    private class SaveHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            // how to download ImageIO???

        }
    }
}