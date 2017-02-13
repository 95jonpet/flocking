package sample;

import math.geom2d.Vector2D;
import java.util.List;

/**
 * Represents a simple agent for a flocking simulation.
 *
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
public class Agent {

    /**
     * Distance the agent moves in one step/update.
     */
    private static final int SPEED = 5;

    /**
     * Maximum distance to apply alignment force to.
     */
    private static final int MAX_ALIGNMENT_DISTANCE = 128 / 2; //Integer.MAX_VALUE;

    /**
     * Minimum distance to apply cohesion force to.
     */
    private static final int MIN_COHESION_DISTANCE = 16; //Integer.MAX_VALUE;

    /**
     * Maximum distance to apply separation force to.
     */
    private static final int MAX_SEPARATION_DISTANCE = 16; //Integer.MAX_VALUE;

    private List<Agent> agents;
    private Vector2D position;
    private Vector2D direction;

    /**
     * Creates a new agent.
     * @param x Horizontal position.
     * @param y Vertical position.
     * @param agents List of all agents.
     */
    public Agent(int x, int y, List<Agent> agents) {
        this.agents = agents;

        position = new Vector2D(x, y);
        direction = new Vector2D(x, y).normalize();
    }

    /**
     * Updates the agent by stepping forward one step of the simulation.
     */
    public void update() {
        Vector2D resultant = direction;

        resultant = resultant.plus(separationVector());
        resultant = resultant.plus(alignmentVector());
        resultant = resultant.plus(cohesionVector());

        if (distanceToPoint(FlockingSimulation.SIZE / 2, FlockingSimulation.SIZE / 2) >= FlockingSimulation.SIZE * (2.0 / 6.0)) {
            Vector2D restraintVector = new Vector2D(
                    FlockingSimulation.SIZE / 2 - getX(),
                    FlockingSimulation.SIZE / 2 - getY()
            ).normalize().times(0.2);
            resultant = resultant.plus(restraintVector);
        }

        resultant = resultant.normalize().times(SPEED); // Normalize
        position = position.plus(resultant);
        direction = resultant.normalize();
    }

    /**
     * Gets the horizontal position of the agent.
     * @return Horizontal coordinate.
     */
    public double getX() {
        return position.x();
    }

    /**
     * Gets the vertical position of the agent.
     * @return Vertical coordinate.
     */
    public double getY() {
        return position.y();
    }

    /**
     * Gets a normalized vector pointing toward the middle of the flock.
     * @return Normalized vector toward flock center.
     */
    private Vector2D cohesionVector() {
        Vector2D cohesion = new Vector2D();

        for (Agent agent : agents) {
            if (agent != this && distanceToAgent(agent) >= MIN_COHESION_DISTANCE) {
                cohesion = cohesion.plus(new Vector2D(agent.getX() - getX(), agent.getY() - getY()));
            }
        }

        if (cohesion.x() != 0 && cohesion.y() != 0) {
            cohesion = cohesion.normalize();
        }

        return cohesion;
    }

    /**
     * Gets a normalized vector representing the general direction of all nearby agents.
     * @return Normalized vector representing general agent direction.
     */
    private Vector2D alignmentVector() {
        Vector2D generalDirection = new Vector2D();

        for (Agent agent : agents) {
            if (agent != this && distanceToAgent(agent) <= MAX_ALIGNMENT_DISTANCE) {
                generalDirection = generalDirection.plus(agent.direction);
            }
        }

        return generalDirection.normalize();
    }

    /**
     * Gets a normalized vector pointing away from the the closest agent.
     * If there is no agent in close proximity, a zero-vector will be returned instead.
     * @return Normalized vector pointing away from the closest agent.
     */
    private Vector2D separationVector() {
        Vector2D separation = new Vector2D();

        // TODO Base on the nearest other agent instead of flock center

        for (Agent agent : agents) {
            if (agent != this && distanceToAgent(agent) <= MAX_SEPARATION_DISTANCE) {
                separation = separation.plus(new Vector2D(agent.getX() - getX(), agent.getY() - getY()));
            }
        }

        if (separation.x() != 0 && separation.y() != 0) {
            separation = separation.normalize();
        }

        return separation.opposite();
    }

    /**
     * Gets the distance to another {@link Agent}.
     * @param agent Other agent.
     * @return Distance to the specified agent.
     */
    private double distanceToAgent(Agent agent) {
        return distanceToPoint(agent.getX(), agent.getY());
    }

    /**
     * Gets the distance to a point.
     * @param x Horizontal coordinate.
     * @param y Vertical coordinate.
     * @return Distance to the specified point.
     */
    private double distanceToPoint(final double x, final double y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    /**
     * Kills the agent.
     * This removes it from the shared list of agents.
     */
    public void kill() {
        agents.remove(this);
    }

}
