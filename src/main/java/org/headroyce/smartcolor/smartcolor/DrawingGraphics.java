package org.headroyce.smartcolor.smartcolor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DrawingGraphics extends BorderPane {

    private Canvas canvas;
    private Logic logic;
    private DrawLogic drawLogic;
    private Image img;

    /**
     *
     * @param logic
     */
    public DrawingGraphics(Logic logic){
        this.logic = logic;

        drawLogic = new DrawLogic();
        this.img = logic.getImg();
        this.setLeft(backLayout());
        setCanvas();

        this.setCenter(canvas);
    }

    private void setCanvas(){
        canvas = new Canvas();
        canvas.heightProperty().bind(this.heightProperty());
        canvas.widthProperty().bind(this.widthProperty());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage(img, 0, 0,canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Creates the back button layout
     * @return the layout
     */
    private VBox backLayout(){
        VBox rtn = new VBox();
        Button back = new Button("Back");
        back.setOnAction(new BackHandler());

        rtn.getChildren().add(back);

        return rtn;
    }




    /**
     * Handler for the back button
     */
    private class BackHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e){
            EditorGraphic m = new EditorGraphic(logic);
            Stage s = (Stage)DrawingGraphics.this.getScene().getWindow();
            Scene i = new Scene(m, 750, 750);
            s.setScene(i);
            s.setMaximized(true);

        }
    }
}
