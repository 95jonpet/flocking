package se.peterjonsson.flocking;

import math.geom2d.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

class Predator {
    /**
     * Distance the agent moves in one step/update.
     */
    private static final int SPEED = 7;

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
        direction = new Vector2D(x, y).normalize();
    }

    /**
     * Updates the predator by stepping forward one step of the simulation.
     */
    void update() {
        Vector2D resultant = direction;

        if (agents.size() != 0) {
            double shortestDistance = Double.MAX_VALUE;
            Iterator<Agent> iterator = agents.iterator();
            Agent closestAgent = iterator.next();
            while (iterator.hasNext()) {
                Agent agent = iterator.next();
                double distance = distanceToAgent(agent);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestAgent = agent;
                }
            }

            Vector2D vector = new Vector2D(closestAgent.getX() - getX(), closestAgent.getY() - getY());
            resultant = resultant.plus(vector.normalize().times(0.5));
        }

        resultant = resultant.normalize().times(SPEED); // Normalize
        position = position.plus(resultant);
        direction = resultant.normalize();

        killNearbyAgents();
    }

    /**
     * Kills all nearby agents.
     */
    private void killNearbyAgents() {
        for (Agent agent : agents) {
            if (distanceToAgent(agent) < KILL_DISTANCE) {
                agent.kill();
            }
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
