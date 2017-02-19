package se.peterjonsson.flocking;

class Position {
    final double x;
    final double y;
    final double angle;

    Position(final double x, final double y, final double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    Position(final double x, final double y) {
        this(x, y, 0);
    }
}
