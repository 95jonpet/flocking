package se.peterjonsson.flocking;

import javafx.geometry.Pos;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A circular obstacle that {@link Agent} objects should avoid.
 *
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
class Obstacle {

    /**
     * Obstacle radius.
     */
    static final int RADIUS = 20;

    /**
     * Horizontal coordinate.
     */
    final int x;

    /**
     * Vertical coordinate.
     */
    final int y;

    /**
     * Creates a new obstacle.
     * @param x Horizontal position.
     * @param y Vertical position.
     */
    Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Renders an obstacle.
     * @param position Obstacle position.
     * @param graphics Canvas to render onto.
     */
    static void render(Position position, Graphics2D graphics) {
        Ellipse2D.Double circle = new Ellipse2D.Double(position.x - RADIUS, position.y - RADIUS, RADIUS * 2, RADIUS * 2);
        graphics.setColor(Color.BLACK);
        graphics.fill(circle);
    }

}
