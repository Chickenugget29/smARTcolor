package org.headroyce.smartcolor.smartcolor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DrawingGraphics extends BorderPane {

    private Canvas canvas;
    private Logic logic;
    private DrawLogic drawLogic;
    private GraphicsContext gc;

    /**
     *
     * @param logic
     */
    public DrawingGraphics(Logic logic){
        //might not need logic
        this.logic = logic;

        drawLogic = new DrawLogic();
        drawLogic.setImg(logic.getImg());
        this.setLeft(backLayout());

        canvas = new Canvas();
        canvas.heightProperty().bind(this.heightProperty());
        canvas.widthProperty().bind(this.widthProperty());
        this.setCenter(canvas);
        gc = canvas.getGraphicsContext2D();

        this.addEventHandler(WindowEvent.WINDOW_SHOWN, new ShownHandler());
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

    private class ShownHandler implements EventHandler<WindowEvent> {
            @Override
            public void handle(WindowEvent e) {
                System.out.println("test1");
                gc.drawImage(drawLogic.getImg(), 0, 0, canvas.getWidth(), canvas.getHeight());
                System.out.println("test2");
            }
        }

}
