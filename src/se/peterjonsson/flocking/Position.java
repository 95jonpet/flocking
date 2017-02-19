package se.peterjonsson.flocking;

/**
 * Represents the position of an object, for example an {@link Agent} or an {@link Obstacle}.
 *
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
class Position {
    /**
     * Horizontal position.
     */
    final double x;

    /**
     * Vertical position.
     */
    final double y;

    /**
     * Angle in radians.
     */
    final double angle;

    /**
     * Creates a new position.
     * @param x Horizontal coordinate.
     * @param y Vertical coordinate.
     * @param angle Angle in radians.
     */
    Position(final double x, final double y, final double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    /**
     * Creates a new position.
     * @see #Position(double, double, double)
     * @param x Horizontal coordinate.
     * @param y Vertical coordinate.
     */
    Position(final double x, final double y) {
        this(x, y, 0);
    }
}
