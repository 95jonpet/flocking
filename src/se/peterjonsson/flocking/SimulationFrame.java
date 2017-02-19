package se.peterjonsson.flocking;

import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Represents a single frame/step of a {@link FlockingSimulation}.
 */
class SimulationFrame {

    private final int number;
    private final List<Position> agents;
    private final List<Position> obstacles;

    /**
     * Creates a new simulation frame from an existing state.
     * @param number Step number in the simulation.
     * @param agents List of agents in their state to take a snapshot of.
     */
    SimulationFrame(final int number, final List<Position> agents, final List<Position> obstacles) {
        this.number = number;
        this.agents = agents;
        this.obstacles = obstacles;
    }

    /**
     * Gets the visual image of the current frame.
     * @return Frame image.
     */
    javafx.scene.image.Image getImage() {
        final BufferedImage image = new BufferedImage(FlockingSimulation.SIZE, FlockingSimulation.SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // Clear graphics
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, FlockingSimulation.SIZE, FlockingSimulation.SIZE);

        graphics.setColor(Color.BLUE); // Render agents in blue

        // Render all obstacles
        for (Position obstacle : obstacles) {
            Obstacle.render(obstacle, graphics);
        }

        // Render all agents.
        for (Position agent : agents) {
            Agent.render(agent, graphics);
        }

        graphics.dispose();

        return SwingFXUtils.toFXImage(image, null);
    }

}
