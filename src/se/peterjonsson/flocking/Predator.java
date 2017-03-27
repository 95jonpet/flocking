package se.peterjonsson.flocking;

import math.geom2d.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

class Predator {
    private static final boolean FLOCKING = false;

    /**
     * Distance the agent moves in one step/update.
     */
    private static final int SPEED = 6;

    /**
     * Distance to kill agents from.
     */
    private static final int KILL_DISTANCE = SPEED;

    /**
     * List of all agents in the simulation.
     */
    private final List<Agent> agents;

    /**
     * List of all obstacles that should be avoided.
     */
    private final List<Obstacle> obstacles;

    /**
     * List of all predators in the simulation.
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
     * The color to use when drawing the predator.
     */
    private static Color color = Color.RED;

    /**
     * The x points used for rendering the predator.
     */
    private static final int[] xPoints = new int[] { 0, 5, 10};

    /**
     * The y points used for rendering the predator.
     */
    private static final int[] yPoints = new int[] { 15, 0, 15 };

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

    private static final int FIELD_OF_VIEW_DEGREES = 140;

    /**
     * Creates a new predator.
     * @param x Horizontal position.
     * @param y Vertical position.
     * @param agents List of all agents.
     * @param obstacles List of all obstacles.
     * @param predators List of all predators.
     */
    Predator(int x, int y, List<Agent> agents, List<Obstacle> obstacles, List<Predator> predators) {
        this.agents = agents;
        this.obstacles = obstacles;
        this.predators = predators;

        position = new Vector2D(x, y);
        direction = new Vector2D(FlockingSimulation.SIZE / 2 - x, FlockingSimulation.SIZE / 2 - y).normalize();
    }

    /**
     * Updates the predator by stepping forward one step of the simulation.
     */
    void update() {
        Vector2D resultant = boidsVector().normalize(); // General boids vector

        if (agents.size() != 0) {
            double shortestDistance = Double.MAX_VALUE;
            Iterator<Agent> iterator = agents.iterator();
            Agent closestAgent = null;
            while (iterator.hasNext()) {
                Agent agent = iterator.next();
                double distance = distanceToAgent(agent);

                Vector2D vToAgent = new Vector2D(agent.getX() - getX(), agent.getY() - getY());
                double diffAngle = (Math.atan2(vToAgent.y(),vToAgent.x()) - Math.atan2(direction.y(),direction.x()));

                if (diffAngle > Math.PI) {
                    diffAngle -= Math.PI;
                }

                boolean validAngle = diffAngle <= Math.toRadians(FIELD_OF_VIEW_DEGREES);

                if (distance < shortestDistance && validAngle) {
                    shortestDistance = distance;
                    closestAgent = agent;
                }
            }

            if (closestAgent != null) {
                Vector2D vector = new Vector2D(closestAgent.getX() - getX(), closestAgent.getY() - getY());
                resultant = resultant.plus(vector.normalize().times(2));
            }
        }

        resultant = resultant.normalize().times(SPEED); // Normalize
        position = position.plus(resultant);
        direction = resultant.normalize();

        killNearbyAgents();
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

        if (FLOCKING) {
            resultant = resultant.plus(alignmentVector());
            resultant = resultant.plus(cohesionVector());
        }

        double distance = distanceToPoint(FlockingSimulation.SIZE / 2, FlockingSimulation.SIZE / 2);
        if (distance >= FlockingSimulation.SIZE / 2) {
            double restraintForce = (distance / (FlockingSimulation.SIZE / 2)) - 1;
            Vector2D restraintVector = new Vector2D(
                    FlockingSimulation.SIZE / 2 - getX(),
                    FlockingSimulation.SIZE / 2 - getY()
            ).normalize().times(restraintForce);
            resultant = resultant.plus(restraintVector);
        }

        return resultant;
    }

    /**
     * Gets a normalized vector pointing toward the middle of the flock.
     * @return Normalized vector toward flock center.
     */
    private Vector2D cohesionVector() {
        Vector2D cohesion = new Vector2D();

        for (Predator predator : predators) {
            double distance = distanceToPredator(predator);
            if (predator != this && distance >= MIN_COHESION_DISTANCE && distance <= MAX_COHESION_DISTANCE) {
                cohesion = cohesion.plus(new Vector2D(predator.getX() - getX(), predator.getY() - getY()));
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

        for (Predator predator : predators) {
            if (predator != this && distanceToPredator(predator) <= MAX_ALIGNMENT_DISTANCE) {
                generalDirection = generalDirection.plus(predator.direction);
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

        for (Predator predator : predators) {
            if (predator != this && distanceToPredator(predator) <= MAX_SEPARATION_DISTANCE) {
                separation = separation.plus(new Vector2D(getX() - predator.getX(), getY() - predator.getY()));
            }
        }

        if (separation.x() != 0 && separation.y() != 0) {
            separation = separation.normalize();
        }

        return separation;
    }

    /**
     * Gets the distance to another {@link Agent}.
     * @param predator Other agent.
     * @return Distance to the specified agent.
     */
    private double distanceToPredator(Predator predator) {
        return distanceToPoint(predator.getX(), predator.getY());
    }

    /**
     * Kills all nearby agents.
     */
    private void killNearbyAgents() {
        Agent closestAgent = null;
        double distance = Double.MAX_VALUE;

        for (Agent agent : agents) {
            if (distanceToAgent(agent) < distance && !agent.isDead()) {
                closestAgent = agent;
                distance = distanceToAgent(agent);
            }
        }

        if (closestAgent != null && distance <= KILL_DISTANCE) {
            closestAgent.kill();
        }
    }

    /**
     * Renders a predator onto a graphics object.
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
     * Gets the horizontal position of the predator.
     * @return Horizontal coordinate.
     */
    double getX() {
        return position.x();
    }

    /**
     * Gets the vertical position of the predator.
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
     * Gets the size of the triangle used to render the predator.
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
}
