package org.headroyce.smartcolor.smartcolor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
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
        Label fillColor = new Label("Fill Color");
        Label pixelColor = new Label("Pixel Color");

        colorPicker = new ColorPicker();
        colorPicker.setOnAction(new ColorHandler());
        colorPicker.setMinSize(20,25);

        Circle circle = new Circle(20, 20, 20);
        circle.setStroke(Color.BLACK);
        circle.setFill(colorPicker.getValue());

        VBox imgvwVB = new VBox();
        imgvwVB.setMinSize(10, 10);
        imgvwVB.setFillWidth(true);
        imgvwVB.prefHeightProperty().bind(this.heightProperty());

        imageView = new ImageView();
        imageView.fitWidthProperty().bind(imgvwVB.widthProperty());
        imageView.fitHeightProperty().bind(imgvwVB.heightProperty());
        imageView.setPreserveRatio(true);
        imgvwVB.getChildren().add(imageView);

        // Robot to trace pixel information
        robot = new Robot();

        imageView.setOnMouseMoved(event -> {
            Color color = robot.getPixelColor((int) event.getScreenX(), (int)event.getScreenY ());
            circle.setFill(color);
        });
        imageView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                int x = (int) (event.getX() / imageView.getFitWidth() * logic.getWidth() + 0.5);
                int y = (int) (event.getY() / imageView.getFitHeight() * logic.getHeight() + 0.5);
                logic.setPixelColor(robot.getPixelColor(event.getScreenX(), event.getScreenY()));
                logic.recolor(x,y,(int)this.getWidth());
                logic.syncImg();
            } else if (event.getButton() == MouseButton.PRIMARY) {
                Color color = robot.getPixelColor((int) event.getScreenX(), (int) event.getScreenY());
                colorPicker.setValue(color);
                logic.setFill(color);
            }
        });

        grayscaleBtn = new Button("Grayscale");
        grayscaleBtn.setOnAction(new GrayscaleHandler());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button uploadBtn = new Button("Upload");
        uploadBtn.setOnAction(e->{
            if( logic.ImgIsNotUploaded() ){
                upload();
            } else {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Upload New Image");
                alert.setContentText("This action will override the existing image. Proceed?");
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType cancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(yes, cancel);
                alert.showAndWait().ifPresent(type -> {
                    if( type == yes ){
                        upload();
                    }
                });
            }
        });

        resetBtn = new Button("Reset Image");
        resetBtn.setOnAction(new ResetHandler());

        Button draw = new Button("Draw");
        draw.setOnAction(new DrawHandler());
        draw.setTextFill(Color.WHITE);
        draw.setStyle("-fx-background-color: #666;");

        Button[] btnArr = new Button[] { grayscaleBtn, uploadBtn, resetBtn, draw };

        for( int i = 0; i < btnArr.length; i++ ){
            btnArr[i].setCursor(Cursor.HAND);
            btnArr[i].setMinWidth(90);
        }

        VBox btns = new VBox(10);
        btns.setPadding(new Insets(20, 5, 20, 5));
        btns.getChildren().addAll( fillColor, colorPicker, pixelColor, circle, grayscaleBtn, spacer, uploadBtn, resetBtn, draw);
        btns.setStyle("-fx-background-color: #999");
        btns.setPrefWidth(100);

        Button saveBtn = new Button("Save Image As...");
        saveBtn.setOnAction(new SaveHandler());
        saveBtn.setCursor(Cursor.HAND);

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

        HBox imgOptions = new HBox();
        imgOptions.getChildren().addAll(saveBtn, saveAsComboBox, notSelected);

        this.setLeft(btns);
        this.setCenter(imgvwVB);
        this.setTop(imgOptions);
        this.logic = new Logic();
    }

    /**
     * Allows user to upload image. Only takes JPGs and PNGs
     */
    private void upload(){
        FileChooser fileChooser = new FileChooser();

        //Set extension filter, only shows jpg and png
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "PNG", "JPEG", "JPG", "*.png", "*.jpeg", "*.jpg"));

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        //Shows selected image
        if (file != null) {
            logic.setImg(new Image(file.toURI().toString()), true);
            logic.setImgNotUploaded(false);
            imageView.setImage(logic.getImg());
            notSelected.setText("");
        }
    }

    /**
     * Handler for the grayscale button
     */
    private class GrayscaleHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            if( logic.getImg() != null ) {
                logic.setImg(logic.toGrayScale());
                imageView.setImage(logic.getImg());
            }
        }
    }

    /**
     * Handler for the reset button
     */
    private class ResetHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Reset Image to Original");
            alert.setContentText("This action cannot be undone. Reset?");
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yes, cancel);
            alert.showAndWait().ifPresent(type -> {
                if( type == yes && logic.getImg() != null ){
                    logic.resetImg();
                    imageView.setImage(logic.getImg());
                }
            });
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
            DrawingGraphics dc = new DrawingGraphics(logic, EditorGraphic.this.getScene().getWidth(), EditorGraphic.this.getScene().getHeight());
            Stage s = (Stage)EditorGraphic.this.getScene().getWindow();
            Scene i = new Scene(dc, EditorGraphic.this.getScene().getWidth(), EditorGraphic.this.getScene().getHeight());
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
            Point p = MouseInfo.getPointerInfo().getLocation();

            int px = p.x;
            int py = p.y;
            int x = (int) (px / imageView.getFitWidth() * logic.getWidth() + 0.5);
            int y = (int) (py / imageView.getFitHeight() * logic.getHeight() + 0.5);

            logic.setPixelColor(robot.getPixelColor(px,py));
            logic.recolor(x,y,(int)logic.getImg().getWidth());
            logic.syncImg();
        }
    }

    /**
     * Deal with key releases
     * @param event the event to handle
     */
    public void handleKeyRelease(KeyEvent event){}
}