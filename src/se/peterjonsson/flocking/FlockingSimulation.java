package se.peterjonsson.flocking;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * A flocking simulation.
 *
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
class FlockingSimulation {

    static final boolean FLOCKING_PREY = true;
    static final boolean FLOCKING_PREDATORS = true;
    //private static final Random random = new Random(861178936920257679L); // Randomized seed

//    private static final Random random = new Random(766104113L);
//    private static final Random random = new Random(965935330L);
//    private static final Random random = new Random(187436842L);
//    private static final Random random = new Random(696054169L);
    private static final Random random = new Random(-915743478L);


    /**
     * The horizontal and vertical size of the simulation.
     */
    static final int SIZE = 2048;

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
        for (int i = 0; i < numberOfAgents; i++) {
            addAgent(random.nextInt(SIZE), random.nextInt(SIZE));
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

        Path path = Paths.get("result.txt");
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter out = Files.newBufferedWriter(path, Charset.defaultCharset())) {
            // Simulate steps as frames
            for (int i = 0; i < STEPS; i++) {
                if (i > 0) {
                    update(i);
                }

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

                out.write(""+agents.size());
                out.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        running = false;
        finished = true;
    }

    /**
     * Updates the simulation by stepping forward once.
     */
    private void update(int step) {
        if (step == 1000) {
            releasePredators();
        }

        for (Agent agent : agents) {
            agent.update();
        }

        for (Predator predator : predators) {
            predator.update();
        }

        agents.removeIf(Agent::isDead); // Remove killed agents
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

    private void releasePredators() {
        int d = 16;

        addPredator(SIZE / 2 - d, SIZE / 2 - d);
        addPredator(SIZE / 2 + d, SIZE / 2 - d);
        addPredator(SIZE / 2 - d, SIZE / 2 + d);
        addPredator(SIZE / 2 + d, SIZE / 2 + d);
    }
}
