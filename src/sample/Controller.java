package sample;

import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Controller {
    private FlockingSimulation simulation;
    private Parent root;
    private Canvas canvas;

    public Controller(Parent root) {
        this.root = root;
        canvas = (Canvas) root.lookup("#canvas");
        canvas.setWidth(FlockingSimulation.SIZE);
        canvas.setHeight(FlockingSimulation.SIZE);

        simulation = new FlockingSimulation(10);

        render();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                simulation.update();
                render();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void render() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        // Clear canvas with white
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        simulation.render(context);
    }

}
