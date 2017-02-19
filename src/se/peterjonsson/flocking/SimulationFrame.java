package se.peterjonsson.flocking;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Represents a single frame/step of a {@link FlockingSimulation}.
 */
class SimulationFrame {

    /**
     * Path to the stored image.
     */
    private final Path imagePath;

    /**
     * Creates a new simulation frame from an existing state.
     * @param number Step number in the simulation.
     * @param agents List of agents in their state to take a snapshot of.
     */
    SimulationFrame(final int number, final List<Position> agents, final List<Position> obstacles) {
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

        imagePath = FileSystems.getDefault().getPath("temp", number + ".jpg");

        try {
            if (Files.notExists(imagePath.getParent())) {
                Files.createDirectories(imagePath.getParent());
            } else {
                Files.deleteIfExists(imagePath);
            }

            FlockingSimulation.IMAGE_WRITER.writeImage(image, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the visual image of the current frame.
     * @return Frame image.
     */
    javafx.scene.image.Image getImage() {
        try {
            return SwingFXUtils.toFXImage(ImageIO.read(imagePath.toFile()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
