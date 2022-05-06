package org.headroyce.smartcolor.smartcolor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import java.io.File;
import javax.imageio.ImageIO;

public class EditorGraphic extends BorderPane {
    private ImageView imageView;
    private Image img;
    private Image originalImg;
    private Logic logic;

    private Robot robot;

    private Button grayscaleBtn;
    private ColorPicker colorPicker;

    public EditorGraphic(){
        this.setCenter(imgLayout());
        logic = new Logic();
    }

    /**
     * Creates the layout of the screen
     * @return the layout
     */
    private VBox imgLayout(){

        colorPicker = new ColorPicker();
        //colorPicker.setOnAction(new ColorHandler());
        colorPicker.getStyleClass().add("split-button");

        Circle circle = new Circle(10, 10, 10);
        circle.setStroke(Color.BLACK);
        circle.setFill(colorPicker.getValue());

        VBox vbox = new VBox();
        vbox.setMinSize(0, 0);
        imageView = new ImageView();
        imageView.fitWidthProperty().bind(vbox.widthProperty());
        imageView.fitHeightProperty().bind(vbox.heightProperty());
        imageView.setPreserveRatio(true);
        robot = new Robot();
        vbox.getChildren().add(imageView);

        imageView.setOnMouseMoved(event -> {
            // Robot and Color to trace pixel information
            Color color = robot.getPixelColor((int) event.getScreenX(), (int)event.getScreenY ());
            circle.setFill(color);
        });
        imageView.setOnMouseClicked(event -> {
            // Robot and Color to trace pixel information
            Color color = robot.getPixelColor((int) event.getScreenX(), (int) event.getScreenY());
            colorPicker.setValue( color );
        });

        Button uploadBtn = new Button("Choose Image");
        uploadBtn.setOnAction(new UploadHandler());

        Button saveBtn = new Button("Save Image As...");
        saveBtn.setOnAction(new SaveHandler());

        ComboBox saveAsComboBox = new ComboBox();
        saveAsComboBox.setPromptText("Select Type");
        saveAsComboBox.getItems().addAll(
            "JPG",
            "PNG",
            "PDF",
            "WebP"
        );
        saveAsComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String s, String newS) {
                logic.setSaveFile(newS);
                System.out.println(newS);
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(saveBtn,saveAsComboBox);

        Button resetBtn = new Button("Reset Image");
        resetBtn.setOnAction(new ResetHandler());
        grayscaleBtn = new Button("Grayscale");
        grayscaleBtn.setOnAction(new GrayscaleHandler());

        VBox rtn = new VBox();
        rtn.getChildren().add(colorPicker);
        rtn.getChildren().add(circle);
        rtn.getChildren().addAll(uploadBtn, hBox, resetBtn, grayscaleBtn, vbox);

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
                    new FileChooser.ExtensionFilter("Image Files","PNG", "JPEG", "JPG", "*.png", "*.jpeg", "*.jpg"));

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
            imageView.setImage(img);
        }
    }

    /**
     * Handler for the reset button
     */
    private class ResetHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            img = originalImg;
            imageView.setImage(img);
        }
    }

    /**
     * Handler for the save image button
     */
    private class SaveHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            logic.saveImage(img);
        }
    }

}