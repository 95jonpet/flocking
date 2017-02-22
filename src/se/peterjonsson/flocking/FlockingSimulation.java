package se.peterjonsson.flocking;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A flocking simulation.
 *
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
class FlockingSimulation {

    static final CustomImageWriter IMAGE_WRITER = new CustomImageWriter();

    /**
     * The horizontal and vertical size of the simulation.
     */
    static final int SIZE = 512;

    /**
     * The number of steps to simulate.
     */
    static final int STEPS = 5000;

    /**
     * List of all agents within the simulation.
     */
    private final List<Agent> agents;

    /**
     * List of obstacles.
     */
    private final List<Obstacle> obstacles;

    /**
     * List of predators.
     */
    private final List<Predator> predators;

    /**
     * Indicates if the simulation has finished or not.
     */
    private volatile boolean finished = false;

    /**
     * Indicates if the simulation is currently running or not.
     */
    private volatile boolean running = false;

    /**
     * Array of individual simulation frames for each step of the simulation.
     */
    private final SimulationFrame[] frame = new SimulationFrame[STEPS];

    /**
     * Property containing the current simulation progress.
     * The value is between 0 and 1 inclusive.
     */
    final DoubleProperty progressProperty = new SimpleDoubleProperty(0);

    /**
     * Creates a new flocking simulation.
     */
    FlockingSimulation(final int numberOfAgents) {
        this.agents = new ArrayList<>();
        obstacles = new ArrayList<>();
        predators = new ArrayList<>();

        //obstacles.add(new Obstacle(SIZE / 2, SIZE / 2));
        predators.add(new Predator(SIZE / 2, SIZE / 2, agents, obstacles, predators));

        // Set up initial state
        // TODO Make more effective
        for (int i = 0; i < numberOfAgents; i++) {
            int position = (int) Math.floor(i * (Math.pow(SIZE, 2) / numberOfAgents));
            addAgent(position / SIZE, position % SIZE);
        }
    }

    /**
     * Gets a specific simulation frame.
     * @param index Frame index.
     * @return Requested simulation frame.
     */
    SimulationFrame getFrame(final int index) {
        return frame[index];
    }

    /**
     * Run the entire simulation.
     * This could be done on a separate thread.
     */
    void run() {
        if (finished || running)
            return;

        running = true;
        progressProperty.set(0);

        // Simulate steps as frames
        for (int i = 0; i < STEPS; i++) {
            update();

            List<Position> agentList = new LinkedList<>();
            for (Agent agent : agents) {
                agentList.add(new Position(agent.getX(), agent.getY(), agent.getAngle()));
            }

            List<Position> obstacleList = new LinkedList<>();
            for (Obstacle obstacle : obstacles) {
                obstacleList.add(new Position(obstacle.x, obstacle.y));
            }

            List<Position> predatorList = new LinkedList<>();
            for (Predator predator : predators) {
                predatorList.add(new Position(predator.getX(), predator.getY(), predator.getAngle()));
            }

            frame[i] = new SimulationFrame(i, agentList, obstacleList, predatorList);

            progressProperty.set((double) i / STEPS);
        }

        IMAGE_WRITER.waitForImages();

        running = false;
        finished = true;
    }

    /**
     * Updates the simulation by stepping forward once.
     */
    private void update() {
        for (Agent agent : agents) {
            agent.update();
        }

        for (Predator predator : predators) {
            predator.update();
        }

        agents.removeIf(Agent::isDead); // Remove killed agents
        System.out.printf("%d agents alive.\n", agents.size());
    }

    /**
     * Adds an agent to the simulation.
     * @param x Horizontal starting position of the agent.
     * @param y Vertical starting position of the agent.
     */
    private void addAgent(int x, int y) {
        agents.add(new Agent(x, y, agents, obstacles));
    }
}
