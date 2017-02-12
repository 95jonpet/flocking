package sample;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Represents a single frame/step of a {@link FlockingSimulation}.
 */
public class SimulationFrame {
    private Image image;

    /**
     * Creates a new simulation frame from an existing state.
     * @param size Horizontal and vertical snapshot size.
     * @param agents List of agents in their state to take a snapshot of.
     */
    public SimulationFrame(final int size, final List<Agent> agents) {
        Canvas canvas = new Canvas(size, size);
        GraphicsContext context = canvas.getGraphicsContext2D();

        // Clear context
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        context.setFill(Color.BLUE); // Render agents in blue

        // Render all agents.
        for (Agent agent : agents) {
            context.fillOval(agent.getX(), agent.getY(), 5, 5);
        }

        image = canvas.snapshot(new SnapshotParameters(), null);
    }

    /**
     * Gets the visual image of the current frame.
     * @return Frame image.
     */
    public Image getImage() {
        return image;
    }

}
