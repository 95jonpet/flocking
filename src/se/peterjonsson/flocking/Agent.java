package se.peterjonsson.flocking;

import math.geom2d.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * Represents a simple agent for a flocking simulation.
 *
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
class Agent {

    /**
     * Distance the agent moves in one step/update.
     */
    private static final int SPEED = 5;

    /**
     * Maximum distance to apply alignment force to.
     */
    private static final int MAX_ALIGNMENT_DISTANCE = 128 / 2;

    /**
     * Minimum distance to apply cohesion force to.
     */
    private static final int MIN_COHESION_DISTANCE = 16;

    /**
     * Maximum distance to apply cohesion force to.
     */
    private static final int MAX_COHESION_DISTANCE = 64;

    /**
     * Maximum distance to apply separation force to.
     */
    private static final int MAX_SEPARATION_DISTANCE = 16;

    /**
     * List of all agents in the simulation.
     * This should include the current agent.
     */
    private final List<Agent> agents;

    /**
     * List of all obstacles that should be avoided.
     */
    private final List<Obstacle> obstacles;

    /**
     * List of all predators that should be avoided.
     */
    private final List<Predator> predators;

    /**
     * Current agent position.
     */
    private Vector2D position;

    /**
     * Normalized vector of the last force applied to the agent.
     * Use {@link Vector2D#angle()} to find the actual angle of this force.
     */
    private Vector2D direction;

    /**
     * Gets if the agent is dead.
     */
    private boolean dead;

    /**
     * The color to use when drawing the agent.
     */
    private static Color color = Color.BLUE;

    /**
     * The x points used for rendering the agent.
     */
    private static final int[] xPoints = new int[] { 0, 5, 10};

    /**
     * The y points used for rendering the agent.
     */
    private static final int[] yPoints = new int[] { 15, 0, 15 };

    /**
     * Creates a new agent.
     * @param x Horizontal position.
     * @param y Vertical position.
     * @param agents List of all agents.
     * @param obstacles List of all obstacles.
     * @param predators List of all predators.
     */
    Agent(int x, int y, List<Agent> agents, List<Obstacle> obstacles, List<Predator> predators) {
        this.agents = agents;
        this.obstacles = obstacles;
        this.predators = predators;

        position = new Vector2D(x, y);
        direction = new Vector2D(x, y).normalize();
    }

    /**
     * Updates the agent by stepping forward one step of the simulation.
     */
    void update() {
        Vector2D resultant = boidsVector(); // General boids vector

        // Avoid obstacles
        Vector2D obstacleVector = obstacleVector();
        if (obstacleVector.getX() != 0 && obstacleVector.getY() != 0)
            resultant = resultant.plus(obstacleVector.normalize().times(3));

        // Avoid predators
        Vector2D predatorVector = predatorVector();
        if (predatorVector.getX() != 0 && predatorVector.getY() != 0)
            resultant = resultant.plus(predatorVector.normalize().times(3));

        resultant = resultant.normalize().times(SPEED); // Normalize
        position = position.plus(resultant);
        direction = resultant.normalize();
    }

    /**
     * Renders an agent onto a graphics object.
     * @param position Position of the agent.
     * @param graphics Canvas to render onto.
     */
    static void render(Position position, Graphics2D graphics) {
        AffineTransform at = new AffineTransform();
        Dimension size = getTriangleSize();

        int x = (int) Math.round(position.x);
        int y = (int) Math.round(position.y);
        double angle = position.angle + Math.PI / 2;

        at.translate(x - size.width / 2, y - size.height / 2);
        at.rotate(angle, size.width / 2, size.height / 2);

        graphics.setTransform(at);
        graphics.setColor(color);
        graphics.fillPolygon(xPoints, yPoints, 3);

        graphics.setTransform(new AffineTransform());
    }

    /**
     * Gets the horizontal position of the agent.
     * @return Horizontal coordinate.
     */
    double getX() {
        return position.x();
    }

