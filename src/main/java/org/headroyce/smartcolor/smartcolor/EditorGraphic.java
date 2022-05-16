package org.headroyce.smartcolor.smartcolor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import javax.imageio.ImageIO;

public class EditorGraphic extends BorderPane {
    private ImageView imageView;
    private Logic logic;
    private ComboBox saveAsComboBox;

    private Robot robot;

    private Button resetBtn;
    private Button grayscaleBtn;
    private ColorPicker colorPicker;
    private Text notSelected;

    /**
     *
     * @param logic
     */
    public EditorGraphic(Logic logic){
        this();
        this.logic = logic;
        imageView.setImage(logic.getImg());
    }

    public EditorGraphic(){
        this.setCenter(imgLayout());
        this.setRight(drawingBtnLayout());
        this.logic = new Logic();
    }

    /**
     * Creates the drawing button layout
     * @return the layout
     */
    private VBox drawingBtnLayout(){
        VBox rtn = new VBox();

        Button draw = new Button("Draw");
        draw.setOnAction(new DrawHandler());
        rtn.getChildren().add(draw);

        return rtn;
    }

    private VBox imgLayout(){

        colorPicker = new ColorPicker();
        colorPicker.setOnAction(new ColorHandler());
        colorPicker.getStyleClass().add("split-button");
        colorPicker.setMinSize(20,25);

        Circle circle = new Circle(10, 10, 10);
        circle.setStroke(Color.BLACK);
        circle.setFill(colorPicker.getValue());

        VBox vbox = new VBox();
        vbox.setMinSize(0, 0);
        vbox.setFillWidth(true);
        vbox.prefHeightProperty().bind(this.heightProperty());

        imageView = new ImageView();
        imageView.fitWidthProperty().bind(vbox.widthProperty());
        imageView.fitHeightProperty().bind(vbox.heightProperty());
        imageView.setPreserveRatio(true);

        // Robot to trace pixel information
        robot = new Robot();
        vbox.getChildren().add(imageView);

        imageView.setOnMouseMoved(event -> {
            Color color = robot.getPixelColor((int) event.getScreenX(), (int)event.getScreenY ());
            circle.setFill(color);
        });
        imageView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                int x = (int) (event.getX() / imageView.getFitWidth() * logic.getWidth() + 0.5);
                int y = (int) (event.getY() / imageView.getFitHeight() * logic.getHeight() + 0.5);
                logic.setPixelColor(robot.getPixelColor(event.getScreenX(), event.getScreenY()));
                logic.recolor(x,y,100);
                logic.syncImg();
            } else if (event.getButton() == MouseButton.PRIMARY) {
                Color color = robot.getPixelColor((int) event.getScreenX(), (int) event.getScreenY());
                colorPicker.setValue(color);
                logic.setFill(color);
            }
        });

        Button uploadBtn = new Button("Choose Image");
        uploadBtn.setOnAction(new UploadHandler());

        Button saveBtn = new Button("Save Image As...");
        saveBtn.setOnAction(new SaveHandler());

        notSelected = new Text("");
        notSelected.setFill(Color.RED);

        saveAsComboBox = new ComboBox();
        saveAsComboBox.setPromptText("Select Type");
        saveAsComboBox.getItems().addAll(
                "JPG",
                "PNG"
        );
        saveAsComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String s, String newS) {
                logic.setSaveFile(newS);
                notSelected.setText("");
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(saveBtn, saveAsComboBox, notSelected);

        resetBtn = new Button("Reset Image");
        resetBtn.setOnAction(new ResetHandler());
        resetBtn.setDisable(true);
        grayscaleBtn = new Button("Grayscale");
        grayscaleBtn.setOnAction(new GrayscaleHandler());
        grayscaleBtn.setDisable(true);

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
                logic.setImg( new Image(file.toURI().toString()), true );
                imageView.setImage(logic.getImg());
                notSelected.setText("");
                resetBtn.setDisable(false);
                grayscaleBtn.setDisable(false);
            }
        }
    }

    /**
     * Handler for the grayscale button
     */
    private class GrayscaleHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            //can be removed if we don't want img to change
            logic.setImg( logic.toGrayScale() );
            imageView.setImage(logic.getImg());
        }
    }

    /**
     * Handler for the reset button
     */
    private class ResetHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            logic.resetImg();
            imageView.setImage(logic.getImg());
        }
    }

    /**
     * Handler for the save image button
     */
    private class SaveHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            if( logic.getImg() == null ){
                notSelected.setText("Upload image first");
            } else if( logic.saveFileIsNull() ) {
                notSelected.setText("Select file type first");
            } else {
                logic.saveImage();
            }
        }
    }

    /**
     * Handler for the color picker
     */
    private class ColorHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            logic.setFill(colorPicker.getValue());
        }
    }

    /**
     * Handler for the draw button
     */
    private class DrawHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            Image img = logic.getImg();
            DrawingGraphics drawing = new DrawingGraphics(logic);
            Stage s = (Stage)EditorGraphic.this.getScene().getWindow();
            Scene i = new Scene(drawing, EditorGraphic.this.getScene().getWidth(), EditorGraphic.this.getScene().getHeight());
            s.setScene(i);
        }
    }

    /**
     * Deal with key presses
     * @param event the event to handle
     */
    public void handleKeyPress(KeyEvent event) {
        // Space is reset button
        if( event.getCode() == KeyCode.F ){

        }
    }
}