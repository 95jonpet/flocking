package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * A flocking simulation.
 *
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
public class FlockingSimulation {
    static final int SIZE = 1024;

    List<Agent> agents;

    /**
     * Creates a new flocking simulation.
     */
    public FlockingSimulation(int numberOfAgents) {
        this.agents = new ArrayList<>();

        for (int i = 0; i < numberOfAgents; i++) {
            int position = (int) Math.floor(i * (Math.pow(SIZE, 2) / numberOfAgents));
            addAgent(position / SIZE, position % SIZE);
        }
    }

    /**
     * Updates the simulation by stepping forward once.
     */
    public void update() {
        for (Agent agent : agents) {
            agent.update();
        }
    }

    /**
     * Renders the simulation onto a {@link GraphicsContext} canvas.
     * @param context Canvas to render onto.
     */
    public void render(GraphicsContext context) {
        context.setFill(Color.BLUE); // Render agents in blue

        for (Agent agent : agents) {
            context.fillOval(agent.getX(), agent.getY(), 5, 5);
        }
    }

    /**
     * Adds an agent to the simulation.
     * @param x Horizontal starting position of the agent.
     * @param y Vertical starting position of the agent.
     */
    private void addAgent(int x, int y) {
        agents.add(new Agent(x, y, agents));
    }
}
