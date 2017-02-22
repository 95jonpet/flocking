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
    private final List<Agent> agents = new ArrayList<>();

    /**
     * List of obstacles.
     */
    private final List<Obstacle> obstacles = new ArrayList<>();

    /**
     * List of predators.
     */
    private final List<Predator> predators = new ArrayList<>();

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
        //addObstacle(SIZE / 2, SIZE / 2);
        addPredator(SIZE / 2, SIZE / 2);

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
        agents.add(new Agent(x, y, agents, obstacles, predators));
    }

    /**
     * Adds an obstacle to the simulation.
     * @param x Horizontal starting position of the obstacle.
     * @param y Vertical starting position of the obstacle.
     */
    private void addObstacle(int x, int y) {
        obstacles.add(new Obstacle(x, y));
    }

    /**
     * Adds a predator to the simulation.
     * @param x Horizontal starting position of the predator.
     * @param y Vertical starting position of the predator.
     */
    private void addPredator(int x, int y) {
        predators.add(new Predator(x, y, agents, obstacles, predators));
    }
}
