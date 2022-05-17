package org.headroyce.smartcolor.smartcolor;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.WritableImage;


public class DrawingGraphics extends BorderPane {

    private Canvas canvas;
    private Logic logic;
    private GraphicsContext gc;

    /**
     *
     * @param logic
     */
    public DrawingGraphics(Logic logic, double width, double height){
        //might not need logic
        this.logic = logic;
        this.setWidth(width);
        this.setHeight(height);

        // Tools to draw
        ToggleButton drawbtn = new ToggleButton("Draw");
        ToggleButton eraserbtn = new ToggleButton("Eraser");
        ToggleButton linebtn = new ToggleButton("Line");
        ToggleButton rectbtn = new ToggleButton("Rectangle");
        ToggleButton circlebtn = new ToggleButton("Circle");
        ToggleButton ellipsebtn = new ToggleButton("Ellipse");
        ToggleButton textbtn = new ToggleButton("Text");

        ToggleButton[] toolsArr = {drawbtn, eraserbtn, linebtn, rectbtn, circlebtn, ellipsebtn, textbtn};

        ToggleGroup tools = new ToggleGroup();

        for( int i = 0; i < toolsArr.length; i++ ){
            toolsArr[i].setMinWidth(90);
            toolsArr[i].setToggleGroup(tools);
            toolsArr[i].setCursor(Cursor.HAND);
        }

        ColorPicker cpLine = new ColorPicker(Color.BLACK);
        ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);

        TextArea text = new TextArea();
        text.setPrefRowCount(1);

        Slider sizeSlider = new Slider(1, 50, 3);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);

        Label lineColor = new Label("Line Color");
        Label fillColor = new Label("Fill Color");
        Label lineWidth = new Label("Width: 3.0");

        Button clear = new Button("Clear");
        clear.setOnAction(e-> {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Clear Canvas");
            alert.setContentText("The canvas (including the image) will be erased. Clear?");
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yes, cancel);
            alert.showAndWait().ifPresent(type -> {
                if( type == yes ){
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                }
            });
        });

        Button back = new Button("Back");
        back.setOnAction(e->{
            WritableImage wImg = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
            canvas.snapshot(null, wImg);
            logic.setImg(wImg);
            if( logic.ImgIsNotUploaded() ) logic.setImg(wImg, true);
            toEditorGraphic();
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e-> {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Cancel Changes");
            alert.setContentText("Drawing changes will not be saved. Cancel changes?");
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancelbtn = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yes, cancelbtn);
            alert.showAndWait().ifPresent(type -> {
                if( type == yes ){
                    toEditorGraphic();
                }
            });
        });

        Button[] options = new Button[]{ clear, back, cancel };
        for( int i = 0; i < options.length; i++ ){
            options[i].setMinWidth(90);
            options[i].setCursor(Cursor.HAND);
            options[i].setTextFill(Color.WHITE);
            options[i].setStyle("-fx-background-color: #666;");
        }

        VBox btns = new VBox(10);
        btns.getChildren().addAll(drawbtn, eraserbtn, linebtn, rectbtn, circlebtn, ellipsebtn,
                textbtn, text, lineColor, cpLine, fillColor, cpFill, lineWidth, sizeSlider, clear, back, cancel);
        btns.setPadding(new Insets(5));
        btns.setStyle("-fx-background-color: #999");
        btns.setPrefWidth(100);

        // canvas
        VBox canvasVB = new VBox();
        canvasVB.setMinSize(width, height);
        canvasVB.setPrefSize(width, height);
        canvasVB.setFillWidth(true);
        canvasVB.prefHeightProperty().bind(this.heightProperty());

        canvas = new Canvas(width, height);
        //canvas.widthProperty().bind(canvasVB.widthProperty());
        //canvas.heightProperty().bind(canvasVB.heightProperty());
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.drawImage(logic.getImg(), 0, 0, canvas.getWidth(), canvas.getHeight());
        canvasVB.getChildren().add(canvas);

        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();

        canvas.setOnMousePressed(e->{
            if( drawbtn.isSelected() ){
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            }
            else if( eraserbtn.isSelected() ){
                double lw = gc.getLineWidth();
                gc.clearRect(e.getX() - lw / 2, e.getY() - lw / 2, lw, lw);
            }
            else if( linebtn.isSelected() ){
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());
            }
            else if( rectbtn.isSelected() ){
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            }
            else if( circlebtn.isSelected() ){
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            }
            else if( ellipsebtn.isSelected() ){
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());
            }
            else if( textbtn.isSelected() ){
                gc.setLineWidth(1);
                gc.setFont(Font.font(sizeSlider.getValue()));
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                gc.fillText(text.getText(), e.getX(), e.getY());
                gc.strokeText(text.getText(), e.getX(), e.getY());
            }
        });

        canvas.setOnMouseDragged(e->{
            if( drawbtn.isSelected() ){
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
            else if( eraserbtn.isSelected() ){
                double lw = gc.getLineWidth();
                gc.clearRect(e.getX() - lw / 2, e.getY() - lw / 2, lw, lw);
            }
        });

        canvas.setOnMouseReleased(e->{
            if( drawbtn.isSelected() ){
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            }
            else if( eraserbtn.isSelected() ){
                double lw = gc.getLineWidth();
                gc.clearRect(e.getX() - lw / 2, e.getY() - lw / 2, lw, lw);
            }
            else if( linebtn.isSelected() ){
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
            }
            else if( rectbtn.isSelected() ){
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                if( rect.getX() > e.getX() ){
                    rect.setX(e.getX());
                }
                if( rect.getY() > e.getY() ){
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            }
            else if( circlebtn.isSelected() ){
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if( circ.getCenterX() > e.getX() ){
                    circ.setCenterX(e.getX());
                }
                if( circ.getCenterY() > e.getY() ){
                    circ.setCenterY(e.getY());
                }

                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
            }
            else if( ellipsebtn.isSelected() ){
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if( elps.getCenterX() > e.getX() ){
                    elps.setCenterX(e.getX());
                }
                if( elps.getCenterY() > e.getY() ){
                    elps.setCenterY(e.getY());
                }

                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
            }
        });
        // handlers for color pickers
        cpLine.setOnAction(e->{
            gc.setStroke(cpLine.getValue());
        });
        cpFill.setOnAction(e->{
            gc.setFill(cpFill.getValue());
        });

        // line width slider
        sizeSlider.valueProperty().addListener(e->{
            double lw = sizeSlider.getValue();
            if( textbtn.isSelected() ){
                gc.setLineWidth(1);
                gc.setFont(Font.font(sizeSlider.getValue()));
                lineWidth.setText("Width: " + String.format("%.1f", lw));
                return;
            }
            lineWidth.setText("Width: " + String.format("%.1f", lw));
            gc.setLineWidth(lw);
        });

        this.setLeft(btns);
        this.setCenter(canvasVB);
    }


    /**
     * Returns scene to editor graphic
     */
    private void toEditorGraphic(){
        EditorGraphic eg = new EditorGraphic(logic);
        Stage s = (Stage)DrawingGraphics.this.getScene().getWindow();
        Scene i = new Scene(eg, DrawingGraphics.this.getScene().getWidth(), DrawingGraphics.this.getScene().getHeight());
        s.setScene(i);
    }

}
