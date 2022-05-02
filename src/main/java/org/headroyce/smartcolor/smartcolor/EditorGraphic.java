package org.headroyce.smartcolor.smartcolor;

import javafx.application.Application;
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
import javafx.stage.Stage;

import java.io.File;

public class EditorGraphic extends BorderPane {
    private ImageView imageView;
    private Image img;
    private Button uploadBtn;
    private Logic logic;

    private Button grayscaleBtn;
    private Button colorBtn;
    private Button resetBtn;

    public EditorGraphic(Stage s){
        VBox imgLayout = imgLayout(s.getHeight(), s.getWidth());
        this.setCenter(imgLayout);
        this.setRight(resetLayout());
        logic = new Logic();
    }

    private VBox resetLayout(){

        resetBtn = new Button("Reset");
        resetBtn.setOnAction(new ResetHandler());
        VBox rtn = new VBox();
        rtn.getChildren().add(resetBtn);
        return rtn;
    }

    private VBox imgLayout(double h, double w){
        imageView = new ImageView();
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);

        uploadBtn = new Button("Choose Image");
        uploadBtn.setOnAction(new UploadHandler());
        grayscaleBtn = new Button("Grayscale");
        grayscaleBtn.setOnAction(new GrayscaleHandler());
        colorBtn = new Button("Randomize Color");
        colorBtn.setOnAction(new RandomizeColor());


        VBox rtn = new VBox();
        rtn.getChildren().addAll(uploadBtn, grayscaleBtn, colorBtn, imageView);
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

    private class RandomizeColor implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) { imageView.setImage( logic.toRandomColors(img));}
    }

    /**
     * Handler for the reset button
     */
    private class ResetHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e){ imageView.setImage( logic.resetImage(img));}
    }
}