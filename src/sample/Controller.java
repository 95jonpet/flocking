package sample;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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

        simulation = new FlockingSimulation(42);

        Platform.runLater(() -> {
            simulation.run();

            Image img = simulation.getFrame(FlockingSimulation.STEPS - 1).getImage();
            canvas.getGraphicsContext2D().drawImage(img, 0, 0);
        });
    }

    private void render() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        // Clear canvas with white
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        simulation.render(context);
    }

}
