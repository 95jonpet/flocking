package se.peterjonsson.flocking;

import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Represents a single frame/step of a {@link FlockingSimulation}.
 */
class SimulationFrame {

    /**
     * Image containing a rendition of the simulation frame.
     */
    private final BufferedImage image;

    /**
     * Creates a new simulation frame from an existing state.
     * @param agents List of agents in their state to take a snapshot of.
     */
    SimulationFrame(final List<Agent> agents) {
        image = new BufferedImage(FlockingSimulation.SIZE, FlockingSimulation.SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();

        // Clear graphics
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, FlockingSimulation.SIZE, FlockingSimulation.SIZE);

        graphics.setColor(Color.BLUE); // Render agents in blue

        // Render all agents.
        for (Agent agent : agents) {
            graphics.fillOval(Math.round((int) agent.getX()), Math.round((int) agent.getY()), 5, 5);
        }

        graphics.dispose();
    }

    /**
     * Gets the visual image of the current frame.
     * @return Frame image.
     */
    javafx.scene.image.Image getImage() {
        return SwingFXUtils.toFXImage(image, null);
    }

}
