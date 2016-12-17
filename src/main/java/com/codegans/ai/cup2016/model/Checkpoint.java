package com.codegans.ai.cup2016.model;

import model.Wizard;

import java.util.Arrays;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2016 20:21
 */
public final class Checkpoint {
    private static final double SIDE = 500;

    public final int x;
    public final int y;
    public final Point center;

    private Checkpoint(int x, int y) {
        this.x = x;
        this.y = y;
        this.center = new Point(x * SIDE + SIDE / 2, y * SIDE + SIDE / 2);
    }

    public static Checkpoint[] createLane(String... coordinates) {
        return Arrays.stream(coordinates).map(e -> new Checkpoint(e.charAt(0) - 'a', e.charAt(1) - '1')).toArray(Checkpoint[]::new);
    }

    public boolean isTaken(Wizard self) {
        double r = self.getRadius() * 2;

        return Double.compare(self.getDistanceTo(center.x, center.y), r) <= 0;
    }

    @Override
    public int hashCode() {
        return x * y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Checkpoint && equals((Checkpoint) obj);
    }

    public boolean equals(Point other) {
        return other != null && x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return String.format("(%.3f;%.3f)", x, y);
    }
}