    /**
     * Gets the vertical position of the agent.
     * @return Vertical coordinate.
     */
    double getY() {
        return position.y();
    }

    /**
     * Gets the agent's angle in radians.
     * @return Angle in radians.
     */
    double getAngle() {
        return direction.angle();
    }

    /**
     * Gets a normalized vector pointing toward the middle of the flock.
     * @return Normalized vector toward flock center.
     */
    private Vector2D cohesionVector() {
        Vector2D cohesion = new Vector2D();

        for (Agent agent : agents) {
            double distance = distanceToAgent(agent);
            if (agent != this && distance >= MIN_COHESION_DISTANCE && distance <= MAX_COHESION_DISTANCE) {
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
        Vector2D separation = new Vector2D(0, 0);

        for (Agent agent : agents) {
            if (agent != this && distanceToAgent(agent) <= MAX_SEPARATION_DISTANCE) {
                separation = separation.plus(new Vector2D(getX() - agent.getX(), getY() - agent.getY()));
            }
        }

        if (separation.x() != 0 && separation.y() != 0) {
            separation = separation.normalize();
        }

        return separation;
    }

    /**
     * Gets a vector for avoiding obstacles.
     * @return Vector pointing away from nearby obstacles.
     */
    private Vector2D obstacleVector() {
        Vector2D vector = new Vector2D(0, 0);
        for (Obstacle obstacle : obstacles) {
            if (distanceToPoint(obstacle.x, obstacle.y) <= Obstacle.RADIUS * 1.5) {
                Vector2D v = new Vector2D(getX() - obstacle.x, getY() - obstacle.y)
                        .normalize()
                        .rotate(-Math.PI / 2)
                        .times(Obstacle.RADIUS * 1.5);

                vector = vector.plus(new Vector2D(v.getX() - getX(), v.getY() - getY()));
                // TODO Aim for point along the normal vector
            }
        }

        return vector;
    }

    /**
     * Gets a vector for avoiding predators.
     * @return Vector pointing away from nearby predators.
     */
    private Vector2D predatorVector() {
        Vector2D vector = new Vector2D(0, 0);
        for (Predator predator : predators) {
            if (distanceToPoint(predator.getX(), predator.getY()) <= MAX_ALIGNMENT_DISTANCE) {
                vector = vector.plus(new Vector2D(getX() - predator.getX(), getY() - predator.getY()));
            }
        }

        return vector;
    }

    /**
     * Gets the boids vector created by the three rules:
     *  1. Separation
     *  2. Alignment
     *  3. Cohesion
     * The vector is not normalized.
     * @return Boids vector.
     */
    private Vector2D boidsVector() {
        Vector2D resultant = direction;

        resultant = resultant.plus(separationVector().times(3));
        resultant = resultant.plus(alignmentVector());
        resultant = resultant.plus(cohesionVector());

        if (distanceToPoint(FlockingSimulation.SIZE / 2, FlockingSimulation.SIZE / 2) >= FlockingSimulation.SIZE * (2.0 / 6.0)) {
            Vector2D restraintVector = new Vector2D(
                    FlockingSimulation.SIZE / 2 - getX(),
                    FlockingSimulation.SIZE / 2 - getY()
            ).normalize().times(0.3);
            resultant = resultant.plus(restraintVector);
        }

        return resultant;
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
    void kill() {
        dead = true;
    }

    /**
     * Gets if the agent is dead or not.
     * @return Agent is dead.
     */
    boolean isDead() {
        return dead;
    }

    /**
     * Gets the size of the triangle used to render the agent.
     * @return Dimensions for rendering a triangle.
     */
    private static Dimension getTriangleSize() {
        int maxX = 0;
        int maxY = 0;

        for (int xPoint : xPoints) {
            maxX = Math.max(maxX, xPoint);
        }

        for (int yPoint : yPoints) {
            maxY = Math.max(maxY, yPoint);
        }

        return new Dimension(maxX, maxY);
    }
}
